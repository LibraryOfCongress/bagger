package gov.loc.repository.transfer.components.fileexamination.impl;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.junit.Assert.*;

import gov.loc.repository.constants.Roles;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.events.filelocation.InventoryFromFilesOnDiskEvent;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.transfer.components.AbstractCorePackageModelerAwareComponentTest;
import gov.loc.repository.transfer.components.fileexamination.FilesOnDiskInventorier;


public class FilesOnDiskInventorierImplTest extends AbstractCorePackageModelerAwareComponentTest {

	static Agent requestingAgent;
	static Repository repository;
	static System rdc;
	Package packge;
	
	@Autowired
	public FilesOnDiskInventorier inventorier;
	
	@Override
	public void createFixtures() throws Exception {
		repository = this.fixtureHelper.createRepository(REPOSITORY_ID1);
		rdc = this.fixtureHelper.createSystem(RDC, new Role[] {fixtureHelper.createRole(Roles.STORAGE_SYSTEM)});
		requestingAgent = this.fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
	}	

	@Override
	public void setup() throws Exception {
		packge = this.modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		this.template.save(packge);
	}
	
	@Test
	public void testInventory() throws Exception
	{
		FileLocation fileLocation = this.modelerFactory.createStorageSystemFileLocation(packge, rdc, BASEPATH_1, true, true);
		
		assertEquals(0, fileLocation.getFileInstances().size());
		assertEquals(0, fileLocation.getFileLocationEvents().size());
		
		inventorier.inventory(fileLocation, this.getFile("batch").toString(), FixityAlgorithm.MD5, reportingAgent );
		
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertEquals(6, fileLocation.getFileInstances().size());
		FileInstance fileInstance = this.dao.findFileInstance(fileLocation, new FileName("manifest-md5.txt"));
		assertNotNull(fileInstance);
		assertTrue(fileInstance.getFixities().isEmpty());
		fileInstance = this.dao.findFileInstance(fileLocation, new FileName("batch1/dir2/dir3/test5.txt"));
		assertNotNull(fileInstance);
		Fixity fixity = fileInstance.getFixity(FixityAlgorithm.MD5);
		assertNotNull(fixity);
		assertEquals("E3D704F3542B44A621EBED70DC0EFE13".toLowerCase(), fixity.getValue().toLowerCase());
		
		assertEquals(1, fileLocation.getFileLocationEvents(InventoryFromFilesOnDiskEvent.class).size());
		
		txManager.commit(status);
	}
}
