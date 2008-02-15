package gov.loc.repository.packagemodeler.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.agents.impl.RoleImpl;
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
import gov.loc.repository.packagemodeler.packge.FixityHelper;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.impl.CanonicalFileImpl;
import gov.loc.repository.packagemodeler.packge.impl.ExternalFileLocationImpl;
import gov.loc.repository.packagemodeler.packge.impl.FileExaminationGroupImpl;
import gov.loc.repository.packagemodeler.packge.impl.FileExaminationImpl;
import gov.loc.repository.packagemodeler.packge.impl.FileInstanceImpl;
import gov.loc.repository.packagemodeler.packge.impl.RepositoryImpl;
import gov.loc.repository.packagemodeler.packge.impl.StorageSystemFileLocationImpl;
import gov.loc.repository.utilities.FilenameHelper;
import gov.loc.repository.utilities.ManifestReader;
import gov.loc.repository.utilities.ManifestReader.FileFixity;

public class ModelerFactoryImpl implements ModelerFactory {

	private static final Log log = LogFactory.getLog(ModelerFactoryImpl.class);
	
	public <T extends Agent> T createAgent(Class<T> agentType, String agentId) throws Exception {
		Agent agent = (Agent)(Class.forName(getImplClassName(agentType))).newInstance();
		agent.setId(agentId);
		log.info("Created " + agent.toString());
		return agentType.cast(agent);
	}

	public CanonicalFile createCanonicalFile(Package packge, FileName fileName, Set<Fixity> fixitySet) {
		CanonicalFile canonicalFile = new CanonicalFileImpl();
		packge.addCanonicalFile(canonicalFile);
		canonicalFile.setFileName(fileName);
		canonicalFile.getFixities().addAll(fixitySet);
		log.info("Created " + canonicalFile.toString());		
		return canonicalFile;
	}

	public CanonicalFile createCanonicalFile(Package packge, FileName fileName, Fixity fixity) {
		return this.createCanonicalFile(packge, fileName,  FixityHelper.createFixitySet(fixity));
	}

	public Collection<CanonicalFile> createCanonicalFiles(Package packge, ManifestReader reader) throws Exception {
		if (! packge.getCanonicalFiles().isEmpty())
		{
			throw new Exception(packge.toString() + " already has canonical files.");
		}
		String root = null;
		while(reader.hasNext())
		{
			FileFixity fileFixity = reader.next();
			//Need to remove the root
			root = FilenameHelper.getRoot(fileFixity.getFile());
			String filename = FilenameHelper.removeBasePath(root, fileFixity.getFile());
			this.createCanonicalFile(packge, new FileName(filename), new Fixity(fileFixity.getFixityValue(), reader.getAlgorithm()));
		}
		reader.close();
		return packge.getCanonicalFiles();
	}	

	public Collection<CanonicalFile> createCanonicalFilesFromFileInstances(Package packge, Collection<FileInstance> fileInstanceCollection) throws Exception {
		if (! packge.getCanonicalFiles().isEmpty())
		{
			throw new Exception(packge.toString() + " already has canonical files.");
		}
		for(FileInstance fileInstance : fileInstanceCollection)
		{
			Set<Fixity> fixitySet = new HashSet<Fixity>();
			for(Fixity fixity : fileInstance.getFixities())
			{
				fixitySet.add(new Fixity(fixity.getValue(), fixity.getAlgorithm()));
			}
			//Filter out fileInstances with no fixities
			if (! fixitySet.isEmpty())
			{
				this.createCanonicalFile(packge, fileInstance.getFileName(), fixitySet);
			}
		}
		return packge.getCanonicalFiles();
	}
	
	
	public FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName, Set<Fixity> fixitySet) {
		FileExamination fileExamination = new FileExaminationImpl();
		fileExaminationGroup.addFileExamination(fileExamination);
		fileExamination.setFileName(fileName);
		fileExamination.getFixities().addAll(fixitySet);
		log.info("Created " + fileExamination.toString());				
		return fileExamination;
	}

	public FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName, Fixity fixity) {
		return this.createFileExamination(fileExaminationGroup, fileName, FixityHelper.createFixitySet(fixity));
	}

	public FileExaminationGroup createFileExaminationGroup(FileLocation fileLocation, boolean isComplete) {
		FileExaminationGroup fileExaminationGroup = new FileExaminationGroupImpl();
		fileLocation.addFileExaminationGroup(fileExaminationGroup);
		fileExaminationGroup.setComplete(isComplete);
		log.info("Created " + fileExaminationGroup.toString());
		return fileExaminationGroup;
	}

	public FileInstance createFileInstance(FileLocation fileLocation, FileName fileName, Set<Fixity> fixitySet) {
		FileInstance fileInstance = new FileInstanceImpl();
		fileLocation.addFileInstance(fileInstance);
		fileInstance.setFileName(fileName);
		if (fixitySet != null)
		{
			fileInstance.getFixities().addAll(fixitySet);
		}
		log.info("Created " + fileInstance.toString());
		return fileInstance;
	}

	public FileInstance createFileInstance(FileLocation fileLocation, FileName fileName, Fixity fixity) {
		return this.createFileInstance(fileLocation, fileName, FixityHelper.createFixitySet(fixity));
	}

	public FileInstance createFileInstance(FileLocation fileLocation, FileName fileName) {
		return this.createFileInstance(fileLocation, fileName, (Set<Fixity>)null);
	}	
	
	public Collection<FileInstance> createFileInstancesFromCanonicalFiles(FileLocation fileLocation, Collection<CanonicalFile> canonicalFileCollection) throws Exception {
		if (! fileLocation.getFileInstances().isEmpty())
		{
			throw new Exception(fileLocation.toString() + " already has File Instances.");
		}
		for(CanonicalFile canonicalFile : canonicalFileCollection)
		{
			Set<Fixity> fixitySet = new HashSet<Fixity>();
			for(Fixity fixity : canonicalFile.getFixities())
			{
				fixitySet.add(new Fixity(fixity.getValue(), fixity.getAlgorithm()));
			}
			this.createFileInstance(fileLocation, canonicalFile.getFileName(), fixitySet);
		}
		return fileLocation.getFileInstances();
	}
	
	public Collection<FileInstance> createFileInstancesFromFileExaminations(FileLocation fileLocation, Collection<FileExamination> fileExaminationCollection) throws Exception {
		if (! fileLocation.getFileInstances().isEmpty())
		{
			throw new Exception(fileLocation.toString() + " already has File Instances.");
		}
		for(FileExamination fileExamination : fileExaminationCollection)
		{			
			if (! fileExamination.getFixities().isEmpty())
			{
				Set<Fixity> fixitySet = new HashSet<Fixity>();
				if (! fileLocation.isLCPackageStructure() || ! (fileExamination.getFileName().getRelativePath() == null || fileExamination.getFileName().getRelativePath().length() == 0))
				{
					for(Fixity fixity : fileExamination.getFixities())
					{
						fixitySet.add(new Fixity(fixity.getValue(), fixity.getAlgorithm()));
					}
				}
				this.createFileInstance(fileLocation, fileExamination.getFileName(), fixitySet);				
			}
		}
		return fileLocation.getFileInstances();
	}	

	public Collection<FileInstance> createFileInstances(FileLocation fileLocation, ManifestReader reader) throws Exception {
		if (! fileLocation.getFileInstances().isEmpty())
		{
			throw new Exception(fileLocation.toString() + " already has File Instances");
		}
		String root = null;
		while(reader.hasNext())
		{
			FileFixity fileFixity = reader.next();
			//Need to remove the root
			root = FilenameHelper.getRoot(fileFixity.getFile());
			String filename = FilenameHelper.removeBasePath(root, fileFixity.getFile());
			this.createFileInstance(fileLocation, new FileName(filename), new Fixity(fileFixity.getFixityValue(), reader.getAlgorithm()));
		}
		reader.close();
		return fileLocation.getFileInstances();
	}	
	
	
	public FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName) {
		FileExamination fileExamination = new FileExaminationImpl();		
		fileExaminationGroup.addFileExamination(fileExamination);
		fileExamination.setFileName(fileName);
		log.info("Created " + fileExamination.toString());
		return fileExamination;
		
	}

	public <T extends Package> T createPackage(Class<T> packageType, Repository repository, String packageId) throws Exception
	{
		Package packge = (Package)(Class.forName(getImplClassName(packageType))).newInstance();
		repository.addPackage(packge);
		packge.setPackageId(packageId);
		log.info("Created " + packge.toString());
		return packageType.cast(packge);
		
	}

	public <T extends PackageEvent> T createPackageEvent(Class<T> eventType, Package packge, Date eventStart, Agent reportingAgent) throws Exception {
		PackageEvent event = (PackageEvent)(Class.forName(getImplClassName(eventType))).newInstance();		
		packge.addPackageEvent(event);
		event.setEventStart(eventStart);
		event.setReportingAgent(reportingAgent);
		log.info("Created " + event.toString());
		return eventType.cast(event);
	}

	public Repository createRepository(String repositoryId) throws Exception {
		Repository repository = new RepositoryImpl();
		repository.setId(repositoryId);
		log.info("Created " + repository.toString());
		return repository;
	}

	public StorageSystemFileLocation createStorageSystemFileLocation(Package packge, System storageSystem, String basePath, boolean isManaged, boolean isLCPackageStructure) {
		StorageSystemFileLocation fileLocation = new StorageSystemFileLocationImpl();
		packge.addFileLocation(fileLocation);
		fileLocation.setStorageSystem(storageSystem);
		fileLocation.setBasePath(basePath);
		fileLocation.setManaged(isManaged);
		fileLocation.setLCPackageStructure(isLCPackageStructure);
		log.info("Created " + fileLocation.toString());
		return fileLocation;
	}

	public ExternalFileLocation createExternalFileLocation(Package packge, MediaType mediaType, ExternalIdentifier externalIdentifier, String basePath, boolean isManaged, boolean isLCPackageStructure) {
		ExternalFileLocation fileLocation = new ExternalFileLocationImpl();
		packge.addFileLocation(fileLocation);
		fileLocation.setMediaType(mediaType);
		fileLocation.setExternalIdentifier(externalIdentifier);
		fileLocation.setBasePath(basePath);
		fileLocation.setManaged(isManaged);
		fileLocation.setLCPackageStructure(isLCPackageStructure);
		log.info("Created " + fileLocation.toString());
		return fileLocation;				
	}

	public Role createRole(String roleId) {
		Role role = new RoleImpl();
		role.setId(roleId);
		log.info("Created " + role.toString());
		return role;
	}

	public <T extends FileExaminationGroupEvent> T createFileExaminationGroupEvent(Class<T> eventType, FileExaminationGroup fileExaminationGroup, Date eventStart, Agent reportingAgent) throws Exception {
		FileExaminationGroupEvent event = (FileExaminationGroupEvent)(Class.forName(getImplClassName(eventType))).newInstance();
		fileExaminationGroup.addFileExaminationGroupEvent(event);
		event.setEventStart(eventStart);
		event.setReportingAgent(reportingAgent);
		log.info("Created " + event.toString());
		return eventType.cast(event);
	}

	public <T extends FileLocationEvent> T createFileLocationEvent(Class<T> eventType, FileLocation fileLocation, Date eventStart, Agent reportingAgent) throws Exception {
		FileLocationEvent event = (FileLocationEvent)(Class.forName(getImplClassName(eventType))).newInstance();
		fileLocation.addFileLocationEvent(event);
		event.setEventStart(eventStart);
		event.setReportingAgent(reportingAgent);
		log.info("Created " + event.toString());
		return eventType.cast(event);
	}
	
	protected String getImplClassName(Class<?> clazz)
	{
		return clazz.getName().substring(0, (clazz.getName().length()-clazz.getSimpleName().length())) + "impl." + clazz.getSimpleName() + "Impl";		
	}	
}
