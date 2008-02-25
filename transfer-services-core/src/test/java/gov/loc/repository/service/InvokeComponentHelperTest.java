package gov.loc.repository.service;


import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.transfer.components.test.TestComponent;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JMock.class)
public class InvokeComponentHelperTest {
	Mockery context = new JUnit4Mockery();
	Map<String,Object> map = new HashMap<String, Object>();
	
	@Before
	public void setup()
	{
		map.put("message", "foo");
		map.put("istrue", true);
		map.put("key", 1L);
		
	}
	
	@Test
	public void testInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			one(mock).test("foo", true, 1L);
		}});
				
		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "test", map);
		assertTrue(helper.invoke());
	}
	
	@Test(expected=Exception.class)
	public void testBadJobTypeInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});
				
		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "xtest", map);
		helper.invoke();
	}

	@Test(expected=Exception.class)
	public void testMissingParameterInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});
		
		map.remove("message");
		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "test", map);
		helper.invoke();
	}

	@Test(expected=Exception.class)
	public void testExtraParameterInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});
		
		map.put("foo", "bar");
		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "test", map);
		helper.invoke();
	}
	
}
