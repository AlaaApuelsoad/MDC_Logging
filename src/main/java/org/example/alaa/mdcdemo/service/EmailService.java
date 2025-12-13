package org.example.alaa.mdcdemo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.alaa.mdcdemo.context.TenantContext;
import org.example.alaa.mdcdemo.model.User;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Async Email Service.
 * simulated for context data propagation
 */
@Service
@Slf4j
public class EmailService {

    @Async
    public void sendingAsyncEmailToUser(User user){
        log.info("Async method for sending email to user - {} - CorrelationId - {}", user.getName(),MDC.get("X-Correlation-ID"));
        log.info("loggedIn username - {}", TenantContext.getTenantContext().getUserName());
    }
}
