package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.filelocation.FileDeleteEvent;

@Entity(name="FileDeleteEvent")
@DiscriminatorValue("filedelete")
public class FileDeleteEventImpl extends FileLocationEventImpl implements FileDeleteEvent
{
}
