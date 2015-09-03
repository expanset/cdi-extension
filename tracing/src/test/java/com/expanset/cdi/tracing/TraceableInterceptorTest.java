package com.expanset.cdi.tracing;

import static org.mockito.Mockito.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.expanset.cdi.tracing.CorrelationIdType;
import com.expanset.cdi.tracing.LogLevel;
import com.expanset.cdi.tracing.Traceable;
import com.expanset.cdi.tracing.TraceableInterceptor;
import com.expanset.cdi.tracing.TracingService;

@RunWith(Arquillian.class)
public class TraceableInterceptorTest {

	@Inject
	private TestBean test;
	
	@Inject
	private TracingService profilingService;
			
	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(TestBean.class)
            .addClass(TraceableInterceptor.class)
            .addClass(ProfilingServiceProducer.class)
            .addAsManifestResource(
            		new ClassLoaderAsset("META-INF/beans.xml"), 
            		"beans.xml");
    }
	
	@Test
	public void simpleProfiling() {
		test.simple();
		
		verify(profilingService, times(1)).startScope(
				any(Logger.class), 
				eq("simple"), 
				eq(LogLevel.DEFAULT), 
				eq(null), 
				eq(CorrelationIdType.NONE), 
				eq(false));
	}
	
	@Test
	public void complexProfiling() {
		test.complex();
		
		verify(profilingService, times(1)).startScope(
				any(Logger.class), 
				eq("opName"), 
				eq(LogLevel.TRACE), 
				eq(null), 
				eq(CorrelationIdType.UUID), 
				eq(true));
	}

	@Dependent
	public static class TestBean {
		
		@Traceable
		public void simple() {
		}

		@Traceable(value=LogLevel.TRACE,idType=CorrelationIdType.UUID,measure=true,name="opName")
		public void complex() {
		}
	}
	
	@ApplicationScoped
	public static class ProfilingServiceProducer {
		
		@Produces
		@Singleton
		public TracingService getProfilingService() {
			return mock(TracingService.class);
		}
	}
}
