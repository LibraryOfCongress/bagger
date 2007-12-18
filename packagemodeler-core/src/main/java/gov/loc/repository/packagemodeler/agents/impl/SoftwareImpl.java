package gov.loc.repository.packagemodeler.agents.impl;

import java.text.MessageFormat;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.agents.Software;

@Entity(name="Software")
@DiscriminatorValue("software")
public class SoftwareImpl extends AgentImpl implements Software {

	public String getName() {
		return this.getId();
	}

	@Override
	public String toString() {
		return MessageFormat.format("Software with id {0}", this.getId());
	}
	
}
