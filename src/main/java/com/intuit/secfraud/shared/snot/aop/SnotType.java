package com.intuit.secfraud.shared.snot.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Filter condition that can be applied to parameters or return values.
 * If the type of the provided parameter or return value implements or extends 
 * the provided fully qualified type, then the condition is met.
 * 
 * @author jingram1
 *
 */
@Target(value = { ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SnotType {
    
    String[] value();

}
