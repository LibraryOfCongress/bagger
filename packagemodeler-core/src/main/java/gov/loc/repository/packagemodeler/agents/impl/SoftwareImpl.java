package gov.loc.repository.packagemodeler.agents.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.agents.Software;

@Entity(name="Software")
@DiscriminatorValue("software")
public class SoftwareImpl extends AgentImpl implements Software {

	public String getName() {
		return this.getId();
	}

}
