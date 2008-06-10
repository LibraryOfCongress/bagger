package gov.loc.repository.packagemodeler.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.bagit.ManifestReader.FileFixity;
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

@Component("modelerFactory")
public class ModelerFactoryImpl implements ModelerFactory {

	private static final Log log = LogFactory.getLog(ModelerFactoryImpl.class);
	
	@Override
	public <T extends Agent> T createAgent(Class<T> agentType, String agentId) throws Exception {
		Agent agent = (Agent)(Class.forName(getImplClassName(agentType))).newInstance();
		agent.setId(agentId);
		log.info("Created " + agent.toString());
		return agentType.cast(agent);
	}

	@Override
	public CanonicalFile createCanonicalFile(Package packge, FileName fileName, Set<Fixity> fixitySet) {
		CanonicalFile canonicalFile = new CanonicalFileImpl();
		packge.addCanonicalFile(canonicalFile);
		canonicalFile.setFileName(fileName);
		canonicalFile.getFixities().addAll(fixitySet);
		log.info("Created " + canonicalFile.toString());		
		return canonicalFile;
	}

	@Override
	public CanonicalFile createCanonicalFile(Package packge, FileName fileName, Fixity fixity) {
		return this.createCanonicalFile(packge, fileName,  FixityHelper.createFixitySet(fixity));
	}

	private Map<String, Map<String,String>> readFixityMap(List<File> manifests)
	{
		Map<String, Map<String,String>> fixityMap = new HashMap<String, Map<String,String>>();
		for(File manifest : manifests)
		{
			ManifestReader reader = new ManifestReader(manifest);
			try
			{
				while(reader.hasNext())
				{
					FileFixity fileFixity = reader.next();
					
					if (! fixityMap.containsKey(fileFixity.getFile()))
					{
						fixityMap.put(fileFixity.getFile(), new HashMap<String,String>());
					}
					fixityMap.get(fileFixity.getFile()).put(reader.getAlgorithm(), fileFixity.getFixityValue());
				}
			}
			finally
			{
				reader.close();
			}
		}
		return fixityMap;
	}

	private Set<Fixity> createFixitySet(Map<String,String> fixityValueMap)
	{
		Set<Fixity> fixitySet = new HashSet<Fixity>();
		for(String algorithm : fixityValueMap.keySet())
		{
			fixitySet.add(new Fixity(fixityValueMap.get(algorithm), algorithm));
		}
		return fixitySet;
	}
	
	@Override
	public Collection<CanonicalFile> createCanonicalFilesFromBagManifests(
			Package packge, List<File> bagManifests) throws Exception {
		if (! packge.getCanonicalFiles().isEmpty())
		{
			throw new Exception(packge.toString() + " already has canonical files.");
		}

		Map<String, Map<String,String>> fixityMap = this.readFixityMap(bagManifests);

		for(String fileKey : fixityMap.keySet())
		{
			Set<Fixity> fixitySet = this.createFixitySet(fixityMap.get(fileKey));
			this.createCanonicalFile(packge, new FileName(fileKey), fixitySet);			
		}
		
		return packge.getCanonicalFiles();
	}	
	
	@Override
	public Collection<CanonicalFile> createCanonicalFilesFromBagManifest(Package packge, File manifestFile) throws Exception {
		List<File> manifestFiles = new ArrayList<File>();
		manifestFiles.add(manifestFile);
		return this.createCanonicalFilesFromBagManifests(packge, manifestFiles);
		
	}	

	@Override
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
				fixitySet.add(new Fixity(fixity.getValue(), fixity.getFixityAlgorithm()));
			}
			//Filter out fileInstances with no fixities
			if (! fixitySet.isEmpty())
			{
				this.createCanonicalFile(packge, fileInstance.getFileName(), fixitySet);
			}
		}
		return packge.getCanonicalFiles();
	}
	
	@Override
	public FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName, Set<Fixity> fixitySet) {
		FileExamination fileExamination = new FileExaminationImpl();
		fileExaminationGroup.addFileExamination(fileExamination);
		fileExamination.setFileName(fileName);
		fileExamination.getFixities().addAll(fixitySet);
		log.info("Created " + fileExamination.toString());				
		return fileExamination;
	}

	@Override
	public FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName, Fixity fixity) {
		return this.createFileExamination(fileExaminationGroup, fileName, FixityHelper.createFixitySet(fixity));
	}

	@Override
	public FileExaminationGroup createFileExaminationGroup(FileLocation fileLocation, boolean isComplete) {
		FileExaminationGroup fileExaminationGroup = new FileExaminationGroupImpl();
		fileLocation.addFileExaminationGroup(fileExaminationGroup);
		fileExaminationGroup.setComplete(isComplete);
		log.info("Created " + fileExaminationGroup.toString());
		return fileExaminationGroup;
	}

	@Override
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

	@Override
	public FileInstance createFileInstance(FileLocation fileLocation, FileName fileName, Fixity fixity) {
		return this.createFileInstance(fileLocation, fileName, FixityHelper.createFixitySet(fixity));
	}

	@Override
	public FileInstance createFileInstance(FileLocation fileLocation, FileName fileName) {
		return this.createFileInstance(fileLocation, fileName, (Set<Fixity>)null);
	}	
	
	@Override
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
				fixitySet.add(new Fixity(fixity.getValue(), fixity.getFixityAlgorithm()));
			}
			this.createFileInstance(fileLocation, canonicalFile.getFileName(), fixitySet);
		}
		return fileLocation.getFileInstances();
	}
	
	@Override
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
				if (! fileLocation.isBag() || ! (fileExamination.getFileName().getRelativePath() == null || fileExamination.getFileName().getRelativePath().length() == 0))
				{
					for(Fixity fixity : fileExamination.getFixities())
					{
						fixitySet.add(new Fixity(fixity.getValue(), fixity.getFixityAlgorithm()));
					}
				}
				this.createFileInstance(fileLocation, fileExamination.getFileName(), fixitySet);				
			}
		}
		return fileLocation.getFileInstances();
	}	

	@Override
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
	
	@Override
	public Collection<FileInstance> createFileInstancesFromBagManifests(
			FileLocation fileLocation, List<File> bagManifests)
			throws Exception {
		Map<String, Map<String,String>> fixityMap = this.readFixityMap(bagManifests);

		for(String fileKey : fixityMap.keySet())
		{
			Set<Fixity> fixitySet = this.createFixitySet(fixityMap.get(fileKey));
			this.createFileInstance(fileLocation, new FileName(fileKey), fixitySet);
		}
		
		return fileLocation.getFileInstances();
	}
	
	@Override
	public Collection<FileInstance> createFileInstancesFromBagTagManifests(
			FileLocation fileLocation, List<File> tagManifests)
			throws Exception {
		Map<String, Map<String,String>> fixityMap = this.readFixityMap(tagManifests);

		for(String fileKey : fixityMap.keySet())
		{
			this.createFileInstance(fileLocation, new FileName(fileKey));
		}
		
		return fileLocation.getFileInstances();
	}
	
	@Override
	public Collection<FileInstance> createFileInstancesFromBagManifest(
			FileLocation fileLocation, File bagManifest) throws Exception {
		List<File> bagManifests = new ArrayList<File>();
		bagManifests.add(bagManifest);
		return this.createFileInstancesFromBagManifests(fileLocation, bagManifests);
	}
	
	@Override
	public Collection<FileInstance> createFileInstancesFromBagTagManifest(
			FileLocation fileLocation, File tagManifest) throws Exception {
		List<File> tagManifests = new ArrayList<File>();
		tagManifests.add(tagManifest);
		return this.createFileInstancesFromBagTagManifests(fileLocation, tagManifests);
	}
	
	@Override
	public FileExamination createFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName) {
		FileExamination fileExamination = new FileExaminationImpl();		
		fileExaminationGroup.addFileExamination(fileExamination);
		fileExamination.setFileName(fileName);
		log.info("Created " + fileExamination.toString());
		return fileExamination;
		
	}

	@Override
	public <T extends Package> T createPackage(Class<T> packageType, Repository repository, String packageId) throws Exception
	{
		Package packge = (Package)(Class.forName(getImplClassName(packageType))).newInstance();
		repository.addPackage(packge);
		packge.setPackageId(packageId);
		log.info("Created " + packge.toString());
		return packageType.cast(packge);
		
	}

	@Override
	public <T extends PackageEvent> T createPackageEvent(Class<T> eventType, Package packge, Date eventStart, Agent reportingAgent) throws Exception {
		PackageEvent event = (PackageEvent)(Class.forName(getImplClassName(eventType))).newInstance();		
		packge.addPackageEvent(event);
		event.setEventStart(eventStart);
		event.setReportingAgent(reportingAgent);
		log.info("Created " + event.toString());
		return eventType.cast(event);
	}

	@Override
	public Repository createRepository(String repositoryId) throws Exception {
		Repository repository = new RepositoryImpl();
		repository.setId(repositoryId);
		log.info("Created " + repository.toString());
		return repository;
	}

	@Override
	public StorageSystemFileLocation createStorageSystemFileLocation(Package packge, System storageSystem, String basePath, boolean isManaged, boolean isLCPackageStructure) {
		StorageSystemFileLocation fileLocation = new StorageSystemFileLocationImpl();
		packge.addFileLocation(fileLocation);
		fileLocation.setStorageSystem(storageSystem);
		fileLocation.setBasePath(basePath);
		fileLocation.setManaged(isManaged);
		fileLocation.setBag(isLCPackageStructure);
		log.info("Created " + fileLocation.toString());
		return fileLocation;
	}

	@Override
	public ExternalFileLocation createExternalFileLocation(Package packge, MediaType mediaType, ExternalIdentifier externalIdentifier, String basePath, boolean isManaged, boolean isLCPackageStructure) {
		ExternalFileLocation fileLocation = new ExternalFileLocationImpl();
		packge.addFileLocation(fileLocation);
		fileLocation.setMediaType(mediaType);
		fileLocation.setExternalIdentifier(externalIdentifier);
		fileLocation.setBasePath(basePath);
		fileLocation.setManaged(isManaged);
		fileLocation.setBag(isLCPackageStructure);
		log.info("Created " + fileLocation.toString());
		return fileLocation;				
	}

	@Override
	public Role createRole(String roleId) {
		Role role = new RoleImpl();
		role.setId(roleId);
		log.info("Created " + role.toString());
		return role;
	}

	@Override
	public <T extends FileExaminationGroupEvent> T createFileExaminationGroupEvent(Class<T> eventType, FileExaminationGroup fileExaminationGroup, Date eventStart, Agent reportingAgent) throws Exception {
		FileExaminationGroupEvent event = (FileExaminationGroupEvent)(Class.forName(getImplClassName(eventType))).newInstance();
		fileExaminationGroup.addFileExaminationGroupEvent(event);
		event.setEventStart(eventStart);
		event.setReportingAgent(reportingAgent);
		log.info("Created " + event.toString());
		return eventType.cast(event);
	}

	@Override
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
