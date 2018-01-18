# Springboot & Angular Frontend Optimising

## Getting Started
To improve performance at the front end, this example application employs below techniques:
 * Reducing the number of HTTP requests initiated by an HTML page.
 * Reducing the number of bytes associated with a web page download, for instance by resizing and compressing images.

## Prerequisites
  
### Backend application setup 
 * To start from the scratch, download Springboot archetype
```
 curl https://start.spring.io/starter.zip \
-d dependencies=web,devtools \
-d groupId=com.sungung \
-d artifactId=optimise-frontend \
-d name=optimise-frontend \
-d description="Optimising frontend" \
-d baseDir=optimise-frontend \
-o optimise-frontend.zip
```

### Frontend application setup
 * nodejs
 * angular-cli
```
npm install -g angular-cli
ng new frontend
cd frontend
ng serve`
```
## Build & deployment
### Integrating frontend & backend through client proxy
 * Create 'proxy.conf.json' in frontend directory
```
{
 "/api" : {
  "target" : "http://localhost:8080",
  "secure" : false
 }
}
```
 
### Build frontend
 * Go to frontend directory and run `ng build --deploy-url assets -prod` to compile the source into an output directory 'dist'

### Build all together into single jar
 * Add **maven-resources-plugin** into 'pom.xml' file to generate frontend artifacts into the jar.
```
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
		</plugin>
		<plugin>
			<artifactId>maven-resources-plugin</artifactId>
			<executions>
				<execution>
					<id>copy-resources</id>
					<phase>validate</phase>
					<goals>
						<goal>copy-resources</goal>
					</goals>
					<configuration>
						<outputDirectory>${basedir}/target/classes/static/</outputDirectory>
						<resources>
							<resource>
								<directory>${basedir}/app-frontend/dist</directory>
							</resource>
						</resources>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```
## Test

### Frontend artifact cache
 * Add CacheControl header in Spring MVC resource handler
```
@Configuration    
public static class WebConfig extends WebMvcConfigurerAdapter{
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/assets/**")
		.addResourceLocations("classpath:/static/assets/")
		.setCacheControl(CacheControl.maxAge(5, TimeUnit.DAYS));
	}    	
}
```

 * ng build will compile bundle with unique id, so it will bust cache when new version to be downloaded.
 * we need to define deployment location in 'angular-cli.json'
```
"apps": [
{
  "root": "src",
  "outDir": "dist",
  "assets": [
	"assets",
	"favicon.ico",
	"uncached.txt"
  ], 
```
 * Test with curl, second request will always get **304** status.
```
$ curl -I localhost:8080/assets/cached.txt
HTTP/1.1 200
Last-Modified: Wed, 17 Jan 2018 16:29:54 GMT
Cache-Control: max-age=432000
Accept-Ranges: bytes
Content-Type: text/plain
Content-Length: 20
Date: Wed, 17 Jan 2018 18:28:18 GMT

$ curl -I --header 'If-Modified-Since: Wed, 17 Jan 2018 16:29:54 GMT' localhost:8080/assets/cached.txt
HTTP/1.1 304
Last-Modified: Wed, 17 Jan 2018 16:29:54 GMT
Date: Wed, 17 Jan 2018 18:30:40 GMT
```
 * Second request for the resource is not cache controlled(Cache-Control: no-store) will return **200** status
```
$ curl -I localhost:8080/uncached.txt
HTTP/1.1 200
Last-Modified: Wed, 17 Jan 2018 16:29:54 GMT
Cache-Control: no-store
Accept-Ranges: bytes
Content-Type: text/plain
Content-Length: 14
Date: Wed, 17 Jan 2018 18:39:27 GMT

$ curl -I localhost:8080/uncached.txt
HTTP/1.1 200
Last-Modified: Wed, 17 Jan 2018 16:29:54 GMT
Cache-Control: no-store
Accept-Ranges: bytes
Content-Type: text/plain
Content-Length: 14
Date: Wed, 17 Jan 2018 18:39:44 GMT`
```

 * Test REST resources, request will get entity tag(ETag) response for caching
```
$ curl -I localhost:8080/api/customer/1
HTTP/1.1 200
ETag: "0"
Cache-Control: max-age=31536000
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Wed, 17 Jan 2018 18:43:45 GMT

$ curl -I --header 'If-None-Match: "0"' localhost:8080/api/customer/1
HTTP/1.1 304
ETag: "0"
Cache-Control: max-age=31536000
Date: Wed, 17 Jan 2018 18:45:56 GMT
```
 
 
 


