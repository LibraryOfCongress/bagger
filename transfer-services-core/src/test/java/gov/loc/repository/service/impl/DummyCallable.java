package gov.loc.repository.service.impl;

import gov.loc.repository.service.TaskResult;

import java.util.concurrent.Callable;

public interface DummyCallable extends Callable<TaskResult> {

	public void setFoo(String foo);
	
	public void setBar(String bar);
}
