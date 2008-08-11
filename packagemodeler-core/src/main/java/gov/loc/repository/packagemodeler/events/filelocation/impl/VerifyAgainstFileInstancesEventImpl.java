package gov.loc.repository.packagemodeler.events.filelocation.impl;

import gov.loc.repository.packagemodeler.events.filelocation.VerifyAgainstFileInstancesEvent;

import javax.persistence.*;

@Entity(name="VerifyAgainstFileInstancesEvent")
@DiscriminatorValue("verifyagainstfileinstances")
public class VerifyAgainstFileInstancesEventImpl extends FileLocationEventImpl implements VerifyAgainstFileInstancesEvent
{
}
