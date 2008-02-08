package gov.loc.repository.service.impl;

import gov.loc.repository.service.Memento;

public class DummyMemento implements Memento
{

	private static final long serialVersionUID = 8475882821724464793L;

	private String jobType="foo";
	public int key;

	public DummyMemento() {
	}		
	
	public String getJobType() {
		return this.jobType;
	}
	
}