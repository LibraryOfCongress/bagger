package gov.loc.repository.transfer.components.fileexamination.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;

import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.impl.StorageSystemFileLocationImpl;
import gov.loc.repository.transfer.components.AbstractComponentTest;
import gov.loc.repository.transfer.components.fileexamination.FileExaminationResult;
import gov.loc.repository.transfer.components.fileexamination.FileExaminer;
import gov.loc.repository.transfer.components.fileexamination.impl.FileExaminerImpl;
import gov.loc.repository.utilities.FixityHelper;
import gov.loc.repository.utilities.ResourceHelper;
import gov.loc.repository.utilities.impl.JavaSecurityFixityHelper;

import org.junit.Test;

import static gov.loc.repository.transfer.components.constants.FixtureConstants.*;
import static gov.loc.repository.constants.Agents.*;

public class FileExaminerImplTest extends AbstractComponentTest{

	private FileExaminer examiner;
	private Package packge;
	
	@Override
	public void createFixtures() throws Exception {
		this.fixtureHelper.createRepository(REPOSITORY_ID1);
		this.fixtureHelper.createStorageSystem(RDC);
		this.fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
		
	}
	
	@Override
	public void setup() throws Exception {
		examiner = new FileExaminerImpl();
		examiner.setModelerFactory(this.modelerFactory);
		examiner.setPackageModelDao(this.packageModelDao);
		FixityHelper fixityHelper = new JavaSecurityFixityHelper();
		fixityHelper.setAlgorithm("MD5");
		examiner.setFixityHelper(fixityHelper);
		
		packge = this.modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
		this.session.save(packge);
	}	
	
	@Test
	public void testExamine() throws Exception {		
		File baseDir = ResourceHelper.getFile(this, "dir1");
		this.modelerFactory.createStorageSystemFileLocation(packge, RDC, baseDir.toString(), true, true);
		examiner.examine(REPOSITORY_ID1, PACKAGE_ID1 + testCounter, RDC, baseDir.toString(), PERSON_ID1, true);
		this.session.save(packge);
		
		this.commitAndRestartTransaction();
		this.session.refresh(packge);
		
		assertEquals(1, packge.getFileLocations().size());
		FileLocation fileLocation = packge.getFileLocations().iterator().next();
		assertEquals(2, fileLocation.getFileInstances().size());
		assertEquals(1, fileLocation.getFileExaminationGroups().size());
		FileExaminationGroup fileExaminationGroup = fileLocation.getFileExaminationGroups().iterator().next();
		assertEquals(2, fileExaminationGroup.getFileExaminations().size());
		assertEquals(1, fileExaminationGroup.getFileExaminationGroupEvents().size());
		
	}

	@Test
	public void testExamineFile() throws Exception {
		File baseDir = ResourceHelper.getFile(this, "dir1");
		//Let's set the modifiedDate
		File file = ResourceHelper.getFile(this, "dir1/file1");
		Date now = new Date();
		file.setLastModified(now.getTime());
		
		FileExaminationResult result = ((FileExaminerImpl)examiner).examine(baseDir.toString(), "file1");
		assertEquals("file1", result.relativeFilename);
		//On some OSes, this fails because of a loss of granularity, so kludging with toString()
		//assertEquals(now.toString(), result.modifiedDate.toString());
		assertEquals("MD5", result.fixityAlgorithm);
		assertEquals("098f6bcd4621d373cade4e832627b4f6", result.fixityValue);
	}

	@Test
	public void testExamineMissingFile() throws Exception {
		FileExaminationResult result = ((FileExaminerImpl)examiner).examine("foo", "bar.html");
		assertNull(result.fixityValue);
	}
	
	@Test
	public void testGenerateBaseDir() throws Exception
	{
		FileLocation fileLocation = new StorageSystemFileLocationImpl();
		fileLocation.setBasePath(BASEPATH_1);
		assertEquals(new File(BASEPATH_1), ((FileExaminerImpl)examiner).determineBaseDir(fileLocation, null));
	}
	
}
