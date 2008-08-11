package gov.loc.repository.packagemodeler.packge.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.AbstractCoreModelersTest;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.impl.CanonicalFileImpl;

import org.hibernate.validator.InvalidStateException;

public class CanonicalFileImplTest extends AbstractCoreModelersTest {
	
	static Repository repository;	
	Package packge;
	
	@Override
	public void createFixtures() throws Exception {
		repository = fixtureHelper.createRepository(REPOSITORY_ID1);
	}	
	
	
	@Override
	public void setup() throws Exception
	{
		packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
	}
		
	@Test
	public void testCanonicalFile() throws Exception
	{
		CanonicalFile file1 = modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		file1.getFixities().add(new Fixity(FIXITY_2, FixityAlgorithm.SHA1));				

		this.template.save(packge);
		
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(file1);
				
		assertNotNull(file1);
		assertEquals(2, file1.getFixities().size());
		assertEquals(FIXITY_2, file1.getFixity(FixityAlgorithm.SHA1).getValue());
		
		txManager.commit(status);
		
	}
		
	@Test(expected=DataIntegrityViolationException.class) 
	public void testUniqueCanonicalFile() throws Exception
	{
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
		
		this.template.save(packge);
					
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
		CanonicalFile file1 = new CanonicalFileImpl();			
		file1.setFileName(new FileName(FILENAME_2));
		packge.addCanonicalFile(file1);
		this.template.save(packge);
	}
		
}
