package gov.loc.repository.packagemodeler.packge.impl;

import static org.junit.Assert.*;
import java.util.Calendar;
import java.util.Iterator;

import org.junit.Test;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.AbstractModelersTest;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.events.fileexaminationgroup.FileExaminationEvent;
import gov.loc.repository.packagemodeler.events.filelocation.IngestEvent;
import gov.loc.repository.packagemodeler.events.packge.PackageAcceptedEvent;
import gov.loc.repository.packagemodeler.events.packge.PackageEvent;
import gov.loc.repository.packagemodeler.events.packge.PackageReceivedEvent;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.packagemodeler.packge.impl.PackageImpl;
import gov.loc.repository.utilities.FilenameHelper;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.PropertyValueException;

public class PackageImplTest extends AbstractModelersTest {
	
	protected static System storageSystem;
	protected static System workflowService;
	protected static Repository repository;

	@Override
	public void createFixtures() throws Exception {
		repository = fixtureHelper.createRepository(REPOSITORY_ID1);
		storageSystem = fixtureHelper.createStorageSystem(RS25);
		workflowService = fixtureHelper.createSystem(JBPM);
	}	
		
	@Override
	public void setup() throws Exception
	{
	}
		
	@Test
	public void testPackageLocation() throws Exception
	{
		Package packge = modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
		modelerFactory.createStorageSystemFileLocation(packge, storageSystem, BASEPATH_1 + testCounter, true, true);
		modelerFactory.createStorageSystemFileLocation(packge, storageSystem, BASEPATH_2 + testCounter, true, true);
		modelerFactory.createExternalFileLocation(packge, MediaType.EXTERNAL_HARDDRIVE, new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER), BASEPATH_1 + testCounter, true, true);				
		modelerFactory.createExternalFileLocation(packge, MediaType.EXTERNAL_HARDDRIVE, new ExternalIdentifier(SERIAL_NUMBER_2, IdentifierType.SERIAL_NUMBER), BASEPATH_2 + testCounter, true, true);				
		session.save(packge);
		this.commitAndRestartTransaction();
		
		fixtureHelper.reload(packge);

		assertNotNull(packge);
		assertEquals(4, packge.getFileLocations().size());		

		assertNotNull(packge.getFileLocation(RS25, BASEPATH_1 + testCounter));
		assertNotNull(packge.getFileLocation(RS25, BASEPATH_2 + testCounter));
		assertNull(packge.getFileLocation(RS15, BASEPATH_1 + testCounter));		
		assertNotNull(packge.getFileLocation(new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER)));
		assertNotNull(packge.getFileLocation(new ExternalIdentifier(SERIAL_NUMBER_2, IdentifierType.SERIAL_NUMBER)));
		assertNull(packge.getFileLocation(new ExternalIdentifier("x" + SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER)));
		
	}

	@Test(expected = PropertyValueException.class)
	public void testMissingRepository() throws Exception
	{		
		
		try
		{
			Package packge = new PackageImpl();
			packge.setPackageId(PACKAGE_ID1 + testCounter);
			session.save(packge);
			session.getTransaction().commit();
		}
		catch(Exception ex)
		{
			session.getTransaction().rollback();
			throw ex;
		}
	}

	@Test(expected = ConstraintViolationException.class)
	public void testUniquePackageIdRepositoryId() throws Exception
	{
		try
		{
			Package packge1 = modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
			session.save(packge1);

			Package packge2 = modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
			session.save(packge2);
			
			session.getTransaction().commit();
		}
		catch(Exception ex)
		{
			session.getTransaction().rollback();
			throw ex;
		}
	}
	
	@Test
	public void testEvent() throws Exception
	{
		Package packge = modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);

		Calendar cal1 = Calendar.getInstance();
		Event event1 = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal1.getTime(), workflowService);
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.HOUR, 1);
		Event event2 = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal2.getTime(), workflowService);
		Calendar cal3 = Calendar.getInstance();
		cal3.add(Calendar.HOUR, 2);
		Event event3 = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal3.getTime(), workflowService);
		Calendar cal4 = Calendar.getInstance();
		cal4.add(Calendar.HOUR, 3);
		Event event4 = modelerFactory.createPackageEvent(PackageReceivedEvent.class, packge, cal4.getTime(), workflowService);
		event4.setSuccess(false);
		Calendar cal5 = Calendar.getInstance();
		cal5.add(Calendar.HOUR, 5);
		modelerFactory.createPackageEvent(PackageAcceptedEvent.class, packge, cal5.getTime(), workflowService);

		Calendar cal6 = Calendar.getInstance();
		cal6.add(Calendar.HOUR, -1);		
		FileLocation fileLocation = modelerFactory.createStorageSystemFileLocation(packge, storageSystem, BASEPATH_1 + testCounter, true, true);
		Event event5 = modelerFactory.createFileLocationEvent(IngestEvent.class, fileLocation, cal6.getTime(), workflowService);
		
		FileExaminationGroup fileExaminationGroup = modelerFactory.createFileExaminationGroup(fileLocation, false);
		modelerFactory.createFileExaminationGroupEvent(FileExaminationEvent.class, fileExaminationGroup, cal4.getTime(), workflowService);

		session.save(packge);
		this.commitAndRestartTransaction();
		
		this.session.refresh(packge);
		assertEquals(7, packge.getEvents().size());
		//Should be sorted
		assertEquals(event5.getKey(), packge.getEvents().get(0).getKey());
		assertEquals(5, packge.getPackageEvents().size());
		assertEquals(4, packge.getPackageEvents(PackageReceivedEvent.class).size());
		assertEquals(event3.getKey(), packge.getMostRecentPackageEvent(PackageReceivedEvent.class, true).getKey());
		assertEquals(event4.getKey(), packge.getMostRecentPackageEvent(PackageReceivedEvent.class, false).getKey());
		assertEquals(event3.getKey(), packge.getMostRecentEvent(PackageReceivedEvent.class, true).getKey());
		assertEquals(event4.getKey(), packge.getMostRecentEvent(PackageReceivedEvent.class, false).getKey());
		
		//Check ordering desc
		Iterator<PackageEvent> eventIter = packge.getPackageEvents().iterator();
		assertEquals(event1.getKey(), eventIter.next().getKey());
		assertEquals(event2.getKey(), eventIter.next().getKey());
		assertEquals(event3.getKey(), eventIter.next().getKey());
		assertEquals(event4.getKey(), eventIter.next().getKey());
		
	}
	
	@Test
	public void testCanonicalFiles() throws Exception
	{
		Package packge = modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
		String root = FilenameHelper.getRoot(FILENAME_1);
		modelerFactory.createCanonicalFile(packge, new FileName(FilenameHelper.removeBasePath(root, FILENAME_1)), new Fixity(FIXITY_1, Algorithm.MD5));
		modelerFactory.createCanonicalFile(packge, new FileName(FilenameHelper.removeBasePath(root, FILENAME_2)), new Fixity(FIXITY_2, Algorithm.MD5));

		session.save(packge);
		this.commitAndRestartTransaction();
		fixtureHelper.reload(packge);
		
		assertEquals(2, packge.getCanonicalFiles().size());
		CanonicalFile file = packge.getCanonicalFiles().iterator().next();
		//Don't know ordering
		assertTrue(FilenameUtils.equalsNormalized(FILENAME_1, FilenameHelper.concat(root, file.getFileName().getFilename())) || FilenameUtils.equalsNormalized(FILENAME_2, FilenameHelper.concat(root, file.getFileName().getFilename())));
		assertEquals(1, file.getFixities().size());
				
	}
	
}
