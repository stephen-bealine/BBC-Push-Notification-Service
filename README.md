# BBC Push Notification Service

## Requirements

* Java version: 11
* Gradle version: 6.8

## Build and test

The application is built using Gradle.

To build and run the source code. Use:
`gradlew build`

To run the application, use:
`gradlew bootRun`

Or run the class `uk.co.bealine.bbc.pushnotificationservice.PushNotificationServiceApplication` in
your favourite IDE

## Limitations

#### Authentication

The API does not require any authentication at all.

#### Validation

Although not specified as a requirement, there is basic field validation for the API. This could be
expanded to further to restrict content with use of regular expressions. Although the requests that
fail validation will result in a Bad request (400), no effort has been made to provide meaningful
validation error messages

#### Exception Handling

The application just uses Spring Boot's default exception handling

#### Logging

#### API Documentation

There is no API documentation. Suggest using something like Swagger to document with the OpenAPI
specification. 