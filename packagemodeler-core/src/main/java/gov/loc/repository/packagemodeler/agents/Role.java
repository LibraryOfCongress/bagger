package gov.loc.repository.packagemodeler.agents;

import java.util.Set;

import gov.loc.repository.Ided;
import gov.loc.repository.Keyed;

public interface Role extends Keyed, Ided{

	public abstract Set<Agent> getAgentSet();
		
}
