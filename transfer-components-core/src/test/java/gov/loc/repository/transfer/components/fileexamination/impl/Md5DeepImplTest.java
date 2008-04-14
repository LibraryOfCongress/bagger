package gov.loc.repository.transfer.components.fileexamination.impl;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import static org.junit.Assert.*;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.events.filelocation.VerifyAgainstManifestEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.transfer.components.AbstractComponentTest;


public class Md5DeepImplTest extends AbstractComponentTest {

	private static Log log = LogFactory.getLog(Md5DeepImplTest.class);
	
	protected static Agent requestingAgent;
	Package packge;
	Md5DeepImpl md5DeepComponent;
	boolean canRunTest = false;
	
	@Override
	public void createFixtures() throws Exception {
		this.fixtureHelper.createRepository(REPOSITORY_ID1);
		this.fixtureHelper.createStorageSystem(RDC);
		requestingAgent = this.fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
	}	
	
	@Override
	public void setup() throws Exception {
		//Look for md5deep
		for(String command : new String[] {"c:/md5deep/md5deep.exe", "c:/Program Files/md5deep\\md5deep.exe"})
		{
			File file = new File(command);
			if (file.exists())
			{
				this.getConfiguration().addProperty("md5.command", command);
				canRunTest = true;
				break;
			}
		}
		
		if (! canRunTest)
		{
			log.warn("Can't run test because can't find md5deep");
		}
		md5DeepComponent = new Md5DeepImpl();
		md5DeepComponent.setModelerFactory(this.modelerFactory);
		md5DeepComponent.setPackageModelDao(this.packageModelDao);
				
		packge = this.modelerFactory.createPackage(Package.class, REPOSITORY_ID1, PACKAGE_ID1 + testCounter);
		this.session.save(packge);
	}
	
	@Test
	public void testGenerateAndValidate() throws Exception
	{
		FileLocation fileLocation = this.modelerFactory.createStorageSystemFileLocation(packge, RDC, BASEPATH_1, true, true);
		
		assertEquals(0, fileLocation.getFileLocationEvents().size());
				
		File packageDir = this.getFile("package");
		md5DeepComponent.generate(fileLocation, packageDir.toString(), Algorithm.MD5, this.packageModelDao.findRequiredAgent(Agent.class, this.getReportingAgent()));
		
		File manifestFile = new File(packageDir, "manifest-md5.txt");
		assertTrue(manifestFile.exists());
		
		md5DeepComponent.verify(fileLocation, packageDir.toString(), this.packageModelDao.findRequiredAgent(Agent.class, this.getReportingAgent()));
		assertTrue(md5DeepComponent.verifyResult());
		
		this.commitAndRestartTransaction();
		
		assertEquals(1, fileLocation.getFileLocationEvents(VerifyAgainstManifestEvent.class).size());		

	}
	
	@Test
	public void testBadValidate() throws Exception
	{
		FileLocation fileLocation = this.modelerFactory.createStorageSystemFileLocation(packge, RDC, BASEPATH_2, true, true);
		
		assertEquals(0, fileLocation.getFileLocationEvents().size());
				
		File packageDir = this.getFile("package");
		md5DeepComponent.generate(fileLocation, packageDir.toString(), Algorithm.MD5, this.packageModelDao.findRequiredAgent(Agent.class, this.getReportingAgent()));
		
		File manifestFile = new File(packageDir, "manifest-md5.txt");
		assertTrue(manifestFile.exists());

		//Let's append a bad line
		Writer manifestWriter = new FileWriter(manifestFile, true);
		manifestWriter.write("ad0234829205b9033196ba818f7a872c  data\\batch1\\test3.txt");
		manifestWriter.close();
		
		md5DeepComponent.verify(fileLocation, packageDir.toString(), this.packageModelDao.findRequiredAgent(Agent.class, this.getReportingAgent()));
		assertFalse(md5DeepComponent.verifyResult());
		
		this.commitAndRestartTransaction();
		
		assertEquals(1, fileLocation.getFileLocationEvents(VerifyAgainstManifestEvent.class).size());
		VerifyAgainstManifestEvent event = fileLocation.getFileLocationEvents(VerifyAgainstManifestEvent.class).iterator().next();
		assertFalse(event.isSuccess());
		assertNotNull(event.getMessage());
	}
	
}
