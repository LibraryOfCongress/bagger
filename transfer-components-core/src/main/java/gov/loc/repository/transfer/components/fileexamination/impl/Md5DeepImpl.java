package gov.loc.repository.transfer.components.fileexamination.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Calendar;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.loc.repository.exceptions.ConfigurationException;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.events.filelocation.VerifyAgainstManifestEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.transfer.components.BaseComponent;
import gov.loc.repository.transfer.components.fileexamination.LCManifestGenerator;
import gov.loc.repository.transfer.components.fileexamination.LCManifestVerifier;
import gov.loc.repository.utilities.ManifestHelper;
import gov.loc.repository.utilities.PackageHelper;

@Component("md5DeepComponent")
@Scope("prototype")
public class Md5DeepImpl extends BaseComponent implements LCManifestGenerator, LCManifestVerifier {

	private boolean result = true;
	
	@Override
	protected String getComponentName() {
		return LCManifestGenerator.COMPONENT_NAME;
	}
	
	@Override
	public void generate(long fileLocationKey, String mountPath,
			String algorithm, String requestingAgentId) throws Exception {
		this.generate(dao.loadRequiredFileLocation(fileLocationKey), mountPath, Algorithm.fromString(algorithm), this.dao.findRequiredAgent(Agent.class, requestingAgentId));

	}

	@Override
	public void generate(FileLocation fileLocation, String mountPath,
			Algorithm algorithm, Agent requestingAgent) throws Exception {
		if (! fileLocation.isLCPackageStructure())
		{
			throw new Exception(fileLocation.toString() + " is not LCPackage structured");
		}
		
		ProcessBuilder builder = new ProcessBuilder(this.getCommand(algorithm.toString()), "-l", "-r", "data");
		
		//Set working directory to the package directory
		File dir = this.getPackageDir(fileLocation, mountPath);
		builder.directory(dir);
		//redirects stderror to stdout
		builder.redirectErrorStream(true);

		Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedWriter writer = new BufferedWriter(new FileWriter(PackageHelper.getManifest(dir, algorithm.toString())));
		String line;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				writer.write(line);
				writer.newLine();
			}
		}
		finally
		{
			writer.close();
		}
		
		int exitValue = process.waitFor();
		if (exitValue != 0)
		{
			throw new RuntimeException("md5deep returned " + exitValue);
		}
	}

	private String getCommand(String algorithm) 
	{
		String key = algorithm.toString().toLowerCase() + ".command";
		String command = this.getConfiguration().getString(key);
		if (command == null)
		{
			throw new ConfigurationException(key + " is missing from configuration");
		}
		return command;
	}
	
	@Override
	public void verify(long fileLocationKey, String mountPath,
			String requestingAgentId) throws Exception {
		this.verify(dao.loadRequiredFileLocation(fileLocationKey), mountPath, this.dao.findRequiredAgent(Agent.class, requestingAgentId));
	}

	@Override
	public void verify(FileLocation fileLocation, String mountPath,
			Agent requestingAgent) throws Exception {
		//Record an Event
		VerifyAgainstManifestEvent event = this.factory.createFileLocationEvent(VerifyAgainstManifestEvent.class, fileLocation, Calendar.getInstance().getTime(), this.getReportingAgent());
		event.setRequestingAgent(requestingAgent);
		event.setPerformingAgent(this.getReportingAgent());
		
		File packageDir = this.getPackageDir(fileLocation, mountPath);
		File manifestFile = PackageHelper.discoverManifest(packageDir);
		String algorithm = ManifestHelper.getAlgorithm(manifestFile);

		ProcessBuilder builder = new ProcessBuilder(this.getCommand(algorithm), "-nx", manifestFile.toString(), "-r", "data");
		
		//Set working directory to the package directory
		File dir = this.getPackageDir(fileLocation, mountPath);
		builder.directory(dir);
		//redirects stderror to stdout
		builder.redirectErrorStream(true);

		Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringWriter stringWriter = new StringWriter();
		BufferedWriter writer = new BufferedWriter(stringWriter);
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				writer.write(line);
			}
		}
		finally
		{
			writer.close();
		}
		int exitValue = process.waitFor();		
		if (exitValue != 0 || ! stringWriter.toString().isEmpty())
		{
			this.getLog().error(manifestFile.toString() + " returned " + exitValue);
			this.result = false;
			event.setSuccess(false);
			event.setMessage(stringWriter.toString());
		}
		event.setEventEnd(Calendar.getInstance().getTime());
		this.dao.save(fileLocation);
		
	}
	
	@Override
	public boolean verifyResult() {
		return this.result;
	}
	
	private File getPackageDir(FileLocation fileLocation, String mountPath)
	{
		File dir = new File(fileLocation.getBasePath());
		if (mountPath != null)
		{
			dir = new File(mountPath);
		}		
		return dir;
		
	}

}
