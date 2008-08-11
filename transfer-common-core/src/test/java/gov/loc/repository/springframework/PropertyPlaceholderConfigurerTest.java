package gov.loc.repository.springframework;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

public class PropertyPlaceholderConfigurerTest {
	
	@Test
	public void testSetConfiguration() throws Exception
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("gov/loc/repository/springframework/beans.xml");
		TestBean testBean = (TestBean)context.getBean("testBean");
		assertNotNull(testBean);
		assertEquals("foobar1", testBean.getFoo1());
		assertEquals("bar2", testBean.getFoo2());
		assertEquals("bar3", testBean.getFoo3());
		assertEquals("bar4", testBean.getFoo4());
	}

}
