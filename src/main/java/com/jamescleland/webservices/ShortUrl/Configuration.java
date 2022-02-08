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
package com.jamescleland.webservices.ShortUrl;

//Java imports
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
//Jersey imports
//Project imports
import com.jamescleland.webservices.ShortUrl.servlet.UrlService;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuration class for ShortUrl application. This class was copied from the
 * example located at: https://stackoverflow.com/questions/29543597/loading-properties-file-in-a-java-jersey-restful-web-app-to-persist-throughout
 */
@ApplicationPath("ShortUrl")
public class Configuration extends Application {
  //Configuration file in deployment directory. This file needs to be created
  // by copying the provided template and adjusting configuration parameters
  // as required for the local database installation and user.
  public static final String PROPERTIES_FILE = "config.properties";
  
  //Properties instance to be used for aggregating configuration values
  public static Properties properties = new Properties();

  /**
   * Read properties from the configuration file resource and load static instance
   * @return The static properties instance
   */
  private Properties readProperties() {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
      if (inputStream != null) {
          try {
              properties.load(inputStream);
          } catch (IOException e) {
              // TODO Add your custom fail-over code here
              e.printStackTrace();
          }
      }
      return properties;
  }
  
  /**
   * Override for Jersey init
   */
  @Override
  public Set<Class<?>> getClasses() {     
      // Read the properties file
      readProperties();
  
      // Set up your Jersey resources
      Set<Class<?>> rootResources = new HashSet<Class<?>>();
      rootResources.add(UrlService.class);
      return rootResources;
  }
}