package gov.loc.repository.packagemodeler.events.packge.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.packge.PackageAcceptedEvent;

@Entity(name="PackageAcceptedEvent")
@DiscriminatorValue("packageaccepted")
public class PackageAcceptedEventImpl extends PackageEventImpl implements PackageAcceptedEvent {

}
