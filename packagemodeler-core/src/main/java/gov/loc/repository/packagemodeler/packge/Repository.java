package gov.loc.repository.packagemodeler.packge;

import java.util.Set;

import gov.loc.repository.Ided;
import gov.loc.repository.Keyed;

public interface Repository extends Keyed, Ided {

	public Set<Package> getPackages();

	public void addPackage(Package packge);

	public void removePackage(Package packge);

}