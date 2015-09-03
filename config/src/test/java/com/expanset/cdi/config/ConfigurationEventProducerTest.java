package com.expanset.cdi.config;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.Writer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
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
	private AbstractFileConfiguration config;		
	
	@Inject
	@AnotherConfig
	private CompositeConfiguration compositeConfig;	
	
	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(TestBean.class)
            .addClass(ConfigurationProducer.class)
            .addClass(CompositeConfigurationProducer.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	@Test
	public void reloadingTest() 
			throws ConfigurationException {
		config.refresh();
		
		assertTrue(test.isBeforeReloading());
		assertTrue(test.isAfterReloading());
	}

	@Test
	public void changingConfigTest() {
		AbstractFileConfiguration fileConfiguration = 
				(AbstractFileConfiguration)compositeConfig.getConfiguration(0);	
		fileConfiguration.setProperty("test", "newValue");
		
		assertTrue(test.isConfigChanged());
	}
		
	@ApplicationScoped
	public static class TestBean {	
		
		private boolean beforeReloading = false;
		
		private boolean afterReloading = false;
		
		private boolean configChanged = false;

		public boolean isBeforeReloading() {
			return beforeReloading;
		}

		public boolean isAfterReloading() {
			return afterReloading;
		}

		public boolean isConfigChanged() {
			return configChanged;
		}

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
	
	@ApplicationScoped
	public static class ConfigurationProducer extends AbstractConfigurationProducer {

		private final AbstractFileConfiguration config;
		
		public ConfigurationProducer() {
			config = new FileConfigurationImpl(null);			
			config.addProperty("test", "value");
			
			addListener(config);
		}
		
		@Override
		@Produces
		@ApplicationScoped
		public Configuration getConfiguration() {			
			return config;
		} 
		
		@Produces
		@ApplicationScoped
		public AbstractFileConfiguration getAbstractFileConfiguration() {			
			return config;
		} 		
			
		@Override
		public void disposeConfiguration(@Disposes Configuration config) {
			removeListener(config);
		} 
	}	
	
	@ApplicationScoped
	public static class CompositeConfigurationProducer extends AbstractConfigurationProducer {
		
		private final CompositeConfiguration config;
		
		public CompositeConfigurationProducer() {
			config = new CompositeConfiguration();
			
			AbstractFileConfiguration subConfig = new FileConfigurationImpl(null);
			subConfig.addProperty("test", "value");
			config.addConfiguration(subConfig);

			addListener(config);
		}
		
		@Override
		@Produces
		@AnotherConfig
		@ApplicationScoped
		public Configuration getConfiguration() {			
			return config;
		} 
		
		@Produces
		@AnotherConfig
		@ApplicationScoped
		public CompositeConfiguration getCompositeConfiguration() {			
			return config;
		} 		
			
		@Override
		public void disposeConfiguration(@Disposes @AnotherConfig Configuration config) {
			removeListener(config);
		} 
	}
	
	private static class FileConfigurationImpl extends AbstractFileConfiguration {
		
		public FileConfigurationImpl(Object obj) {			
		}
		
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
	}
}
