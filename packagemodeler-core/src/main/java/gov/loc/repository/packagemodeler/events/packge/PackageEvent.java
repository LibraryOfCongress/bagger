package gov.loc.repository.packagemodeler.events.packge;

import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.packge.Package;

public interface PackageEvent extends Event {
	public abstract void setPackage(Package packge);

	public abstract Package getPackage();


}
