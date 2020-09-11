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

//Java imports
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ResponseBase {
  //List of errors associated with the request
  private List<String> errors = new ArrayList<String>();

  //HTTP status code associated with the request
  //TODO: This will always be 200 for now, but may be used to indicate request-
  // specific conditions in the future. For example, if a duplicate URL is 
  // provided in a 'create' request and the token returned already existed.
  private Integer httpStatus = 200;

  //Indicates whether the request succeeded or failed
  private Boolean valid = true;

  /**
   * Return the list of errors associated with the request
   * @return A List<String> containing 0 or more error messages
   */
  public List<String> getErrors() {
    return this.errors;
  }

  /**
   * Sets the list of errors 
   * @param errors A List<String> containing 0 or more error strings
   */
  public void setErrors(List<String> errors) {
    this.errors = errors;
  }

  /**
   * Appends an error message to the List<String> of errors
   * @param error The error message string to append to error list
   */
  public void appendError(String error) {
    this.errors.add(error);
  }
  
  /**
   * Returns the HTTP status code associated with the the request
   * @return An HTTP status code, 200 for example when the request was successful
   */
  public Integer getHttpStatus() {
    return this.httpStatus;
  }

  /**
   * Sets the HTTP status code associated with the processing of the request
   * @param status The HTTP status code
   */
  public void setHttpStatus(Integer status) {
    this.httpStatus = status;
  }

  /**
   * Returns a boolean indicating whether or not the request was processed 
   * successfully.
   * @return True when the request was processed successfully, false on error.
   */
  public boolean isValid() {
    return this.valid;
  }

  /**
   * Sets the status of the request processing, depending on success or failure
   * @param valid Boolean indicating whether or not the request was processed 
   */
  public void setValid(boolean valid) {
    this.valid = valid;
  }
}
