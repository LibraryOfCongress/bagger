package gov.loc.repository.packagemodeler.dao.impl;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.exceptions.RequiredEntityNotFound;
import gov.loc.repository.packagemodeler.AbstractCoreModelersTest;
import gov.loc.repository.packagemodeler.agents.Organization;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.dao.FileListComparisonResult;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.results.Result;
import gov.loc.repository.results.ResultList;

public class PackageModelDAOImplTest extends AbstractCoreModelersTest {
	
	static Repository repository;
	static System rs25;
	static System rdc;

	FileLocation fileLocation1;
	FileExaminationGroup fileExaminationGroup1;
	FileName fileName1;
	
	@Resource(name="packageModelDao")
	PackageModelDAO dao;
		
	@Override
	public void createFixtures() throws Exception {
		repository = fixtureHelper.createRepository(REPOSITORY_ID1);
		rs25 = fixtureHelper.createSystem(RS25, new Role[] {storageSystemRole});
		rdc = fixtureHelper.createSystem(RDC, new Role[] {storageSystemRole});
		fixtureHelper.createSystem(JBPM);
		fixtureHelper.createOrganization(ORGANIZATION_ID1, ORGANIZATION_NAME1, new Role[] {fixtureHelper.createRole(ROLE_1)});		
	}
		
	@Override
	public void setup() throws Exception
	{
		fileName1 = new FileName(FILENAME_1);
				
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		modelerFactory.createCanonicalFile(packge, fileName1, new Fixity(FIXITY_1, FixityAlgorithm.MD5));		
		fileLocation1 = modelerFactory.createStorageSystemFileLocation(packge, rs25, BASEPATH_1 + testCounter, true, true);		
		modelerFactory.createFileInstance(fileLocation1, fileName1, new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		fileExaminationGroup1 = modelerFactory.createFileExaminationGroup(fileLocation1, true);		
		modelerFactory.createFileExamination(fileExaminationGroup1, fileName1, new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		
		this.template.save(packge);
		
	}		

	@Test
	public void testFindPackage() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertNotNull(dao.findPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter));
		assertNull(dao.findPackage(Package.class, REPOSITORY_ID1, "x" + PACKAGE_ID1 + testCounter));
		
		txManager.commit(status);
	}

	/*
	@Test(expected=IllegalTransactionStateException.class)
	public void testRequiredTransaction() throws Exception
	{
		assertNotNull(dao.findPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter));
	}
	*/
	
	@Test(expected=Exception.class)
	public void testFindRequiredPackage() throws Exception
	{
		dao.findRequiredPackage(Package.class, REPOSITORY_ID1, "x" + PACKAGE_ID1 + testCounter);
	}

	
	@Test
	public void testFindPackages() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertTrue(dao.findPackages(Package.class).size() > 0);
		assertTrue(dao.findPackages(Package.class).get(0) instanceof Package);
		
		txManager.commit(status);
		
	}

	@Test
	public void testFindPackagesWithFileCount() throws Exception
	{
		//Let's also make sure there is a package with no canonical files
		Package packge1 = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID2 + testCounter);
		this.template.save(packge1);
				
		//And another with no html files
		Package packge2 = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID3 + testCounter);
		modelerFactory.createCanonicalFile(packge2, new FileName("test.xml"), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
		this.template.save(packge2);
		
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		
		ResultList resultList = dao.findPackagesWithFileCount(Package.class, "html");
		Iterator<Result> resultIter = resultList.iterator();
		assertTrue(resultIter.hasNext());
		int assertPackageCount = dao.findPackages(Package.class).size();
		int packageCount = 0;
		while(resultIter.hasNext())
		{
			Map<String, Object> resultMap = resultIter.next();
			Package packge = (Package)resultMap.get("package");
			Long fileCount = new Long(0);
			for(CanonicalFile canonicalFile : packge.getCanonicalFiles())
			{
				if ("html".equals(canonicalFile.getFileName().getExtension()))
				{
					fileCount++;
				}
			}
			assertEquals(fileCount, (Long)resultMap.get("file_count"));
			packageCount++;
		}
		assertEquals(assertPackageCount, packageCount);
		
		txManager.commit(status);

	}
	
	@Test
	public void testFindCanonicalFile() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertNotNull(dao.findCanonicalFile(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, FILENAME_1));
		assertNull(dao.findCanonicalFile(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, "x" + FILENAME_1));
		
		txManager.commit(status);

	}

	@Test
	public void testFindFileInstance() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertNotNull(dao.findFileInstance(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1 + testCounter, FILENAME_1));
		assertNull(dao.findFileInstance(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1 + testCounter, "x" + FILENAME_1));
		assertNotNull(dao.findFileInstance(fileLocation1, new FileName(FILENAME_1)));
		
		txManager.commit(status);
		
	}

	@Test
	public void testFindFileExamination() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertNotNull(dao.findFileExamination(fileExaminationGroup1, fileName1));
		assertNull(dao.findFileExamination(fileExaminationGroup1, new FileName("x" + FILENAME_1)));

		txManager.commit(status);

	}
	
	@Test
	public void testFindRepositories() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertEquals(1, dao.findRepositories().size());
		assertTrue(dao.findRepositories().get(0) instanceof Repository);

		txManager.commit(status);

	}

	@Test
	public void testFindRepository() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertNotNull(dao.findRepository(REPOSITORY_ID1));
		assertNull(dao.findRepository("x" + REPOSITORY_ID1));
		
		txManager.commit(status);
		
	}

	@Test(expected=RequiredEntityNotFound.class)
	public void testFindRequiredRepository() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		try
		{
			dao.findRequiredRepository("x" + REPOSITORY_ID1);
		}
		finally
		{
			txManager.rollback(status);	
		}		
	}

	
	@Test
	public void testFindAgent() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		
		Organization organization = dao.findAgent(Organization.class, ORGANIZATION_ID1);
		assertNotNull(organization);
		assertEquals(ORGANIZATION_ID1, organization.getId());
		
		txManager.commit(status);
	}

	@Test(expected=RequiredEntityNotFound.class)
	public void testFindRequiredAgent() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		try
		{
			dao.findRequiredAgent(Organization.class, "x" + ORGANIZATION_ID1);
		}
		finally
		{
			txManager.rollback(status);
		}
	}

	@Test
	public void testFindRole() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());		

		Role role = dao.findRole(ROLE_1);
		assertNotNull(role);
		assertEquals(1, role.getAgentSet().size());
		
		assertNull(dao.findRole("x" + ROLE_1));
		
		txManager.commit(status);
	}
	
	@Test(expected=RequiredEntityNotFound.class)
	public void testFindRequiredRole() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		try
		{
			dao.findRequiredRole("x" + ROLE_1);
		}
		finally
		{
			txManager.rollback(status);
		}
		
	}
	
	@Test
	public void testCompareFileInstancesFileExaminations() throws Exception
	{
		//Comparing file instances and file examinations
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID2 + testCounter);
		FileLocation fileLocation = modelerFactory.createStorageSystemFileLocation(packge, rdc, BASEPATH_1 + testCounter, true, true);
		FileExaminationGroup fileExaminationGroup = modelerFactory.createFileExaminationGroup(fileLocation, true);
			
		//Add file instance for FILE1
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_1), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		
		this.template.save(packge);

		//FILE1 should be in file instances, but not file examinations
		FileListComparisonResult result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(1, result.missingFromTargetList.size());
		assertEquals(new FileName(FILENAME_1), result.missingFromTargetList.get(0));

		//Add file exam for FILE1
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_1), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		this.template.update(packge);
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		
		//Add file exam for FILE2
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_2), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
		this.template.update(packge);
		
		//FILE2 should be in file exams, but not file instances
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(1, result.additionalInTargetList.size());
		assertEquals(new FileName(FILENAME_2), result.additionalInTargetList.get(0));		
		
		//Add file instance for FILE2
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_2), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
		this.template.update(packge);
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());

		//Add file exam with SHA1 and file instance with MD5 for FILE3
		FileExamination fileExamination = modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_3), new Fixity(FIXITY_3, FixityAlgorithm.SHA1));
		FileInstance fileInstance = modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_3), new Fixity(FIXITY_4, FixityAlgorithm.MD5));
		fileInstance.getFixities().add(new Fixity(FIXITY_5, FixityAlgorithm.SHA256));

		this.template.update(packge);
		
		//FILE3 should be in incomparable
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(1, result.incomparableList.size());
		assertEquals(new FileName(FILENAME_3), result.incomparableList.get(0));		
				
		//Add MD5 for FILE3 file exam
		//session.refresh(fileExamination);
		fileExamination.getFixities().add(new Fixity(FIXITY_4, FixityAlgorithm.MD5));

		this.template.update(packge);
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
	
		//Add file instance for FILE4, which is changeable.  This should not show up as incomparable.
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_4));		
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_4), new Fixity(FIXITY_6, FixityAlgorithm.MD5));		

		this.template.update(packge);
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		
		//Add file exam and file instance for FILE5, but with conflicting fixity values
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_5), new Fixity(FIXITY_7, FixityAlgorithm.MD5));
		fileInstance = modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_5), new Fixity(FIXITY_8, FixityAlgorithm.MD5));

		this.template.update(packge);
				
		//FILE4 should be in conflict
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		assertEquals(1, result.fixityMismatchList.size());
		assertEquals(new FileName(FILENAME_5), result.fixityMismatchList.get(0));
		
		//Fix it
		fileInstance.getFixity(FixityAlgorithm.MD5).setValue(FIXITY_7);

		this.template.update(fileInstance);
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		assertEquals(0, result.fixityMismatchList.size());
		
		//Now add a bad FileExamination (i.e., no fixities)
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_6));
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_6), new Fixity(FIXITY_9, FixityAlgorithm.MD5));		

		this.template.update(packge);
		
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		assertEquals(1, result.fixityMismatchList.size());
		
	}

	@Test
	public void testCompareCanonicalFilesFileInstances() throws Exception
	{
		//Comparing canonical files and file instances
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID2 + testCounter);
		FileLocation fileLocation = modelerFactory.createStorageSystemFileLocation(packge, rdc, BASEPATH_1 + testCounter, true, true);
		
		//Add canonical file for FILE1
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		
		this.template.save(packge);
		
		//FILE1 should be in canonical files, but not file instances
		FileListComparisonResult result = dao.compare(packge, fileLocation);
		assertEquals(1, result.missingFromTargetList.size());
		assertEquals(new FileName(FILENAME_1), result.missingFromTargetList.get(0));
		//Add file instance for FILE1
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_1), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		this.template.update(packge);
		
		//Everything should be OK
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		
		//Add file instance for FILE2
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_2), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
		this.template.update(packge);
		
		//FILE2 should be in file instances, but not canonicalFiles
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(1, result.additionalInTargetList.size());
		assertEquals(new FileName(FILENAME_2), result.additionalInTargetList.get(0));		
		
		//Add canonical file for FILE2
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_2), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
		this.template.update(packge);
		
		//Everything should be OK
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());

		//Add file instance with SHA1 and canonical file with MD5 for FILE3
		FileInstance fileInstance = modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_3), new Fixity(FIXITY_3, FixityAlgorithm.SHA1));
		CanonicalFile canonicalFile = modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_3), new Fixity(FIXITY_4, FixityAlgorithm.MD5));
		canonicalFile.getFixities().add(new Fixity(FIXITY_5, FixityAlgorithm.SHA256));

		this.template.update(packge);
		
		//FILE3 should be in incomparable
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(1, result.incomparableList.size());
		assertEquals(new FileName(FILENAME_3), result.incomparableList.get(0));		
				
		//Add MD5 for FILE3 file instance
		fileInstance.getFixities().add(new Fixity(FIXITY_4, FixityAlgorithm.MD5));

		this.template.update(packge);
		
		//Everything should be OK
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		
		//Add file instance and canonicalFile for FILE4, but with conflicting fixity values
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_4), new Fixity(FIXITY_6, FixityAlgorithm.MD5));
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_4), new Fixity(FIXITY_7, FixityAlgorithm.MD5));

		this.template.update(packge);
				
		//FILE4 should be in conflict
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		assertEquals(1, result.fixityMismatchList.size());
		assertEquals(new FileName(FILENAME_4), result.fixityMismatchList.get(0));
		
	}

	@Test
	public void testLoadFileLocation() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		
		assertNotNull(dao.loadRequiredFileLocation(this.fileLocation1.getKey()));
		
		txManager.commit(status);
	}
	
	@Test(expected=RequiredEntityNotFound.class)
	public void testLoadBadFileLocation() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		try
		{
			dao.loadRequiredFileLocation(10001L);
		}
		finally
		{
			txManager.rollback(status);
		}			
	}
	
	@Test
	public void testDeleteCanonicalFiles() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		Package packge = dao.findRequiredPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		assertFalse(packge.getCanonicalFiles().isEmpty());
		dao.deleteCanonicalFiles(packge);
		txManager.commit(status);
		
		status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(packge);
		assertTrue(packge.getCanonicalFiles().isEmpty());
		txManager.commit(status);
				
	}
	
	@Test
	public void testDeleteFileInstances() throws Exception
	{
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		Package packge = dao.findRequiredPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		FileLocation fileLocation = packge.getFileLocation(RS25, BASEPATH_1 + testCounter);
		assertNotNull(fileLocation);
		assertFalse(fileLocation.getFileInstances().isEmpty());
		dao.deleteFileInstances(fileLocation);
		txManager.commit(status);
		
		status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(fileLocation);
		assertTrue(fileLocation.getFileInstances().isEmpty());
		txManager.commit(status);
		
	}
}
