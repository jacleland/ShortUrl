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
import java.sql.SQLException;
import java.util.Properties;
//Import MariaDB packages
import org.mariadb.jdbc.MariaDbPoolDataSource;
//SLF4J Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection pool-based implementation of DbInterface.
 */
public class ConnectionPool extends DbConnection {
  //Logger instance
  private static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
  
  //The static connection pool instance
  private MariaDbPoolDataSource pool = null;
  
  /**
   * Default constructor
   */
  public ConnectionPool() {
    logger.atDebug()
      .log("Constructor");
  }

  /**
   * Initialize the connection pool
   */
  @Override
  public void init(Properties props) throws SQLException {
    super.init(props);
    
    //Log initialization
    logger.atDebug()
      .log("Initializing MariaDB Pool Data Source with URL {} and username {}", 
        getDbUrl(), getDbUser());
    
    //Initialize connection pool
    pool = new MariaDbPoolDataSource(getDbUrl());
    pool.setUser(getDbUser());
    pool.setPassword(getDbPassword());
  }

  /**
   * Returns a pool connection
   */
  @Override
  public Connection getConnection() throws SQLException {
    logger.atDebug().log("Returning pooled connection");
    return pool.getConnection();
  }

  /**
   * Implementation-specific close()/release() for connections obtained from 
   * this instance via getConnection()
   * TODO: Remove releaseConnection() from interface and subclasses? Pool connections
   * seem to be self-aware and Connection.close() should be sufficient for both
   * raw as well as pool connection implementations.
   */
  @Override
  public void releaseConnection(Connection conn) {
    //Pool-specific implementation should return the connection to the pool, 
    // just call connection.close()
    if(conn != null) {
      try {
        logger.atDebug().log("Closing connection");
        conn.close(); conn = null;
      }
      catch(Throwable t) {
        //Just swallow exceptions thrown from the close without propagating.
        //Log errors anyway
        t.printStackTrace();
      }
    }
  }

  /**
   * Clean up connection pool and related resources
   */
  @Override
  public void destroy() {
    //Close pool if it's been created
    if(pool != null) {
      logger.atDebug().log("Destroying connection pool");
      pool.close(); pool = null;
    }
    else {
      logger.atDebug().log("destroy() called for invalid pool (null)");
    }
  }

}
