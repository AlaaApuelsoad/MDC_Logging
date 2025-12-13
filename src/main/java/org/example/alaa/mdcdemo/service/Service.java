package org.example.alaa.mdcdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.alaa.mdcdemo.context.TenantContext;
import org.example.alaa.mdcdemo.model.User;

@org.springframework.stereotype.Service
@Slf4j
@RequiredArgsConstructor
public class Service {

    private final EmailService emailService;

    public void createUser(User user) {
        log.info("Get user information from controller : {}", user);
        log.info("UserContext info : {}", TenantContext.getTenantContext());
        //sending async email to user
        emailService.sendingAsyncEmailToUser(user);
        //database operation
        log.info("User saved to database");
    }

    public void getUser() throws Exception {
        throw new Exception("User Not Found");
    }
}
