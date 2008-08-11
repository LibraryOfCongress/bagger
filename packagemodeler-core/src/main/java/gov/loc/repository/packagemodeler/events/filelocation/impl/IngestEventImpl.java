package gov.loc.repository.packagemodeler.events.filelocation.impl;

import javax.persistence.*;

import org.hibernate.validator.AssertTrue;

import gov.loc.repository.constants.Roles;
import gov.loc.repository.packagemodeler.agents.impl.SystemImpl;
import gov.loc.repository.packagemodeler.events.filelocation.IngestEvent;

@Entity(name="IngestEvent")
@DiscriminatorValue("ingest")
public class IngestEventImpl extends FileLocationEventImpl implements IngestEvent {

	@ManyToOne(targetEntity=SystemImpl.class)
	@JoinColumn(name="repository_system_key")
	private gov.loc.repository.packagemodeler.agents.System repositorySystem;	
	
	public gov.loc.repository.packagemodeler.agents.System getRepositorySystem() {
		return this.repositorySystem;
	}

	public void setRepositorySystem(gov.loc.repository.packagemodeler.agents.System system) {
		this.repositorySystem = system;
	}

	@SuppressWarnings("unused")
	@AssertTrue
	private boolean validateRepositorySystem()
	{
		if (this.repositorySystem == null)
		{
			return true;
		}
		return this.repositorySystem.isInRole(Roles.REPOSITORY_SYSTEM);
	}
	
}
