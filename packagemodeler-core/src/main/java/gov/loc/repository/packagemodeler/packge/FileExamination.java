package gov.loc.repository.packagemodeler.packge;

import gov.loc.repository.Keyed;
import gov.loc.repository.Timestamped;
import gov.loc.repository.fixity.FixityAlgorithm;

import java.util.Date;
import java.util.Set;

public interface FileExamination extends Keyed, Timestamped {

	public abstract FileName getFileName();
	
	public abstract void setFileName(FileName fileName); 
			
	public abstract void setBytes(Long size);

	public abstract Long getBytes();

	public abstract Date getFileModifiedTimestamp();
	
	public abstract void setFileModifiedTimestamp(Date fileModifiedTimestamp);
	
	public abstract FileExaminationGroup getFileExaminationGroup();
	
	public abstract void setFileExaminationGroup(FileExaminationGroup fileExaminationGroup);
		
	/*
	 * The absence of Fixities indicates that the file is not present or can't be read.
	 */
	public abstract Set<Fixity> getFixities();
	
	public abstract Fixity getFixity(FixityAlgorithm algorithm);
	
	
}
