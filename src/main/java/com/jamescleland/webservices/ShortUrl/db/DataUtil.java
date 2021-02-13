/**
 *  This file belongs to the ShortUrl project, the latest version of which
 *  can be found at https://github.com/jacleland/ShortUrl.
 *
 *  Copyright (c) 2020, James A. Cleland <jcleland at jamescleland dot com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jamescleland.webservices.ShortUrl.db;

import java.lang.reflect.Constructor;
//Java imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
//Apache imports
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//Local imports
import com.jamescleland.webservices.ShortUrl.models.CreateUrlRequest;
import com.jamescleland.webservices.ShortUrl.models.UrlResponse;

/**
 * Data utility subclass extending the connection policy-specific implementation
 * for accessing the application backing store.
 */
public class DataUtil {
  //Logger instance
  private static Logger logger = LoggerFactory.getLogger(DataUtil.class);
  
  //The name of the database interface implementation provider
  private String dbImplProvider = null;
  
  //The DB interface implementation provider
  private DbInterface dbImpl = null;
  
  //The length of the token to generate
  private int tokenLength = 5;
  
  /**
   * Initialize underlying database connection
   */
  public void init(Properties props) throws SQLException {
    //Get the data interface implementation provider from configuration props
    dbImplProvider = props.getProperty("dbProviderImpl", 
        "com.jamescleland.webservices.ShortUrl.db.ConnectionPool");

    //Log provider class
    logger.atDebug().log("Initialize using provider{}", dbImplProvider);
    
    try {
      //Create instance of database interface provider implementation as
      // specified in the configuration properties (or using default implementation
      // from ConnectionPool)
      Class<?> clazz = Class.forName(dbImplProvider);
      Constructor<?> constructor = clazz.getConstructor();
      dbImpl = (DbInterface) constructor.newInstance();
    } 
    catch(Exception e) {
      //Create implementation from class name using reflection failed
      throw new SQLException("Unable to create DbInterace implementation: "
          + e.getMessage());
    } 
    
    //Initialize the implementation instance
    dbImpl.init(props);
    
    //Get token length from properties
    tokenLength = Integer.valueOf(props.getProperty("tokenLength", "8"));
    if(tokenLength < 5) tokenLength = 5;
    
    //Log token length value
    logger.atDebug().log("Using token length of {} characters", tokenLength);
  }
  
  /**
   * Creates and registers a token for the URL specified in the register
   * object instance.
   * @param reg The registration data containing the URL to map
   * @return A MappedUrl instance that contains the short URL mapping data for the full URL
   */
  @SuppressWarnings("resource")
  public UrlResponse create(CreateUrlRequest reg) {
    UrlResponse response = new UrlResponse();
    Connection conn = null;
    PreparedStatement stmt = null;
    int retry = 3;
    
    try {
      //Get a connection from provider and create an insert statement
      conn = dbImpl.getConnection();
      stmt = conn.prepareStatement(
          "INSERT INTO ShortUrl.ShortMap(token, url) VALUES(?, ?);");

      //Completed flag for successful statement execute to fall out of while
      boolean done = false;
      do {
        try {
          //Generate a token for the URL
          String token = RandomStringUtils.randomAlphanumeric(tokenLength);
          
          //Set parameters for prepared statement and execute
          stmt.setString(1, token);
          stmt.setString(2, reg.getUrl());
          
          logger
            .atDebug()
            .log("Creating URL mapping for {} using token {}", reg
              .getUrl(), token);
          logger
            .atTrace()
            .log("Statement: {}", stmt
              .toString());
          
          stmt.execute(); //Throw on unique constraint
          
          //Set the response data token and URL, the insert would throw on error
          response.setToken(token);
          response.setUrl(reg.getUrl());
          done = true; //Here on no errors during INSERT
        }
        catch(SQLException sqle) {
          //Throw out of while, outer try/catch handles response and cleanup
          if(retry <= 0) {
            
            throw sqle; 
          }
          
          logger
            .atWarn()
            .log("Duplicate token generated, retries left: {}", retry);
         
          //Decrement retry counter
          retry--;
        }
        
        //Fall out on no errors, retry<1 throws past on exception
      } while(!done); 
    }
    catch(SQLException sqle) {
      //Print the stack trace to log
      sqle.printStackTrace();
      
      //Append the exception error message and set response valid state and code
      response.appendError(sqle.getMessage());
      response.setHttpStatus(507);
      response.setValid(false);
    }
    finally {
      //Cleanup SQL objects
      sqlCleanup(conn, stmt, null);
    }
    
    return response;
  }
  
  /**
   * Returns a response record containing short URL data for the specified
   * token if it exists in the database.
   * @param token The token that represents the shortened URL record data
   * @return A UrlResponse object containing row data or error info if not found
   */
  public UrlResponse read(String token) {
    //Declare locals
    UrlResponse response = new UrlResponse();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    //Set token in the response object
    response.setToken(token);

    try {
      logger
        .atDebug()
        .log("Retrieving mapped URL for token {}", token);
      
      //TODO: Validate token - [a-zA-Z0-9]{min,max} might be enough here
      //Get connection
      conn = dbImpl.getConnection();
      
      //Create a query statement to retrieve a row by token
      stmt = conn.prepareStatement("SELECT id, createTime, token, url"
          + " FROM ShortUrl.ShortMap WHERE token='" + token + "';");
      
      logger
        .atTrace()
        .log("Statement: {}", stmt.toString());
      
      rs = stmt.executeQuery();
      if(rs.next()) {
        //Read first returned row
        //TODO: Slight chance of duplicate tokens, maybe we should add ORDER BY
        // on timestamp so that we return the first created? Eventually, there
        // should be a unique constraint on the token column and retry logic in 
        // the app OR generate a more unique token. Likely the former in the 
        // interest of keeping the token short and tolerating 5% chance of 
        // duplicates at, say 100,000 records.
        response.setId(rs.getInt("id"));
        response.setCreateTime(rs.getTimestamp("createTime"));
        response.setUrl(rs.getString("url"));
      }
      else {
        //No rows returned for this query, result is invalid and 404 (not found)
        String err = "The URL for token '"+token+"' was not found";
        logger
          .atWarn()
          .log(err);
        
        response.setValid(false);
        response.setHttpStatus(404);
        response.appendError(err);
      }
    } 
    catch(SQLException sqle) {
      //Print stack trace to log
      sqle.printStackTrace();
      
      //Append exception error message to response and set code/invalid - This
      // is a server error indicating unable to connect or bad DB/user/etc
      response.appendError(sqle.getMessage());
      response.setHttpStatus(507);
      response.setValid(false);
    }
    finally {
      //Cleanup SQL objects
      sqlCleanup(conn, stmt, rs);
    }
    
    //Return response
    return response;
  }
  
  /**
   * Cleans up the connection base class and any resources that we've allocated
   * in our subclass.
   */
  public void destroy() {
    if(dbImpl != null)
      dbImpl.destroy();
  }

  /**
   * Cleanup resources associated with a statement, query, and result set. All
   * parameters are optional and can be null. Non-null parameters are assumed
   * to be close()able. No exceptions are propagated from this method.
   * @param conn A connection object instance that can be closed/freed or null 
   * @param stmt A prepared statement that can be closed or null
   * @param rs A result set that can be closed or null
   */
  private void sqlCleanup(Connection conn, PreparedStatement stmt, ResultSet rs) {
    if(stmt != null) {
      try { stmt.close(); } 
      catch(Throwable t) {}
    }
    
    if(conn != null) {
      try { dbImpl.releaseConnection(conn); } 
      catch(Throwable t) {}
    }
    
    if(rs != null) {
      try { rs.close(); } 
      catch(Throwable t) {}
    }
    
    stmt = null; conn = null; rs = null;
  }
}
