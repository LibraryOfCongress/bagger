package gov.loc.repository.packagemodeler.agents;

import gov.loc.repository.Ided;

import java.util.Set;

public interface Agent extends Ided {
	
	public abstract String getName();	
	
	public abstract Set<Role> getRoles();
	
	public abstract void addRole(Role role);
	
	public abstract void removeRole(Role role);
	
	public abstract boolean isInRole(String roleId);
}
