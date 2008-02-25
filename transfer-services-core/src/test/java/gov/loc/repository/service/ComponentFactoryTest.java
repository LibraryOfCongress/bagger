package gov.loc.repository.service;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ComponentFactoryTest {

	protected ComponentFactory factory;
	
	@Before
	public void setup() throws Exception
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("services-context-core.xml");
		factory = (ComponentFactory)context.getBean("componentFactory");
		assertNotNull(factory);
	}
	
	@Test
	public void testGetComponent() throws Exception
	{
		assertNotNull(factory.getComponent("testcomponent"));
	}
	
	@Test(expected=Exception.class)
	public void testBadGetComponent() throws Exception
	{
		factory.getComponent("foo");
	}
}
