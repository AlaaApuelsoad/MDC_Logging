package org.example.alaa.mdcdemo;

import org.example.alaa.mdcdemo.context.TenantContext;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * Issue is MDC context data or thread local data not propagated threads do NOT share context.
 * This causes two problems:
 *
 * Context is lost (MDC, SecurityContext)
 *
 * Old context may leak to the next task
 * TaskDecorator solve this issue
 */
public class MDCTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {

        Map<String,String> contextMap = MDC.getCopyOfContextMap();
        org.example.alaa.mdcdemo.model.UserInfo userInfo = TenantContext.getTenantContext();


        return ()->{
            try {
                if (contextMap != null){
                    MDC.setContextMap(contextMap);
                    TenantContext.setTenantContext(userInfo);
                }
                runnable.run();
            }finally {
                MDC.clear();
            }
        };
    }
}
