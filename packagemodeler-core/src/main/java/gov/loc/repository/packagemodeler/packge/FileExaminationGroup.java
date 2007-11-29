package gov.loc.repository.packagemodeler.packge;

import java.util.Set;

import gov.loc.repository.Keyed;
import gov.loc.repository.Timestamped;
import gov.loc.repository.packagemodeler.events.fileexaminationgroup.FileExaminationGroupEvent;

public interface FileExaminationGroup extends Timestamped, Keyed {
	public FileLocation getFileLocation();
	
	public void setFileLocation(FileLocation fileLocation);
	
	public abstract void addFileExamination(FileExamination fileExamination);
	
	public abstract Set<FileExamination> getFileExaminations();

	public abstract void removeFileExamination(FileExamination fileExamination);

	/*
	 * Indicates whether every file was examined or if only a select subset.
	 * If a FileExaminationGroup is complete then the absence of a FileExamination for a file indicates that the file is missing.
	 * If a FileExaminationGroup then the absence of a FileExamination for a file indicates that the file wasn't examined.
	 * If, however, a file that was examined, but was missing, will be indicated by a FileExamination that is false for isPresent(). 
	*/
	public abstract boolean isComplete();
	
	public abstract void setComplete(boolean isComplete);
	
	public abstract Set<FileExaminationGroupEvent> getFileExaminationGroupEvents();
	
	public abstract <T extends FileExaminationGroupEvent> Set<T> getFileExaminationGroupEvents(Class<T> eventType);
	
	public abstract void addFileExaminationGroupEvent(FileExaminationGroupEvent event);
	
	public abstract void removeFileExaminationGroupEvent(FileExaminationGroupEvent event);
	
	public abstract <T extends FileExaminationGroupEvent> T getMostRecentFileExaminationGroupEvent(Class<T> eventType, boolean isSuccessOnly);
	
}
