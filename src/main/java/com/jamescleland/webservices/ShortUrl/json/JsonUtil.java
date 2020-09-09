/**
 * 
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
   * 
   * @return
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
   * 
   * @param o
   * @return
   * @throws JsonProcessingException
   */
  public String toJson(Object o) throws JsonProcessingException {
    return mapper.writeValueAsString(o);
  }
  
  /**
   * 
   * @param o
   * @return
   * @throws JsonProcessingException
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
