package gov.loc.repository.service.impl;

import static org.junit.Assert.*;
import gov.loc.repository.service.Memento;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class FileSystemMementoStoreTest {

	private FileSystemMementoStore store;
	
	@Before
	public void setUp() throws Exception {
		store = new FileSystemMementoStore();
		FileUtils.cleanDirectory(store.getStoreDirectory());
	}

	@Test
	public void testStore() throws Exception {
		assertTrue(this.store.getMementoMap().isEmpty());
		DummyMemento memento1 = new DummyMemento();
		memento1.key = 1;
		this.store.put(memento1.key, memento1);
		assertEquals(1, this.store.getMementoMap().size());
		
		DummyMemento memento2 = new DummyMemento();
		memento2.key = 2;
		this.store.put(memento2.key, memento2);
		assertEquals(2, this.store.getMementoMap().size());
		
		Memento memento = this.store.get(1);
		assertTrue(memento instanceof DummyMemento);
		assertEquals(1, ((DummyMemento)memento).key);
		
		this.store.delete(1);
		assertEquals(1, this.store.getMementoMap().size());
	}
	
}
