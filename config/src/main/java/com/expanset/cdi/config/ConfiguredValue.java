package com.expanset.cdi.config;

import static java.lang.annotation.RetentionPolicy.*;
import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import org.apache.commons.lang3.StringUtils;

@Qualifier
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD, PARAMETER })
public @interface ConfiguredValue {

    @Nonbinding
    String value();

    @Nonbinding
    String def() default StringUtils.EMPTY;
    
    @Nonbinding
    boolean required() default true;
}
