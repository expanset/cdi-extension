package com.expanset.cdi.tracing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

/**
 * Annotation for methods, which are needed to be profiled.
 */
@Inherited
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Traceable {

	/**
	 * @return Log level for log output.
	 */
	@Nonbinding
	LogLevel value() default LogLevel.DEFAULT;
	
	/**
	 * @return Name of current operation.
	 */
	@Nonbinding
	String name() default "";

	/**
	 * @return Type of correlation ID, which will be generated for method call.
	 */
	@Nonbinding
	CorrelationIdType idType() default CorrelationIdType.NONE;
	
	/**
	 * @return true - calculates method duration in milliseconds.
	 */
	@Nonbinding
	boolean measure() default false;
}
