package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.events.impl.EventImpl;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.impl.FileLocationImpl;

@Entity(name="FileLocationEvent")
@Table(name = "event_file_location", schema="core")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class FileLocationEventImpl extends EventImpl implements
		FileLocationEvent {

	@ManyToOne(targetEntity=FileLocationImpl.class )	
    @JoinColumn(name="file_location_key", nullable=false)
	private FileLocation fileLocation;
		
	public FileLocation getFileLocation() {
		return this.fileLocation;
	}

	public void setFileLocation(FileLocation fileLocation) {
		this.fileLocation = fileLocation;
	}

	@Override
	protected String getPremisLinkingObjectIdentifierValueText() {
		return "info:loc-repo/entity/filelocation/" + this.fileLocation.getKey();
	}	


}
