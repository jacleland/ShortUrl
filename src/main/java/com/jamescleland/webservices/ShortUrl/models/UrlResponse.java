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
package com.jamescleland.webservices.ShortUrl.models;

//Java SQL imports
import java.sql.Timestamp;
import javax.validation.constraints.NotNull;
//Jackson imports
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * ShortUrl response object for web service token request
 */
public class UrlResponse extends ResponseBase {
  private Integer       id;
  private Timestamp     createTime;
  private String        token;
  private String        url;
  
  @JsonIgnore
  private String        shortUrl;

  /**
   * Default constructor
   */
  public UrlResponse() {
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
  public UrlResponse(Integer id, Timestamp createTime, String token, String url) {
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
   * Return the short URL value for this instance
   * @return String containing URL value
   */
  public String getShortUrl() {
    return this.shortUrl;
  }

  /**
   * Set the short URL value for this instance
   * @param url A String containing the URL value to set
   */
  public void setShortUrl(String shortUrl) {
    this.shortUrl = shortUrl;
  }

  /**
   * Return a type reference for use by Jackson object mapper
   * for serializing JSON data.
   * @return TypeReference raw instance for this class type
   */
  @NotNull
  @JsonIgnore
  public TypeReference<UrlResponse> getType() {
    return new TypeReference<UrlResponse>() {};
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    Timestamp ts = new Timestamp(0);
    System.out.println("Timestamp value="+ts.toString());
  }
}
