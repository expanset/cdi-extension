package com.expanset.cdi.config;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

public class ConfiguredValueProducer {
	
	@Inject
    protected Configuration config;

    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public String getStringConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final String result;
    	final String defaultValue = StringUtils.isNotEmpty(ann.def()) ? ann.def() : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getString(ann.value());
    	} else {
    		result = config.getString(ann.value(), defaultValue);
    	}
    	return result;
    }

    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public Double getDoubleConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final Double result;
    	final Double defaultValue = StringUtils.isNotEmpty(ann.def()) ? Double.valueOf(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getDouble(ann.value());
    	} else {
    		result = config.getDouble(ann.value(), defaultValue);
    	}
    	return result;
    }
}
