package gov.loc.repository.packagemodeler.agents.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.agents.System;

@Entity(name="System")
@DiscriminatorValue("system")
public class SystemImpl extends AgentImpl implements System {

	public String getName() {
		return this.getId();
	}

}
