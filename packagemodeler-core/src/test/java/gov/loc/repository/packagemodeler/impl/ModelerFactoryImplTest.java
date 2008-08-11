package gov.loc.repository.packagemodeler.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.fixity.FixityAlgorithm;
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
import gov.loc.repository.utilities.ResourceHelper;

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
		modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILE_IN_ROOT), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1));
		modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_2), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
		modelerFactory.createFileInstancesFromFileExaminations(fileLocation1, fileExaminationGroup1.getFileExaminations());

		FileLocation fileLocation2 = modelerFactory.createStorageSystemFileLocation(packge, storageSystem, BASEPATH_2 + testCounter, true, false);
		FileExaminationGroup fileExaminationGroup2 = modelerFactory.createFileExaminationGroup(fileLocation2, true);
		modelerFactory.createFileExamination(fileExaminationGroup2, new FileName(FILE_IN_ROOT), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		modelerFactory.createFileExamination(fileExaminationGroup2, new FileName(FILENAME_1));
		modelerFactory.createFileExamination(fileExaminationGroup2, new FileName(FILENAME_2), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
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
	
	@Test
	public void testCreateCanonicalFilesFromBagManifests() throws Exception
	{
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		List<File> manifestList = new ArrayList<File>();
		manifestList.add(this.getFile("bag/manifest-md5.txt"));
		manifestList.add(this.getFile("bag/manifest-sha1.txt"));
		
		modelerFactory.createCanonicalFilesFromBagManifests(packge, manifestList);
		this.template.save(packge);

		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(packge);
		
		assertEquals(5, packge.getCanonicalFiles().size());
		assertEquals(2, packge.getCanonicalFiles().iterator().next().getFixities().size());

		txManager.commit(status);
		
	}

	@Test
	public void testCreateFileInstancesFromBagManifests() throws Exception
	{
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		FileLocation fileLocation1 = modelerFactory.createStorageSystemFileLocation(packge, storageSystem, BASEPATH_1 + testCounter, true, true);
		List<File> manifestList = new ArrayList<File>();
		manifestList.add(this.getFile("bag/manifest-md5.txt"));
		manifestList.add(this.getFile("bag/manifest-sha1.txt"));
		
		modelerFactory.createFileInstancesFromBagManifests(fileLocation1, manifestList);
		this.template.save(fileLocation1);

		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(fileLocation1);
		
		assertEquals(5, fileLocation1.getFileInstances().size());
		assertEquals(2, fileLocation1.getFileInstances().iterator().next().getFixities().size());

		txManager.commit(status);

		List<File> tagManifestList = new ArrayList<File>();
		tagManifestList.add(this.getFile("bag/tagmanifest-sha1.txt"));
		
		modelerFactory.createFileInstancesFromBagTagManifests(fileLocation1, tagManifestList);
		this.template.update(fileLocation1);

		status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(fileLocation1);
		
		assertEquals(8, fileLocation1.getFileInstances().size());

		txManager.commit(status);		
	}
	
	
	protected File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this.getClass(), filename);
	}
	
}
