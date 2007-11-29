package gov.loc.repository.packagemodeler.events.filelocation;

public interface FileLocationStatusChange extends FileLocationEvent {
	public boolean isChangedToManaged();
	
	public boolean setChangedToManaged(boolean isChangedToManaged);
	
}
