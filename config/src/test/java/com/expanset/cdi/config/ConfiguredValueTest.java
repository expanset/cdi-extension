package 
com.expanset.cdi.config;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.expanset.cdi.config.ConfiguredValue;
import com.expanset.cdi.config.ConfiguredValueProducer;

@RunWith(Arquillian.class)
public class ConfiguredValueTest {
	
	@Inject
	private TestBean test;	
	
	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(TestBean.class)
            .addClass(ConfigurationProducer.class)
            .addClass(ConfiguredValueProducer.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	@Test
	public void injectValues() {
		assertEquals("test", test.testString);
		assertEquals(Double.valueOf(0.1), test.testDouble);
	}

	public static class TestBean {	
		
		@Inject 
		@ConfiguredValue("testString")
		public String testString;

		@Inject 
		@ConfiguredValue("testDouble")
		public Double testDouble;
	}
	
	@ApplicationScoped
	public static class ConfigurationProducer {
		
		@Produces
		public Configuration getConfiguration() {
			final Map<String, Object> props = new HashMap<>();
			props.put("testString", "test");
			props.put("testDouble", 0.1);
			final Configuration config = new MapConfiguration(props);
			return config;
		} 
	}
}
