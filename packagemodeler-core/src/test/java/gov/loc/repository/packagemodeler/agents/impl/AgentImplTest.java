package gov.loc.repository.packagemodeler.agents.impl;

import static org.junit.Assert.*;
import gov.loc.repository.packagemodeler.AbstractCoreModelersTest;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.impl.ModelerFactoryImpl;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import static gov.loc.repository.constants.Roles.*;

public class AgentImplTest extends AbstractCoreModelersTest {

	ModelerFactory factory = new ModelerFactoryImpl();
	
	@Test
	public void testIsInRole() throws Exception {
		Agent agent = factory.createAgent(Person.class, PERSON_ID1);
		agent.addRole(factory.createRole(STORAGE_SYSTEM));
		
		assertTrue(agent.isInRole(STORAGE_SYSTEM));
		assertFalse(agent.isInRole(REPOSITORY_SYSTEM));
		
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void testNoDuplicates() throws Exception {
		Person person1 = new PersonImpl();
		person1.setId(PERSON_ID1);
		person1.setFirstName(PERSON_FIRSTNAME1);
		person1.setSurname(PERSON_SURNAME1);
		this.template.save(person1);
		
		Person person2 = new PersonImpl();
		person2.setId(PERSON_ID1);
		person2.setFirstName(PERSON_FIRSTNAME1);
		person2.setSurname(PERSON_SURNAME1);
		this.template.save(person2);


	}
	
}
