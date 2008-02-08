package gov.loc.repository.service;

import static org.junit.Assert.*;

import gov.loc.repository.service.impl.DummyComponent;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class CallableAdapterTest {
	static Mockery context = new JUnit4Mockery();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCallReturnsTrue() throws Exception {
		Map<String,Object> variableMap = new HashMap<String, Object>();
		variableMap.put("fooParam", "foo");
		variableMap.put("barParam", "bar");
		variableMap.put("booleanParam", true);
		CallableAdapter callableAdapter = new CallableAdapter(createMockDummyComponentReturnsTrue(), "foo", variableMap);
		TaskResult taskResult = callableAdapter.call();
		assertTrue(taskResult.isSuccess);
		assertNull(taskResult.error);
	}

	public static DummyComponent createMockDummyComponentReturnsTrue() throws Exception
	{
		final DummyComponent component = context.mock(DummyComponent.class);
		context.checking(new Expectations() {{
			one(component).executeFoo(with(equal("foo")), with(equal("bar")), with(equal(true)));
			one(component).getExecuteFooResult();
			will(returnValue(true));
		}});
		
		return component;
	}

	@Test
	public void testCallNoReturn() throws Exception {
		Map<String,Object> variableMap = new HashMap<String, Object>();
		variableMap.put("fooParam", "foo");
		variableMap.put("barParam", "bar");
		CallableAdapter callableAdapter = new CallableAdapter(createMockDummyComponentNoReturn(), "foo2", variableMap);
		TaskResult taskResult = callableAdapter.call();
		assertTrue(taskResult.isSuccess);
		assertNull(taskResult.error);
	}
	
	
	public static DummyComponent createMockDummyComponentNoReturn() throws Exception
	{
		final DummyComponent component = context.mock(DummyComponent.class);
		context.checking(new Expectations() {{
			one(component).executeFoo2(with(equal("foo")), with(equal("bar")));
		}});
		
		return component;
	}
	
	@Test
	public void testCallReturnsFalse() throws Exception {
		Map<String,Object> variableMap = new HashMap<String, Object>();
		variableMap.put("fooParam", "foo");
		variableMap.put("barParam", "bar");
		variableMap.put("booleanParam", true);
		CallableAdapter callableAdapter = new CallableAdapter(createMockDummyComponentReturnsFalse(), "foo", variableMap);
		TaskResult taskResult = callableAdapter.call();
		assertFalse(taskResult.isSuccess);
		assertNull(taskResult.error);
	}

	public static DummyComponent createMockDummyComponentReturnsFalse() throws Exception
	{
		final DummyComponent component = context.mock(DummyComponent.class);
		context.checking(new Expectations() {{
			one(component).executeFoo(with(equal("foo")), with(equal("bar")), with(equal(true)));
			one(component).getExecuteFooResult();
			will(returnValue(false));
		}});
		
		return component;
	}
	
	@Test
	public void testCallMissingParam() throws Exception {
		Map<String,Object> variableMap = new HashMap<String, Object>();
		variableMap.put("fooParam", "foo");
		CallableAdapter callableAdapter = new CallableAdapter(createMockDummyComponentNeverCalled(), "foo", variableMap);
		TaskResult taskResult = callableAdapter.call();
		assertFalse(taskResult.isSuccess);
		assertNotNull(taskResult.error);
	}

	@Test
	public void testCallMissingJobType() throws Exception {
		Map<String,Object> variableMap = new HashMap<String, Object>();
		variableMap.put("fooParam", "foo");
		variableMap.put("barParam", "bar");
		CallableAdapter callableAdapter = new CallableAdapter(createMockDummyComponentNeverCalled(), "foo3", variableMap);
		TaskResult taskResult = callableAdapter.call();
		assertFalse(taskResult.isSuccess);
		assertNotNull(taskResult.error);
	}
		
	public static DummyComponent createMockDummyComponentNeverCalled() throws Exception
	{
		final DummyComponent component = context.mock(DummyComponent.class);
		context.checking(new Expectations() {{
			never(component).executeFoo(with(any(String.class)), with(any(String.class)), with(any(Boolean.class)));
			will(returnValue(true));
		}});
		
		return component;
	}
	
}
