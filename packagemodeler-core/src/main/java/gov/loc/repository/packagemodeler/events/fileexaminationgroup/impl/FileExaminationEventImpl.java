package gov.loc.repository.packagemodeler.events.fileexaminationgroup.impl;

import gov.loc.repository.packagemodeler.events.fileexaminationgroup.FileExaminationEvent;

import javax.persistence.*;

@Entity(name="FileExaminationEvent")
@DiscriminatorValue("fileexamination")
public class FileExaminationEventImpl extends FileExaminationGroupEventImpl
		implements FileExaminationEvent {

}
