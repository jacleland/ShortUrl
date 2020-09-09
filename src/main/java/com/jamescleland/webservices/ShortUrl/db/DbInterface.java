package com.jamescleland.webservices.ShortUrl.db;

import java.sql.Connection;
import java.sql.SQLException;
//Java imports
import java.util.Properties;

/**
 * Interface specification for DB layer implementations. This interface will be
 * implemented by various connection and pool types, while those should be 
 * extended to implement a basic database layer for the application.
 */
public interface DbInterface {
  
  /**
   * Initialize the database layer using the properties provided. Classes 
   * that implement this interface should strive to use standardized property
   * names here: dbHost, dbPort, dbName, dbUser, dbPassword. Secure implementations
   * should read sensitive data such as passwords directly from the application 
   * properties only when requested and avoid retaining such values in memory
   * by way of forced GC, etc.
   * @param props A Properties or subclass instance providing connection-specific values
   * @throws Exception on invalid property values or missing required properties
   * or error creating cached or pooled resources.
   */
  public void init(Properties props) throws Exception;

  /**
   * Retrieves a connection from the database layer according to the implementation
   * protocol being defined. Ie: single connections for a simple connection-based
   * implementation or a pool resource from a connection pool implementation.
   * @return A Connection instance established using properties or default values
   * @throws SQLException on error creating connection or retrieving pool resource
   */
  public Connection getConnection() throws SQLException;
  
  /**
   * Releases and possibly destroys a connection previously retrieved from the
   * implementation using getConnection(). This method should be defined in
   * accordance with the implementing class protocol for handling connections.
   * Ie: tear down connections for a simple connection class or release a pool
   * resource for a connection pool implementation.
   * @param conn The connection to release or destroy, retrieved using getConnection()
   */
  public void releaseConnection(Connection conn);
  
  /**
   * Destroy the database layer, according to the implementation protocol. Ie:
   * free all pool resources for a connection pool implementation, etc.
   */
  public void destroy();
}
