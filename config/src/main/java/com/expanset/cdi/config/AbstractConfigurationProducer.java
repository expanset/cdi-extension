package com.expanset.cdi.config;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.event.EventSource;

public abstract class AbstractConfigurationProducer implements ConfigurationListener {
	
	@Inject
	@BeforeReloading
	private Event<ConfigurationEvent> beforeReloadingEvent;

	@Inject
	@AfterReloading
	private Event<ConfigurationEvent> afterReloadingEvent;

	@Inject
	@ConfigChanged
	private Event<ConfigurationEvent> configChangedEvent;
			
	@Produces
	@ApplicationScoped	
	public abstract Configuration getConfiguration();
	
	public abstract void disposeConfiguration(@Disposes Configuration config);
	
	@Override
	public void configurationChanged(ConfigurationEvent event) {
		if(event.getType() == AbstractFileConfiguration.EVENT_RELOAD) {
			if(event.isBeforeUpdate()) {
				beforeReloadingEvent.fire(event);
			} else {
				afterReloadingEvent.fire(event);
			}
			
		} else {
			configChangedEvent.fire(event);
		}			
	}
		
	protected void addListener(Configuration config) {
		for(EventSource eventSource : getEventSources(config)) {
			eventSource.addConfigurationListener(this);
		}		
	}
	
	protected void removeListener(Configuration config) {
		for(EventSource eventSource : getEventSources(config)) {
			for(ConfigurationListener listener : eventSource.getConfigurationListeners()) {
				if(listener instanceof AbstractConfigurationProducer) {
					eventSource.removeConfigurationListener(listener);
				}
			}
		}		
	}	
	
	private List<EventSource> getEventSources(Configuration config) {
		final List<EventSource> result = new ArrayList<>();
		if(config instanceof CompositeConfiguration) {
			final CompositeConfiguration compositeConfig = (CompositeConfiguration)config;
			for(int i = 0; i < compositeConfig.getNumberOfConfigurations(); i++) {
				final Configuration childConfig = compositeConfig.getConfiguration(i); 
				if(childConfig instanceof EventSource) {
					result.add((EventSource)childConfig);
				}
			}
		} else if(config instanceof EventSource) {
			result.add((EventSource)config);
		} else {
			assert false : "Unknown type";
		}
		
		return result;
	}
}
