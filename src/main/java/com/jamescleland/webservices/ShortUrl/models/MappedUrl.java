package com.jamescleland.webservices.ShortUrl.models;

//Java SQL imports
import java.sql.Timestamp;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * ShortUrl response object for web service token request
 */
public class MappedUrl extends ResponseBase {
  private Integer       id;
  private Timestamp     createTime;
  private String        token;
  private String        url;
  

  /**
   * Default constructor
   */
  public MappedUrl() {
    setId(-1);
    setCreateTime(new Timestamp(0));
    setToken(null);
    setUrl(null);
  }

  /**
   * Construct with parameters
   * @param id
   * @param createTime
   * @param token
   * @param url
   */
  public MappedUrl(Integer id, Timestamp createTime, String token, String url) {
    setId(id);
    setCreateTime(createTime);
    setToken(token);
    setUrl(url);
  }
  
  /**
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return the createTime
   */
  public Timestamp getCreateTime() {
    return createTime;
  }

  /**
   * @param createTime the createTime to set
   */
  public void setCreateTime(Timestamp createTime) {
    this.createTime = createTime;
  }

  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * @param token the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * Return the URL value for this instance
   * @return String containing URL value
   */
  public String getUrl() {
    return url;
  }

  /**
   * Set the URL value for this instance
   * @param url A String containing the URL value to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Return a type reference for use by Jackson object mapper
   * for serializing JSON data.
   * @return TypeReference raw instance for this class type
   */
  @NotNull
  @JsonIgnore
  public TypeReference<MappedUrl> getType() {
    return new TypeReference<MappedUrl>() {};
  }
  
  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    Timestamp ts = new Timestamp(0);
    System.out.println("Timestamp value="+ts.toString());
  }
}
