package com.expanset.cdi.config;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
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
		assertNull(test.testStringNull);
		assertEquals("test", test.testStringDefault);

		assertEquals(new Boolean(true), test.testBoolean);
		assertNull(test.testBooleanNull);
		assertEquals(new Boolean(true), test.testBooleanDefault);		

		assertEquals(Byte.valueOf((byte)1), test.testByte);
		assertNull(test.testByteNull);
		assertEquals(Byte.valueOf((byte)1), test.testByteDefault);		
		
		assertEquals(Short.valueOf((short)2), test.testShort);
		assertNull(test.testShortNull);
		assertEquals(Short.valueOf((short)2), test.testShortDefault);		

		assertEquals(Integer.valueOf((int)3), test.testInteger);
		assertNull(test.testIntegerNull);
		assertEquals(Integer.valueOf((int)3), test.testIntegerDefault);		

		assertEquals(Long.valueOf((int)4), test.testLong);
		assertNull(test.testLongNull);
		assertEquals(Long.valueOf((int)4), test.testLongDefault);		
		
		assertEquals(BigInteger.valueOf((long)5), test.testBigInteger);
		assertNull(test.testBigIntegerNull);
		assertEquals(BigInteger.valueOf((long)5), test.testBigIntegerDefault);			

		assertEquals(BigDecimal.valueOf(6.1), test.testBigDecimal);
		assertNull(test.testBigDecimalNull);
		assertEquals(BigDecimal.valueOf(6.1), test.testBigDecimalDefault);			

		assertEquals(Float.valueOf((float)7.1), test.testFloat);
		assertNull(test.testFloatNull);
		assertEquals(Float.valueOf((float)7.1), test.testFloatDefault);			
		
		assertEquals(Double.valueOf(8.1), test.testDouble);
		assertNull(test.testDoubleNull);
		assertEquals(Double.valueOf(8.1), test.testDoubleDefault);
		
		assertEquals("test", test.testStringInstance.get());
	}

	@Dependent
	public static class TestBean {	
		
		@Inject 
		@ConfiguredValue("testString")
		String testString;

		@Inject 
		@ConfiguredValue(value="testStringNull",required=false)
		String testStringNull;

		@Inject 
		@ConfiguredValue(value="testStringDefault",def="test")
		String testStringDefault;

		@Inject
		@ConfiguredValue("testBoolean")
		Boolean testBoolean;

		@Inject 
		@ConfiguredValue(value="testFloatNull",required=false)
		Boolean testBooleanNull;
		
		@Inject 
		@ConfiguredValue(value="testBooleanDefault",def="true")
		Boolean testBooleanDefault;
		
		@Inject
		@ConfiguredValue("testByte")
		Byte testByte;

		@Inject 
		@ConfiguredValue(value="testByteNull",required=false)
		Byte testByteNull;
		
		@Inject 
		@ConfiguredValue(value="testByteDefault",def="1")
		Byte testByteDefault;	
		
		@Inject
		@ConfiguredValue("testShort")
		Short testShort;

		@Inject 
		@ConfiguredValue(value="testShortNull",required=false)
		Short testShortNull;
		
		@Inject 
		@ConfiguredValue(value="testShortDefault",def="2")
		Short testShortDefault;		
		
		@Inject
		@ConfiguredValue("testInteger")
		Integer testInteger;

		@Inject 
		@ConfiguredValue(value="testIntegerNull",required=false)
		Integer testIntegerNull;
		
		@Inject 
		@ConfiguredValue(value="testIntegerDefault",def="3")
		Integer testIntegerDefault;			
		
		@Inject
		@ConfiguredValue("testLong")
		Long testLong;

		@Inject 
		@ConfiguredValue(value="testLongNull",required=false)
		Long testLongNull;
		
		@Inject 
		@ConfiguredValue(value="testLongDefault",def="4")
		Long testLongDefault;	
		
		@Inject
		@ConfiguredValue("testBigInteger")
		BigInteger testBigInteger;

		@Inject 
		@ConfiguredValue(value="testBigIntegerNull",required=false)
		BigInteger testBigIntegerNull;
		
		@Inject 
		@ConfiguredValue(value="testBigIntegerDefault",def="5")
		BigInteger testBigIntegerDefault;					
		
		@Inject
		@ConfiguredValue("testBigDecimal")
		BigDecimal testBigDecimal;

		@Inject 
		@ConfiguredValue(value="testBigDecimalNull",required=false)
		BigDecimal testBigDecimalNull;
		
		@Inject 
		@ConfiguredValue(value="testBigDecimalDefault",def="6.1")
		BigDecimal testBigDecimalDefault;			
		
		@Inject
		@ConfiguredValue("testFloat")
		Float testFloat;

		@Inject 
		@ConfiguredValue(value="testFloatNull",required=false)
		Float testFloatNull;
		
		@Inject 
		@ConfiguredValue(value="testFloatDefault",def="7.1")
		Float testFloatDefault;
		
		@Inject
		@ConfiguredValue("testDouble")
		Double testDouble;

		@Inject 
		@ConfiguredValue(value="testDoubleNull",required=false)
		Double testDoubleNull;
		
		@Inject 
		@ConfiguredValue(value="testDoubleDefault",def="8.1")
		Double testDoubleDefault;
		
		@Inject 
		@ConfiguredValue("testString")
		Instance<String> testStringInstance;
	}
	
	@ApplicationScoped
	public static class ConfigurationProducer extends AbstractConfigurationProducer {
				
		@Override
		@Produces
		@ApplicationScoped
		public Configuration getConfiguration() {
			final Map<String, Object> props = new HashMap<>();
			props.put("testString", "test");
			props.put("testBoolean", true);
			props.put("testByte", (byte)1);
			props.put("testShort", (short)2);
			props.put("testInteger", (int)3);
			props.put("testLong", (long)4);
			props.put("testBigInteger", BigInteger.valueOf(5));
			props.put("testBigDecimal", BigDecimal.valueOf(6.1));
			props.put("testFloat", (float)7.1);
			props.put("testDouble", (double)8.1);
			final Configuration config = new MapConfiguration(props);
			
			addListener(config);
			
			return config;
		}

		@Override
		public void disposeConfiguration(@Disposes Configuration config) {
			removeListener(config);
		}
	}
}
