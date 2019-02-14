# PriceAnalyzer
Google App Engine java application that estimates the cost for a lambda function

**Specifications**

1.  Google App Engine Project
2.  Build tool: Gradle/Maven
3.  Server: Google Cloud
4.  Frameworks: JSP as GUI Technology, Cloud Datastore, Java Mail App Engine
5.  Environment: Google App Engine Standard Environment
6.  Data Management: Google Datastore API

## Setup

• Download and initialize the [Cloud SDK](https://cloud.google.com/sdk/)

    gcloud init

* Create an App Engine app within the current Google Cloud Project

    gcloud app create

## Maven
### Running locally

    mvn appengine:run

To use vist: http://localhost:8080/

### Deploying

    mvn appengine:deploy

To use vist:  https://YOUR-PROJECT-ID.appspot.com

## Gradle
### Running locally

    gradle appengineRun

If you do not have gradle installed, you can run using `./gradlew appengineRun`.

To use vist: http://localhost:8080/

### Deploying

    gradle appengineDeploy

If you do not have gradle installed, you can deploy using `./gradlew appengineDeploy`.
