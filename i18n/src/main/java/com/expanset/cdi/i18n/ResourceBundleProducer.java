package com.expanset.cdi.i18n;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Factory for {@link ResourceBundle}.
 * <p>{@link ResourceBundle} retrieved from configured {@link ResourceBundleProvider}.</p>
 */
@Dependent
public class ResourceBundleProducer {

	@Inject
	private Instance<Locale> localeProvider;
	
	@Inject	
	private ResourceBundleProvider resourceBundleProvider;

	@Named
	@Produces
	@Dependent
	public ResourceBundle getResourceBundle() {
		return new ResourceBundleDelegated();
	}
	
	private class ResourceBundleDelegated extends ResourceBundle {
			
		@Override
		public Enumeration<String> getKeys() {
			return getResourceBundle().getKeys();
		}
		
		@Override
		protected Object handleGetObject(String key) {
			return getResourceBundle().getObject(key);
		}
		
		private ResourceBundle getResourceBundle() {
			Locale locale = localeProvider.get();		
			if(locale == null) {
				locale = Locale.getDefault();
			}
			
			final ResourceBundle bundle = resourceBundleProvider.get(locale);
			return bundle;			
		}
	}	
}
