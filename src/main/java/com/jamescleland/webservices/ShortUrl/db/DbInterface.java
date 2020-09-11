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
