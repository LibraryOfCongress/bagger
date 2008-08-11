package gov.loc.repository.packagemodeler.events.impl;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.AbstractCoreModelersTest;
import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.events.packge.PackageReceivedEvent;
import gov.loc.repository.packagemodeler.events.packge.impl.PackageReceivedEventImpl;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;

import org.hibernate.validator.InvalidStateException;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Calendar;

public class EventImplTest extends AbstractCoreModelersTest {

	Package packge;
	static Repository repository;
	static System workflow;
	static Person person1;
	static Person person2;
	Calendar cal;	
	
	@Override
	public void createFixtures() throws Exception {
		repository = fixtureHelper.createRepository(REPOSITORY_ID1);		
		
		workflow = fixtureHelper.createSystem(JBPM);
		person1 = fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
		person2 = fixtureHelper.createPerson(PERSON_ID2, PERSON_FIRSTNAME2, PERSON_SURNAME2);
	}		

	@Override
	public void setup() throws Exception {
		packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		this.template.save(packge);
		this.cal = Calendar.getInstance();
	}

	@Test
	public void testEvent() throws Exception
	{
		PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal.getTime(), workflow);
		event.setPerformingAgent(person1);
		//A RequestingAgent for a PackageReceivedEvent doesn't make sense, but we're just testing EventImpl.
		event.setRequestingAgent(person2);
		cal.add(Calendar.HOUR, 1);
		event.setEventEnd(cal.getTime());	
		event.setMessage("foo");
		
		this.template.update(packge);
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(event);
		this.template.refresh(packge);
		
		//Check package
		assertEquals(packge.getKey(), event.getPackage().getKey());
		//Check bi-directional
		assertTrue(packge.getEvents().contains(event));		
		//Check reporting service
		assertEquals(workflow.getKey(), event.getReportingAgent().getKey());
		//Check requesting agent
		assertEquals(person2.getKey(), event.getRequestingAgent().getKey());		
		//Check performing agent
		assertEquals(person1.getKey(), event.getPerformingAgent().getKey());
		//Check message
		assertEquals("foo", event.getMessage());
		
		txManager.commit(status);
	}
		
	@Test(expected=InvalidStateException.class)
	public void testPerformingAgentValidation() throws Exception
	{
		PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal.getTime(), workflow);
		event.setPerformingAgent(person1);
		event.setUnknownPerformingAgent(true);
		this.template.save(event);
	}

	@Test(expected=InvalidStateException.class)
	public void testRequestingAgentValidation() throws Exception
	{
		PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal.getTime(), workflow);
		event.setRequestingAgent(person1);
		event.setUnknownRequestingAgent(true);
		this.template.save(event);
	}
	
	
	@Test(expected=InvalidStateException.class)
	public void testEventStartValidation() throws Exception
	{
		PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal.getTime(), workflow);
		event.setUnknownEventStart(true);
		this.template.save(event);
	}
		
	@Test
	public void testGetName() throws Exception
	{
		PackageReceivedEvent event = new PackageReceivedEventImpl();
		assertEquals("Package Received Event", event.getName());
	}
	
	@Test
	public void testToPremis() throws Exception
	{
		PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal.getTime(), workflow);
		cal.add(Calendar.HOUR, 1);
		event.setEventEnd(cal.getTime());				

		//This makes sure it validates properly
		assertNotNull(event.toPremis());
	}
	
}
