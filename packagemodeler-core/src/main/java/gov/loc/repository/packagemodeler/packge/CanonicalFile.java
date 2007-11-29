package gov.loc.repository.packagemodeler.packge;

import gov.loc.repository.Keyed;
import gov.loc.repository.Timestamped;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;

import java.util.Set;

public interface CanonicalFile extends Keyed, Timestamped {

	public abstract FileName getFileName();
	
	public abstract void setFileName(FileName fileName); 
	
	public abstract Set<Fixity> getFixities();
	
	public abstract Fixity getFixity(Algorithm algorithm);
	
	public abstract void setBytes(Long size);

	public abstract Long getBytes();
	
	public abstract void setPackage(Package packge);

	public abstract Package getPackage();
	
}