## Introduction

Demo service for APIPortal apis platform

What you need
	* JDK 1.8
	* Maven 3.2+
	* Docker & Docker-compose
	* Node & npm (for script to generate jwt token)

## Run
To run the application

	mvn spring-boot:run

As soon as application ready, you should get OK response from health endpoint

	http://localhost:8080/health

To browse available apis

	http://localhost:8080/swagger-ui.html#/

/apis is protected by JWT token, so you need to generate `token` from output as bellow:

	cd ./script && npm i && node index.js

Put `Bearer {token}` to Swagger Authorize box before testing Api calls
Note: you may want to customize `./script/index.js` to generate different type of tokens: invalid scope, expired, etc..

## Testing
By default, only unit-tests are performed

	mvn clean test

To also activate integration-tests, please specify `integration-tests` profile

	mvn clean test -P integration-tests

Note: separating test profile is to keep test cases running on dev machine as fast as possible while letting real tests able to perform on CI/CD

## Configuration & Migration
Configuration files are located in src/main/resource
	* db: liquibase changelogs
	* es: mapping for api indices (need to be configured at indices creating time)
	* application-{env}.properties: application config
	* logback.xml: config logging

## Package
To package an application
	
	mvn clean package

To build output jar into Docker container

	docker build .

To run whole system in docker-compose env(including `Newman` sanity check)
	
	docker-compose up







