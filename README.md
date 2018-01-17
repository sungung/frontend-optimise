# Springboot & Angular Frontend Optimising

## Getting Started
To improve performance at the front end, this example application employs below techniques:
 * Reducing the number of HTTP requests initiated by an HTML page.
 * Reducing the number of bytes associated with a web page download, for instance by resizing and compressing images.

## Prerequisites
  
### Backend application setup 
 * To start from the scratch, download Springboot archetype
`curl https://start.spring.io/starter.zip \
-d dependencies=web,devtools \
-d groupId=com.sungung \
-d artifactId=optimise-frontend \
-d name=optimise-frontend \
-d description="Optimising frontend" \
-d baseDir=optimise-frontend \
-o optimise-frontend.zip`

### Frontend application setup
 * nodejs
 * angular-cli
`npm install -g angular-cli
ng new frontend
cd frontend
ng serve`

## Build & deployment
### Integrating frontend & backend through client proxy
 * Create 'proxy.conf.json' in frontend directory
`{
 "/api" : {
  "target" : "http://localhost:8080",
  "secure" : false
 }
}`
 
### Build frontend
 * Go to frontend directory and run "ng build --deploy-url assets -prod" to compile the source into an output directory 'dist'

### Build all together into single jar
 * Add 'maven-resources-plugin' into pom.xml file to generate frontend artifacts into the jar.
`<build>
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
</build>`	
 


