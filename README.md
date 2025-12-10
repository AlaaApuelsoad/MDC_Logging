# Logging & MDC Guide for Spring Boot

This guide introduces the fundamentals of logging, explains SLF4J, logging frameworks, and demonstrates how a Spring Boot application processes HTTP requests—laying the foundation for understanding Mapped Diagnostic Context (MDC).

##  1. Importance of Logging

Observability & Monitoring: Logging is one of the three pillars of observability: metrics, logs, traces.

Logging is essential for any backend system. It provides insight into what the application is doing at runtime and is the foundation for debugging and observability.

Understanding Application Behavior: Logs help developers see how requests flow and how services behave during execution.

Troubleshooting Issues: When failures or unexpected behavior occur, logs are the first source of truth.

Tracing User Activities: Logging helps correlate user actions across multiple services in distributed systems.

Compliance & Audit Requirements: Systems in domains like healthcare or finance rely heavily on structured logging for audits.

##  2. What Is SLF4J?

![diagram](https://github.com/AlaaApuelsoad/MDC_Logging/blob/master/images/SLF4J.png)
SLF4J (Simple Logging Facade for Java) is an abstraction layer for logging in Java.
It does not write logs by itself. Instead, your application depends on SLF4J APIs, and an actual logging implementation performs the real work.
SLF4J provides a common API, allowing developers to log using log.info(), log.error(), etc., while plugging in any backend at runtime.
This means you can switch from Logback to Log4j2 without modifying your application code.


##  3. What Is MDC and Why Does It Matter?

MDC (Mapped Diagnostic Context) allows adding contextual key–value data to logs.
This improves traceability and debugging, especially in multi-user or multi-threaded applications.

Examples of MDC Values

requestId

correlationId

userId

ipAddress

sessionId

MDC attaches data to the thread that is processing the request, ensuring all logs during that request contain the same context.

This is extremely useful for tracking an individual request through:

Filters

Controllers

Services

Repository layer

Asynchronous tasks (with decorators)


# Spring Boot Request Flow

This document explains the full **HTTP request flow inside Spring /
Spring Boot**, from the moment a client sends a request until a response
is returned.

##  1. Client Sends an HTTP Request

Example requests: - `GET /users` - `POST /login` - `PUT /products/5`

The request arrives at the Spring Boot embedded server.

##  2. Embedded Server Receives the Request

Spring Boot uses an embedded servlet container: - Tomcat (default) -
Jetty - Undertow

## 3. Filter Chain

Filters run before and after request processing.

## 4. DispatcherServlet (The Heart of Spring MVC)

Central controller that routes all Spring MVC requests.

## 5. Handler Mapping

DispatcherServlet checks which controller should handle the request.

## 6. HandlerAdapter

Executes the matched controller.

## 7. Controller Method Executes

Spring handles: - argument resolving\
- validation\
- request body parsing

## 8. Service Layer

Business logic lives here.

## 9. Repository Layer

Interacts with the database using JPA / JDBC / Hibernate.

## 10. Response Returned

## 11. View Resolver (MVC only)

## 12. HttpMessageConverter

Converts response into JSON / XML.

## 13. Post-filters

## 14. Response Sent Back to Client

# Overview diagram
![Flow Diagram](https://github.com/AlaaApuelsoad/MDC_Logging/blob/master/images/RequestFlow.png)

# Full Request Flow Diagram

    Client
       ↓
    Embedded Server
       ↓
    Filter Chain
       ↓
    DispatcherServlet
       ↓
    HandlerMapping
       ↓
    HandlerAdapter
       ↓
    Controller
       ↓
    Service
       ↓
    Repository
       ↓
    Database
       ↑
    Response
       ↑
    HttpMessageConverter
       ↑
    DispatcherServlet
       ↑
    Filters
       ↑
    Client


## Reference Articles
[Filter vs Interceptors](https://medium.com/@rhom159/filters-vs-interceptors-in-spring-a-simple-guide-for-easy-understanding-70f5e397fa32)
[Thread vs ThreadLocal](https://medium.com/@sachinkg12/understanding-threadlocal-vs-thread-in-java-a908b5390207)
[MDC](https://medium.com/@sudacgb/enhancing-logging-in-spring-boot-with-mapped-diagnostic-context-mdc-a-step-by-step-tutorial-0a57b0304dd3)



