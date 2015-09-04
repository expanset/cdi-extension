package com.expanset.cdi.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Contract for services that implements access to {@link ResourceBundle} based on specified locale. 
 */
public interface ResourceBundleProvider {

	/**
	 * @param locale Locale for localized resources.
	 * @return {@link ResourceBundle} based on specified locale.
	 */
	public ResourceBundle get(Locale locale);
}
