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
package com.jamescleland.webservices.ShortUrl.servlet;

//Java imports
import java.net.URI;
import javax.servlet.ServletContext;
//Jersey imports
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//Local library imports
import com.jamescleland.webservices.ShortUrl.Configuration;
import com.jamescleland.webservices.ShortUrl.db.DataUtil;
import com.jamescleland.webservices.ShortUrl.models.CreateUrlRequest;
import com.jamescleland.webservices.ShortUrl.models.UrlResponse;

/**
 * Short URL web service servlet
 * @author jcleland (James A. Cleland)
 */
@Path("/") 
public class UrlService {
  ///Class final definitions
  public static final String        PARAM_PREFIX    = "SSU-";
  
  ///TODO: Temporary URL to be removed after testing
  public static final String        TEMP_URL        = "http://www.google.com";
  
  //The DbConnection instance to be used by this servlet instance
  private static DataUtil           db;
  
  ///Servlet context for configuration parameters, etc
  @Context ServletContext           context;
  
  static {
    //Initialize the DB connection instance with properties from the configuration
    db = new DataUtil();
    db.init(Configuration.properties);
  }
  
  /**
   * Returns HTML that will redirect the browser to the URL saved with the 
   * token specified in the path parameter. If the token specified is not
   * found, an error page will be returned.
   * @param token Parameterized token value as SU-{token}
   * @return HTML content that will forward to the page keyed by the specified token
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
  @PUT 
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response newTokenForUrl(CreateUrlRequest request) {
    //Local data
    UrlResponse response = new UrlResponse();
    
    try {
      //Create short URL for request
      response = db.createShortUrl(request);
    }
    catch(Exception e) {
      e.printStackTrace();
      response.appendError(e.getMessage());
      response.setHttpStatus(500);
      response.setValid(false);
    }
    
    //Return the JSON data from registration
    return Response.status(response.getHttpStatus()).entity(response).build();
  }
  
  /**
   * Read the data associated with the path parameter token specified
   * @param token JSON containing data associated with this token
   * @return
   */
  @GET
  @Path("/read/{token}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response read(@PathParam("token") String token) {
    //Get short URL record for token
    UrlResponse response = db.readShortUrl(token);
    
    //Return JSON string
    return Response.status(response.getHttpStatus()).entity(response).build();
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
