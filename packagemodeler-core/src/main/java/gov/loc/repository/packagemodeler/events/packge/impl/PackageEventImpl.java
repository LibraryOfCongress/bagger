package gov.loc.repository.packagemodeler.events.packge.impl;

import java.text.DateFormat;
import java.text.MessageFormat;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.impl.EventImpl;
import gov.loc.repository.packagemodeler.events.packge.PackageEvent;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.impl.PackageImpl;

@Entity(name="PackageEvent")
@Table(name = "event_package", schema="core")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PackageEventImpl extends EventImpl implements PackageEvent {

	@ManyToOne(targetEntity=PackageImpl.class )	
    @JoinColumn(name="package_key", nullable=false)
	private Package packge;
	
	public void setPackage(Package packge) {
		this.packge = packge;
	}

	public Package getPackage() {
		return packge;
	}

	@Override
	protected String getPremisLinkingObjectIdentifierValueText() {
		return "info:loc-repo/entity/package/" + this.packge.getRepository().getId() + "/" + this.packge.getPackageId();
	}
	
	@Override
	public String toString() {
		String msg = MessageFormat.format("Package Event of type {0}, associated with {1}, ", this.getClass().getName(), this.packge.toString());
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
