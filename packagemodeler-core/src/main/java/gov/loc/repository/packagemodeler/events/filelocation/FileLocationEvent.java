package gov.loc.repository.packagemodeler.events.filelocation;

import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.packge.FileLocation;

public interface FileLocationEvent extends Event {

	public FileLocation getFileLocation();
	
	public void setFileLocation(FileLocation fileLocation);
	
}
