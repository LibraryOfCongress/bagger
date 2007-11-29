package gov.loc.repository.packagemodeler.agents.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.agents.Organization;

@Entity(name="Organization")
@DiscriminatorValue("organization")
public class OrganizationImpl extends AgentImpl implements Organization {

	@Column(name = "name", nullable = true)
	private String name;	
	
	public void setName(String name) {
		this.name = name;

	}

	public String getName() {
		return this.name;
	}

}
