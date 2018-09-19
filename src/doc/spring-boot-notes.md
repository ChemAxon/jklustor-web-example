Notes on Spring Boot application configuration
==============================================

[Spring Boot](https://projects.spring.io/spring-boot/) provides an application framework for the embedded server in this
project.


Recommended `gradle` tasks
--------------------------

Note that credentials usually must be specified for all `gradle` invocations.

  - `./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> bootRun`: Launch server
  - `./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> processResources`: Recommended to invoke
    when the server is running and static content changed.


Application config
------------------

Aplication configuration is stored in file `src/main/resources/application.properties`.


Static content
--------------

Static content and JS client is served from `src/main/static`.