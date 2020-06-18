
# Repayment Plan Generator

### Java, Maven, Spring Boot, Spring Security, Rest API, Swagger 2

This is an test project to generate Repayment plan for loan.

## Requirements

1. Java 8

2. Maven 

3. Spring Boot

4. Swagger 

5. Lombok 

## Steps to Setup

**1. Install the application to fetch dependencies.**
```bash
mvn install
```
In case you are facing compilation errors for domain beans, Please check the **lombok dependency** supported in your IDE. This should not be a problem from command line.

**2. Run the app using maven**

```bash
mvn spring-boot:run
```

The app will start running at <http://localhost:8080>.
You can use inbuilt swagger ui to test the app or you go for the postman/curl clients.

Swagger: <http://localhost:8080/swagger-ui.html>.
Swagger-docs:<http://localhost:8080/v2/api-docs>.

## Explore Rest APIs

The app defines following APIs.
    
    POST localhost:8080/plans (fetch plans)


You can test them using postman or any other rest client.

    You must to add Basic Auth in your rest client. 

+ open `RestSecurityConfiguration`and you can find 2 roles and credentials
    
    Authorized user: admin/admin
    Unauthorized user: user/user

## Key points to note

+ API implementation and validation of parameters.
+ Error handling to build the response (code + message).
+ Supports Security /plans** requests
+ Use lombok library 

## Possible improvements / out of scope

+ Rest documentation.



