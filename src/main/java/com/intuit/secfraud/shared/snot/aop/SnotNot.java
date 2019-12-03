package com.intuit.secfraud.shared.snot.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Filter condition that can be applied to parameters or return values.
 * If the value provided or returned has a string representation not equal
 * to the mandatory value of the annotation, then the condition is met.
 * 
* A spring property can be specified instead of value, in which case the value will
 * be looked up and used if not null and empty
 * 
 * @author jingram1
 *
 */
@Target(value = { ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SnotNot {

    String[] value();
    
    /**
     * a valid spring property.  The value will be used in lieu of value() 
     * if found to be not null and not empty
     */
    String property() default "";

}
