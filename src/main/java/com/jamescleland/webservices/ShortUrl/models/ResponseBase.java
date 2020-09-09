package com.jamescleland.webservices.ShortUrl.models;

//Java imports
import java.util.List;

/**
 * 
 */
public class ResponseBase {
  //List of errors associated with the request
  private List<String> errors;

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
