package gov.loc.repository.service;

import java.util.Set;

public interface Messenger {
		
	public RequestMessage getNextRequestMessage() throws Exception;
	
	public void sendResponseMessage(Memento memento, TaskResult taskResult) throws Exception;

	public void start(Set<String> jobTypeList) throws Exception;
	
	public void stop() throws Exception;
	
	public Memento createMemento(RequestMessage message) throws Exception;
		
}
