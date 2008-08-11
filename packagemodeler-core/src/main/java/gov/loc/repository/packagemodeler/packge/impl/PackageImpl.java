package gov.loc.repository.packagemodeler.packge.impl;

import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.events.packge.PackageEvent;
import gov.loc.repository.packagemodeler.events.packge.impl.PackageEventImpl;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.packagemodeler.packge.impl.FileLocationImpl;
import gov.loc.repository.packagemodeler.packge.impl.RepositoryImpl;
import gov.loc.repository.utilities.FilenameHelper;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity(name="Package")
@Table(name = "package", schema="core", uniqueConstraints={@UniqueConstraint(columnNames={"repository_key","package_id"})})
@Inheritance(strategy=InheritanceType.JOINED)
public class PackageImpl implements Package, Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
	
	@Column(name="create_timestamp", nullable = false)
	private Date createTimestamp;

	@Column(name="update_timestamp", nullable = false)
	private Date updateTimestamp;
		
	@ManyToOne(targetEntity=RepositoryImpl.class )	
	@JoinColumn(name="repository_key", nullable = false)
	private Repository repository;
	
	@Column(name = "package_id", nullable = false)
	private String packageId;

	@Column(name = "processinstance_id", nullable = true)
	private Long processInstanceId = null;
		
	@OneToMany(mappedBy="packge", targetEntity=FileLocationImpl.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private Set<FileLocation> fileLocationSet = new HashSet<FileLocation>();

    @OneToMany(mappedBy="packge", targetEntity=PackageEventImpl.class)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OrderBy("eventStart asc")
    private Set<PackageEvent> eventSet = new HashSet<PackageEvent>();
	    
    @OneToMany(mappedBy="packge", targetEntity=CanonicalFileImpl.class, cascade={CascadeType.PERSIST, CascadeType.MERGE })
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private Set<CanonicalFile> canonicalFileSet = new HashSet<CanonicalFile>();

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getPackageId() {
		return packageId;
	}

	protected void setKey(Long key) {
		this.key = key;
	}

	public Long getKey() {
		return key;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void addFileLocation(FileLocation fileLocation) {
		if (fileLocation.getPackage() != null)
		{
			fileLocation.getPackage().removeFileLocation(fileLocation);
		}
		fileLocation.setPackage(this);
		this.fileLocationSet.add(fileLocation);
	}

	public void removeFileLocation(FileLocation fileLocation) {
		fileLocation.setPackage(null);
		this.fileLocationSet.remove(fileLocation);		
	}	
	
	public Set<FileLocation> getFileLocations() {
		return Collections.unmodifiableSet(this.fileLocationSet);
	}
		
	public void addPackageEvent(PackageEvent event) {
		event.setPackage(this);
		this.eventSet.add(event);
	}
	
	public void removePackageEvent(PackageEvent event) {
		event.setPackage(null);
		this.eventSet.remove(event);
	}	
	
	public Set<PackageEvent> getPackageEvents() {
		return Collections.unmodifiableSet(this.eventSet);
	}
	
	public <T extends PackageEvent> Set<T> getPackageEvents(Class<T> eventType) {
		Set<T> eventSet = new HashSet<T>();
		for(Event event : this.eventSet)
		{
			if (eventType.isInstance(event))
			{
				eventSet.add(eventType.cast(event));
			}
		}
		return Collections.unmodifiableSet(eventSet);
	}
	
	public List<Event> getEvents() {
		List<Event> eventSet = new ArrayList<Event>();
		eventSet.addAll(this.eventSet);
		for(FileLocation fileLocation : this.fileLocationSet)
		{
			eventSet.addAll(fileLocation.getFileLocationEvents());
			for (FileExaminationGroup fileExaminationGroup : fileLocation.getFileExaminationGroups())
			{
				eventSet.addAll(fileExaminationGroup.getFileExaminationGroupEvents());
			}
			
		}
		Collections.sort(eventSet);
		return eventSet;
	}	
	
	public <T extends PackageEvent> T getMostRecentPackageEvent(Class<T> eventType, boolean isSuccessOnly)
	{		
		Event recentEvent = null;
		for(Event event : this.getEvents() )
		{
			if (eventType.isInstance(event))
			{
				if ((isSuccessOnly && event.isSuccess()) || ! isSuccessOnly)
				{
					if (recentEvent == null || recentEvent.getEventStart().before(event.getEventStart()))
					{
						recentEvent = event;
					}
				}
				
			}
		}
		return eventType.cast(recentEvent);
	}
		
	public Set<CanonicalFile> getCanonicalFiles() {
		return Collections.unmodifiableSet(this.canonicalFileSet);
	}

	public void addCanonicalFile(CanonicalFile file) {
		if (file.getPackage() != null)
		{
			file.getPackage().removeCanonicalFile(file);
		}
		file.setPackage(this);
		this.canonicalFileSet.add(file);
	}
	
	public void removeCanonicalFile(CanonicalFile file) {
		file.setPackage(null);
		this.canonicalFileSet.remove(file);
	}	
	
	@Override
	public void removeCanonicalFiles() {
		this.canonicalFileSet = new HashSet<CanonicalFile>();		
	}
	
	public StorageSystemFileLocation getFileLocation(String storageSystemId, String basePath)
	{
		for(FileLocation fileLocation : this.fileLocationSet)
		{
			if (fileLocation instanceof StorageSystemFileLocation)
			{
				StorageSystemFileLocation storageSystemFileLocation = (StorageSystemFileLocation)fileLocation;
				if (storageSystemId.equals(storageSystemFileLocation.getStorageSystem().getId()) && FilenameHelper.normalize(basePath).equals(fileLocation.getBasePath()))
				{
					return storageSystemFileLocation;
				}
			}
		}
		return null;
	}
	
	public ExternalFileLocation getFileLocation(ExternalIdentifier externalIdentifier) {
		for(FileLocation fileLocation : this.fileLocationSet)
		{
			if (fileLocation instanceof ExternalFileLocation)
			{
				ExternalFileLocation externalFileLocation = (ExternalFileLocation)fileLocation;
				if (externalIdentifier.equals(externalFileLocation.getExternalIdentifier()))
				{
					return externalFileLocation;
				}
			}
		}
		return null;
	}	

	public CanonicalFile findCanonicalFile(FileName fileName) {
		for(CanonicalFile canonicalFile : this.getCanonicalFiles())
		{
			if (fileName.equals(canonicalFile.getFileName()))
			{
				return canonicalFile;
			}
		}
		return null;
	}
	
	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}
	
	public <T extends Event> T getMostRecentEvent(Class<T> eventType, boolean isSuccessOnly) {
		List<Event> eventList = this.getEvents();
		for(int i=eventList.size()-1; i >= 0; i--)
		{
			Event event = (Event)eventList.get(i);
			if (eventType.isInstance(event))
			{
				if ((isSuccessOnly && event.isSuccess()) || ! isSuccessOnly)
				{
					return eventType.cast(event);
				}
				
			}
		}
		return null;

	}
	
	@Override
	public String toString() {
		return MessageFormat.format("Package with id {0} that is part of {1}", this.packageId, this.repository.toString());
	}
}
