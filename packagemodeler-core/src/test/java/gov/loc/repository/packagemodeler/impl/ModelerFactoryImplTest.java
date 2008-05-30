package gov.loc.repository.packagemodeler.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.AbstractCoreModelersTest;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;

public class ModelerFactoryImplTest extends AbstractCoreModelersTest {
	
	static System storageSystem;
	static Repository repository;

	@Override
	public void createFixtures() throws Exception {
		repository = fixtureHelper.createRepository(REPOSITORY_ID1);
		storageSystem = fixtureHelper.createSystem(RS25, new Role[] {storageSystemRole});
	}	
				
	@Test
	public void testCreateFileInstancesFromFileExaminations() throws Exception
	{
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		FileLocation fileLocation1 = modelerFactory.createStorageSystemFileLocation(packge, storageSystem, BASEPATH_1 + testCounter, true, true);
		FileExaminationGroup fileExaminationGroup1 = modelerFactory.createFileExaminationGroup(fileLocation1, true);
		final String FILE_IN_ROOT = "foo.txt";
		modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILE_IN_ROOT), new Fixity(FIXITY_1, Algorithm.MD5));
		modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1));
		modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_2), new Fixity(FIXITY_2, Algorithm.MD5));
		modelerFactory.createFileInstancesFromFileExaminations(fileLocation1, fileExaminationGroup1.getFileExaminations());

		FileLocation fileLocation2 = modelerFactory.createStorageSystemFileLocation(packge, storageSystem, BASEPATH_2 + testCounter, true, false);
		FileExaminationGroup fileExaminationGroup2 = modelerFactory.createFileExaminationGroup(fileLocation2, true);
		modelerFactory.createFileExamination(fileExaminationGroup2, new FileName(FILE_IN_ROOT), new Fixity(FIXITY_1, Algorithm.MD5));
		modelerFactory.createFileExamination(fileExaminationGroup2, new FileName(FILENAME_1));
		modelerFactory.createFileExamination(fileExaminationGroup2, new FileName(FILENAME_2), new Fixity(FIXITY_2, Algorithm.MD5));
		modelerFactory.createFileInstancesFromFileExaminations(fileLocation2, fileExaminationGroup2.getFileExaminations());
		
		this.template.save(packge);
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(fileLocation1);
		this.template.refresh(fileLocation2);
		
		assertEquals(2, fileLocation1.getFileInstances().size());
		for(FileInstance fileInstance : fileLocation1.getFileInstances())
		{
			if (fileInstance.getFileName().equals(new FileName(FILE_IN_ROOT)))
			{
				assertTrue(fileInstance.getFixities().isEmpty());
			}
			else
			{
				assertFalse(fileInstance.getFixities().isEmpty());
			}
		}
		
		assertEquals(2, fileLocation2.getFileInstances().size());
		for(FileInstance fileInstance : fileLocation2.getFileInstances())
		{
				assertFalse(fileInstance.getFixities().isEmpty());
		}
		
		txManager.commit(status);
	}
	
}
