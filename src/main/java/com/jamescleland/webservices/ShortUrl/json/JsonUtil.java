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
package com.jamescleland.webservices.ShortUrl.json;

//Java imports
import java.io.IOException;
//Jackson imports
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility class for serializing and deserializing JSON data objects
 * using Jackson databind library.
 */
public class JsonUtil {
  //Singleton instance of this class
  private static JsonUtil           instance;
  
  //Jackson object mapper
  private ObjectMapper              mapper          = new ObjectMapper();
  private ObjectWriter              prettyWriter    = mapper.writerWithDefaultPrettyPrinter();

  /**
   * Returns the singleton instance of the JSON utility class, allocating a new
   * instance if necessary.
   * @return The singleton instance of the JsonUtil class
   */
  public static JsonUtil getInstance() {
    if(JsonUtil.instance == null) 
      JsonUtil.instance = new JsonUtil();
    
    return JsonUtil.instance;
  }
  
  /**
   * Private default constructor for singleton class
   */
  private JsonUtil() {
    //Initialize and configure the object mapper
    mapper.configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setDefaultPropertyInclusion(
        JsonInclude.Value.construct(Include.ALWAYS, Include.NON_NULL));
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }
  
  /**
   * Static construct with configured mapper instance
   * @param mapper ObjectMapper instance as configured during class static init
   */
  private JsonUtil(ObjectMapper mapper) {
    this.mapper = mapper;
  }
  
  /**
   * Serializes a java object to a JSON string
   * @param o The object to serialize
   * @return A string containing the JSON representation of the specified object
   * @throws JsonProcessingException On error serializing the java object to JSON string
   */
  public String toJson(Object o) throws JsonProcessingException {
    return mapper.writeValueAsString(o);
  }
  
  /**
   * Serializes a java object to a JSON string that is formatted/indented
   * @param o The object to serialize
   * @return A string containing the formatted JSON representation of the specified object
   * @throws JsonProcessingException On error serializing the java object to JSON string
   */
  public String toPrettyJson(Object o) throws JsonProcessingException {
    return prettyWriter.writeValueAsString(o);
  }
  
  /**
   * Returns an instance of T as parsed from JSON string data
   * @param <T> Class type for object to serialize data to
   * @param json String containing JSON data to parse
   * @param type Type reference for instance to return
   * @return An instance of T containing data from parsed JSON string
   */
  public <T> T fromJson(String json, TypeReference<T> type) throws Exception {
    try {
      return mapper.readValue(json, type);
    }
    catch(IOException ioe) {
      throw ioe;
    }
  }
}
