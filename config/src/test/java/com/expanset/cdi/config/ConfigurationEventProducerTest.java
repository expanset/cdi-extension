package com.expanset.cdi.config;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.Writer;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigurationEventProducerTest {
	
	@Inject
	private TestBean test;	
		
	@Inject
	private Configuration config;		
	
	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(TestBean.class)
            .addClass(ConfigurationProducer.class)
            .addClass(AbstractConfigurationProducer.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	@Test
	public void reloadingTest() 
			throws ConfigurationException {
		((AbstractFileConfiguration)config).refresh();
		
		assertTrue(test.beforeReloading);
		assertTrue(test.afterReloading);
	}

	@Test
	public void changingConfigTest() {
		((AbstractFileConfiguration)config).setProperty("test", "newValue");
		
		assertTrue(test.configChanged);
	}
		
	@Singleton
	public static class TestBean {	
		
		boolean beforeReloading = false;
		
		boolean afterReloading = false;
		
		boolean configChanged = false;
		
		public void beforeReloading(@Observes @BeforeReloading ConfigurationEvent event) {
			assertTrue(event.isBeforeUpdate());
			assertEquals(AbstractFileConfiguration.EVENT_RELOAD, event.getType());
			
			beforeReloading = true;
		}
		
		public void afterReloading(@Observes @AfterReloading ConfigurationEvent event) {
			assertFalse(event.isBeforeUpdate());
			assertEquals(AbstractFileConfiguration.EVENT_RELOAD, event.getType());
			
			afterReloading = true;
		}

		public void configChanged(@Observes @ConfigChanged ConfigurationEvent event) {
			configChanged = true;
		}		
	}
	
	public static class ConfigurationProducer extends AbstractConfigurationProducer {
		
		@Override
		@Produces
		@Singleton
		public Configuration getConfiguration() {
			final Configuration config = new AbstractFileConfiguration() {

				@Override
				public void load(Reader in) 
						throws ConfigurationException {				
				}

				@Override
				public void save(Writer out) 
						throws ConfigurationException {				
				}
				
				@Override
				public void load(String fileName)
						throws ConfigurationException {				
				}			
			};
			config.addProperty("test", "value");
			
			addListener(config);
			
			return config;
		} 
		
		@Override
		public void disposeConfiguration(@Disposes Configuration config) {
			removeListener(config);
		} 		
	}	
}
