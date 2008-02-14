package gov.loc.repository.service.impl;

import gov.loc.repository.service.Memento;
import gov.loc.repository.service.MementoStore;
import gov.loc.repository.service.Messenger;
import gov.loc.repository.service.RequestMessage;
import gov.loc.repository.service.Service;
import gov.loc.repository.service.ServiceConstants;
import gov.loc.repository.service.TaskFactory;
import gov.loc.repository.service.TaskResult;
import gov.loc.repository.utilities.ConfigurationFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ServiceImpl implements Service, Runnable {	
	private static final Log log = LogFactory.getLog(ServiceImpl.class);
		
	private List<Future<TaskResult>> futureList = null;
	private TaskFactory taskFactory;	
	private Messenger messenger;
	private MementoStore store;
	private int threadCount = 0;
	private int pollInterval = 500;
	private Status status = Status.STOPPED;
	public Set<String> queueList = new HashSet<String>();
	
	
	
	public ServiceImpl() throws Exception {
		Configuration configuration = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
		threadCount = configuration.getInt("services.threads");		                                                    
		pollInterval = configuration.getInt("services.pollinterval");	
		String[] queueArray = configuration.getStringArray("jms.queues");
		for(String queue : queueArray)
		{
			queueList.add(queue);
		}
	}	
	
	public void run() {
		try
		{
			log.info("Starting");
			if (this.taskFactory == null)
			{
				throw new Exception("TaskFactory not provided");
			}
			if (this.messenger == null)
			{
				throw new Exception("Messenger not provided");
			}
			if (this.store == null)
			{
				throw new Exception("Memento store not provided");
			}
			log.debug("Starting messenger");
			status = Status.RUNNING;
			this.messenger.start(this.taskFactory.getJobTypeList(), this.queueList);
			
			//Check to see if anything is left over in the memento store
			this.initializeMementoStore();
			
			this.initializeFutureList();
			while(status == Status.RUNNING)
			{
				log.info("Running");
				this.checkExistingTasks();
				this.processNewTasks();
				log.info("Sleeping");
				Thread.sleep(this.pollInterval);
			}
			log.info("Waiting for tasks to complete.");
			while(this.hasActiveTasks())
			{
				this.checkExistingTasks();				
			}
			log.info("Stopped.");
			this.status = Status.STOPPED;
		}
		catch(Exception ex)
		{
			log.error("Error running service", ex);
			this.status = Status.STOPPED;
		}
		finally
		{
			try
			{
				if (this.messenger != null)
				{
					log.debug("Stopping messenger");
					this.messenger.stop();
				}
			}
			catch(Exception ex)
			{
				log.error("Error stopping Messenger", ex);
			}
		}
	}	
	
	public void start()
	{
		(new Thread(this)).start();
	}
	
	public void stop()
	{
		log.info("Stop requested.");
		this.status = Status.STOPPING;
	}

	private boolean hasActiveTasks()
	{
		for(int i=0; i < this.threadCount; i++)
		{
			if (this.futureList.get(i) != null)
			{
				return true;
			}
		}
		return false;
		
	}
	
	protected void initializeFutureList()
	{
		futureList = new ArrayList<Future<TaskResult>>(this.threadCount);
		for(int i=0; i < this.threadCount; i++)
		{
			this.futureList.add(null);
		}
	}
	
	protected void processNewTasks() throws Exception
	{
		if (this.messenger != null && this.isTaskAvailable())
		{
			RequestMessage message = this.messenger.getNextRequestMessage();
			if (message != null)
			{
				log.debug("Submitting new task");
				this.submit(message);
				processNewTasks();
			}
		}
	}
	
	protected void checkExistingTasks() throws Exception
	{
		for(int i=0; i < this.threadCount; i++)
		{
			Future<TaskResult> future = futureList.get(i);
			if (future != null && future.isDone())
			{
				log.debug("Task " + i + " is done.");
				this.messenger.sendResponseMessage(this.store.get(i), future.get());
				log.debug("Setting task slot " + i + " to null.");
				futureList.set(i, null);
				this.store.delete(i);
			}
		}
	}
	
	public void setTaskFactory(TaskFactory taskFactory)
	{
		this.taskFactory = taskFactory;
	}
	
	public void setMessenger(Messenger messenger)
	{
		this.messenger = messenger;
	}
	
	protected boolean isTaskAvailable()
	{
		return futureList.contains(null);
	}
	
	protected void submit(RequestMessage message) throws Exception
	{
		if (! isTaskAvailable())
		{
			throw new Exception("Attempting to submit a task when no more threads are allowed");
		}
		log.debug("Creating new futureTask");
		FutureTask<TaskResult> futureTask = new FutureTask<TaskResult>(this.taskFactory.newTask(message));
		for(int i=0; i < this.threadCount; i++)
		{
			if (futureList.get(i) == null)
			{
				log.debug("Setting task to task slot " + i);				
				futureList.set(i, futureTask);
				this.store.put(i, this.messenger.createMemento(message));
				log.debug("Running futureTask");
				futureTask.run();
				return;
			}
		}
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public long getPollInterval() {
		return this.pollInterval;
	}

	public int getThreadCount() {
		return this.threadCount;
	}

	public Set<String> getQueueList()
	{
		return this.queueList;
	}
	
	public boolean isThreadActive(int thread) {
		if (thread < futureList.size() && futureList.get(thread) != null)
		{
			return true;
		}
		return false;
	}

	public Set<String> getJobTypeList() {
		return this.taskFactory.getJobTypeList();
	}

	public void setMementoStore(MementoStore store) {
		this.store = store;
		
	}
	
	private void initializeMementoStore() throws Exception
	{
		//Look for anything left over and send error responses
		Map<Integer, Memento> mementoMap = this.store.getMementoMap();
		if (! mementoMap.isEmpty())
		{
			log.debug("MementoStore contains " + mementoMap.size() + " mementos");
			for(Integer key : mementoMap.keySet())
			{
				log.debug(MessageFormat.format("Sending an error response for memento with key {0}", key));
				TaskResult result = new TaskResult();
				result.error = "A fatal error occurred in the service processing this task";
				this.messenger.sendResponseMessage(mementoMap.get(key), result);
				this.store.delete(key);
			}
		}
	}

	public MementoStore getMementoStore() {
		return this.store;
	}
}
