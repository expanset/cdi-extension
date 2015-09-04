package com.expanset.cdi.tracing;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Service for profile methods or parts of code.
 * <p>Default MDC key name is 'opId'. You may use it in log record, there is a logback sample:</p>
 * <pre>
 * %d{HH:mm:ss.SSS} [%thread] %X{opId} %-5level %logger{36} - %msg%n
 * </pre>
 */
@ApplicationScoped
public class TracingService {

	private String mdcKey = "opId";

	private String startPrint = "start {}";
	
	private String completedPrint = "completed";
	
	private String completedNoMDCPrint = "completed {}";
	
	private String measureDurationPrint = "completed, elapsed: {}ms";
	
	private String measureDurationNoMDCPrint = "completed {}, elapsed: {}ms";
	
	private LogLevel defaultLogLevel = LogLevel.DEBUG;	
	
	protected final AtomicLong longCorrelationId = new AtomicLong();	
	
	protected final static Logger defaultLog = LoggerFactory.getLogger(TracingService.class);
	
	/**
	 * @return Name of MCD key, default - 'opId'.
	 */
	public String getMDCKey() {
		return mdcKey;
	}

	public void setMDCKey(String mdcKey) {
		this.mdcKey = mdcKey;
	}

	/**
	 * @return Log record pattern for starting profile scope.
	 */
	public String getStartPrint() {
		return startPrint;
	}

	public void setStartPrint(String startPrint) {
		this.startPrint = startPrint;
	}

	/**
	 * @return Log record pattern for completing profile scope.
	 */	
	public String getCompletedPrint() {
		return completedPrint;
	}

	public void setCompletedPrint(String completedPrint) {
		this.completedPrint = completedPrint;
	}

	/**
	 * @return Log record pattern for completing profile scope without using MDC.
	 */	
	public String getCompletedNoMDCPrint() {
		return completedNoMDCPrint;
	}

	public void setCompletedNoMDCPrint(String completedNoMDCPrint) {
		this.completedNoMDCPrint = completedNoMDCPrint;
	}

	/**
	 * @return Log record pattern for completing measured profile scope.
	 */			
	public String getMeasureDurationPrint() {
		return measureDurationPrint;
	}

	public void setMeasureDurationPrint(String measureDurationPrint) {
		this.measureDurationPrint = measureDurationPrint;
	}	
	
	/**
	 * @return Log record pattern for completing measured profile scope without using MDC.
	 */		
	public String getMeasureDurationNoMDCPrint() {
		return measureDurationNoMDCPrint;
	}

	public void setMeasureDurationNoMDCPrint(String measureDurationNoMDCPrint) {
		this.measureDurationNoMDCPrint = measureDurationNoMDCPrint;
	}

	/**
	 * @return Default log level for profile operations (default  - DEBUG).
	 */
	public LogLevel getDefaultLogLevel() {
		return defaultLogLevel;
	}

	public void setDefaultLogLevel(LogLevel defaultLogLevel) {
		this.defaultLogLevel = defaultLogLevel;
	}	
	
	/**
	 * Starts new profiling scope (writes 'start' and 'complete' of current scope).
	 * @param log Log to write profiling output.
	 * @param name Name of operation.
	 * @param logLevel Log level to output log records.
	 * @return Scope control.
	 */
	public AutoCloseable startScope(
			@Nullable Logger log,
			@Nonnull String name,
			@Nullable LogLevel logLevel) {
		return new ProfilerScope(
				log,
				name,
				logLevel,
				null,
				null,
				false);
	}
	
	/**
	 * Starts new profiling scope (writes 'start' and 'complete' of current scope with specified ID for MDC).
	 * @param log Log to write profiling output.
	 * @param name Name of operation.
	 * @param logLevel Log level to output log records.
	 * @param correlationId Current ID of operation.
	 * @param measureDuration true - measure duration of current operation.
	 * @return Scope control.
	 */
	public AutoCloseable startScope(
			@Nullable Logger log,
			@Nonnull String name,
			@Nullable LogLevel logLevel,
			@Nullable Object correlationId,
			boolean measureDuration) {
		return new ProfilerScope(
				log,
				name,
				logLevel,
				correlationId,
				null,
				measureDuration);
	}	
	
	/**
	 * Starts new profiling scope (writes 'start' and 'complete' of current scope with generated ID for MDC).
	 * @param log Log to write profiling output.
	 * @param name Name of operation.
	 * @param logLevel Log level to output log records.
	 * @param correlationIdType Type of operation ID for creation if necessary.
	 * @return Scope control.
	 */
	public AutoCloseable startScope(
			@Nullable Logger log,
			@Nonnull String name,
			@Nullable LogLevel logLevel,
			@Nullable CorrelationIdType correlationIdType) {
		return new ProfilerScope(
				log,
				name,
				logLevel,
				null,
				correlationIdType,
				false);
	}

	/**
	 * Starts new profiling scope (writes 'start' and 'complete' of current scope with generated ID for MDC).
	 * @param log Log to write profiling output.
	 * @param name Name of operation.
	 * @param logLevel Log level to output log records.
	 * @param correlationIdType Type of operation ID for creation if necessary.
	 * @param measureDuration true - measure duration of current operation.
	 * @return Scope control.
	 */	
	public AutoCloseable startScope(
			@Nullable Logger log,
			@Nonnull String name,
			@Nullable LogLevel logLevel,
			@Nullable CorrelationIdType correlationIdType,
			boolean measureDuration) {
		return new ProfilerScope(
				log,
				name,
				logLevel,
				null,
				correlationIdType,
				measureDuration);
	}

	/**
	 * Starts new profiling scope.
	 * @param log Log to write profiling output.
	 * @param name Name of operation.
	 * @param logLevel Log level to output log records.
	 * @param correlationId Current ID of operation.
	 * @param correlationIdType Type of operation ID for creation if necessary.
	 * @param measureDuration true - measure duration of current operation.
	 * @return Scope control.
	 */
	public AutoCloseable startScope(
			@Nullable Logger log,
			@Nonnull String name,
			@Nullable LogLevel logLevel,
			@Nullable Object correlationId,
			@Nullable CorrelationIdType correlationIdType,
			boolean measureDuration) {
		return new ProfilerScope(
				log,
				name,
				logLevel,
				correlationId,
				correlationIdType,
				measureDuration);
	}
	
	protected class ProfilerScope implements AutoCloseable {
		
		protected final Logger log;
		
		protected final String name;
		
		protected final LogLevel logLevel; 
		
		protected final CorrelationIdType correlationIdType;
		
		protected final boolean useMDC;
		
		protected final boolean measureDuration;
		
		protected final String prevCorrelationId;

		protected final StopWatch stopWatch;
		
		public ProfilerScope(
				Logger log,
				String name,
				LogLevel logLevel,
				Object correlationId,
				CorrelationIdType correlationIdType,
				boolean measureDuration) {
			this.log = log; 
			this.name = name;
			this.logLevel = (logLevel == null || logLevel == LogLevel.DEFAULT) ? getDefaultLogLevel() : logLevel;
			this.correlationIdType = correlationIdType;
			this.measureDuration = measureDuration;

			if(correlationId == null) {
				if(correlationIdType != null && correlationIdType != CorrelationIdType.NONE) {
					if(correlationIdType == CorrelationIdType.SIMPLE) {
						correlationId = longCorrelationId.addAndGet(1);
					} else if(correlationIdType == CorrelationIdType.UUID) {
						correlationId = UUID.randomUUID();
					}
				}
			}
			
			if(correlationId != null) {
				useMDC = true;
				prevCorrelationId = MDC.get(getMDCKey());
				if(StringUtils.isNotEmpty(prevCorrelationId)) {
					MDC.put(getMDCKey(), prevCorrelationId + " > " + correlationId.toString());
				} else {
					MDC.put(getMDCKey(), correlationId.toString());
				}
			} else {
				useMDC = false;
				prevCorrelationId = null;
			}
			
			if(log == null) {
				log = defaultLog;
			}
			
			if(this.logLevel == LogLevel.TRACE) {
				log.trace(getStartPrint(), name);
			} else if(this.logLevel == LogLevel.DEBUG) {
				log.debug(getStartPrint(), name);
			}  else if(this.logLevel == LogLevel.INFO) {
				log.info(getStartPrint(), name);
			}
			
			if(measureDuration) {
				stopWatch = new StopWatch();
				stopWatch.start();
			} else {
				stopWatch = null;
			}
		}
		
		@Override
		public void close() 
				throws Exception {
			
			if(stopWatch != null) {
				stopWatch.stop();
			}
			
			if(useMDC) {
				if(stopWatch != null) {
					if(logLevel == LogLevel.TRACE) {
						log.trace(getMeasureDurationPrint(), stopWatch.getTime());
					} else if(logLevel == LogLevel.DEBUG) {
						log.debug(getMeasureDurationPrint(), stopWatch.getTime());
					}  else if(logLevel == LogLevel.INFO) {
						log.info(getMeasureDurationPrint(), stopWatch.getTime());
					}					
				} else {
					if(logLevel == LogLevel.TRACE) {
						log.trace(getCompletedPrint());
					} else if(logLevel == LogLevel.DEBUG) {
						log.debug(getCompletedPrint());
					}  else if(logLevel == LogLevel.INFO) {
						log.info(getCompletedPrint());
					}										
				}
			} else {
				if(stopWatch != null) {
					if(logLevel == LogLevel.TRACE) {
						log.trace(getMeasureDurationNoMDCPrint(), name, stopWatch.getTime());
					} else if(logLevel == LogLevel.DEBUG) {
						log.debug(getMeasureDurationNoMDCPrint(), name, stopWatch.getTime());
					}  else if(logLevel == LogLevel.INFO) {
						log.info(getMeasureDurationNoMDCPrint(), name, stopWatch.getTime());
					}					
				} else {
					if(logLevel == LogLevel.TRACE) {
						log.trace(getCompletedNoMDCPrint(), name);
					} else if(logLevel == LogLevel.DEBUG) {
						log.debug(getCompletedNoMDCPrint(), name);
					}  else if(logLevel == LogLevel.INFO) {
						log.info(getCompletedNoMDCPrint(), name);
					}										
				}
			}
			
			if(useMDC) {
				if(StringUtils.isNotEmpty(prevCorrelationId)) {
					MDC.put(getMDCKey(), prevCorrelationId);
				} else {
					MDC.remove(getMDCKey());
				}
			}
		}
	} 
}
