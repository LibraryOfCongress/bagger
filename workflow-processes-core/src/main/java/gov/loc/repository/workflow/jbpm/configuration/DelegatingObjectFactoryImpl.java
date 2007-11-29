package gov.loc.repository.workflow.jbpm.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jbpm.configuration.ObjectFactory;
import org.jbpm.configuration.ObjectFactoryImpl;

import java.util.Map;
import java.util.HashMap;

public class DelegatingObjectFactoryImpl implements ObjectFactory {

	private static final long serialVersionUID = 1L; 
	private static final Log log = LogFactory.getLog(DelegatingObjectFactoryImpl.class);
	
	private ObjectFactoryImpl factory;
	private Map<String, Object> objMap = new HashMap<String, Object>();
	
	public DelegatingObjectFactoryImpl(ObjectFactoryImpl factory) {
		this.factory = factory;
	}
	
	public Object createObject(String name) {
		log.debug("Requested object: " + name);
		System.out.println("Requested object: " + name);
		if (this.objMap.containsKey(name))
		{
			System.out.println("Object found");
			return this.objMap.get(name);
		}
		return this.factory.createObject(name);
		
	}

	public boolean hasObject(String name) {
		return this.factory.hasObject(name);
	}
	
	public void registerObject(String name, Object obj)
	{
		objMap.put(name, obj);
	}

	public void clear()
	{
		objMap.clear();
	}
}
