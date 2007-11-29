package gov.loc.repository.packagemodeler.packge;

import gov.loc.repository.Keyed;
import gov.loc.repository.Timestamped;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;

import java.util.Date;
import java.util.Set;

public interface FileInstance extends Keyed, Timestamped {

	public abstract FileLocation getFileLocation();
	
	public abstract void setFileLocation(FileLocation location);
	
	public abstract Date getFileCreateTimestamp();
	
	public abstract void setFileCreateTimestamp(Date fileCreateTimestamp);
		
	public abstract void setFileName(FileName fileName);
	
	public abstract FileName getFileName();
	
	/*
	 * A FileInstance with no fixities is considered changeable.
	 */
	public abstract Set<Fixity> getFixities();
	
	public abstract Fixity getFixity(Algorithm algorithm);
	
	/*
	 * Returns true if neither have fixities or there is at least one fixity match and no fixity mismatches.
	 */
	public abstract boolean matches(FileInstance otherFileInstance);
		
	
	/*
	 * Returns true if this doesn't have fixities or fileExamination has fixities and at least one matches and no fixity mismatches.	 * 
	 */
	public abstract boolean matches(FileExamination fileExamination);
}
