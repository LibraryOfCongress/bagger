package gov.loc.repository.transfer.components.fileexamination.impl;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.*;

import org.junit.Test;
import static org.junit.Assert.*;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.events.filelocation.InventoryFromFilesOnDiskEvent;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.transfer.components.AbstractComponentTest;
import gov.loc.repository.utilities.FixityHelper;
import gov.loc.repository.utilities.impl.JavaSecurityFixityHelper;


public class FilesOnDiskInventorierImplTest extends AbstractComponentTest {

	protected static Agent requestingAgent;
	Package packge;
	FilesOnDiskInventorierImpl inventorier;
	
	@Override
	public void createFixtures() throws Exception {
		this.fixtureHelper.createRepository(REPOSITORY_ID1);
		this.fixtureHelper.createStorageSystem(RDC);
		requestingAgent = this.fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
	}	

	@Override
	public void setup() throws Exception {
		FixityHelper fixityHelper = new JavaSecurityFixityHelper();

		inventorier = new FilesOnDiskInventorierImpl();
		inventorier.setFixityHelper(fixityHelper);
		inventorier.setModelerFactory(this.modelerFactory);
		inventorier.setPackageModelDao(this.packageModelDao);
				
		packge = this.modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
		this.session.save(packge);
	}
	
	@Test
	public void testInventory() throws Exception
	{
		FileLocation fileLocation = this.modelerFactory.createStorageSystemFileLocation(packge, RDC, BASEPATH_1, true, true);
		
		assertEquals(0, fileLocation.getFileInstances().size());
		assertEquals(0, fileLocation.getFileLocationEvents().size());
		
		inventorier.inventory(fileLocation, this.getFile("batch").toString(), Fixity.Algorithm.MD5, this.packageModelDao.findRequiredAgent(Agent.class, this.getReportingAgent()));
		
		this.commitAndRestartTransaction();

		assertEquals(6, fileLocation.getFileInstances().size());
		FileInstance fileInstance = this.packageModelDao.findFileInstance(fileLocation, new FileName("manifest-md5.txt"));
		assertNotNull(fileInstance);
		assertTrue(fileInstance.getFixities().isEmpty());
		fileInstance = this.packageModelDao.findFileInstance(fileLocation, new FileName("batch1/dir2/dir3/test5.txt"));
		assertNotNull(fileInstance);
		Fixity fixity = fileInstance.getFixity(Fixity.Algorithm.MD5);
		assertNotNull(fixity);
		assertEquals("E3D704F3542B44A621EBED70DC0EFE13".toLowerCase(), fixity.getValue().toLowerCase());
		
		assertEquals(1, fileLocation.getFileLocationEvents(InventoryFromFilesOnDiskEvent.class).size());
	}
}
