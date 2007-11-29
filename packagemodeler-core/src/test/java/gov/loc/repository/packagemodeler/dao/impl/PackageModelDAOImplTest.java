package gov.loc.repository.packagemodeler.dao.impl;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.constants.Roles.*;
import static gov.loc.repository.packagemodeler.constants.FixtureConstants.*;
import gov.loc.repository.utilities.results.ResultIterator;
import gov.loc.repository.packagemodeler.AbstractModelersTest;
import gov.loc.repository.packagemodeler.agents.Organization;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.dao.FileListComparisonResult;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;

public class PackageModelDAOImplTest extends AbstractModelersTest {
	private PackageModelDAO dao = new PackageModelDAOImpl();
	
	private Repository repository;
	private FileName fileName1;
	private System rs25Service;
	private FileLocation fileLocation1;
	private FileExaminationGroup fileExaminationGroup1;
	
	@Override
	public void createFixtures() throws Exception {
		repository = fixtureHelper.createRepository(REPOSITORY_ID1);
		rs25Service = fixtureHelper.createStorageSystem(RS25);
		fixtureHelper.createStorageSystem(RDC);
		fixtureHelper.createSystem(JBPM);
		fixtureHelper.createOrganization(ORGANIZATION_ID1, ORGANIZATION_NAME1, new String[] {NDNP_AWARDEE});		
	}
		
	@Override
	public void setup() throws Exception
	{
		fileName1 = new FileName(FILENAME_1);
		
		fixtureHelper.reload(repository);
		fixtureHelper.reload(rs25Service);
		
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		session.save(packge);

		modelerFactory.createCanonicalFile(packge, fileName1, new Fixity(FIXITY_1, Algorithm.MD5));
		
		fileLocation1 = modelerFactory.createStorageSystemFileLocation(packge, this.rs25Service, BASEPATH_1 + testCounter, true, true);
		
		modelerFactory.createFileInstance(fileLocation1, fileName1, new Fixity(FIXITY_1, Algorithm.MD5));

		fileExaminationGroup1 = modelerFactory.createFileExaminationGroup(fileLocation1, true);
		
		modelerFactory.createFileExamination(fileExaminationGroup1, fileName1, new Fixity(FIXITY_1, Algorithm.MD5));
		
		this.commitAndRestartTransaction();
		
	}		

	@Test
	public void testFindPackage() throws Exception
	{
		assertNotNull(dao.findPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter));
		assertNull(dao.findPackage(Package.class, REPOSITORY_ID1, "x" + PACKAGE_ID1 + testCounter));
	}

	@Test(expected=Exception.class)
	public void testFindRequiredPackage() throws Exception
	{
		dao.findRequiredPackage(Package.class, REPOSITORY_ID1, "x" + PACKAGE_ID1 + testCounter);
	}
	
	@Test
	public void testFindPackages() throws Exception
	{
		assertTrue(dao.findPackages(Package.class).size() > 0);
		assertTrue(dao.findPackages(Package.class).get(0) instanceof Package);
	}

	@Test
	public void testFindPackagesWithFileCount() throws Exception
	{
		//Let's also make sure there is a package with no canonical files
		Package packge1 = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID2 + testCounter);
		session.save(packge1);
				
		//And another with no html files
		Package packge2 = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID3 + testCounter);
		session.save(packge2);		
		modelerFactory.createCanonicalFile(packge2, new FileName("test.xml"), new Fixity(FIXITY_2, Algorithm.MD5));
		
		this.commitAndRestartTransaction();
		
		ResultIterator resultIter = dao.findPackagesWithFileCount(Package.class, "html");
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
	}

	@Test
	public void testFindCanonicalFile() throws Exception
	{
		assertNotNull(dao.findCanonicalFile(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, FILENAME_1));
		assertNull(dao.findCanonicalFile(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, "x" + FILENAME_1));		
	}
	
	@Test
	public void testFindFileInstance() throws Exception
	{
		assertNotNull(dao.findFileInstance(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1 + testCounter, FILENAME_1));
		assertNull(dao.findFileInstance(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RS25, BASEPATH_1 + testCounter, "x" + FILENAME_1));
		assertNotNull(dao.findFileInstance(fileLocation1, new FileName(FILENAME_1)));
	}
	
	@Test
	public void testFindFileExamination() throws Exception
	{
		assertNotNull(dao.findFileExamination(fileExaminationGroup1, fileName1));
		assertNull(dao.findFileExamination(fileExaminationGroup1, new FileName("x" + FILENAME_1)));
		
	}
	
	@Test
	public void testFindRepositories() throws Exception
	{
		assertEquals(1, dao.findRepositories().size());
		assertTrue(dao.findRepositories().get(0) instanceof Repository);
	}

	@Test
	public void testFindRepository() throws Exception
	{
		assertNotNull(dao.findRepository(REPOSITORY_ID1));
		assertNull(dao.findRepository("x" + REPOSITORY_ID1));
	}

	@Test(expected=Exception.class)
	public void testFindRequiredRepository() throws Exception
	{
		dao.findRequiredRepository("x" + REPOSITORY_ID1);
	}

	@Test
	public void testFindAgent() throws Exception
	{
		Organization organization = dao.findAgent(Organization.class, ORGANIZATION_ID1);
		assertNotNull(organization);
		assertEquals(ORGANIZATION_ID1, organization.getId());
	}

	@Test(expected=Exception.class)
	public void testFindRequiredAgent() throws Exception
	{
		dao.findRequiredAgent(Organization.class, "x" + ORGANIZATION_ID1);
	}

	@Test
	public void testFindRole() throws Exception
	{
		Role role = dao.findRole(NDNP_AWARDEE);
		assertNotNull(role);
		assertEquals(1, role.getAgentSet().size());
		
		assertNull(dao.findRole("x" + NDNP_AWARDEE));
	}
	
	@Test(expected=Exception.class)
	public void testFindRequiredRole() throws Exception
	{
		dao.findRequiredRole("x" + NDNP_AWARDEE);
	}
	
	@Test
	public void testCompareFileInstancesFileExaminations() throws Exception
	{
		//Comparing file instances and file examinations
		Package packge = modelerFactory.createPackage(Package.class, repository, PACKAGE_ID2 + testCounter);
		dao.save(packge);
		FileLocation fileLocation = modelerFactory.createStorageSystemFileLocation(packge, RDC, BASEPATH_1, true, true);
		FileExaminationGroup fileExaminationGroup = modelerFactory.createFileExaminationGroup(fileLocation, true);
			
		//Add file instance for FILE1
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		this.commitAndRestartTransaction();
		
		//FILE1 should be in file instances, but not file examinations
		FileListComparisonResult result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(1, result.missingFromTargetList.size());
		assertEquals(new FileName(FILENAME_1), result.missingFromTargetList.get(0));
		//Add file exam for FILE1
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		
		//Add file exam for FILE2
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_2), new Fixity(FIXITY_2, Algorithm.MD5));
		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//FILE2 should be in file exams, but not file instances
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(1, result.additionalInTargetList.size());
		assertEquals(new FileName(FILENAME_2), result.additionalInTargetList.get(0));		
		
		//Add file instance for FILE2
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_2), new Fixity(FIXITY_2, Algorithm.MD5));
		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());

		//Add file exam with SHA1 and file instance with MD5 for FILE3
		FileExamination fileExamination = modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_3), new Fixity(FIXITY_3, Algorithm.SHA1));
		FileInstance fileInstance = modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_3), new Fixity(FIXITY_4, Algorithm.MD5));
		fileInstance.getFixities().add(new Fixity(FIXITY_5, Algorithm.SHA256));

		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//FILE3 should be in incomparable
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(1, result.incomparableList.size());
		assertEquals(new FileName(FILENAME_3), result.incomparableList.get(0));		
				
		//Add MD5 for FILE3 file exam
		//session.refresh(fileExamination);
		fileExamination.getFixities().add(new Fixity(FIXITY_4, Algorithm.MD5));

		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
	
		//Add file instance for FILE4, which is changeable.  This should not show up as incomparable.
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_4));		
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_4), new Fixity(FIXITY_6, Algorithm.MD5));		

		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		
		//Add file exam and file instance for FILE5, but with conflicting fixity values
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_5), new Fixity(FIXITY_7, Algorithm.MD5));
		fileInstance = modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_5), new Fixity(FIXITY_8, Algorithm.MD5));

		dao.save(packge);
		this.commitAndRestartTransaction();
				
		//FILE4 should be in conflict
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		assertEquals(1, result.fixityMismatchList.size());
		assertEquals(new FileName(FILENAME_5), result.fixityMismatchList.get(0));
		
		//Fix it
		fileInstance.getFixity(Algorithm.MD5).setValue(FIXITY_7);
		dao.save(fileInstance);
		this.commitAndRestartTransaction();
		
		//Everything should be OK
		result = dao.compare(fileLocation, fileExaminationGroup);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		assertEquals(0, result.fixityMismatchList.size());
		
		//Now add a bad FileExamination (i.e., no fixities)
		modelerFactory.createFileExamination(fileExaminationGroup, new FileName(FILENAME_6));
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_6), new Fixity(FIXITY_9, Algorithm.MD5));		

		dao.save(packge);
		this.commitAndRestartTransaction();
		
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
		dao.save(packge);
		FileLocation fileLocation = modelerFactory.createStorageSystemFileLocation(packge, RDC, BASEPATH_1, true, true);
		
		//Add canonical file for FILE1
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		this.commitAndRestartTransaction();
		
		//FILE1 should be in canonical files, but not file instances
		FileListComparisonResult result = dao.compare(packge, fileLocation);
		assertEquals(1, result.missingFromTargetList.size());
		assertEquals(new FileName(FILENAME_1), result.missingFromTargetList.get(0));
		//Add file instance for FILE1
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_1), new Fixity(FIXITY_1, Algorithm.MD5));
		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//Everything should be OK
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		
		//Add file instance for FILE2
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_2), new Fixity(FIXITY_2, Algorithm.MD5));
		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//FILE2 should be in file instances, but not canonicalFiles
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(1, result.additionalInTargetList.size());
		assertEquals(new FileName(FILENAME_2), result.additionalInTargetList.get(0));		
		
		//Add canonical file for FILE2
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_2), new Fixity(FIXITY_2, Algorithm.MD5));
		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//Everything should be OK
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());

		//Add file instance with SHA1 and canonical file with MD5 for FILE3
		FileInstance fileInstance = modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_3), new Fixity(FIXITY_3, Algorithm.SHA1));
		CanonicalFile canonicalFile = modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_3), new Fixity(FIXITY_4, Algorithm.MD5));
		canonicalFile.getFixities().add(new Fixity(FIXITY_5, Algorithm.SHA256));

		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//FILE3 should be in incomparable
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(1, result.incomparableList.size());
		assertEquals(new FileName(FILENAME_3), result.incomparableList.get(0));		
				
		//Add MD5 for FILE3 file instance
		fileInstance.getFixities().add(new Fixity(FIXITY_4, Algorithm.MD5));

		dao.save(packge);
		this.commitAndRestartTransaction();
		
		//Everything should be OK
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		
		//Add file instance and canonicalFile for FILE4, but with conflicting fixity values
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_4), new Fixity(FIXITY_6, Algorithm.MD5));
		modelerFactory.createCanonicalFile(packge, new FileName(FILENAME_4), new Fixity(FIXITY_7, Algorithm.MD5));

		dao.save(packge);
		this.commitAndRestartTransaction();
				
		//FILE4 should be in conflict
		result = dao.compare(packge, fileLocation);
		assertEquals(0, result.missingFromTargetList.size());
		assertEquals(0, result.additionalInTargetList.size());
		assertEquals(0, result.incomparableList.size());
		assertEquals(1, result.fixityMismatchList.size());
		assertEquals(new FileName(FILENAME_4), result.fixityMismatchList.get(0));
		
	}
	
}
