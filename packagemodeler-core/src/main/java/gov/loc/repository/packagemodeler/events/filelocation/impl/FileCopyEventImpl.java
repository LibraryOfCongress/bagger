package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.impl.FileLocationImpl;

@Entity(name="FileCopyEvent")
@DiscriminatorValue("filecopy")
public class FileCopyEventImpl extends FileLocationEventImpl implements FileCopyEvent
{
	@ManyToOne(targetEntity=FileLocationImpl.class)
	@JoinColumn(name="source_filelocation_key")
	private FileLocation fileLocationSource;
		
	public FileLocation getFileLocationSource() {
		return this.fileLocationSource;
	}

	public void setFileLocationSource(FileLocation source) {
		this.fileLocationSource = source;		
	}

}
