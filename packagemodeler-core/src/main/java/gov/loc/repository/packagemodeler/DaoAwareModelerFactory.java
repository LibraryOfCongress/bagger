package gov.loc.repository.packagemodeler;

import java.util.Set;

import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;

public interface DaoAwareModelerFactory extends ModelerFactory {

	public void setPackageModelerDao(PackageModelDAO dao);
	
	public <T extends Package> T createPackage(Class<T> packageType, String repositoryId, String packageId) throws Exception;
	
	public abstract FileInstance createFileInstance(Package packge, String storageSystemId, String basePath, boolean isManaged, boolean isLCPackageStructure, FileName fileName, Set<Fixity> fixitySet) throws Exception;

	public abstract FileInstance createFileInstance(Package packge, String storageSystemId, String basePath, boolean isManaged, boolean isLCPackageStructure, FileName fileName, Fixity fixity) throws Exception;
	
	public abstract StorageSystemFileLocation createStorageSystemFileLocation(Package packge, String storageSystemId, String basePath, boolean isManaged, boolean isLCPackageStructure) throws Exception;
	
}
