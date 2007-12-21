package gov.loc.repository.transfer.components.filemanagement.impl;

import static gov.loc.repository.constants.Agents.RDC;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.PACKAGE_ID1;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.PERSON_FIRSTNAME1;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.PERSON_ID1;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.PERSON_SURNAME1;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.REPOSITORY_ID1;
import static org.junit.Assert.*;

import java.io.File;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.impl.ModelerFactoryImpl;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.transfer.components.AbstractComponentTest;
import gov.loc.repository.transfer.components.fileexamination.FileExaminer;
import gov.loc.repository.transfer.components.fileexamination.impl.FileExaminerImpl;
import gov.loc.repository.transfer.components.filemanagement.FileCopier;
import gov.loc.repository.transfer.components.filemanagement.filters.FileFilter;
import gov.loc.repository.transfer.components.filemanagement.impl.FileCopierImpl;
import gov.loc.repository.utilities.FixityHelper;
import gov.loc.repository.utilities.impl.JavaSecurityFixityHelper;
import gov.loc.repository.utilities.persistence.HibernateUtil;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

public class FileCopierImplTest extends AbstractComponentTest {

	FileCopier copier;
	Package packge;
	FileExaminer examiner;
	protected static Agent requestingAgent;
	
	@Override
	public void createFixtures() throws Exception {
		this.fixtureHelper.createRepository(REPOSITORY_ID1);
		this.fixtureHelper.createStorageSystem(RDC);
		requestingAgent = this.fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
	}
	
	@Override
	public void setup() throws Exception {
		copier = new FileCopierImpl();
		copier.setModelerFactory(new ModelerFactoryImpl());
		copier.setPackageModelDao(new PackageModelDAOImpl());
	
		examiner = new FileExaminerImpl();
		examiner.setModelerFactory(new ModelerFactoryImpl());
		examiner.setPackageModelDao(new PackageModelDAOImpl());
		FixityHelper fixityHelper = new JavaSecurityFixityHelper();
		fixityHelper.setAlgorithm("MD5");
		examiner.setFixityHelper(fixityHelper);
		copier.setFileExaminer(examiner);
				
		packge = this.modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
		this.session.save(packge);
	}	

	@Test
	public void testCopyLCFileLocationToLCFileLocation() throws Exception {
		//Create a Source File Location and File Instances
		File srcPackageDir = this.getFile("lcpackage");
		StorageSystemFileLocation srcFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, srcPackageDir.toString(), true, true);
		this.examiner.examine(srcFileLocation, requestingAgent, true);
		
		//Create a Destination File Location
		File destPackageDir = new File(srcPackageDir.getParent(), "copiedpackage" + testCounter);
		if (destPackageDir.exists())
		{
			FileUtils.deleteDirectory(destPackageDir);
		}
		StorageSystemFileLocation destFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, destPackageDir.toString(), true, true);
		
		this.commitAndRestartTransaction();
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);
		
		assertTrue(this.copier.copy(srcFileLocation, destFileLocation, requestingAgent, null));
		
		this.commitAndRestartTransaction();
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);		
		
		//Make sure the files are copied
		assertTrue(destPackageDir.exists());
		assertTrue(new File(destPackageDir, "manifest-md5.txt").exists());
		assertTrue(new File(destPackageDir, "contents/batch.xml").exists());
		assertTrue(new File(destPackageDir, "contents/encyclopedia/encyclopedia.xml").exists());
		
		//Check for File Copy Event
		FileCopyEvent event = destFileLocation.getMostRecentFileLocationEvent(FileCopyEvent.class, false);
		assertNotNull(event);
		assertEquals(srcFileLocation.getKey(), event.getFileLocationSource().getKey());
		
		//Check for new FileExaminations
		assertEquals(1, destFileLocation.getFileExaminationGroups().size());
		
		//Check for new File Instances
		assertEquals(srcFileLocation.getFileInstances().size(), destFileLocation.getFileInstances().size());
	}

	@After
	public void reset() throws Exception
	{
		//Need to reset the db, otherwise have key constraint problems with File Locations
		isSetup = false;
		HibernateUtil.createDatabase();
		
	}
	
	@Test
	public void testCopyNonLCFileLocationToNonLCFileLocation() throws Exception {
		
		
		//Create a Source File Location and File Instances
		File srcPackageDir = this.getFile("non_lcpackage");
		StorageSystemFileLocation srcFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, srcPackageDir.toString(), true, false);
		this.examiner.examine(srcFileLocation, requestingAgent, true);
		
		//Create a Destination File Location
		File destPackageDir = new File(srcPackageDir.getParent(), "copiedpackage" + testCounter);
		if (destPackageDir.exists())
		{
			FileUtils.deleteDirectory(destPackageDir);
		}
		StorageSystemFileLocation destFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, destPackageDir.toString(), true, false);
		
		this.commitAndRestartTransaction();
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);
		
		assertTrue(this.copier.copy(srcFileLocation, destFileLocation, requestingAgent, null));
		
		this.commitAndRestartTransaction();
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);		
		
		//Make sure the files are copied
		assertTrue(destPackageDir.exists());
		assertTrue(new File(destPackageDir, "batch.xml").exists());
		assertTrue(new File(destPackageDir, "encyclopedia/encyclopedia.xml").exists());
		
		//Check for File Copy Event
		FileCopyEvent event = destFileLocation.getMostRecentFileLocationEvent(FileCopyEvent.class, false);
		assertNotNull(event);
		assertEquals(srcFileLocation.getKey(), event.getFileLocationSource().getKey());
		
		//Check for new FileExaminations
		assertEquals(1, destFileLocation.getFileExaminationGroups().size());
		
		//Check for new File Instances
		assertEquals(srcFileLocation.getFileInstances().size(), destFileLocation.getFileInstances().size());
	}

	@Test(expected=Exception.class)
	public void testCopyLCFileLocationToNonLCFileLocation() throws Exception {
		//Create a Source File Location and File Instances
		File srcPackageDir = this.getFile("lcpackage");
		StorageSystemFileLocation srcFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, srcPackageDir.toString(), true, true);
		this.examiner.examine(srcFileLocation, requestingAgent, true);
		
		//Create a Destination File Location
		File destPackageDir = new File(srcPackageDir.getParent(), "copiedpackage" + testCounter);
		if (destPackageDir.exists())
		{
			FileUtils.deleteDirectory(destPackageDir);
		}
		StorageSystemFileLocation destFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, destPackageDir.toString(), true, false);
		
		this.packageModelDao.save(this.packge);
		this.commitAndRestartTransaction();
		
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);
		
		assertTrue(this.copier.copy(srcFileLocation, destFileLocation, requestingAgent, null));
		
	}

	@Test
	public void testCopyNonLCFileLocationToLCFileLocation() throws Exception {
		//Create a Source File Location and File Instances
		File srcPackageDir = this.getFile("non_lcpackage");
		StorageSystemFileLocation srcFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, srcPackageDir.toString(), true, false);
		this.examiner.examine(srcFileLocation, requestingAgent, true);
		
		//Create a Destination File Location
		File destPackageDir = new File(srcPackageDir.getParent(), "copiedpackage" + testCounter);
		if (destPackageDir.exists())
		{
			FileUtils.forceDelete(destPackageDir);
		}
		StorageSystemFileLocation destFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, destPackageDir.toString(), true, true);
		
		this.commitAndRestartTransaction();
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);
		
		assertTrue(this.copier.copy(srcFileLocation, destFileLocation, requestingAgent, null));
		
		this.commitAndRestartTransaction();
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);		
		
		//Make sure the files are copied
		assertTrue(destPackageDir.exists());
		assertTrue(new File(destPackageDir, "contents/batch.xml").exists());
		assertTrue(new File(destPackageDir, "contents/encyclopedia/encyclopedia.xml").exists());
		
		//Check for File Copy Event
		FileCopyEvent event = destFileLocation.getMostRecentFileLocationEvent(FileCopyEvent.class, false);
		assertNotNull(event);
		assertEquals(srcFileLocation.getKey(), event.getFileLocationSource().getKey());
		
		//Check for new FileExaminations
		assertEquals(1, destFileLocation.getFileExaminationGroups().size());
		
		//Check for new File Instances
		assertEquals(srcFileLocation.getFileInstances().size(), destFileLocation.getFileInstances().size());
	}

	@Test
	public void testCopyLCFileLocationToLCFileLocationWithFileFilter() throws Exception {
		//Create a Source File Location and File Instances
		File srcPackageDir = this.getFile("lcpackage");
		StorageSystemFileLocation srcFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, srcPackageDir.toString(), true, true);
		this.examiner.examine(srcFileLocation, requestingAgent, true);
		
		//Create a Destination File Location
		File destPackageDir = new File(srcPackageDir.getParent(), "copiedpackage" + testCounter);
		if (destPackageDir.exists())
		{
			FileUtils.deleteDirectory(destPackageDir);
		}
		StorageSystemFileLocation destFileLocation = this.modelerFactory.createStorageSystemFileLocation(this.packge, RDC, destPackageDir.toString(), true, true);
		
		this.commitAndRestartTransaction();
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);
		
		//Doesn't accept if filename starts with "b"
		FileFilter filter = new DummyFileFilter();
		filter.setFileLocation(srcFileLocation);
		
		assertTrue(this.copier.copy(srcFileLocation, destFileLocation, requestingAgent, filter));
		
		this.commitAndRestartTransaction();
		this.session.refresh(srcFileLocation);
		this.session.refresh(destFileLocation);		
		
		//Make sure the files are copied
		assertTrue(destPackageDir.exists());
		assertTrue(new File(destPackageDir, "manifest-md5.txt").exists());
		assertFalse(new File(destPackageDir, "contents/batch.xml").exists());
		assertFalse(new File(destPackageDir, "contents/batch_1.xml").exists());
		assertTrue(new File(destPackageDir, "contents/encyclopedia/encyclopedia.xml").exists());
		
		//Check for File Copy Event
		FileCopyEvent event = destFileLocation.getMostRecentFileLocationEvent(FileCopyEvent.class, false);
		assertNotNull(event);
		assertEquals(srcFileLocation.getKey(), event.getFileLocationSource().getKey());
		
		//Check for new FileExaminations
		assertEquals(1, destFileLocation.getFileExaminationGroups().size());
		
		//Check for new File Instances
		assertTrue(srcFileLocation.getFileInstances().size() != destFileLocation.getFileInstances().size());
	}
	
}
