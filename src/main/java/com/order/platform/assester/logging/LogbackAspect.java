package com.order.platform.assester.logging;

import com.order.platform.assester.logging.annotation.Audited;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Logs every method annotated with {@link Audited}: invocation, outcome and duration.
 * Only the signature is logged - argument values are intentionally omitted because they may
 * contain credentials. Unexpected exceptions are re-thrown and logged with a full stack trace by
 * {@code GlobalExceptionHandler}, so this aspect stays at WARN without a stack trace.
 *
 * @author: user,
 * date: 02.10.2026
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
            log.warn("{} failed after {} ms with {}",
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