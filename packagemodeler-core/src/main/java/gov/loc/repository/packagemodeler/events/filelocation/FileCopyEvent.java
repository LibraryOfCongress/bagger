package gov.loc.repository.packagemodeler.events.filelocation;

import gov.loc.repository.packagemodeler.packge.FileLocation;

/*
 * An File Location Event that records the copying of files from one File Location to another File Location.
 * Note that the File Location that corresponds with getFileLocation()/setFileLocation() is the destination of the copy.
 * Note that files may have been added, excluded, or modified as part of the copy.
 * The File Instances of the destination File Location represents the outcome of copy.
 */
public interface FileCopyEvent extends FileLocationEvent {

	public abstract void setFileLocationSource(FileLocation source);

	public abstract FileLocation getFileLocationSource();
	
}