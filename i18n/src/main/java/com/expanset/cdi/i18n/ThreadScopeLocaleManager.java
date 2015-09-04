package com.expanset.cdi.i18n;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;

import org.apache.commons.lang3.Validate;

import com.expanset.common.RememberOptions;

/**
 * Controls {@link Locale}  lifecycle, bind {@link Locale} to the current thread.
 */
@ApplicationScoped
@Typed(LocaleManager.class)
public class ThreadScopeLocaleManager implements LocaleManager {

	protected final ThreadLocal<Locale> localeInThread = new ThreadLocal<>(); 
	
	@Override
	public AutoCloseable beginScope(@Nonnull Locale locale) {
		Validate.notNull(locale, "locale");
		
		localeInThread.set(locale);
		
		return new AutoCloseable() {
			@Override
			public void close() 
					throws Exception {
				localeInThread.remove();
			}
		};	
	}
	
	@Override
	public Locale getCurrentLocale() {
		return localeInThread.get();
	}
	
	@Override
	public void saveLocale(@Nonnull Locale locale, @Nullable RememberOptions rememberOptions) {
		// It isn't necessary save locale for the current thread.
	}

	@Override
	public void removeLocale(@Nullable RememberOptions rememberOptions) {
		// It isn't necessary save locale for the current thread.
	}
}
