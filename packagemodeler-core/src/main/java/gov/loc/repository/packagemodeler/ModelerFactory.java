package gov.loc.repository.packagemodeler;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import gov.loc.repository.bagit.manifest.ManifestReader;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.events.fileexaminationgroup.FileExaminationGroupEvent;
import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.events.packge.PackageEvent;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;

public interface ModelerFactory {
	public Repository createRepository(String repositoryId) throws Exception;
	
	public <T extends Package> T createPackage(Class<T> packageType, Repository repository, String packageId) throws Exception;
	
	public abstract <T extends PackageEvent> T createPackageEvent(Class<T> eventType, Package packge, Date eventStart, Agent reportingAgent) throws Exception;

	public abstract CanonicalFile createCanonicalFile(Package packge, FileName fileName, Set<Fixity> fixitySet);

	public abstract CanonicalFile createCanonicalFile(Package packge, FileName fileName, Fixity fixity);
	
	public abstract Collection<CanonicalFile> createCanonicalFilesFromBagManifest(Package packge, File bagManifest) throws Exception;

	public abstract Collection<CanonicalFile> createCanonicalFilesFromBagManifests(Package packge, List<File> bagManifests) throws Exception;
	
	public abstract Collection<CanonicalFile> createCanonicalFilesFromFileInstances(Package packge, Collection<FileInstance> fileInstanceCollection) throws Exception;
	
	public abstract StorageSystemFileLocation createStorageSystemFileLocation(Package packge, System storageSystem, String basePath, boolean isManaged, boolean isLCPackageStructure);
	
	public abstract ExternalFileLocation createExternalFileLocation(Package packge, MediaType mediaType, ExternalIdentifier externalIdentifier, String basePath, boolean isManaged, boolean isLCPackageStructure);
	
	public abstract <T extends FileLocationEvent> T createFileLocationEvent(Class<T> eventType, FileLocation fileLocation, Date eventStart, Agent reportingAgent) throws Exception;
	
	public abstract FileInstance createFileInstance(FileLocation fileLocation, FileName fileName, Set<Fixity> fixitySet);
	
	public abstract FileInstance createFileInstance(FileLocation fileLocation, FileName fileName, Fixity fixity);

	/*
	 * A FileInstance without a fixity is allowed to change.
	 */
	public abstract FileInstance createFileInstance(FileLocation fileLocation, FileName fileName);
		
	public abstract Collection<FileInstance> createFileInstancesFromCanonicalFiles(FileLocation fileLocation, Collection<CanonicalFile> canonicalFileCollection) throws Exception;

	public abstract Collection<FileInstance> createFileInstancesFromFileExaminations(FileLocation fileLocation, Collection<FileExamination> fileExaminationCollection) throws Exception;

	public abstract Collection<FileInstance> createFileInstances(FileLocation fileLocation, ManifestReader reader) throws Exception;
	
	public abstract Collection<FileInstance> createFileInstancesFromBagManifest(FileLocation fileLocation, File bagManifest) throws Exception;

	public abstract Collection<FileInstance> createFileInstancesFromBagManifests(FileLocation fileLocation, List<File> bagManifests) throws Exception;

	public abstract Collection<FileInstance> createFileInstancesFromBagTagManifest(FileLocation fileLocation, File tagManifest) throws Exception;

	public abstract Collection<FileInstance> createFileInstancesFromBagTagManifests(FileLocation fileLocation, List<File> tagManifests) throws Exception;
	
	
	public abstract FileExaminationGroup createFileExaminationGroup(FileLocation fileLocation, boolean isComplete);
	
	public abstract <T extends FileExaminationGroupEvent> T createFileExaminationGroupEvent(Class<T> eventType, FileExaminationGroup fileExaminationGroup, Date eventStart, Agent reportingAgent) throws Exception;
	
	public abstract FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName, Set<Fixity> fixitySet);
	
	public abstract FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName, Fixity fixity);

	public abstract FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName);
	
	public <T extends Agent> T createAgent(Class<T> agentType, String agentId) throws Exception;
	
	public abstract Role createRole(String roleId);
}
