package gov.loc.repository.packagemodeler.packge.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.AbstractModelersTest;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.packagemodeler.packge.impl.CanonicalFileImpl;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.validator.InvalidStateException;

public class CanonicalFileImplTest extends AbstractModelersTest {
	
	protected Repository repository;	
	protected Package packge;
	protected PackageModelDAO dao = new PackageModelDAOImpl();
	
	@Override
	public void createFixtures() throws Exception {
		fixtureHelper.createRepository(REPOSITORY_ID1);
	}	
	
	
	@Override
	public void setup() throws Exception
	{
		packge = modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
	}
		
	@Test
	public void testCanonicalFile() throws Exception
	{
		CanonicalFile file1 = modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		file1.getFixities().add(new Fixity(FIXITY_2, Algorithm.SHA1));				

		session.save(packge);
		this.commitAndRestartTransaction();
		
		this.session.refresh(file1);
		
		assertNotNull(file1);
		assertEquals(2, file1.getFixities().size());
		assertEquals(FIXITY_2, file1.getFixity(Algorithm.SHA1).getValue());		
	}
		
	@Test(expected=ConstraintViolationException.class) 
	public void testUniqueCanonicalFile() throws Exception
	{
		try
		{
			modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
			modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_2, Algorithm.MD5));
						
			session.save(packge);
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
		CanonicalFile file1 = new CanonicalFileImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file1.getFixities().add(new Fixity(FIXITY_2, this.fixityAlgorithm2));						
		file1.setFileName(new FileName(FILENAME_1));
		
		CanonicalFile file2 = new CanonicalFileImpl();		
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file2.setFileName(new FileName(FILENAME_1));

		assertTrue(file1.equals(file2));
	}

	@Test
	public void testNotEqualsFilenameMismatch() throws Exception
	{
		CanonicalFile file1 = new CanonicalFileImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));
		file1.getFixities().add(new Fixity(FIXITY_2, this.fixityAlgorithm2));						
		file1.setFileName(new FileName(FILENAME_1));
		
		CanonicalFile file2 = new CanonicalFileImpl();
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));						
		file2.setFileName(new FileName("x" + FILENAME_1));

		assertFalse(file1.equals(file2));
	}	
	
	@Test
	public void testNotEqualsFixityMismatch() throws Exception
	{
		CanonicalFile file1 = new CanonicalFileImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));		
		file1.getFixities().add(new Fixity(FIXITY_2, this.fixityAlgorithm2));						
		file1.setFileName(new FileName(FILENAME_1));
		
		CanonicalFile file2 = new CanonicalFileImpl();
		file2.getFixities().add(new Fixity("x" + FIXITY_1, this.fixityAlgorithm1));						
		file2.setFileName(new FileName(FILENAME_1));

		assertFalse(file1.equals(file2));
	}
	
	@Test
	public void testNotEqualsNoFixityMatch() throws Exception
	{
		CanonicalFile file1 = new CanonicalFileImpl();
		file1.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm1));		
		file1.setFileName(new FileName(FILENAME_1));
		
		CanonicalFile file2 = new CanonicalFileImpl();
		file2.getFixities().add(new Fixity(FIXITY_1, this.fixityAlgorithm2));						
		file2.setFileName(new FileName(FILENAME_1));

		assertFalse(file1.equals(file2));
	}	
	*/
	
	@Test(expected=InvalidStateException.class)
	public void testRequiredFixity() throws Exception
	{
		try
		{
			CanonicalFile file1 = new CanonicalFileImpl();			
			file1.setFileName(new FileName(FILENAME_2));
			packge.addCanonicalFile(file1);
			session.save(packge);
		}
		catch(Exception ex)
		{
			session.getTransaction().rollback();
			throw ex;
		}
	}
		
}
