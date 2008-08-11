package gov.loc.repository.packagemodeler.events.fileexaminationgroup;

import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;

public interface FileExaminationGroupEvent extends Event {
	public FileExaminationGroup getFileExaminationGroup();
	
	public void setFileExaminationGroup(FileExaminationGroup fileExaminationGroup);
}
