package gov.loc.repository.packagemodeler.packge.impl;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;

import gov.loc.repository.packagemodeler.events.fileexaminationgroup.FileExaminationGroupEvent;
import gov.loc.repository.packagemodeler.events.fileexaminationgroup.impl.FileExaminationGroupEventImpl;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileLocation;

@Entity(name="FileExaminationGroup")
@Table(name = "fileexamination_group", schema="core")
public class FileExaminationGroupImpl implements FileExaminationGroup {

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
		
	@OneToMany(targetEntity=FileExaminationImpl.class, mappedBy="fileExaminationGroup" )
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	private Set<FileExamination> fileExaminationSet = new HashSet<FileExamination>();

    @OneToMany(mappedBy="fileExaminationGroup", targetEntity=FileExaminationGroupEventImpl.class)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @OrderBy("eventStart asc")
    private Set<FileExaminationGroupEvent> eventSet = new HashSet<FileExaminationGroupEvent>();
		
	@Column(name="create_timestamp", nullable = false)
	private Date createTimestamp;

	@Column(name="update_timestamp", nullable = false)
	private Date updateTimestamp;
		
	@Column(name="is_complete", nullable = false)
	private boolean isComplete = false;
	
	@ManyToOne(targetEntity=FileLocationImpl.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name="filelocation_key", nullable = false)
	private FileLocation fileLocation;
		
	public void addFileExamination(FileExamination fileExamination) {
		fileExamination.setFileExaminationGroup(this);
		this.fileExaminationSet.add(fileExamination);	
	}
	
	public Set<FileExamination> getFileExaminations() {
		return Collections.unmodifiableSet(this.fileExaminationSet);
	}
	
	public void removeFileExamination(FileExamination fileExamination) {
		this.fileExaminationSet.remove(fileExamination);
		
	}
		
	public Date getCreateTimestamp() {
		return this.createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return this.updateTimestamp;
	}

	public FileLocation getFileLocation() {
		return this.fileLocation;
	}

	public boolean isComplete() {
		return this.isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
		
	}

	public void setFileLocation(FileLocation fileLocation) {
		this.fileLocation = fileLocation;
	}

	public Long getKey() {
		return this.key;
	}

	public void addFileExaminationGroupEvent(FileExaminationGroupEvent event) {
		this.eventSet.add(event);
		event.setFileExaminationGroup(this);
	}	
	
	public Set<FileExaminationGroupEvent> getFileExaminationGroupEvents() {
		return Collections.unmodifiableSet(this.eventSet);
	}
	
	public <T extends FileExaminationGroupEvent> Set<T> getFileExaminationGroupEvents(Class<T> eventType) {
		Set<T> eventSet = new HashSet<T>();
		for(FileExaminationGroupEvent event : this.eventSet)
		{
			if (eventType.isInstance(event))
			{
				eventSet.add(eventType.cast(event));
			}
		}
		return Collections.unmodifiableSet(eventSet);
	}
		
	public <T extends FileExaminationGroupEvent> T getMostRecentFileExaminationGroupEvent(Class<T> eventType, boolean isSuccessOnly) {
		FileExaminationGroupEvent recentEvent = null;
		for(FileExaminationGroupEvent event : this.eventSet )
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
	
	public void removeFileExaminationGroupEvent(FileExaminationGroupEvent event) {
		event.setFileExaminationGroup(null);
		this.eventSet.remove(event);		
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("File Examination Group that is part of {0}", this.fileLocation.toString());
	}
	
}
