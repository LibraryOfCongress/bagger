package gov.loc.repository.packagemodeler.packge.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.events.filelocation.impl.FileLocationEventImpl;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Package;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;

@Entity(name="FileLocation")
@Table(name = "filelocation", schema = "core")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class FileLocationImpl implements FileLocation
{
	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
	
	@Column(name="create_timestamp", nullable = false)
	private Date createTimestamp;

	@Column(name="update_timestamp", nullable = false)
	private Date updateTimestamp;
		
	@OneToMany(mappedBy="fileLocation", targetEntity=FileInstanceImpl.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private Set<FileInstance> fileInstanceSet = new HashSet<FileInstance>();
	
	@ManyToOne(targetEntity=PackageImpl.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name="package_key", nullable = false)
	private Package packge;

    @OneToMany(mappedBy="fileLocation", targetEntity=FileLocationEventImpl.class)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OrderBy("eventStart asc")
    private Set<FileLocationEvent> eventSet = new HashSet<FileLocationEvent>();
	
	@OneToMany(mappedBy="fileLocation", targetEntity=FileExaminationGroupImpl.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private List<FileExaminationGroup> fileExaminationGroupList = new ArrayList<FileExaminationGroup>();
		
	@Column(name="is_managed", nullable = false)
	private boolean isManaged = false;

	@Column(name="is_bag", nullable = false)
	private boolean isBag = false;	
	
	public void setKey(Long key) {
		this.key = key;
	}

	public Long getKey() {
		return key;
	}

	public void addFileInstance(FileInstance fileInstance) {
		fileInstance.setFileLocation(this);		
	}

	public Set<FileInstance> getFileInstances() {
		return this.fileInstanceSet;
	}
		
	public void setPackage(Package packge) {
		this.packge = packge;
	}

	public Package getPackage() {
		return packge;
	}

	public void setManaged(boolean isManaged) {
		this.isManaged = isManaged;
	}

	public boolean isManaged() {
		return isManaged;
	}
	
	public void addFileLocationEvent(FileLocationEvent event) {
		this.eventSet.add(event);
		event.setFileLocation(this);
	}	
	
	public Set<FileLocationEvent> getFileLocationEvents() {
		return Collections.unmodifiableSet(this.eventSet);
	}
	
	public <T extends FileLocationEvent> Set<T> getFileLocationEvents(Class<T> eventType) {
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
	
	public <T extends FileLocationEvent> T getMostRecentFileLocationEvent(Class<T> eventType, boolean isSuccessOnly) {
		Event recentEvent = null;
		for(Event event : this.eventSet )
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
	
	public void removeFileLocationEvent(FileLocationEvent event) {
		event.setFileLocation(null);
		this.eventSet.remove(event);		
	}
	
	public void addFileExaminationGroup(FileExaminationGroup fileExaminationGroup) {
		fileExaminationGroup.setFileLocation(this);
		this.fileExaminationGroupList.add(fileExaminationGroup);
	}
	
	public void removeFileExaminationGroup(FileExaminationGroup fileExaminationGroup) {
		fileExaminationGroup.setFileLocation(null);
		this.fileExaminationGroupList.remove(fileExaminationGroup);		
	}
	
	public List<FileExaminationGroup> getFileExaminationGroups() {
		return Collections.unmodifiableList(this.fileExaminationGroupList);
	}
	
	public FileInstance findFileInstance(FileName fileName) {
		for(FileInstance fileInstance : this.getFileInstances())
		{
			if (fileName.equals(fileInstance.getFileName()))
			{
				return fileInstance;
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
	
	public boolean isBag() {
		return this.isBag;
	}
	
	public void setBag(boolean isBag) {
		this.isBag = isBag;
		
	}
}
