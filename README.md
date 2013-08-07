# Project INWESTUJWFIRMY.PL

## How to build project:

1. Download App Engine SDK (tested with 1.8.2), unpack it and set environment variable APPENGINE_HOME to that folder.
2. Set JAVA_HOME to JDK6 (JDK7 seems not to be supported).
3. Download Apache Ant version 1.8.2 or newer (eg. 1.9.2), set variable ANT_HOME and add %ANT_HOME%\bin to the PATH.
4. Download M4 and add its bin folder to the PATH.
5. Go to the project main folder and run:

		ant clean compile
		ant runserver
6. Open browser, go to http://localhost:7777, click on login button, then select "Sign in as Administrator"
7. In the footer you should see link to Setup, go there, then click on the following buttons:

* "Recreate mock data"
* "Associate mock images"
* "Update test avatars, dragon/lister flags"
* "Migrate version ..."
* "Update all listing stats"
* "Update all aggregate stats"


## How to setup Apache to see UI project files:

1. Open your apache config file (httpd.conf or simiar)
2. Define the following alias and directory (change folder location)

	```ApacheConf
		Alias /inwestujwfirmy "C:/greg/projects/inwestuj-w-firmy/inwestujwfirmy_ui"
		<Directory "C:/greg/projects/inwestuj-w-firmy/inwestujwfirmy_ui">
			Options Indexes FollowSymLinks
			Options +Includes
			
			AddType text/html .html
			AddOutputFilter INCLUDES .html
			
			Require all granted
		</Directory>
	```
3. Restart your apache
4. Access http://localhost:8080/inwestujwfirmy/index.html


## Building project under Eclipse

If you're using Eclipse (Google Plugin required) then you can directly import project and run it.
To set up project in Eclipse (or IntelliJ Idea) you'll have to add the following jars to the project:
* %APPENGINE_HOME%\lib\impl\appengine-api.jar
* %APPENGINE_HOME%\lib\testing\appengine-testing.jar

## How to upload war to GAE environment:
1. Set APPENGINE_HOME variable to your AppEngine SDK location
2. Set APPENGINE_EMAIL to your AppEngine user name
3. First upload has to be manual to store your credentials:

		cd <<your inwestuj-w-firmy project>>
		$APPENGINE_HOME/bin/appcfg.sh --sdk_root=$APPENGINE_HOME --email=$APPENGINE_EMAIL update war

		%APPENGINE_HOME%/bin/appcfg.cmd --sdk_root=%APPENGINE_HOME% --email=%APPENGINE_EMAIL% update war

4. After first successful usage of appcfg you can use ant script to upload application
5. Then to build client:

		cd html5-boilerplate/build
		ant

6. Note that if you get stuck you can clean the war/ directory then rebuild the backend and client (in that order)


![](https://raw.github.com/f-inwest/inwestuj-w-firmy/master/inwestujwfirmy_ui/images/stopka-ue.png?login=grzegorznittner&token=44a462809a214d6f1875824929c1e33a)
