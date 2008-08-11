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
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.FixityHelper;

@Entity(name="FileExamination")
@Table(name = "fileexamination", schema="core", uniqueConstraints={@UniqueConstraint(columnNames={"fileexamination_group_key","relative_path","base_name","extension"})})
public class FileExaminationImpl implements FileExamination {

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
	
	@Column(name="create_timestamp", nullable = false)
	private Date createTimestamp;

	@Column(name="update_timestamp", nullable = false)
	private Date updateTimestamp;
		
	@Column(name="bytes", nullable = true)
	private Long bytes;

	@Embedded
	private FileName fileName;	

	@Column(name="file_modified_timestamp", nullable=true)
	private Date fileModifiedTimestamp; 

	@CollectionOfElements
	@JoinTable( name = "fileexamination_fixity", schema="core", joinColumns = @JoinColumn (name = "fileexamination_key"), uniqueConstraints={@UniqueConstraint(columnNames={"fileexamination_key","algorithm"})})
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)	
	Set<Fixity> fixitySet = new HashSet<Fixity>();
			
	@ManyToOne(targetEntity=FileExaminationGroupImpl.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name="fileexamination_group_key", nullable = false)
	private FileExaminationGroup fileExaminationGroup;	
	
	public Long getBytes() {
		return this.bytes;
	}
		
	public FileName getFileName() {
		return this.fileName;
	}
		
	public Long getKey() {
		return this.key;
	}

	public Date getFileModifiedTimestamp() {
		return this.fileModifiedTimestamp;
	}

	public void setBytes(Long size) {
		this.bytes = size;

	}

	public void setFileName(FileName fileName) {
		this.fileName = fileName;
	}

	public void setFileModifiedTimestamp(Date modifiedDate) {
		this.fileModifiedTimestamp = modifiedDate;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof FileExamination))
		{
			return false;
		}
		//Same just in case file name is the same at least one equal fixity and no conflicting fixities
		FileExamination fileObservation = (FileExamination)obj;
		if ((this.getFileName() == null && fileObservation.getFileName() != null) || (! this.getFileName().equals(fileObservation.getFileName())))
		{
			return false;
		}
		return FixityHelper.isConsistent(fileObservation.getFixities(), this.fixitySet);
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
	
	public FileExaminationGroup getFileExaminationGroup() {
		return this.fileExaminationGroup;
	}

	public void setFileExaminationGroup(FileExaminationGroup fileExaminationGroup) {
		this.fileExaminationGroup = fileExaminationGroup;		
	}
	
	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}
	
	@Override
	public String toString() {		
		return MessageFormat.format("File Examination with filename {0} that is part of {1}", this.fileName.toString(), this.fileExaminationGroup.toString());
	}
}
