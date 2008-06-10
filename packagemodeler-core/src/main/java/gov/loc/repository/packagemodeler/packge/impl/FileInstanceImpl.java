package gov.loc.repository.packagemodeler.packge.impl;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;

import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.FixityHelper;

@Entity(name="FileInstance")
@Table(name = "fileinstance", schema="core", uniqueConstraints={@UniqueConstraint(columnNames={"filelocation_key","relative_path","base_name","extension"})})
public class FileInstanceImpl implements FileInstance {

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
	
	@Column(name="create_timestamp", nullable = false)
	private Date createTimestamp;

	@Column(name="update_timestamp", nullable = false)
	private Date updateTimestamp;
		
	@Column(name="file_create_timestamp", nullable=true)
	private Date fileCreateTimestamp; 
		
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity=FileLocationImpl.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name="filelocation_key", nullable = false)
	private FileLocation fileLocation;
	
	@Embedded	
	private FileName fileName;
	
	@CollectionOfElements
	@JoinTable( name = "fileinstance_fixity", schema="core", joinColumns = @JoinColumn (name = "fileinstance_key"), uniqueConstraints={@UniqueConstraint(columnNames={"fileinstance_key","algorithm"})})
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)	
	Set<Fixity> fixitySet = new HashSet<Fixity>();
	
	public FileName getFileName() {
		return this.fileName;
	}

	public Long getKey() {
		return this.key;
	}

	public FileLocation getFileLocation() {
		return this.fileLocation;
	}

	public void setFileCreateTimestamp(Date fileCreateTimestamp) {
		this.fileCreateTimestamp = fileCreateTimestamp;
	}

	public Date getFileCreateTimestamp() {
		return this.fileCreateTimestamp;
	}	
	
	public void setFileName(FileName fileName) {
		this.fileName = fileName;
	}

	public void setFileLocation(FileLocation location) {
		this.fileLocation = location;
		location.getFileInstances().add(this);
	}

	public Fixity getFixity(FixityAlgorithm algorithm) {
		for(Fixity fixity : this.fixitySet)
		{
			if (fixity.getFixityAlgorithm().equals(algorithm))
			{
				return fixity;
			}
		}
		return null;
	}	
	
	public Set<Fixity> getFixities() {
		return this.fixitySet;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof FileInstance))
		{
			return false;
		}
		//Same just in case file name is the same at least one equal fixity and no conflicting fixities
		FileInstance fileInstance = (FileInstance)obj;
		if ((this.getFileName() == null && fileInstance.getFileName() != null) || (! this.getFileName().equals(fileInstance.getFileName())))
		{
			return false;
		}
		return FixityHelper.isConsistent(fileInstance.getFixities(), this.fixitySet);
	}
	
	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}
	
	public boolean matches(FileInstance otherFileInstance) {
		if (this.fixitySet.isEmpty() && otherFileInstance.getFixities().isEmpty())
		{
			return true;
		}
		boolean matchFound = false;
		for(Fixity fixity : this.fixitySet)
		{
			Fixity otherFixity = otherFileInstance.getFixity(fixity.getFixityAlgorithm()); 
			if (otherFixity != null)
			{
				if (otherFixity.getValue().equals(fixity.getValue()))
				{
					matchFound = true;
				}
				else
				{
					return false;
				}
			}
		}
		return matchFound;
	}
	
	public boolean matches(FileExamination fileExamination) {
		//Returns true if this doesn't have fixities or fileExamination has fixities and at least one matches and no fixity mismatches.	 *
		if (this.fixitySet.isEmpty())
		{
			return true;
		}
		if (fileExamination.getFixities().isEmpty())
		{
			return false;
		}
		boolean matchFound = false;
		for(Fixity fixity : this.fixitySet)
		{
			Fixity otherFixity = fileExamination.getFixity(fixity.getFixityAlgorithm()); 
			if (otherFixity != null)
			{
				if (otherFixity.getValue().equals(fixity.getValue()))
				{
					matchFound = true;
				}
				else
				{
					return false;
				}
			}
		}
		return matchFound;
		
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("File Instance with filename {0} that is part of {1}", this.fileName.toString(), this.fileLocation.toString());
	}
}
