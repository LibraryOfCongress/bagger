package gov.loc.repository.packagemodeler.events.packge.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.packge.PackageCancelledEvent;

@Entity(name="PackageCancelledEvent")
@DiscriminatorValue("packagecancelled")
public class PackageCancelledEventImpl extends PackageEventImpl implements PackageCancelledEvent {

}
