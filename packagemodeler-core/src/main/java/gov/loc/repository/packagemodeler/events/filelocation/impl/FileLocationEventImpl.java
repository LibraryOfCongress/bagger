package gov.loc.repository.packagemodeler.events.filelocation.impl;

import java.text.DateFormat;
import java.text.MessageFormat;

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

	@Override
	public String toString() {
		String msg = MessageFormat.format("File Location Event of type {0}, associated with {1}, ", this.getClass().getName(), this.fileLocation.toString());
		if (this.isUnknownEventStart())
		{
			msg += "and with an unknown event start";
		}
		else
		{
			msg += "and with an event start of " + DateFormat.getDateTimeInstance().format(this.getEventStart());
		}
		return msg;
	}
	

}
