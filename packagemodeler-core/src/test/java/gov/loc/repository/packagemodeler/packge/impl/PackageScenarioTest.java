package gov.loc.repository.packagemodeler.packge.impl;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.loc.repository.packagemodeler.AbstractCoreModelersTest;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.dao.FileListComparisonResult;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;

public class PackageScenarioTest extends AbstractCoreModelersTest {
	
	static System rs25;
	static System rs15;
	static System rs5;
	static Repository repository;

	@Resource(name="packageModelDao")
	PackageModelDAO dao;
	
	@Override
	public void createFixtures() throws Exception {
		repository = fixtureHelper.createRepository(REPOSITORY_ID1);
		rs25 = fixtureHelper.createSystem(RS25, new Role[] {storageSystemRole});
		rs15 = fixtureHelper.createSystem(RS15, new Role[] {storageSystemRole});
		rs5 = fixtureHelper.createSystem(RS5, new Role[] {storageSystemRole});		
	}	
						
	@Test
	public void testScenario() throws Exception
	{
	
		//A package is created
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		
		//It has 3 canonical files
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_2), new Fixity(FIXITY_2, Algorithm.MD5));
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_3), new Fixity(FIXITY_3, Algorithm.MD5));		
		
		this.template.save(packge);
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(packge);
		
		//How many canonical files does the package have? 3
		assertEquals(3, packge.getCanonicalFiles().size());
				
		//A complete set is copied to RS25
		FileLocation rs25FileLocation = modelerFactory.createStorageSystemFileLocation(packge, rs25, BASEPATH_1, true, true);
		modelerFactory.createFileInstancesFromCanonicalFiles(rs25FileLocation, packge.getCanonicalFiles());

		txManager.commit(status);
		
		this.template.update(packge);
		status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(packge);
		
		assertEquals(3, packge.getFileLocation(RS25, BASEPATH_1).getFileInstances().size());
						
		//A partial set is copied to RS15
		this.template.refresh(rs15);
		FileLocation rs15FileLocation = modelerFactory.createStorageSystemFileLocation(packge, rs15, BASEPATH_1, true, true);
		modelerFactory.createFileInstance(rs15FileLocation, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		modelerFactory.createFileInstance(rs15FileLocation, new FileName(FILENAME_2), new Fixity(FIXITY_2, Algorithm.MD5));
		
		//A complete set plus an additional file is copies to RS5
		this.template.refresh(rs5);
		FileLocation rs5FileLocation = modelerFactory.createStorageSystemFileLocation(packge, rs5, BASEPATH_1, true, true);
		modelerFactory.createFileInstancesFromCanonicalFiles(rs5FileLocation, packge.getCanonicalFiles());
		modelerFactory.createFileInstance(rs5FileLocation, new FileName(FILENAME_4), new Fixity(FIXITY_4, Algorithm.MD5));
		
		txManager.commit(status);
		this.template.update(packge);
		status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.template.refresh(packge);
		
		//Where are there partial or full copies?  RS25 and RS15 and RS5
		assertEquals(3, packge.getFileLocations().size());
		assertEquals(3, packge.getFileLocation(RS25, BASEPATH_1).getFileInstances().size());
		assertEquals(2, packge.getFileLocation(RS15, BASEPATH_1).getFileInstances().size());
		assertEquals(4, packge.getFileLocation(RS5, BASEPATH_1).getFileInstances().size());
		
		//Where are there full/equal copies?  RS25
		for(FileLocation fileLocation : packge.getFileLocations())
		{
			FileListComparisonResult result = dao.compare(packge, fileLocation);
			assertTrue((! RS25.equals(((StorageSystemFileLocation)fileLocation).getStorageSystem().getId())) || result.isEqual());
		}
		
		//What's missing from RS15?
		FileListComparisonResult result = dao.compare(packge, packge.getFileLocation(RS15, BASEPATH_1));
		assertEquals(1, result.missingFromTargetList.size());
		assertEquals(new FileName(FILENAME_3), result.missingFromTargetList.get(0));
		
		//What's extra in RS5
		result = dao.compare(packge, packge.getFileLocation(RS5, BASEPATH_1));
		assertEquals(1, result.additionalInTargetList.size());
		assertEquals(new FileName(FILENAME_4), result.additionalInTargetList.get(0));

		txManager.commit(status);
		
		/*
		//What is the status of RS25?  Don't know because haven't done any observances
		FileLocation rs25FileLocation = packge.getFileLocation(RS25, BASEPATH_1);
		report = comparisonDao.generateFileInstanceListFileObservationListComparisonReport(packge, rs25FileLocation);
		assertEquals(rs25FileLocation.getFileInstances().size(), report.incomparableList.size());
		
		//Observe all files on RS25
		Calendar cal = Calendar.getInstance();
		dao.createFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, FILENAME_1, cal, FIXITY_1, MD5);
		dao.createFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, FILENAME_2, cal, FIXITY_2, MD5);
		dao.createFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, FILENAME_3, cal, FIXITY_3, MD5);
		this.commitAndRestartTransaction();
		
		//What is the status of RS25?  It is complete.
		assertTrue(comparisonDao.generateFileInstanceListFileObservationListComparisonReport(packge, rs25FileLocation).isEqual());
		
		//A month passes.  Observe FILENAME_1, which is missing
		cal.add(Calendar.MONTH, 1);
		dao.createNotPresentFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, FILENAME_1, cal);
		this.commitAndRestartTransaction();
		
		report = comparisonDao.generateFileInstanceListFileObservationListComparisonReport(packge, rs25FileLocation);
		assertEquals(1, report.missingFromTargetList.size());		
		assertTrue(FileNameHelper.equals(FILENAME_1, ((FileName)report.missingFromTargetList.get(0)).getFileName()));
		
		//A day passes.  FILENAME_1 is replaced.
		cal.add(Calendar.HOUR, 24);
		dao.createFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, FILENAME_1, cal, FIXITY_1, MD5);
		this.commitAndRestartTransaction();
		
		//What is the status of RS25?  It is complete.
		assertTrue(comparisonDao.generateFileInstanceListFileObservationListComparisonReport(packge, rs25FileLocation).isEqual());
		
		//A month passes.  Observe an extra file "foo/ooops.html"
		cal.add(Calendar.MONTH, 1);
		dao.createFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, "foo/ooops.html", cal, "xxxxxx", MD5);
		this.commitAndRestartTransaction();
		
		report = comparisonDao.generateFileInstanceListFileObservationListComparisonReport(packge, rs25FileLocation);
		assertEquals(1, report.additionalInTargetList.size());		
		assertTrue(FileNameHelper.equals("foo/ooops.html", ((FileName)report.additionalInTargetList.get(0)).getFileName()));
				
		//A day passes.  "foo/ooops.html" is removed.
		cal.add(Calendar.HOUR, 24);
		dao.createNotPresentFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, "foo/ooops.html", cal);
		this.commitAndRestartTransaction();
		
		//What is the status of RS25?  It is complete.
		assertTrue(comparisonDao.generateFileInstanceListFileObservationListComparisonReport(packge, rs25FileLocation).isEqual());
				
		//A month passes.  FILENAME_2 is corrupted.
		cal.add(Calendar.MONTH, 1);
		dao.createFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, FILENAME_2, cal, "zzzzzzzz", MD5);
		this.commitAndRestartTransaction();
		
		report = comparisonDao.generateFileInstanceListFileObservationListComparisonReport(packge, rs25FileLocation);
		assertEquals(1, report.fixityMismatchList.size());		
		assertTrue(FileNameHelper.equals(FILENAME_2, ((FileName)report.fixityMismatchList.get(0)).getFileName()));
		
		//A day passes.  FILENAME_2 is corrected.
		cal.add(Calendar.HOUR, 24);
		dao.createFileObservation(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1, FILENAME_2, cal, FIXITY_2, MD5);
		this.commitAndRestartTransaction();
		
		//What is the status of RS25?  It is complete.
		assertTrue(comparisonDao.generateFileInstanceListFileObservationListComparisonReport(packge, rs25FileLocation).isEqual());
		*/
	}
				
}

