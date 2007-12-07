package gov.loc.repository.packagemodeler.agents.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Role;

@Entity(name="Role")
@Table(name="role", schema="agent")
public class RoleImpl implements Role {

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
		
	@Column(name = "role_id", nullable = false)
	private String identifier;
	
	@ManyToMany(mappedBy="roleSet", targetEntity=AgentImpl.class)
	private Set<Agent> agentSet = new HashSet<Agent>();
		
	public Long getKey() {		
		return this.key;
	}

	public String getId() {
		return this.identifier;
	}

	public void setId(String roleId) {
		this.identifier = roleId;
	}

	public Set<Agent> getAgentSet() {
		return this.agentSet;
	}	
}
