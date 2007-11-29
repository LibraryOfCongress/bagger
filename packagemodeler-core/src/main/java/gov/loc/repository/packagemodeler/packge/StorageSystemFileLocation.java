package gov.loc.repository.packagemodeler.packge;

import gov.loc.repository.packagemodeler.agents.System;

public interface StorageSystemFileLocation extends FileLocation {
	public abstract void setStorageSystem(System storageSystem);

	public abstract System getStorageSystem();


}
