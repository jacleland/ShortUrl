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

//Jersey imports
import jakarta.validation.constraints.NotNull;
//Jackson imports
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 
 */
public class CreateUrlRequest {
  //URL to register with token
  private String    url;
  
  /**
   * Default constructor
   */
  public CreateUrlRequest() {
    this.url = "";
  }

  /**
   * Returns the URL associated with this instance
   * @return A String containing a URL value
   */
  public String getUrl() {
    return this.url;
  }
  
  /**
   * Sets the URL value
   * @param url A String containing the new URL value
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
  public TypeReference<CreateUrlRequest> getType() {
    return new TypeReference<CreateUrlRequest>() {};
  }
}
