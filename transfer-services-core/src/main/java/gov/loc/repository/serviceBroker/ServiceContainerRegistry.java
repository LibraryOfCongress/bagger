package gov.loc.repository.serviceBroker;

import java.util.List;

public interface ServiceContainerRegistry {
	public void register(String serviceUrl);
	
	public void unregister(String serviceUrl);
	
	public List<String> listServiceContainers();
	
}
