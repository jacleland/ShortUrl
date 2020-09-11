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

//Java imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
//Java imports
import org.apache.commons.lang3.RandomStringUtils;
import com.jamescleland.webservices.ShortUrl.models.CreateUrlRequest;
import com.jamescleland.webservices.ShortUrl.models.UrlResponse;

/**
 * Data utility subclass extending the connection policy-specific implementation
 * for accessing the application backing store.
 */
public class DataUtil extends DbConnection {
  /**
   * Initialize underlying database connection
   */
  @Override
  public void init(Properties props) {
    //Init base class first
    super.init(props);
  }
  
  /**
   * Creates and registers a token for the URL specified in the register
   * object instance.
   * @param reg The registration data containing the URL to map
   * @return A MappedUrl instance that contains the short URL mapping data for the full URL
   */
  public UrlResponse createShortUrl(CreateUrlRequest reg) {
    UrlResponse response = new UrlResponse();
    Connection conn = null;
    PreparedStatement stmt = null;
    
    try {
      //Generate a token for the URL
      String token = RandomStringUtils.randomAlphanumeric(12);
      
      //Insert the token, URL into the map table
      conn = getConnection();
      
      //Create the prepared statement and execute
      stmt = conn.prepareStatement("INSERT INTO ShortUrl.ShortMap(token, url)"
          + " VALUES('" + token + "', '" + reg.getUrl() + "');");
      stmt.execute();
      
      //Set the response data token and URL, the insert would throw on error
      response.setToken(token);
      response.setUrl(reg.getUrl());
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
  public UrlResponse readShortUrl(String token) {
    //Declare locals
    UrlResponse response = new UrlResponse();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
      //TODO: Validate token - [a-zA-Z0-9]{min,max} might be enough here
      //Get connection
      conn = getConnection();
      
      //Create a query statement to retrieve a row by token
      stmt = conn.prepareStatement("SELECT id, createTime, token, url"
          + " FROM ShortUrl.ShortMap WHERE token='" + token + "';");
      rs = stmt.executeQuery();
      while(rs.next()) {
        response.setId(rs.getInt("id"));
        response.setCreateTime(rs.getTimestamp("createTime"));
        response.setToken(rs.getString("token"));
        response.setUrl(rs.getString("url"));
      }
    } 
    catch(SQLException sqle) {
      //Print stack trace to log
      sqle.printStackTrace();
      
      //Append exception error message to response and set code/invalid
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
  @Override
  public void destroy() {
    super.destroy();
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
      try { releaseConnection(conn); } 
      catch(Throwable t) {}
    }
    
    if(rs != null) {
      try { rs.close(); } 
      catch(Throwable t) {}
    }
    
    stmt = null; conn = null; rs = null;
  }
}
