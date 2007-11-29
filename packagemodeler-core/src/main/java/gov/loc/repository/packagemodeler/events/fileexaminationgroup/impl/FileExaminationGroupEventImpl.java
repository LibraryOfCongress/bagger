package gov.loc.repository.packagemodeler.events.fileexaminationgroup.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.events.fileexaminationgroup.FileExaminationGroupEvent;
import gov.loc.repository.packagemodeler.events.impl.EventImpl;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.impl.FileExaminationGroupImpl;

@Entity(name="FileExaminationGroupEvent")
@Table(name = "event_file_examination_group", schema="core")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class FileExaminationGroupEventImpl extends EventImpl implements
		FileExaminationGroupEvent {

	@ManyToOne(targetEntity=FileExaminationGroupImpl.class )	
    @JoinColumn(name="file_examination_group_key", nullable=false)
	private FileExaminationGroup fileExaminationGroup;
		
	@Override
	protected String getPremisLinkingObjectIdentifierValueText() {
		return "info:loc-repo/entity/filelocation/" + this.fileExaminationGroup.getKey();
	}	

	public FileExaminationGroup getFileExaminationGroup() {
		return this.fileExaminationGroup;
	}

	public void setFileExaminationGroup(
		FileExaminationGroup fileExaminationGroup) {
		this.fileExaminationGroup = fileExaminationGroup;
	}

}
