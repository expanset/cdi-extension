package com.expanset.cdi.i18n;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ResourceBundleTest {
	
	@Inject
	private LocaleManager localeManager;

	@Inject
	private ResourceBundleProviderProducer resourceBundleProviderProducer;
	
	@Inject
	private Instance<TestBean> testBeanProvider;	
	
	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(TestBean.class)
            .addClass(ThreadScopeLocaleManager.class)
            .addClass(LocaleProducer.class)
            .addClass(ResourceBundleProducer.class)
            .addClass(ResourceBundleProviderProducer.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }	
	
	@Before
	public void initialize() {
		resourceBundleProviderProducer.setFileName("src/test/resources/strings");
	}

	@Test
	public void testEnLocale() {
		localeManager.run(new Locale("en"), () -> {
			final TestBean testbean = testBeanProvider.get();
			
			assertEquals("en", testbean.locale.getLanguage());
			assertEquals("string_en", testbean.resourceBundle.getString("testString"));
		});
	}

	@Test
	public void testDeLocale() {
		localeManager.run(new Locale("de"), () -> {
			final TestBean testbean = testBeanProvider.get();
			
			assertEquals("de", testbean.locale.getLanguage());
			assertEquals("string_de", testbean.resourceBundle.getString("testString"));
		});
	}
	
	@Test
	public void testResourceBundleProvider() {
		final String value = localeManager.get(new Locale("en"), () -> {
			final TestBean testbean = testBeanProvider.get();
			
			assertEquals("en", testbean.locale.getLanguage());
			
			return testbean.resourceBundleProvider.get(new Locale("de")).getString("testString");
		});
		
		assertEquals("string_de", value);
	}	
	
	@Dependent
	public static class TestBean {
		
		@Inject
		private Locale locale;
		
		@Inject
		private ResourceBundle resourceBundle;		

		@Inject
		private ResourceBundleProvider resourceBundleProvider;

		public Locale getLocale() {
			return locale;
		}

		public ResourceBundle getResourceBundle() {
			return resourceBundle;
		}		
		
		public ResourceBundleProvider getResourceBundleProvider() {
			return resourceBundleProvider;
		}
	}
}
