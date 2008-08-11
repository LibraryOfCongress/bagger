package gov.loc.repository.packagemodeler.packge;

import java.util.List;
import java.util.Set;

import gov.loc.repository.Keyed;
import gov.loc.repository.Timestamped;
import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.events.packge.PackageEvent;

public interface Package extends Keyed, Timestamped {
	
	public abstract void setRepository(Repository repository);

	public abstract Repository getRepository();

	public abstract void setPackageId(String packageId);

	public abstract String getPackageId();
	
	public abstract Long getProcessInstanceId();
	
	public abstract void setProcessInstanceId(Long processInstanceId);

	public abstract void removeFileLocation(FileLocation fileLocation);
	
	public abstract void addFileLocation(FileLocation fileLocation);
	
	public abstract Set<FileLocation> getFileLocations();
		
	public abstract Set<PackageEvent> getPackageEvents();
	
	public abstract <T extends PackageEvent> Set<T> getPackageEvents(Class<T> eventType);
	
	public abstract void addPackageEvent(PackageEvent event);
	
	public abstract void removePackageEvent(PackageEvent event);
	
	public abstract <T extends PackageEvent> T getMostRecentPackageEvent(Class<T> eventType, boolean isSuccessOnly);

	public abstract List<Event> getEvents();
	
	public abstract <T extends Event> T getMostRecentEvent(Class<T> eventType, boolean isSuccessOnly);
			
	public abstract void addCanonicalFile(CanonicalFile file);

	public abstract Set<CanonicalFile> getCanonicalFiles();

	public abstract void removeCanonicalFile(CanonicalFile file);
	
	public abstract void removeCanonicalFiles();
	
	public abstract CanonicalFile findCanonicalFile(FileName fileName);
	
	public StorageSystemFileLocation getFileLocation(String storageServiceId, String basePath);
	
	public ExternalFileLocation getFileLocation(ExternalIdentifier externalIdentifier);

}