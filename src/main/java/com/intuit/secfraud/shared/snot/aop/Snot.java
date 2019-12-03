package com.intuit.secfraud.shared.snot.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that established the pointcut we will apply our
 * aspect around.
 * 
 * @author jingram1
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Snot {

    String message() default "";

    String[] targets() default {};

}
