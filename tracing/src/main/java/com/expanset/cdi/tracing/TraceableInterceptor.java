package com.expanset.cdi.tracing;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor for methods or classes with {@link Traceable} annotation.
 */
@Traceable
@Dependent
@Interceptor
public class TraceableInterceptor implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	protected TracingService profileService;
	
	private final ConcurrentMap<String, Traceable> annotationCache = new ConcurrentHashMap<>();
	
	@AroundInvoke
	public Object invoke(InvocationContext invocation) 
			throws Throwable {
		final Class<?> targetClass = invocation.getTarget().getClass();
		final String key = targetClass.getCanonicalName() + "." + invocation.getMethod().getName();
		final Traceable ann = annotationCache.computeIfAbsent(key, (keyValue) -> {
			Traceable result = invocation.getMethod().getAnnotation(Traceable.class);
			if(result != null) {
				return result;
			}			
			return targetClass.getAnnotation(Traceable.class);
		});
		
		final Logger log = LoggerFactory.getLogger(targetClass);
		try(AutoCloseable scope = profileService.startScope(
				log, 
				StringUtils.isNotEmpty(ann.name()) ? ann.name() : invocation.getMethod().getName(), 
				ann.value(), 
				null, 
				ann.idType(),
				ann.measure())) {
			return invocation.proceed();
		}
	}
}
