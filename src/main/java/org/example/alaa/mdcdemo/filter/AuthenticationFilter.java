package org.example.alaa.mdcdemo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.alaa.mdcdemo.context.TenantContext;
import org.example.alaa.mdcdemo.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Simulated Authentication filter
 */
@Component
@Order(value = 2)
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /**
         * Simulation for validating the user token in the request.
         * If the user is authenticated, spring security will generate a new authentication object.
         * Extract userInfo from the authentication Object.
         * Add the object to the userContext.
         */
        try {
            //loggedIn user data getting from an authentication object
            UserInfo userInfo = UserInfo.builder()
                    .userId(1)
                    .userName("alaaapuelsoad")
                    .realmId(1)
                    .role("admin")
                    .build();

            TenantContext.setTenantContext(userInfo);

            filterChain.doFilter(request, response);

        }catch (Exception e){
           throw new RuntimeException("error in request - [ " + MDC.get("X-Correlation-ID")+" ]");
        }

    }
}
