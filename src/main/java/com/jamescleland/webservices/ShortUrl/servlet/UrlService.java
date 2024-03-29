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
//Jersey imports
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
//SLF4J Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
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
  //Logger instance
  private static Logger logger = LoggerFactory.getLogger(UrlService.class);

  //The DbConnection instance to be used by this servlet instance
  private static DataUtil           shortUrlDb;
  
  ///Servlet context for configuration parameters, etc
  @Context ServletContext           context;
  
  static {
    //Initialize the DB connection instance with properties from the configuration
    shortUrlDb = new DataUtil();
    
    try {
      //Initialize the database connection provider
      logger
        .atDebug()
        .log("Static initialize DB interface");
      
      shortUrlDb.init(Configuration.properties);
    }
    catch(Exception e) {
      //Log DB initialization errors here, application may not function properly
      System.out.println("CRITICAL ERROR during initialization - "
          + "Application may not function properly");
      e.printStackTrace();
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
  public Response newTokenForUrl(@Context HttpServletRequest servletRequest,
      CreateUrlRequest request) 
  {
    //Local data
    UrlResponse response = new UrlResponse();
    String sessionId = servletRequest.getSession().getId();
    
    //Create profiler
    //TODO: This should be controlled by properties
    Profiler profiler = new Profiler(sessionId);
    profiler.setLogger(logger);
    profiler.start("create");
    
    try {
      //Create short URL for request
      logger
        .atDebug()
        .log("/create - Creating short URL for {}", request.getUrl());
      
      response = shortUrlDb.create(request);
      response.setShortUrl(createShortUrl(servletRequest, response));
    }
    catch(Exception e) {
      e.printStackTrace();
      response.appendError(e.getMessage());
      response.setHttpStatus(500);
      response.setValid(false);
    }
    
    //Profiler elapsed time
    //TODO: Conditional based on properties
    TimeInstrument instrument = profiler.stop();
    instrument.log();
    
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
  public Response read(@Context HttpServletRequest request,
      @PathParam("token") String token) 
  {
    //Get short URL record for token and build the short URL w/prefix
    UrlResponse response = shortUrlDb.read(token);
    response.setShortUrl(createShortUrl(request, response));
    
    //Return JSON string
    return Response.status(response.getHttpStatus()).entity(response).build();
  }
  
  /**
   * Returns HTML that will redirect the browser to the URL saved with the 
   * token specified in the path parameter. If the token specified is not
   * found, an error page will be returned.
   * @param token Parameterized token value as SU-{token}
   * @return HTML content that will forward to the page keyed by the specified token
   */
  @GET
  @Path("/{token}")
  @Produces(MediaType.TEXT_HTML)
  public Response redirectForToken(@Context HttpServletRequest request,
      @PathParam("token") String token) 
  {
    //System.out.println(request.getRequestURL());
    Response response;
    String sessionId = request.getSession().getId();
    
    //Create profiler
    //TODO: This should be controlled by properties
    Profiler profiler = new Profiler(sessionId);
    profiler.setLogger(logger);
    profiler.start("redirect");
    
    try {
      logger
        .atDebug()
        .log("/{token} - Redirecting request for token {}", token);
      
      //Get short URL record for token and build the short URL w/prefix
      UrlResponse urlResponse = shortUrlDb.read(token);
      
      
      //Build redirect response and return
      URI target = new URI(urlResponse.getUrl());
      //System.out.println(target.toString());
      logger
        .atDebug()
        .log("Generated redirect response to URL {}", target.toString());
      
      response = Response.temporaryRedirect(target).build();
    }
    catch(Exception e) {
      logger
        .atWarn()
        .log("No mapping found for token {}", token);
      
      response =Response.status(404).build();
    }
    
    //Profiler elapsed time
    //TODO: Conditional based on properties
    TimeInstrument instrument = profiler.stop();
    instrument.log();
    
    //Return the response object
    return response;
  }
  
  /**
   * Builds the shortened URL for the specified UrlResponse object that has 
   * bee populated with a valid token.
   * @param response A UrlResponse instance that has been successfully populated 
   * with a valid token
   * @return A String containing the short URL for the provided response
   * object's token
   */
  private String createShortUrl(HttpServletRequest request, 
      UrlResponse response) 
  {
    //Declare local
    String shortUrl = null;
    
    //Parse full request URL and base URI from HTTP request
    String requestUrl = request.getRequestURL().toString();
    String contextPath = request.getContextPath().toString();
    
    //Context path info should be a substring of the request URL. If index is 
    // valid, take the context path base substring.
    int index = requestUrl.indexOf(contextPath);
    if(index <= 0) {
      //Shouldn't be here, leave short URL empty and append an error. The
      // JS at the page should try to build the URL from the token in the 
      // reply and the context path that was used to make this request.
      response.appendError("Unable to extract context base URL from servlet request");
    }
    else {
      //Index looks valid so we take the request URL + context path substring
      shortUrl = requestUrl.substring(0, index + contextPath.length());
    }

    
    //Build short URL from context path and token
    shortUrl += "/" +response.getToken();
    
    logger
      .atDebug()
      .log("Created short URL {}", shortUrl);
    
    return shortUrl;
  }
}
