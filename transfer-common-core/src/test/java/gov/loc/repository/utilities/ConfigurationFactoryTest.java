package gov.loc.repository.utilities;


import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigurationFactoryTest {
	
	@Test
	public void testGetConfiguration() throws Exception
	{
		Configuration config = ConfigurationFactory.getConfiguration("test");
		assertEquals("foobar1", config.getProperty("foo1"));
		assertEquals("bar2", config.getProperty("foo2"));
		assertEquals("bar3", config.getProperty("foo3"));
		assertEquals("bar4", config.getProperty("foo4"));
	}

}
