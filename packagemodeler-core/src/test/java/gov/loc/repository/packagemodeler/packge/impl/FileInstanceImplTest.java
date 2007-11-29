package gov.loc.repository.packagemodeler.packge.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.AbstractModelersTest;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.packagemodeler.packge.impl.FileExaminationImpl;
import gov.loc.repository.packagemodeler.packge.impl.FileInstanceImpl;

import org.hibernate.exception.ConstraintViolationException;

public class FileInstanceImplTest extends AbstractModelersTest {
	
	protected FileLocation fileLocation1;
	
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
		fileLocation1 = modelerFactory.createStorageSystemFileLocation(packge, dao.findRequiredAgent(System.class, RS25), BASEPATH_1 + testCounter, true, true);
		
		this.commitAndRestartTransaction();
		
		fixtureHelper.reload(fileLocation1);		
	}
	
	
	@Test
	public void testFileInstance() throws Exception
	{		
		FileInstance file1 = modelerFactory.createFileInstance(fileLocation1, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));		
		file1.getFixities().add(new Fixity(FIXITY_2, Algorithm.SHA1));

		FileInstance file2 = modelerFactory.createFileInstance(fileLocation1, new FileName(FILENAME_2));
		
		session.save(fileLocation1);
		this.commitAndRestartTransaction();
		
		fixtureHelper.reload(file1);
		assertNotNull(file1);
		assertEquals(2, file1.getFixities().size());
		assertEquals(FIXITY_2, file1.getFixity(Algorithm.SHA1).getValue());
		fixtureHelper.reload(file2);
		assertNotNull(file2);
		assertEquals(0, file2.getFixities().size());
		
	}
		
	@Test(expected=ConstraintViolationException.class) 
	public void testUniqueFileInstance() throws Exception
	{
		try
		{
			modelerFactory.createFileInstance(fileLocation1, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));		
			modelerFactory.createFileInstance(fileLocation1, new FileName(FILENAME_1), new Fixity(FIXITY_2, Algorithm.MD5));
						
			session.save(fileLocation1);
			session.getTransaction().commit();
		}
		catch(Exception ex)
		{
			session.getTransaction().rollback();
			throw ex;
		}
		
	}

	@Test
	public void testFileInstanceMatches() throws Exception
	{
		FileInstance fileInstance1 = new FileInstanceImpl();
		FileInstance fileInstance2 = new FileInstanceImpl();
		
		assertTrue(fileInstance1.matches(fileInstance2));
		
		fileInstance1.getFixities().add(new Fixity(FIXITY_1, Algorithm.MD5));
		assertFalse(fileInstance1.matches(fileInstance2));
		fileInstance2.getFixities().add(new Fixity(FIXITY_1, Algorithm.MD5));
		assertTrue(fileInstance1.matches(fileInstance2));
		fileInstance1.getFixities().add(new Fixity(FIXITY_2, Algorithm.SHA1));
		assertTrue(fileInstance1.matches(fileInstance2));
		fileInstance2.getFixities().add(new Fixity(FIXITY_3, Algorithm.SHA1));
		assertFalse(fileInstance1.matches(fileInstance2));
	}

	@Test
	public void testFileExaminationMatches() throws Exception
	{
		FileInstance fileInstance = new FileInstanceImpl();
		FileExamination fileExamination = new FileExaminationImpl();
		
		assertTrue(fileInstance.matches(fileExamination));
		
		fileInstance.getFixities().add(new Fixity(FIXITY_1, Algorithm.MD5));
		assertFalse(fileInstance.matches(fileExamination));
		fileExamination.getFixities().add(new Fixity(FIXITY_1, Algorithm.MD5));
		assertTrue(fileInstance.matches(fileExamination));
		fileInstance.getFixities().add(new Fixity(FIXITY_2, Algorithm.SHA1));
		assertTrue(fileInstance.matches(fileExamination));
		fileExamination.getFixities().add(new Fixity(FIXITY_3, Algorithm.SHA1));
		assertFalse(fileInstance.matches(fileExamination));
	}
	
	/*
	@Test
	public void testEquals() throws Exception
	{
		FileInstance file1 = new FileInstanceImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));		
		file1.getFixities().add(new Fixity("hijklmn", this.fixityAlgorithm2));				
		file1.setFileName(new FileName(FILENAME_1));
		
		FileInstance file2 = new FileInstanceImpl();
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file2.setFileName(new FileName(FILENAME_1));

		assertTrue(file1.equals(file2));
	}

	@Test
	public void testNotEqualsFilenameMismatch() throws Exception
	{
		FileInstance file1 = new FileInstanceImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));		
		file1.getFixities().add(new Fixity("hijklmn", this.fixityAlgorithm2));				
		file1.setFileName(new FileName(FILENAME_1));
		
		FileInstance file2 = new FileInstanceImpl();
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));				
		file2.setFileName(new FileName(FILENAME_2));

		assertFalse(file1.equals(file2));
	}	
	
	@Test
	public void testNotEqualsFixityMismatch() throws Exception
	{
		FileInstance file1 = new FileInstanceImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));		
		file1.getFixities().add(new Fixity(FIXITY_2, this.fixityAlgorithm2));				
		file1.setFileName(new FileName(FILENAME_1));
		
		FileInstance file2 = new FileInstanceImpl();
		file2.getFixities().add(new Fixity(FIXITY_3, this.fixityAlgorithm1));		
		file2.setFileName(new FileName(FILENAME_1));

		assertFalse(file1.equals(file2));
	}
	
	@Test
	public void testNotEqualsNoFixityMatch() throws Exception
	{
		FileInstance file1 = new FileInstanceImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));						
		file1.setFileName(new FileName(FILENAME_1));
		
		FileInstance file2 = new FileInstanceImpl();
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm2));		
		file2.setFileName(new FileName(FILENAME_1));

		assertFalse(file1.equals(file2));
	}	
	*/
		
	
}
