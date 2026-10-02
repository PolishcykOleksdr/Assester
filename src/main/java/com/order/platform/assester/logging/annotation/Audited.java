package com.order.platform.assester.logging.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as auditable: {@link com.order.platform.assester.logging.LogbackAspect} logs its
 * invocation, duration and outcome. Method arguments are never logged, because they may carry
 * credentials (see {@code LoginUserDTO} / {@code RegisterUserDTO}).
 *
 * @author: user,
 * date: 02.10.2026
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
}