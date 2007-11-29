package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.filelocation.VerifyEvent;

@Entity(name="VerifyEvent")
@DiscriminatorValue("verify")
public class VerifyEventImpl extends FileLocationEventImpl implements VerifyEvent
{
}
