package gov.loc.repository.service.impl;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import gov.loc.repository.service.Memento;
import gov.loc.repository.service.MementoStore;
import gov.loc.repository.service.Messenger;
import gov.loc.repository.service.RequestMessage;
import gov.loc.repository.service.Service;
import gov.loc.repository.service.TaskFactory;
import gov.loc.repository.service.TaskResult;
import gov.loc.repository.service.Service.Status;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(JMock.class)
public class ServiceImplTest {
	static Mockery context = new JUnit4Mockery();

	Service service;
	
	@Before
	public void setUp() throws Exception
	{
		service = new ServiceImpl();
	}
	
	@Test
	public void testService() throws Exception
	{
		service.setMessenger(createMockMessenger());
		service.setTaskFactory(createMockTaskFactory());
		service.setMementoStore(createMockMementoStore());
		assertEquals(Status.STOPPED, service.getStatus());
		service.start();
		Thread.sleep(4000);
		assertEquals(Status.RUNNING, service.getStatus());
		service.stop();
		Thread.sleep(service.getPollInterval());
		assertTrue(service.getStatus() == Status.STOPPING || service.getStatus() == Status.STOPPED);
	}
			
	@SuppressWarnings("unchecked")
	public static Messenger createMockMessenger() throws Exception
	{
		final Messenger messenger = context.mock(Messenger.class);
		final Sequence seq = context.sequence("seq");
		context.checking(new Expectations() {{
			one(messenger).start(with(any(Set.class)), with(any(Set.class)));
			inSequence(seq);
			one(messenger).sendResponseMessage(with(any(Memento.class)), with(any(TaskResult.class)));			
			inSequence(seq);			
			one(messenger).getNextRequestMessage();
			will(returnValue(null));
			inSequence(seq);
			//Return a mock request message
			one(messenger).getNextRequestMessage();
			will(returnValue(createMockRequestMessage()));
			inSequence(seq);
			one(messenger).createMemento(with(any(RequestMessage.class)));
			will(returnValue(createMockMemento()));
			inSequence(seq);			
			one(messenger).sendResponseMessage(with(any(Memento.class)), with(any(TaskResult.class)));
			allowing(messenger).getNextRequestMessage();
			will(returnValue(null));
			inSequence(seq);
			one(messenger).stop();
			inSequence(seq);
			
		}});
		return messenger;
		
	}

	public static RequestMessage createMockRequestMessage()
	{
		final RequestMessage message = context.mock(RequestMessage.class);
		return message;
	}
	
	
	public static TaskFactory createMockTaskFactory() throws Exception
	{
		final TaskFactory taskFactory = context.mock(TaskFactory.class);
		final Set<String> jobTypeList = new HashSet<String>();
		jobTypeList.add("foo");
		context.checking(new Expectations() {{
			one(taskFactory).newTask(with(any(RequestMessage.class)));
			will(returnValue(createMockCallable()));
			one(taskFactory).getJobTypeList();
			will(returnValue(jobTypeList));			
		}});
		
		return taskFactory;
	}
	
	@SuppressWarnings("unchecked")
	public static Callable<TaskResult> createMockCallable() throws Exception
	{
		final Callable<TaskResult> callable = context.mock(Callable.class);
		context.checking(new Expectations() {{
			one(callable).call();
			will(returnValue(new TaskResult()));
		}});
		
		return callable;
	}

	public static MementoStore createMockMementoStore() throws Exception
	{
		final MementoStore store = context.mock(MementoStore.class);
		final Sequence seq = context.sequence("seq-store");
		final Map<Integer,Memento> mementoMap = new HashMap<Integer,Memento>();
		mementoMap.put(1, createMockMemento());
		context.checking(new Expectations() {{
			one(store).getMementoMap();
			will(returnValue(mementoMap));
			inSequence(seq);
			one(store).delete(with(equal(1)));
			inSequence(seq);			
			one(store).put(with(equal(0)), with(any(Memento.class)));
			inSequence(seq);
			one(store).get(with(equal(0)));
			will(returnValue(createMockMemento()));
			inSequence(seq);
			one(store).delete(with(equal(0)));
			inSequence(seq);
		}});
		
		return store;
	}
	
	public static Memento createMockMemento() throws Exception
	{
		final Memento memento = context.mock(Memento.class);
		return memento;
	}
}
