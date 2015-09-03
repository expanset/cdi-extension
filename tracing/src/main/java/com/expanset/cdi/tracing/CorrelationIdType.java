package com.expanset.cdi.tracing;

/**
 * Correlation ID type, used for MDC logging feature.
 */
public enum CorrelationIdType {

	/**
	 * No use.
	 */
	NONE,
	
	/**
	 * Incrementation of static long value.
	 */
	SIMPLE, 
	
	/**
	 * Generate UUID.
	 */
	UUID
}
