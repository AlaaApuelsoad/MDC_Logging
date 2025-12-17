# Logging, MDC & Context Propagation in Spring Boot

This README explains **logging**, **MDC (Mapped Diagnostic Context)**, and a **common real-world problem: losing context across threads**, then shows the **correct solution using **``.

The goal is to be **simple, practical, and straight to the point**.

---

## 1. Why Logging Matters

Logging is a core part of **observability** (Logs, Metrics, Traces).

You need logs to:

* Understand application behavior
* Debug errors and production issues
* Trace a single request across layers and services
* Meet audit and compliance requirements

Without good logging, debugging distributed systems is almost impossible.

---

## 2. What Is SLF4J?
![diagram](https://github.com/AlaaApuelsoad/MDC_Logging/blob/master/images/SLF4J.png)

**SLF4J (Simple Logging Facade for Java)** is a logging abstraction.

* Your code logs using `log.info()`, `log.error()`
* SLF4J delegates to a real implementation (Logback, Log4j2, etc.)
* You can change the logging framework **without changing your code**

---

## 3. What Is MDC (Mapped Diagnostic Context)?

**MDC allows you to attach key–value data to logs.**

Typical MDC values:

* `traceId`
* `requestId`
* `userId`
* `tenantId`
* `ipAddress`

MDC data is stored in a **ThreadLocal**, meaning:

* It is available only inside the current thread
* All logs in that thread automatically include the context

Example:

```java
MDC.put("traceId", "abc-123");
log.info("Processing request");
```

Log output:

```text
[abc-123] Processing request
```

---

## 4. Spring Boot HTTP Request Flow (High Level)
![Flow Diagram](https://github.com/AlaaApuelsoad/MDC_Logging/blob/master/images/RequestFlow.png)


```text
Client
 ↓
Embedded Server (Tomcat)
 ↓
Filter Chain   ← Best place to set MDC
 ↓
DispatcherServlet
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Database
```

MDC is usually:

* **Added in a Filter (request start)**
* **Cleared at the end of the request**

---

## 5. The Context Problem

MDC, SecurityContext, and TenantContext are stored in **ThreadLocal**.

### What goes wrong?

When you use:

* `@Async`
* Thread pools
* `CompletableFuture`

the code runs in **another thread**.

That means:

```java
TenantContext.getTenant(); // null 
MDC.get("traceId");        // null 
```

Because **ThreadLocal data does NOT propagate automatically**.

---

## 6. Example: TenantContext Using ThreadLocal

```java
public class TenantContext {
    private static final ThreadLocal<String> TENANT = new ThreadLocal<>();

    public static void setTenant(String tenant) {
        TENANT.set(tenant);
    }

    public static String getTenant() {
        return TENANT.get();
    }

    public static void clear() {
        TENANT.remove();
    }
}
```

Works in the same thread fails in async threads.

---

## 7. The Correct Solution: TaskDecorator

``** allows you to capture context in the calling thread and restore it in the worker thread.**

---

## 8. How TaskDecorator Works (Conceptually)

```text
Caller Thread
 ├─ Context exists (tenant, MDC)
 ├─ TaskDecorator captures context
 └─ Submits wrapped task

Worker Thread
 ├─ Restores context
 ├─ Executes business logic
 └─ Clears context
```

---

## 9. Tenant Context Propagation with TaskDecorator

### TaskDecorator Implementation

```java
public class TaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        String tenant = TenantContext.getTenant();

        return () -> {
            try {
                if (tenant != null) {
                    TenantContext.setTenant(tenant);
                }
                runnable.run();
            } finally {
                TenantContext.clear();
            }
        };
    }
}
```

---

### Registering the Decorator

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setTaskDecorator(new TaskDecorator());
        executor.initialize();
        return executor;
    }
}
```

Now:

* `@Async`
* Thread pool tasks
* `CompletableFuture` (using this executor)

➡ automatically receive tenant context.

---

## 10. Combining MDC + Tenant Context (Best Practice)

In real systems you usually propagate:

* MDC
* Tenant
* SecurityContext

Always:

* Capture context **before thread switch**
* Restore context **inside worker thread**
* Clear context **after execution**

---

## 11. Common Mistakes (A

* Forgetting to clear ThreadLocal values
* Using `InheritableThreadLocal` with thread pools
* Creating threads manually with `new Thread()`
* Assuming MDC works automatically in async code

---

## 12. Key Takeaways

* MDC and TenantContext are **ThreadLocal-based**
* ThreadLocal data does **not propagate across threads**
* Thread pools reuse threads → risk of context leaks
* **TaskDecorator is the correct and safe solution**

---

## Reference Articles

* [Filter vs Interceptors](https://medium.com/@rhom159/filters-vs-interceptors-in-spring-a-simple-guide-for-easy-understanding-70f5e397fa32)
* [Thread vs ThreadLocal](https://medium.com/@sachinkg12/understanding-threadlocal-vs-thread-in-java-a908b5390207)
* [MDC Guide](https://medium.com/@sudacgb/enhancing-logging-in-spring-boot-with-mapped-diagnostic-context-mdc-a-step-by-step-tutorial-0a57b0304dd3)



