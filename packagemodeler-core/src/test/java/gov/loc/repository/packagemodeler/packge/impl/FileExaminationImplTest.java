package gov.loc.repository.packagemodeler.packge.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.AbstractCoreModelersTest;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;

public class FileExaminationImplTest extends AbstractCoreModelersTest {
	
	static Repository repository;
	static System rs25;
	FileExaminationGroup fileExaminationGroup1;	
		
	@Override
	public void createFixtures() throws Exception {
		repository = fixtureHelper.createRepository(REPOSITORY_ID1);
		rs25 = fixtureHelper.createSystem(RS25, new Role[] {storageSystemRole});
	}	

	@Override
	public void setup() throws Exception
	{
		
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		FileLocation fileLocation = modelerFactory.createStorageSystemFileLocation(packge, rs25, BASEPATH_1 + testCounter, true, true);
		
		this.template.save(packge);
		
		fileExaminationGroup1 = modelerFactory.createFileExaminationGroup(fileLocation, false);
	}
	
	@Test
	public void testFileExamination() throws Exception
	{				
		//A typical file observation
		FileExamination fileExamination1 = modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		fileExamination1.getFixities().add(new Fixity(FIXITY_2, Algorithm.SHA1));

		this.template.save(fileExaminationGroup1);
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(fileExamination1);

		assertNotNull(fileExamination1);
		assertEquals(2, fileExamination1.getFixities().size());
		assertEquals(FIXITY_2, fileExamination1.getFixity(Algorithm.SHA1).getValue());
		assertEquals(fileExaminationGroup1.getKey(), fileExamination1.getFileExaminationGroup().getKey());
		
		txManager.commit(status);
	}

	@Test
	public void testMissingFileExamination() throws Exception
	{
		//A file observation for a file that is missing
		FileExamination fileExamination1 = modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1));

		this.template.save(fileExaminationGroup1);
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(fileExamination1);

		assertNotNull(fileExamination1);
		assertEquals(0, fileExamination1.getFixities().size());
		
		txManager.commit(status);
		
	}
	
	@Test(expected=DataIntegrityViolationException.class) 
	public void testUniqueFileObservation() throws Exception
	{
		modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1), new Fixity(FIXITY_2, Algorithm.MD5));
		
		this.template.save(fileExaminationGroup1);		
	}
	
	/*
	@Test
	public void testEquals() throws Exception
	{
		FileExamination file1 = new FileExaminationImpl();
		file1.setObservationDate(Calendar.getInstance());
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file1.getFixities().add(new Fixity(FIXITY_2, this.fixityAlgorithm2));
		file1.setFileName(new FileName(FILENAME_1));
		
		FileExamination file2 = new FileExaminationImpl();
		file2.setObservationDate(Calendar.getInstance());
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file2.setFileName(new FileName(FILENAME_1));

		assertTrue(file1.equals(file2));
	}

	@Test
	public void testNotEqualsFilenameMismatch() throws Exception
	{
		FileExamination file1 = new FileExaminationImpl();
		file1.setObservationDate(Calendar.getInstance());
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file1.getFixities().add(new Fixity(FIXITY_2, this.fixityAlgorithm2));
		file1.setFileName(new FileName(FILENAME_1));
		
		FileExamination file2 = new FileExaminationImpl();
		file2.setObservationDate(Calendar.getInstance());
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file2.setFileName(new FileName(FILENAME_2));

		assertFalse(file1.equals(file2));
	}	
	
	@Test
	public void testNotEqualsFixityMismatch() throws Exception
	{
		FileExamination file1 = new FileExaminationImpl();
		file1.setObservationDate(Calendar.getInstance());
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file1.getFixities().add(new Fixity(FIXITY_2, this.fixityAlgorithm2));
		file1.setFileName(new FileName(FILENAME_1));
		
		FileExamination file2 = new FileExaminationImpl();
		file2.setObservationDate(Calendar.getInstance());
		file2.getFixities().add(new Fixity(FIXITY_3, this.fixityAlgorithm1));
		file2.setFileName(new FileName(FILENAME_1));

		assertFalse(file1.equals(file2));
	}
	
	@Test
	public void testNotEqualsNoFixityMatch() throws Exception
	{
		FileExamination file1 = new FileExaminationImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));		
		file1.setFileName(new FileName(FILENAME_1));
		
		FileExamination file2 = new FileExaminationImpl();
		file2.setObservationDate(Calendar.getInstance());		
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm2));
		file2.setFileName(new FileName(FILENAME_1));

		assertFalse(file1.equals(file2));
	}	
	*/
}
