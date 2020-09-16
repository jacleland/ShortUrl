ShortUrl
========
The [ShortUrl](https://github.com/jacleland/ShortUrl) project is a web service implementation of a redirecting short URL web application, developed by [James A. Cleland](mailto:jcleland@jamescleland.com). The web service is implemented in [Java](https://openjdk.java.net/) and utilizes the [Jersey](https://eclipse-ee4j.github.io/jersey/) implementation of the [JAX-RX](https://github.com/jax-rs) specification, as well as [Jackson](https://github.com/FasterXML/jackson) for parsing [JSON](https://www.json.org/json-en.html). A light-weight HTML5/CSS3/JavaScript front-end is provided as part of the project, although the web service implementation is designed to be easily integrated into other applications as a plugin.


## Prerequisites
The application is distributed as Java source code and includes a build file for use with the [Apache Maven](https://maven.apache.org/) software management system. Building ShortUrl requires that a JDK be installed. The remaining dependencies are included in the Maven pom.xml build file, which is located in the project root directory, and will be resolved/downloaded by Maven during the build process. A summary of compile-time (indicated by **build**) and run-time environment (indicated by **run**) prerequisites is provided below:

* [**Apache Maven** Version 3.6.3+](apache-maven-3.6.3-bin.tar.gz) (**build**)  - Available via most Linux distribution package managers.
* [**OpenJDK/JRE** Version 11+](https://openjdk.java.net/) (**build/run**) - Compliance level 11 required, although newer JDK versions can be used.
* [**Apache Tomcat** Version 9.0.37](https://tomcat.apache.org/download-90.cgi)  (**run**) - Version 9 required, see package manager on Linux platforms.
* [**MariaDB** Version 10.3.22+](https://mariadb.org/) (**run**) - Available with most Linux Distributions and commonly installed by default.

### Servlet Container
[Apache Tomcat](http://tomcat.apache.org/) was used as a servlet container during development and testing of the application. Any version of Tomcat that is compatible with the Servlet API 3.1 specification can be used.

### Database
ShortUrl makes use of a relational database to store mapping information that associates a generated 'token' with the URL of a site to be referenced. The current implementation requires use of [MariaDB](https://mariadb.org/), an open-source RDBMS implementation based on the original MySQL project. MariaDB is a stable, high-performance database, widely used by cloud-service providers and is included as the default relational database management system with most Linux distributions.

Your deployment should have a working MariaDB version 10+ installation that has a dedicated database named `ShortUrl` created for use by this application. Your database should also have a user account that has been granted access to the ShortUrl database. The default database, user, and password can be customized by way of the application properties file. A template properties file is included in the src/main directory of the project and includes the default application settings for a typical database deployment. From the `mysql` command prompt as the administrative user, the database and user can be created using:

```sql
CREATE DATABASE ShortUrl;
GRANT ALL ON ShortUrl.* to 'ShortUrl'@'%' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
```
where `password` is the database password for the ShortUrl user, specified as 'ShortUrl' in the default configuration properties file.

DDL for creating the required database objects is provided in the [db/schema.ddl](https://github.com/jacleland/ShortUrl/blob/master/db/schema.ddl) file. Before deploying the application, initialize the database by executing the following `mysql` command from within the application directory. Mysql will prompt you for the password specified above when the database user was created.

```sql
mysql -u ShortUrl -p < db/schema.ddl
```

## Building the Application
Prior to building the application, copy or rename the file `src/main/resources/config.properties.template` to `src/main/resources/config.properties`. The properties contained within the file should be edited as necessary for your database installation and runtime preferences. The configuration can be edited *after* deployment as well, but it is recommended that `src/main/resources/config.properties` be included in the web archive to prevent Tomcat from using default property values.

Switch directories to the application root (ShortUrl) and initiate the build process by running Maven with the target 'package':

```bash
mvn package
```
Subsequent builds can be executed by specifying the 'clean' target in addition to 'package':

```bash
mvn clean package
```
Maven will produce a web archive file called `ShortUrl.war` in the `target` directory. This file can be automatically deployed by Tomcat, if configured to do so, by placing the .war file in the tomcat `/webapps` directory. **Please Note** that the application URL will be `ShortUrl` by default unless the `ShortUrl.war` file is renamed. You may choose, for example, to name your war file `s.war` *before* copying/uploading it to the Tomcat `webapps` directory so that it will be deployed with a shorter application/context name. For example, if your domain name is `example.com`, a war file named `s.war` would be auto-deployed by Tomcat as `https://example.com/s`.

# Usage
The application, as deployed, can be accessed via the context URL (`https://example.com/s`, for instance). The static web content provided can also be used as a reference when writing applications to use the web service directly. 

If a web services-only deployment is needed, then the contents of the `src/main/webapp/static` directory can be removed or relocated outside the project tree. App developers may also wish to review the deployment descriptor (`src/main/webapp/WEB-INF/web.xml`) and remove the static filter declaration and mapping, along with the welcome-file list that includes `static/index.html`.

# Web Services Interface
The ShortUrl web service exposes three interfaces, described below. Hereafter, the documentation examples will use `https://examp.le/s` as the URL and context prefix. This should be interpreted to represent your domain name, IP address, HTTP scheme, and application deployment context.

## /create
*HTTP 1.1 command:* **PUT** 

*Content-type:* application/json

*Response type:* application/json

*Example BODY content*

```json
{
	"url": "<url_to_shorten>"
}
```
<url_to_shorte> should be a long URL for which a short URL will be returned. 
*JavaScript Example:*

```javascript
var xhrPut = new XMLHttpRequest();
xhrPut.open('PUT', 'https://examp.le/s/create');
xhrPut.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
xhrPut.onload = function() {
	handleResponse(xhrPut.responseText);
}
xhrPut.send('{ "url": "https://github.com/jacleland/ShortUrl/blob/master/src/main/java/com/jamescleland/webservices/ShortUrl/servlet/UrlService.java" }');
```
The `handleResponse()` function could be implemented using JSON.parse() to extract key/value pairs from the response body. A successful response body where the request body contained a URL for `https://google.com` might be:

```json
{
	"errors":[],
	"httpStatus":200,
	"valid":true,
	"token":"4NJkQT",
	"url":"https://github.com/jacleland/ShortUrl/blob/master/src/main/java/com/jamescleland/webservices/ShortUrl/servlet/UrlService.java",
	"shortUrl":"http://examp.le/s/4NJkQT"
}
```
The web service client can determine success or failure immediately by evaluating the `valid` or `httpStatus` properties. Upon success, these property values should be `true` and `200`, respectively.

Upon success, the value returned for the `shortUrl` property (in this case, `http://examp.le/s/4NJkQT`) can be used to reference the URL provided in the JSON request body, also reference in the response as `url`.

