package gov.loc.repository.packagemodeler.events.impl;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.AbstractModelersTest;
import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.events.packge.PackageReceivedEvent;
import gov.loc.repository.packagemodeler.events.packge.impl.PackageReceivedEventImpl;
import gov.loc.repository.packagemodeler.packge.Package;

import org.hibernate.validator.InvalidStateException;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Calendar;

public class EventImplTest extends AbstractModelersTest {

	protected Package packge;
	protected System workflow;
	protected Person person1;
	protected Person person2;
	protected Calendar cal;
	
	@Override
	public void createFixtures() throws Exception {
		fixtureHelper.createRepository(REPOSITORY_ID1);		
		packge = modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1);
		session.save(packge);
		
		workflow = fixtureHelper.createSystem(JBPM);
		person1 = fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
		person2 = fixtureHelper.createPerson(PERSON_ID2, PERSON_FIRSTNAME2, PERSON_SURNAME2);
	}		

	@Override
	public void setup() throws Exception {
		this.cal = Calendar.getInstance();
	}

	@Test
	public void testEvent() throws Exception
	{
		PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, this.packge, cal.getTime(), this.workflow);
		event.setPerformingAgent(person1);
		//A RequestingAgent for a PackageReceivedEvent doesn't make sense, but we're just testing EventImpl.
		event.setRequestingAgent(person2);
		cal.add(Calendar.HOUR, 1);
		event.setEventEnd(cal.getTime());	
		event.setMessage("foo");
		session.save(event);

		this.commitAndRestartTransaction();
		
		//Reload the event
		this.session.refresh(event);
		
		//Check package
		assertEquals(packge.getKey(), event.getPackage().getKey());
		//Check bi-directional
		assertTrue(this.packge.getEvents().contains(event));		
		//Check reporting service
		assertEquals(workflow.getKey(), event.getReportingAgent().getKey());
		//Check requesting agent
		assertEquals(person2.getKey(), event.getRequestingAgent().getKey());		
		//Check performing agent
		assertEquals(person1.getKey(), event.getPerformingAgent().getKey());
		//Check message
		assertEquals("foo", event.getMessage());
	}
		
	@Test(expected=InvalidStateException.class)
	public void testPerformingAgentValidation() throws Exception
	{
		try
		{
			PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, this.packge, cal.getTime(), this.workflow);
			event.setPerformingAgent(person1);
			event.setUnknownPerformingAgent(true);
			session.save(event);
		}
		finally
		{
			session.getTransaction().rollback();
		}
	}

	@Test(expected=InvalidStateException.class)
	public void testRequestingAgentValidation() throws Exception
	{
		try
		{
			PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, this.packge, cal.getTime(), this.workflow);
			event.setRequestingAgent(person1);
			event.setUnknownRequestingAgent(true);
			session.save(event);
		}
		finally
		{
			session.getTransaction().rollback();
		}
	}
	
	
	@Test(expected=InvalidStateException.class)
	public void testEventStartValidation() throws Exception
	{
		try
		{
			PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, this.packge, cal.getTime(), this.workflow);
			event.setUnknownEventStart(true);
			session.save(event);
		}
		finally
		{
			session.getTransaction().rollback();
		}
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
		PackageReceivedEvent event = modelerFactory.createPackageEvent(PackageReceivedEvent.class, this.packge, cal.getTime(), this.workflow);
		cal.add(Calendar.HOUR, 1);
		event.setEventEnd(cal.getTime());				
		session.save(event);
		
		this.commitAndRestartTransaction();
		fixtureHelper.reload(event);

		//This makes sure it validates properly
		assertNotNull(event.toPremis());
	}
	
}
