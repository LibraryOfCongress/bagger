package gov.loc.repository.packagemodeler.agents.impl;

import static org.junit.Assert.*;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.impl.ModelerFactoryImpl;

import org.junit.Test;

import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import static gov.loc.repository.constants.Roles.*;

public class AgentImplTest {

	ModelerFactory factory = new ModelerFactoryImpl();
	
	@Test
	public void testIsInRole() throws Exception {
		Agent agent = factory.createAgent(Person.class, PERSON_ID1);
		agent.addRole(factory.createRole(STORAGE_SYSTEM));
		
		assertTrue(agent.isInRole(STORAGE_SYSTEM));
		assertFalse(agent.isInRole(REPOSITORY_SYSTEM));
		
	}

}
