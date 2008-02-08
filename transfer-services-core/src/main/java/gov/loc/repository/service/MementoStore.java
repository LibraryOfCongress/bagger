package gov.loc.repository.service;

import java.util.Map;

public interface MementoStore {
	public void put(int key, Memento memento) throws Exception;
	
	public void delete(int key) throws Exception;
	
	public Memento get(int key) throws Exception;
	
	public Map<Integer,Memento> getMementoMap() throws Exception;
}
