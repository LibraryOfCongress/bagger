package gov.loc.repository.transfer.components.fileexamination.impl;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.transfer.components.constants.FixtureConstants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.junit.Assert.*;

import gov.loc.repository.constants.Roles;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.events.filelocation.VerifyAgainstManifestEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.transfer.components.AbstractCorePackageModelerAwareComponentTest;
import gov.loc.repository.transfer.components.fileexamination.LCManifestGenerator;
import gov.loc.repository.transfer.components.fileexamination.LCManifestVerifier;
import gov.loc.repository.packagemodeler.agents.System;

public class Md5DeepImplTest extends AbstractCorePackageModelerAwareComponentTest {

	private static Log log = LogFactory.getLog(Md5DeepImplTest.class);
	
	static Agent requestingAgent;
	static Repository repository;
	static System rdc;
	Package packge;
	boolean canRunTest = false;

	@Autowired
	LCManifestGenerator generator;
	
	@Autowired
	LCManifestVerifier verifier;

	
	@Override
	public void createFixtures() throws Exception {
		repository = this.fixtureHelper.createRepository(REPOSITORY_ID1);
		rdc = this.fixtureHelper.createSystem(RDC, new Role[] {fixtureHelper.createRole(Roles.STORAGE_SYSTEM)});
		requestingAgent = this.fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
	}	
	
	@Override
	public void setup() throws Exception {
		//Look for md5deep
		Map<String, String> commandMap = new HashMap<String, String>();
	    for (String command : new String[] {"c:/md5deep/md5deep.exe", "c:/Program Files/md5deep\\md5deep.exe", "/usr/bin/md5deep"})
		{
			File file = new File(command);
			if (file.exists())
			{
				commandMap.put("md5", command);
				canRunTest = true;
				break;
			}
		}
		
		if (! canRunTest)
		{
			log.warn("Can't run test because can't find md5deep");
		}
		
		generator.setCommandMap(commandMap);
		verifier.setCommandMap(commandMap);
				
		packge = this.modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		this.template.save(packge);
	}
	
	@Test
	public void testGenerateAndValidate() throws Exception
	{
		if (! this.canRunTest)
		{
			return;
		}
		
		FileLocation fileLocation = this.modelerFactory.createStorageSystemFileLocation(packge, rdc, BASEPATH_1, true, true);
						
		assertEquals(0, fileLocation.getFileLocationEvents().size());
				
		File packageDir = this.getFile("package");
		generator.generate(fileLocation, packageDir.toString(), Algorithm.MD5, reportingAgent);
		
		File manifestFile = new File(packageDir, "manifest-md5.txt");
		assertTrue(manifestFile.exists());
		
		verifier.verify(fileLocation, packageDir.toString(), reportingAgent);
		assertTrue(verifier.verifyResult());
		
		
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

		assertEquals(1, fileLocation.getFileLocationEvents(VerifyAgainstManifestEvent.class).size());
		
		txManager.commit(status);

	}
	
	@Test
	public void testBadValidate() throws Exception
	{
		if (! this.canRunTest)
		{
			return;
		}

		FileLocation fileLocation = this.modelerFactory.createStorageSystemFileLocation(packge, rdc, BASEPATH_2, true, true);
		
		assertEquals(0, fileLocation.getFileLocationEvents().size());
				
		File packageDir = this.getFile("package");
		generator.generate(fileLocation, packageDir.toString(), Algorithm.MD5, reportingAgent);
		
		File manifestFile = new File(packageDir, "manifest-md5.txt");
		assertTrue(manifestFile.exists());

		//Let's append a bad line
		Writer manifestWriter = new FileWriter(manifestFile, true);
		manifestWriter.write("ad0234829205b9033196ba818f7a872c  data\\batch1\\test3.txt");
		manifestWriter.close();
		
		verifier.verify(fileLocation, packageDir.toString(), reportingAgent);
		assertFalse(verifier.verifyResult());
		
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		
		assertEquals(1, fileLocation.getFileLocationEvents(VerifyAgainstManifestEvent.class).size());
		VerifyAgainstManifestEvent event = fileLocation.getFileLocationEvents(VerifyAgainstManifestEvent.class).iterator().next();
		assertFalse(event.isSuccess());
		assertNotNull(event.getMessage());
		
		txManager.commit(status);
		
	}
	
}
