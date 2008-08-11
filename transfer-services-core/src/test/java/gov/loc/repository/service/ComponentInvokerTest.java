package gov.loc.repository.service;

import gov.loc.repository.component.TestComponent;
import gov.loc.repository.service.component.ComponentRequest;
import gov.loc.repository.service.component.ComponentInvoker;
import gov.loc.repository.service.component.impl.ComponentRequestImpl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JMock.class)
public class ComponentInvokerTest {
	Mockery context = new JUnit4Mockery();
	ComponentRequest req;
	ComponentInvoker helper = new ComponentInvoker();
		
	@Test
	public void testInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			one(mock).test("foo", true, 1L);
			one(mock).getRespMessage();
			will(returnValue("bar"));
			one(mock).getRespKey();
			will(returnValue(2L));
			one(mock).getRespIsTrue();
			will(returnValue(false));
			one(mock).getResult();
			will(returnValue(true));
			
		}});

		req = new ComponentRequestImpl("test");
		req.addRequestString("message", "foo");
		req.addRequestBoolean("istrue",true);
		req.addRequestInteger("key",1L);
				
		helper.invoke(mock, req);
		
		assertTrue(req.isSuccess());
		assertEquals(3, req.getResponseEntries().size());
		assertFalse(req.getResponseBooleanEntries().iterator().next().getValue());
		assertEquals(Long.valueOf(2L), req.getResponseIntegerEntries().iterator().next().getValue());
		assertEquals("bar", req.getResponseStringEntries().iterator().next().getValue());
	}

	@Test
	public void testInvokeWithNull() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			one(mock).test(null, true, 1L);
			one(mock).getRespMessage();
			will(returnValue("bar"));
			one(mock).getRespKey();
			will(returnValue(2L));
			one(mock).getRespIsTrue();
			will(returnValue(false));
			one(mock).getResult();
			will(returnValue(true));
		}});
		
		req = new ComponentRequestImpl("test");
		req.addRequestString("message", null);
		req.addRequestBoolean("istrue",true);
		req.addRequestInteger("key",1L);

		
		helper.invoke(mock, req);
		
		assertTrue(req.isSuccess());
	}
	
	
	@Test
	public void testBadJobTypeInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});

		req = new ComponentRequestImpl("xtest");
		req.addRequestString("message", "foo");
		req.addRequestBoolean("istrue",true);
		req.addRequestInteger("key",1L);
				
		helper.invoke(mock, req);
		
		assertFalse(req.isSuccess());
				
	}

	@Test
	public void testMissingParameterInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});
		
		req = new ComponentRequestImpl("test");
		req.addRequestBoolean("istrue",true);
		req.addRequestInteger("key",1L);
				
		helper.invoke(mock, req);
		
		assertFalse(req.isSuccess());
	}

	@Test
	public void testExtraParameterInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});
		
		req = new ComponentRequestImpl("xtest");
		req.addRequestString("message", "foo");
		req.addRequestBoolean("istrue",true);
		req.addRequestInteger("key",1L);
		req.addRequestString("foo", "bar");
				
		helper.invoke(mock, req);
		
		assertFalse(req.isSuccess());
	}
	
}
