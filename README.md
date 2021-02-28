# BBC Push Notification Service

## Requirements

* Java version: 11
* Gradle version: 6.8

## Build and test

The application is built using Gradle.

To build and run the source code. Use: `gradlew build`

To run the application, use: `gradlew bootRun`

Or run the class `uk.co.bealine.bbc.pushnotificationservice.PushNotificationServiceApplication` in
your favourite IDE

## API Definition

To register an account use:

```
curl --location --request POST 'http://localhost:8080/accounts' 
--header 'Content-Type: application/json' 
--data-raw '{
"username": "<username here>",
"accessToken": "<push bullet access token here>"
}'
```

To list all accounts use:

```
curl --location --request GET 'http://localhost:8080/accounts'
```

To push a message to Push Bullet use:

```
curl --location --request PUT 'http://localhost:8080/push' 
--header 'Content-Type: application/json' 
--data-raw '{
"username": "<username>",
"title": "Message Title",
"message": "Message body"
}'
```

## Enhancements

* Additional validation should be added to not allow duplicate usernames
* On account creation, the access token could be verified against the Push Bullet 'me' API
* Verify that the push bullet message was successful. At the moment if no exception is raised it is
  assumed to have been sent

## Limitations

#### Authentication

The API does not require any authentication at all.

#### Validation

Although not specified as a requirement, there is basic field validation for the API. This could be
expanded to further to restrict content with use of regular expressions. Although the requests that
fail validation will result in a Bad request (400), no effort has been made to provide meaningful
validation error messages.

#### Exception Handling

The application just uses Spring Boot's default exception handling.

The custom exceptions are pretty basic. Simply having a specific response status code.

The Push Bullet error responses are not handled properly and are simply thrown up the stack and
converted into a bad request.

#### API Documentation

Other than this read me, there is no API documentation. Suggest using something like Swagger to
document with the OpenAPI specification.

#### Clear accounts method on the Account Repository

This method is used purely to allow testing to have a known state of the repo. There should not be
code just to enable tests. Really there should be a database instead of the simple in memory Map,
which would then allow for test interaction directly with the DB.

An alternative would be to configure the repo with a test Map. Hence, allowing tests direct access
to the underlying map. I took the decision that this was a very minor part of the application and
took the easiest option. 