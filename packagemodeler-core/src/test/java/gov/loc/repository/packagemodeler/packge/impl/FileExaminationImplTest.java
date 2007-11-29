package gov.loc.repository.packagemodeler.packge.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.AbstractModelersTest;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;

import org.hibernate.exception.ConstraintViolationException;

public class FileExaminationImplTest extends AbstractModelersTest {
	
	protected FileExaminationGroup fileExaminationGroup1;	
		
	@Override
	public void createFixtures() throws Exception {
		fixtureHelper.createRepository(REPOSITORY_ID1);
		fixtureHelper.createStorageSystem(RS25);
	}	

	@Override
	public void setup() throws Exception
	{
		
		Package packge = modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
		session.save(packge);
		FileLocation fileLocation = modelerFactory.createStorageSystemFileLocation(packge, dao.findRequiredAgent(System.class, RS25), BASEPATH_1 + testCounter, true, true);
		fileExaminationGroup1 = modelerFactory.createFileExaminationGroup(fileLocation, false);
		
		this.commitAndRestartTransaction();
		
		fixtureHelper.reload(fileExaminationGroup1);		
	}
	
	@Test
	public void testFileExamination() throws Exception
	{				
		//A typical file observation
		FileExamination fileExamination1 = modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		fileExamination1.getFixities().add(new Fixity(FIXITY_2, Algorithm.SHA1));
				
		session.save(fileExaminationGroup1);
		this.commitAndRestartTransaction();		
		assertNotNull(fileExamination1);
		assertEquals(2, fileExamination1.getFixities().size());
		assertEquals(FIXITY_2, fileExamination1.getFixity(Algorithm.SHA1).getValue());
		assertEquals(fileExaminationGroup1, fileExamination1.getFileExaminationGroup());
	}

	@Test
	public void testMissingFileExamination() throws Exception
	{
		//A file observation for a file that is missing
		FileExamination fileExamination1 = modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1));

		session.save(fileExaminationGroup1);
		this.commitAndRestartTransaction();		

		assertNotNull(fileExamination1);
		assertEquals(0, fileExamination1.getFixities().size());
	}
	
	@Test(expected=ConstraintViolationException.class) 
	public void testUniqueFileObservation() throws Exception
	{
		try
		{
			modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
			modelerFactory.createFileExamination(fileExaminationGroup1, new FileName(FILENAME_1), new Fixity(FIXITY_2, Algorithm.MD5));
						
			session.save(fileExaminationGroup1);
			session.getTransaction().commit();
		}
		catch(Exception ex)
		{
			session.getTransaction().rollback();
			throw ex;
		}
		
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
