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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simple connection class implementing DbInterface. This class should not be
 * used for a production deployment - The connection pool implementation from 
 * DbConnectionPool is preferred. 
 * @author jcleland (James A. Cleland)
 */
public class DbConnection implements DbInterface {
  //Driver manager instance, created and initialized during init()
  @SuppressWarnings("unused")
  private DriverManager         manager;
  
  //Properties manager for database configuration
  protected Properties          properties;
  
  //The host name that will be used in the connect string
  private String dbHost;
  
  //The port to use when connecting to the database
  private String dbPort;
  
  //The database name
  private String dbName;
  
  //The database user name
  private String dbUser;
  
  //The database user's password
  private String dbPassword;
  
  /**
   * Default constructor
   */
  public DbConnection() {}

  /**
   * Initialize the connection class instance and read parameters from the 
   * provided properties instance. Default values will be used if the properties
   * are missing, ShortUrl database at localhost:3306 with admin user and no 
   * password are assumed.
   */
  @Override
  public void init(Properties props) {
    //Keep reference to properties instance
    this.properties = props;

    //Read values from properties
    //TODO: Validate required properties?
    dbHost = properties.getProperty("dbHost", "localhost");
    dbPort = properties.getProperty("dbPort", "3306");
    dbName = properties.getProperty("dbName", "ShortUrl");
    dbUser = properties.getProperty("dbUser", null);
    dbPassword = properties.getProperty("dbPassword", null);
    
    try {
      //Force driver load (Not supposed to need this anymore?)
      Class.forName("org.mariadb.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Returns a connection to the MariaDB database
   */
  @Override
  public Connection getConnection() throws SQLException {
    Connection conn = 
        DriverManager.getConnection(getDbUrl(), dbUser, dbPassword);
    return conn;
  }

  /**
   * Releases a connection retrieved using getConnection()
   */
  @Override
  public void releaseConnection(Connection conn) {
    try {
      if(conn != null) {
        conn.close();
      }
    }
    catch(SQLException sqle) {
      //Ignore errors during connection.close()
    }
  }

  /**
   * Cleans up the connection class, freeing any resources allocated during
   * init() or runtime.
   */
  @Override
  public void destroy() {
  }

  /**
   * Returns the connection URL for MariaDB JDBC connection using values from
   * properties or defaults. The user parameter is only specified if found in
   * configuration and a password is only specified if provided and the user
   * value is not null. Otherwise, the default (root) user is assumed with an
   * empty password.
   * @return Connect string (URL) for MariaDB JDBC driver
   */
  private String getDbUrl() {
    //Build connection URL for MariaDB JDBC connection
    String url = "jdbc:mariadb://"+dbHost+":"+dbPort+"/"+dbName;
//    if(dbUser != null) {
//      url += "?user="+dbUser;
//      if(dbPassword != null) {
//        url += "&password="+dbPassword;
//      }
//    }
    return url;
  }
}
