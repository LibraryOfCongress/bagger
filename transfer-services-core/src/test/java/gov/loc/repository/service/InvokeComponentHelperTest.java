package gov.loc.repository.service;

import gov.loc.repository.component.TestComponent;
import gov.loc.repository.serviceBroker.ServiceRequest.ObjectEntry;
import gov.loc.repository.serviceBroker.impl.BooleanEntryImpl;
import gov.loc.repository.serviceBroker.impl.IntegerEntryImpl;
import gov.loc.repository.serviceBroker.impl.StringEntryImpl;

import java.util.ArrayList;
import java.util.Collection;

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
	Collection<ObjectEntry> entries = new ArrayList<ObjectEntry>();
	
	@Before
	public void setup()
	{
		entries.add(new StringEntryImpl("message", "foo"));
		entries.add(new BooleanEntryImpl("istrue",true));
		entries.add(new IntegerEntryImpl("key",1L));
		
	}
	
	@Test
	public void testInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			one(mock).test("foo", true, 1L);
		}});
				
		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "test", entries);
		assertTrue(helper.invoke());
	}

	@Test
	public void testInvokeWithNull() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			one(mock).test(null, true, 1L);
		}});
		
		entries.clear();
		entries.add(new StringEntryImpl("message", null));
		entries.add(new BooleanEntryImpl("istrue",true));
		entries.add(new IntegerEntryImpl("key",1L));

		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "test", entries);
		assertTrue(helper.invoke());
	}
	
	
	@Test(expected=Exception.class)
	public void testBadJobTypeInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});
				
		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "xtest", entries);
		helper.invoke();
	}

	@Test(expected=Exception.class)
	public void testMissingParameterInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});
		
		entries.remove(entries.iterator().next());
		//map.remove("message");
		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "test", entries);
		helper.invoke();
	}

	@Test(expected=Exception.class)
	public void testExtraParameterInvoke() throws Exception
	{
		final TestComponent mock = context.mock(TestComponent.class);
		
		context.checking(new Expectations() {{
			never(mock).test("foo", true, 1L);
		}});
		
		entries.add(new StringEntryImpl("foo","bar"));
		InvokeComponentHelper helper = new InvokeComponentHelper(mock, "test", entries);
		helper.invoke();
	}
	
}
