package com.expanset.cdi.i18n;

import java.util.Locale;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Helps to inject {@link Locale}.
 * <p>{@link Locale} retrieved from configured {@link LocaleManager}.</p>
 */
@Dependent
public class LocaleProducer {
	
	@Inject
	protected LocaleManager localeManager;

	@Named
	@Produces
	@Dependent
	public Locale getLocale() {
		return localeManager.getCurrentLocale();
	}
}
