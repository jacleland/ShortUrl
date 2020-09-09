package com.jamescleland.webservices.ShortUrl.servlet;

import java.net.URI;
import java.sql.Timestamp;
import javax.ws.rs.Consumes;
//Jersey imports
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamescleland.webservices.ShortUrl.json.JsonUtil;
import com.jamescleland.webservices.ShortUrl.models.MappedUrl;

/**
 * 
 */
@Path("/") 
public class UrlService {
  ///Class final definitions
  public static final String        PARAM_PREFIX    = "SSU-";
  
  ///TODO: Temporary URL to be removed after testing
  public static final String        TEMP_URL        = "http://www.google.com";
  
  /**
   * Returns HTML that will redirect the browser to the URL saved with the 
   * token specified in the path parameter. If the token specified is not
   * found, an error page will be returned.
   * @param token Parameterized token value as SU-{token}
   * @return HTML content that will foward to the page keyed by the specified token
   */
  @GET
  @Path("/{token_param}")
  @Produces(MediaType.TEXT_HTML)
  public Response redirectForToken(@PathParam("token_param") String token_param) {
    try {
      //Parse token from path parameter to remove well-known prefix
      @SuppressWarnings("unused")
      String token = parseToken(token_param);
      
      //Retrieve URI for specified token
      URI target = new URI(TEMP_URL);
      
      //Build redirect response and return
      return Response.temporaryRedirect(target).build();
    }
    catch(Exception e) {
      return Response.status(404).build();
    }
  }
  
  /** 
   * Creates a new token for the URL contained within the JSON data provided.
   * @return JSON containing the retrieved URL info or error information if not found
   */
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public String newTokenForUrl() {
    return "";
  }
  
  /**
   * Read the data associated with the path parameter token specified
   * @param token JSON containing data associated with this token
   * @return
   */
  @GET
  @Path("/read/{token}")
  @Produces(MediaType.APPLICATION_JSON)
  public String read(@PathParam("token") String token) {
    //Local data
    String json = "";
    
    try {
      //TODO: Temporary - create mapped url instance with dummy data
      MappedUrl mappedUrl = new MappedUrl(1, new Timestamp(0), token, TEMP_URL);
      json = JsonUtil.getInstance().toPrettyJson(mappedUrl);
    }
    catch(JsonProcessingException jpe) {
      //Internal error generating JSON from model
      json = "ERROR";
    }
    
    //Return JSON string
    return json;
  }
  
  /**
   * Parses token value from the specified parameter.
   * @param param
   * @return
   * @throws Exception on invalid token path parameter format/prefix
   */
  private String parseToken(String param) throws Exception {
    int index = param.indexOf(PARAM_PREFIX);
    if(index != 0) {
      throw new Exception("Invalid token path parameter");
    }
    return param.substring(index+PARAM_PREFIX.length());
  }
}
