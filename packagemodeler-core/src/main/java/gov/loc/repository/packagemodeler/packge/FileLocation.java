package gov.loc.repository.packagemodeler.packge;

import java.util.List;
import java.util.Set;

import gov.loc.repository.Keyed;
import gov.loc.repository.Timestamped;
import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.packge.Package;

public abstract interface FileLocation extends Keyed, Timestamped {

	public abstract void setBasePath(String basePath);

	public abstract String getBasePath();
		
	public abstract void addFileInstance(FileInstance fileInstance);
	
	public abstract Set<FileInstance> getFileInstances();
		
	public abstract FileInstance findFileInstance(FileName fileName);
	
	public abstract Package getPackage();
	
	public abstract void setPackage(Package packge);

	public abstract List<FileExaminationGroup> getFileExaminationGroups();
	
	public abstract void addFileExaminationGroup(FileExaminationGroup fileExaminationGroup);
	
	public abstract void removeFileExaminationGroup(FileExaminationGroup fileExaminationGroup);
	
	public abstract Set<FileLocationEvent> getFileLocationEvents();
	
	public abstract <T extends FileLocationEvent> Set<T> getFileLocationEvents(Class<T> eventType);
	
	public abstract void addFileLocationEvent(FileLocationEvent event);
	
	public abstract void removeFileLocationEvent(FileLocationEvent event);
	
	public abstract <T extends FileLocationEvent> T getMostRecentFileLocationEvent(Class<T> eventType, boolean isSuccessOnly);
	
	public boolean isManaged();
	
	public void setManaged(boolean isManaged);
	
	public boolean isBag();
	
	public void setBag(boolean isBag);
}