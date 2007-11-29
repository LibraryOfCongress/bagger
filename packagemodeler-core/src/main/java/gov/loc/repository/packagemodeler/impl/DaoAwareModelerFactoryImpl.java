package gov.loc.repository.packagemodeler.impl;

import java.util.Set;

import gov.loc.repository.packagemodeler.DaoAwareModelerFactory;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.FixityHelper;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;

public class DaoAwareModelerFactoryImpl extends ModelerFactoryImpl implements
		DaoAwareModelerFactory {

	protected PackageModelDAO dao;
	
	public <T extends Package> T createPackage(Class<T> packageType,
			String repositoryId, String packageId) throws Exception {
		return this.createPackage(packageType, dao.findRequiredRepository(repositoryId), packageId);
	}

	public void setPackageModelerDao(PackageModelDAO dao) {
		this.dao = dao;

	}

	public FileInstance createFileInstance(Package packge, String storageSystemId, String basePath, boolean isManaged, boolean isLCPackageStructure, FileName fileName, Set<Fixity> fixitySet) throws Exception {
		FileLocation fileLocation = packge.getFileLocation(storageSystemId, basePath);
		if (fileLocation == null)
		{
			System storageSystem = dao.findRequiredAgent(System.class, storageSystemId);			
			fileLocation = this.createStorageSystemFileLocation(packge, storageSystem, basePath, isManaged, isLCPackageStructure);
		}
		return this.createFileInstance(fileLocation, fileName, fixitySet);
	}

	public FileInstance createFileInstance(Package packge, String storageSystemId, String basePath, boolean isManaged, boolean isLCPackageStructure, FileName fileName, Fixity fixity) throws Exception {
		return this.createFileInstance(packge, storageSystemId, basePath, isManaged, isLCPackageStructure, fileName, FixityHelper.createFixitySet(fixity));
	}

	public StorageSystemFileLocation createStorageSystemFileLocation(Package packge, String storageSystemId, String basePath, boolean isManaged, boolean isLCPackageStructure) throws Exception {
		System storageSystem = dao.findRequiredAgent(System.class, storageSystemId);			
		return this.createStorageSystemFileLocation(packge, storageSystem, basePath, isManaged, isLCPackageStructure);
	}

	
}
