package com.expanset.cdi.i18n;

import java.nio.file.Paths;
import java.util.ResourceBundle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Injection binder for using {@link ResourceBundle} loaded from properties file (like java.properties).
 */
@ApplicationScoped
public class ResourceBundleProviderProducer {

	protected String fileName;
	
	protected String encoding = "utf-8";
		
	protected long timeToLive = 30000;
    
	/**
	 * @return Name of file with strings.
	 */
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return File encoding.
	 */
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return Time (milliseconds) through which verification of the file on need of reset is allowed.
	 */
	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}
	
	@Named
	@Produces
	@ApplicationScoped
	public ResourceBundleProvider getResourceBundleProvider() {
		if(StringUtils.isEmpty(fileName)) {
			throw new IllegalStateException("fileName is not initialized");
		}
		
		final String baseName = 
				Paths.get(fileName).getFileName().toString();
		final ResourceBundle.Control control = 
				createResourceBundleControl(fileName, encoding, timeToLive);
		final ResourceBundleProvider resourceBundleProvider = 
				createResourceBundleProvider(baseName, control);
		
		return resourceBundleProvider;
	}

	protected ResourceBundle.Control createResourceBundleControl(
			@Nonnull String fileName, 
			@Nullable String encoding, 
			long timeToLive) {
		Validate.notEmpty(fileName, "fileName");

		return new PropertyResourceBundleControl(
				Paths.get(fileName).getParent().toString(), encoding, timeToLive);
	}
	
	protected ResourceBundleProvider createResourceBundleProvider(
			@Nonnull String baseName,
			@Nonnull ResourceBundle.Control control) {
		Validate.notEmpty(baseName, "baseName");
		Validate.notNull(control, "control");

		return new DefaultResourceBundleProvider(baseName, control);
	}
}
