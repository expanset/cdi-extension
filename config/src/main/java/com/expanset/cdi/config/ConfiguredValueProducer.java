package com.expanset.cdi.config;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;

@Dependent
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
    public Boolean getBooleanConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final Boolean result;
    	final Boolean defaultValue = StringUtils.isNotEmpty(ann.def()) ? new Boolean(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getBoolean(ann.value());
    	} else {
    		result = config.getBoolean(ann.value(), defaultValue);
    	}
    	return result;
    }

    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public Byte getByteConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final Byte result;
    	final Byte defaultValue = StringUtils.isNotEmpty(ann.def()) ? Byte.valueOf(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getByte(ann.value());
    	} else {
    		result = config.getByte(ann.value(), defaultValue);
    	}
    	return result;
    }    
    
    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public Short getShortConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final Short result;
    	final Short defaultValue = StringUtils.isNotEmpty(ann.def()) ? Short.valueOf(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getShort(ann.value());
    	} else {
    		result = config.getShort(ann.value(), defaultValue);
    	}
    	return result;
    }    

    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public Integer getIntegerConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final Integer result;
    	final Integer defaultValue = StringUtils.isNotEmpty(ann.def()) ? Integer.valueOf(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getInt(ann.value());
    	} else {
    		result = config.getInteger(ann.value(), defaultValue);
    	}
    	return result;
    }

    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public Long getLongConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final Long result;
    	final Long defaultValue = StringUtils.isNotEmpty(ann.def()) ? Long.valueOf(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getLong(ann.value());
    	} else {
    		result = config.getLong(ann.value(), defaultValue);
    	}
    	return result;
    }

    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public BigInteger getBigIntegerConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final BigInteger result;
    	final BigInteger defaultValue = StringUtils.isNotEmpty(ann.def()) ? new BigInteger(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getBigInteger(ann.value());
    	} else {
    		result = config.getBigInteger(ann.value(), defaultValue);
    	}
    	return result;
    }
    
    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public BigDecimal getBigDecimalConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final BigDecimal result;
    	final BigDecimal defaultValue = StringUtils.isNotEmpty(ann.def()) ? new BigDecimal(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getBigDecimal(ann.value());
    	} else {
    		result = config.getBigDecimal(ann.value(), defaultValue);
    	}
    	return result;
    }
    
    @Produces
    @ConfiguredValue(StringUtils.EMPTY)
    public Float getFloatConfigValue(InjectionPoint ip) {
    	final ConfiguredValue ann = ip.getAnnotated().getAnnotation(ConfiguredValue.class);
    	final Float result;
    	final Float defaultValue = StringUtils.isNotEmpty(ann.def()) ? Float.valueOf(ann.def()) : null;    	
    	if(ann.required() && defaultValue == null) {
    		result = config.getFloat(ann.value());
    	} else {
    		result = config.getFloat(ann.value(), defaultValue);
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
