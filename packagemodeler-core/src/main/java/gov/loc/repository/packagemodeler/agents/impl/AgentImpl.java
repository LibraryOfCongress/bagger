package gov.loc.repository.packagemodeler.agents.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Role;

@Entity(name="Agent")
@Table(name = "agent", schema="core")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="agent_type",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class AgentImpl implements Agent {

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	protected Long key;
	
	@Column(name = "agent_id", nullable = false)
	protected String identifier;

	@ManyToMany(targetEntity=RoleImpl.class, fetch=FetchType.EAGER)
	@JoinTable(name="agentmodeler_agent_role", joinColumns={@JoinColumn(name="agent_key")}, inverseJoinColumns={@JoinColumn(name="role_key")})
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	protected Set<Role> roleSet = new HashSet<Role>();
	
	public String getId() {
		return this.identifier;
	}

	public Long getKey() {
		return this.key;
	}

	public Set<Role> getRoles() {
		return Collections.unmodifiableSet(this.roleSet);
	}
	
	public void addRole(Role role) {
		this.roleSet.add(role);
		role.getAgentSet().add(this);
	}	

	public void removeRole(Role role) {
		this.roleSet.remove(role);
		role.getAgentSet().remove(this);		
	}	
	
	public void setId(String agentId) {
		this.identifier = agentId;

	}
	@Override
	public String toString() {
		return this.getName();
	}
	
	public boolean isInRole(String roleId) {
		for(Role role : this.roleSet)
		{
			if (roleId.equals(role.getId()))
			{
				return true;
			}
		}
		return false;		
	}	

}
