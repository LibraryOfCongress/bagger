package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.filelocation.FileLocationAnomalyEvent;

@Entity(name="FileLocationAnomalyEvent")
@DiscriminatorValue("filelocationanomaly")
public class FileLocationAnomalyEventImpl extends FileLocationEventImpl implements FileLocationAnomalyEvent
{
}
