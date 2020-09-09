/**
 * 
 */
package com.jamescleland.webservices.ShortUrl.db;

import java.sql.Connection;
//Java imports
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 */
public class DbConnection implements DbInterface {
  //Driver manager instance, created and initialized during init()
  @SuppressWarnings("unused")
  private DriverManager         manager;
  
  //Properties manager for database configuration
  protected Properties          properties;
  
  //The hostname that will be used in the connect string
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
  }

  /**
   * Returns a connection to the MariaDB database
   */
  @Override
  public Connection getConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(getDbUrl());
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
    if(dbUser != null) {
      url += "?user="+dbUser;
      if(dbPassword != null) {
        url += "&password="+dbPassword;
      }
    }
    return url;
  }
}
