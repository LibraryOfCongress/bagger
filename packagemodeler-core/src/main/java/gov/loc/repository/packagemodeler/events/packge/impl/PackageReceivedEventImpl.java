package gov.loc.repository.packagemodeler.events.packge.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.packge.PackageReceivedEvent;

@Entity(name="PackageReceivedEvent")
@DiscriminatorValue("packagereceived")
public class PackageReceivedEventImpl extends PackageEventImpl implements PackageReceivedEvent {

}
