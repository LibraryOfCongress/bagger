package gov.loc.repository.packagemodeler.packge.impl;

import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.Size;

@Entity(name="CanonicalFile")
@Table(name = "canonicalfile", schema="core", uniqueConstraints={@UniqueConstraint(columnNames={"package_key","relative_path","base_name","extension"})})
public class CanonicalFileImpl implements CanonicalFile {

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
	
	@Column(name="create_timestamp", nullable = false)
	private Date createTimestamp;

	@Column(name="update_timestamp", nullable = false)
	private Date updateTimestamp;
		
	@Column(name="bytes", nullable = true)
	private Long bytes;
	
	@ManyToOne(targetEntity=PackageImpl.class )	
	@JoinColumn(name="package_key", nullable = false)
	private Package packge;

	@Embedded
	private FileName fileName;
	
	@CollectionOfElements
	@JoinTable( name = "canonicalfile_fixity", schema="core", joinColumns = @JoinColumn (name = "canonicalfile_key"), uniqueConstraints={@UniqueConstraint(columnNames={"canonicalfile_key","algorithm"})})
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)	
	@Size(min=1)
	Set<Fixity> fixitySet = new HashSet<Fixity>();
	
	public Long getBytes() {
		return this.bytes;
	}

	public void setBytes(Long size) {
		this.bytes = size;

	}
	
	public Long getKey() {
		return this.key;
	}
		
	public Package getPackage() {
		return this.packge;
	}	
	
	public void setPackage(Package packge) {
		this.packge = packge;
	}

	public FileName getFileName() {
		return this.fileName;
	}
	
	public void setFileName(FileName fileName) {
		this.fileName = fileName;	
	}
	
	public Fixity getFixity(Algorithm algorithm) {
		for(Fixity fixity : this.fixitySet)
		{
			if (fixity.getAlgorithm().equals(algorithm))
			{
				return fixity;
			}
		}
		return null;
	}	
	
	public Set<Fixity> getFixities() {
		return this.fixitySet;
	}

	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}
		
	/*
	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof CanonicalFile))
		{
			return false;
		}
		//Same just in case file name is the same at least one equal fixity and no conflicting fixities
		CanonicalFile canonicalFile = (CanonicalFile)obj;
		
		if ((this.getFileName() == null && canonicalFile.getFileName() != null) || (! this.getFileName().equals(canonicalFile.getFileName())))
		{
			return false;
		}
		return FixityHelper.isConsistent(canonicalFile.getFixities(), this.fixitySet);
	}
	*/
	
	@Override
	public String toString() {
		return MessageFormat.format("Canonical File with filename {0} that is part of {1}", this.fileName.toString(), this.packge.toString());
	}
}
