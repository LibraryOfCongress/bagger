package gov.loc.repository.service;

import java.util.Set;
import java.util.concurrent.Callable;

public interface TaskFactory {
	public Callable<TaskResult> newTask(RequestMessage requestMessage) throws Exception;
	
	/*
	 * Returns a list of jobTypes that the TaskFactory can process.
	 */
	public Set<String> getJobTypeList();
}
