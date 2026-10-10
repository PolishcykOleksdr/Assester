package com.order.platform.assester.logging;

import com.order.platform.assester.logging.annotation.Audited;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * author: user,
 * date: 03.10.2026
 */

@Slf4j
@Aspect
@Component
public class LogbackAspect {

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
        String signature = signatureOf(joinPoint);
        long startedAt = System.currentTimeMillis();

        log.debug("-> {}", signature);
        try {
            Object result = joinPoint.proceed();
            log.debug("<- {} completed in {} ms", signature, System.currentTimeMillis() - startedAt);
            return result;
        } catch (Throwable throwable) {
            log.debug("{} failed after {} ms with {}",
                    signature,
                    System.currentTimeMillis() - startedAt,
                    throwable.getClass().getSimpleName());
            throw throwable;
        }
    }

    private String signatureOf(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getDeclaringType().getSimpleName()
                + "#"
                + methodSignature.getName();
    }
}
