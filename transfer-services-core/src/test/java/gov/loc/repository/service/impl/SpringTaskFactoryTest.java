package gov.loc.repository.service.impl;

import static org.junit.Assert.*;

import gov.loc.repository.service.RequestMessage;
import gov.loc.repository.service.ServiceConstants;
import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.utilities.ConfigurationFactory;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

@RunWith(JMock.class)
public class SpringTaskFactoryTest {
	static Mockery context = new JUnit4Mockery();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNewTask() throws Exception {
		ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME).addProperty("service.fooId.enabled", true);
		
		SpringTaskFactory factory = new SpringTaskFactory();
	
		factory.setBeanFactory(createMockBeanFactory());
		
		assertNotNull(factory.newTask(createMockRequestMessage()));
	}
	
	public static RequestMessage createMockRequestMessage() throws Exception
	{
		final RequestMessage message = context.mock(RequestMessage.class);
		final Map<String,String> variableMap = new HashMap<String, String>();
		variableMap.put("foo", "foo");
		variableMap.put("bar", "bar");
		context.checking(new Expectations() {{
			one(message).getJobType();
			will(returnValue("foo"));
			one(message).getVariableMap();
			will(returnValue(variableMap));			
		}});
		
		return message;
	}
		
	@SuppressWarnings("unchecked")
	public static BeanFactory createMockBeanFactory() throws Exception
	{
		final ListableBeanFactory factory = context.mock(ListableBeanFactory.class);
		final Map beanMap = new HashMap();
		beanMap.put("fooId", new DummyComponentImpl());
		context.checking(new Expectations() {{
			one(factory).getBeansOfType(with(equal(Component.class)));
			will(returnValue(beanMap));
			one(factory).containsBean(with(equal("fooId")));
			will(returnValue(true));
			one(factory).getBean(with(equal("fooId")));
			will(returnValue(createMockDummyCallable()));			
		}});
		return factory;
	}
	
	public static DummyCallable createMockDummyCallable() throws Exception
	{
		final DummyCallable callable = context.mock(DummyCallable.class);
		return callable;
	}
}
