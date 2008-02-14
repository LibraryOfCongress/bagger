package gov.loc.repository.service;

import java.util.Set;


public interface Service {

	public enum Status {STOPPED, RUNNING, STOPPING};	
	
	public abstract void start() throws Exception;

	public abstract void stop();

	public abstract void setTaskFactory(TaskFactory taskFactory);

	public abstract void setMessenger(Messenger messenger);
	
	public abstract void setMementoStore(MementoStore store);

	public abstract MementoStore getMementoStore();
	
	public Status getStatus();
	
	public long getPollInterval();
	
	public int getThreadCount();
	
	public boolean isThreadActive(int thread);
	
	public Set<String> getJobTypeList();

	public Set<String> getQueueList();
}