package com.intuit.secfraud.shared.snot.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.annotation.Resource;

import com.intuit.secfraud.shared.snot.Tissue;
import com.intuit.secfraud.shared.snot.config.SnotProperties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.intuit.secfraud.shared.snot.SnotService;
import com.intuit.secfraud.shared.snot.dto.ShortShot;

/**
 * An aspect to add Slack notification behavior around methods via pointcut(s).
 * 
 * @author jingram1
 *
 */
@Aspect
@Component
public class Booger {

    @Resource
    SnotService snotService;

    @Resource
    private SnotProperties snotProperties;
    
    @Resource 
    Environment environment;
    
    private String getProperty(String property) {
        return environment.getProperty(property);
    }
    /**
     * Build a debug string to list all parameters including: type and value.
     * 
     * @param method The method that was annotated.
     * @param args The arguments to the method.
     * 
     * @return The string representation of the method arguments including: type and value.
     */
    private String parametersDebugString(final Method method, final Object[] args) {
        String readable = "";
        for (int i = 0; i < args.length; i++) {
            final Parameter p = method.getParameters()[i];
            final Class<?> c = p.getType();
            final String v = args[i] != null ? args[i].toString() : "null";
            readable += c.getName() + "=" + v + "|";
        }
        return readable.length() > 0 ? readable.substring(0, readable.length() - 1) : readable;
    }

    /**
     * Build a debug string that represents the return type and value.
     * 
     * @param method The method that was annotated.
     * @param returnValue The object representing the return value.
     * 
     * @return The string representation of the return value including: type and value.
     */
    private String returnValueDebugString(final Method method, final Object returnValue) {
        final String value = method.getReturnType()
                .getName()
                .equals("void") ? "" : "=" + (returnValue == null ? "null" : returnValue.toString());
        return method.getReturnType()
                .getName() + value + "\n";
    }

    /**
     * Build a debug string that provides information regarding the method that was annotated
     * and the input and outputs of the method when the pointcut is executed.
     * 
     * @param call The method invocation information.
     * @param method The method that was annotated.
     * 
     * @return The debug string including method information.
     */
    private String classMethodDebugString(final ProceedingJoinPoint call, final Method method) {
        return "Class: " + call.getTarget()
                .getClass()
                .getName() + "\nMethodName: " + method.getName();
    }

    /**
     * Inject behavior around a method including sending Slack notifications when:
     * 
     *   1. An exception is thrown.
     *   2. A general message is provided.
     *   3. A message is provided with set of conditions to be met.
     *    
     * @param call The method invocation information.
     * 
     * @return The original return value from proxied method.
     * @throws Throwable Any throwable thrown from proxied method.
     */
    @Around("@annotation(Snot)")
    public Object snotFired(final ProceedingJoinPoint call) throws Throwable {
        final Method method = ((MethodSignature) call.getSignature()).getMethod();
        final Snot mucus = method.getAnnotation(Snot.class);
        Object result;
        
        // default behavior sends (red) Slack notification on exception and rethrows
        try {
            result = call.proceed();
        } catch (final Throwable t) {
            if (snotProperties.isBlowOnException()) {
                snotService.sneeze(
                        new ShortShot(t.getMessage()
                                + (snotProperties.isDebug()
                                        ? "\n\n" + classMethodDebugString(call, method) + "\nMethodParameters: "
                                                + parametersDebugString(method, call.getArgs()) + "\nStacktrace: "
                                                + ExceptionUtils.getStackTrace(t)
                                        : "")),
                        SnotService.COLOR.RED, Lists.newArrayList(mucus.targets()), Tissue.BLOW);
            }
            throw t;
        }
        
        // if no exception and no message, do nothing
        if (Strings.isNullOrEmpty(mucus.message())) {
            return result;
        }
        // if message provided and meets filter criteria, send Slack notification
        if (goldFound(method, call.getArgs(), result)) {
            snotService.sneeze(
                    new ShortShot(mucus.message()
                            + (snotProperties.isDebug()
                                    ? "\n\n" + classMethodDebugString(call, method) + "\nMethodParameters: "
                                            + parametersDebugString(method, call.getArgs()) + "\nMethodReturnValue: "
                                            + returnValueDebugString(method, result)
                                    : "")),
                    snotProperties.isDebug() ? SnotService.COLOR.YELLOW : SnotService.COLOR.GREEN,
                    Lists.newArrayList(mucus.targets()), Tissue.GENERAL);
        }
        
        // return method invocation results
        return result;
    }

    /**
     * Check all parameters and return value to see if filter annotations have been defined,
     * and if so, check that filter conditions are met and return a boolean result true: met,
     * false: not met.
     * 
     * @param method The method that was annotated.
     * @param args The arguments to the method.
     * @param returnValue The object representing the return value. 
     * 
     * @return True if filter conditions met, or false otherwise.
     */
    private boolean goldFound(final Method method, final Object[] args, final Object returnValue) {
        return inNose(method, args) && onFloor(method, returnValue) && inNoseAndOfType(method, args);
    }

    /**
     * Check method parameter values and annotated conditions.
     * 
     * @param method The method that was annotated.
     * @param args The arguments to the method.
     * 
     * @return True if conditions met or no conditions defined, or false otherwise.
     */
    private boolean inNose(final Method method, final Object[] args) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (final Annotation annotation : parameterAnnotations[i]) {
                String[] got = null, not = null;
                if (annotation.annotationType()
                        .equals(SnotGot.class)) {
                    SnotGot snotGot = (SnotGot) annotation;
                    got = getHotSnot(snotGot.value(), snotGot.property());
                } else if (annotation.annotationType()
                        .equals(SnotNot.class)) {
                    SnotNot snotNot = (SnotNot) annotation;
                    not = getHotSnot(snotNot.value(), snotNot.property());
                }
                if (got != null && !gotSnot(got, args[i])) {
                    return false;
                } else if (not != null && gotSnot(not, args[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check method return value and annotated conditions.
     * 
     * @param method The method that was annotated.
     * @param returnValue The object representing the return value.
     * 
     * @return True if conditions met or no conditions defined, or false otherwise.
     */
    private boolean onFloor(final Method method, final Object returnValue) {
        final SnotGot got = method.getAnnotatedReturnType()
                .getAnnotation(SnotGot.class);
        final SnotNot not = method.getAnnotatedReturnType()
                .getAnnotation(SnotNot.class);
        if (got != null && !gotSnot(getHotSnot(got.value(), got.property()), returnValue)) {
            return false;
        }
        if (not != null && gotSnot(getHotSnot(not.value(),not.property()), returnValue)) {
            return false;
        }
        return true;
    }
    
    /**
     * uses the Spring property value of the property if property both present and property value is not null or empty
     * otherwise, uses the value
     * @param value the value from annotation
     * @param property the property from annotation
     * @return
     */
    private String[] getHotSnot(String[] value, String property) {
        if(!Strings.isNullOrEmpty(property)) {
            String propertyValue = getProperty(property);
            if(Strings.isNullOrEmpty(propertyValue)) {
                return value;
            }
            return new String[]{propertyValue};
        }
        return value;
    }
    
    /**
     * Check method parameter type(s) and annotated conditions.  Parameter types
     * are fully qualified.  I.e. 'com.intuit.secfraud.shared.snot.aop.Booger'
     * is correct, and 'Booger' is incorrect.  If type not found on classpath, the
     * condition will be ignored.
     * 
     * @param method The method that was annotated.
     * @param args The arguments to the method.
     * 
     * @return True if conditions met or no conditions defined, or false otherwise.
     */
    private boolean inNoseAndOfType(final Method method, final Object[] args) {
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (final Annotation annotation : parameterAnnotations[i]) {
                if (annotation.annotationType()
                        .equals(SnotType.class)) {
                    final String[] types = ((SnotType) annotation).value();
                    for (final String type : types) {
                        try {
                            final Class<?> clazz = ClassUtils.forName(type, this.getClass().getClassLoader());
                            if (clazz.isAssignableFrom(args[i].getClass())) {
                                return true;
                            }
                        } catch (final Throwable t) {
                            t.printStackTrace();
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Evaluate condition equality on Object toString method.
     * 
     * @param want What value we want.
     * @param got The value we got.
     * 
     * @return True if toString matches annotated value, else false.
     */
    private boolean gotSnot(final String[] want, final Object got) {
        for (final String item : want) {
            if (got != null && item.equalsIgnoreCase(got.toString())) {
                return true;
            }
        }
        return false;
    }

}
