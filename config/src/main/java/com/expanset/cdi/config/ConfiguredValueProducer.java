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
    	if(ann.required()) {
    		result = config.getString(ann.value());
    	} else {
    		result = config.getString(ann.value(), ann.def());
    	}
    	return result;
    }

    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public Double getDoubleConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final Double result;
    	if(ann.required()) {
    		result = config.getDouble(ann.value());
    	} else {
    		final Double def;
    		if(StringUtils.isNotEmpty(ann.def())) {
    			def = Double.valueOf(ann.def());
    		} else {
    			def = null;
    		}
    		result = config.getDouble(ann.value(), def);
    	}
    	return result;
    }
}
