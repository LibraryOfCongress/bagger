package gov.loc.repository.bagit.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.BagHelper;
import gov.loc.repository.exceptions.ConfigurationException;
import gov.loc.repository.results.SimpleResult;

@Component("md5DeepManifestGeneratorVerifier")
public class Md5DeepManifestGeneratorVerifierImpl extends AbstractManifestGeneratorVerifier {

	private static final Log log = LogFactory.getLog(Md5DeepManifestGeneratorVerifierImpl.class);
	
	private Map<String,String> commandMap;
	
	public Md5DeepManifestGeneratorVerifierImpl(Map<String,String> commandMap) {
		this.commandMap = commandMap;
	}

	private void executeGenerate(ProcessBuilder builder, File manifestFile)
	{
		//Set working directory to the package directory
		builder.directory(manifestFile.getParentFile());
		
		//redirects stderror to stdout
		builder.redirectErrorStream(true);
		try
		{
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new FileWriter(manifestFile));
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
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
		
	}
	
	@Override
	public void generateManifest(File packageDir, String algorithm) {
		ProcessBuilder builder = new ProcessBuilder(this.getCommand(algorithm.toString()), "-l", "-r", BagHelper.DATA_DIRECTORY);		
		this.executeGenerate(builder, BagHelper.getManifest(packageDir, algorithm.toString()));
				
	}

	private void addTagFileArgs(List<String> args, File packageDir)
	{
		List<File> tagFiles = BagHelper.getTags(packageDir, false);
		for(File tagFile : tagFiles)
		{
			args.add(tagFile.getName());
		}

	}
	
	@Override
	public void generateTagManifest(File packageDir, String algorithm) {
		
		List<String> args = new ArrayList<String>();
		args.add(this.getCommand(algorithm.toString()));
		args.add("-l");
		this.addTagFileArgs(args, packageDir);
		
		ProcessBuilder builder = new ProcessBuilder(args);		
		this.executeGenerate(builder, BagHelper.getTagManifest(packageDir, algorithm.toString()));
		
	}
	
	private String getCommand(String algorithm) 
	{
		
		String command = this.commandMap.get(algorithm.toString().toLowerCase());
		if (command == null)
		{
			throw new ConfigurationException(algorithm + " is missing from configuration");
		}
		return command;
	}
	

	@Override
	public SimpleResult verify(File manifest) {
		String algorithm = ManifestHelper.getAlgorithm(manifest);

		ProcessBuilder builder;
		if (ManifestHelper.isManifest(manifest))
		{		
			log.debug("Verifying manifest");
			builder = new ProcessBuilder(this.getCommand(algorithm), "-nx", manifest.toString(), "-r", BagHelper.DATA_DIRECTORY);
		}
		else if (ManifestHelper.isTagManifest(manifest))
		{
			log.debug("Verifying tag manifest");			
			List<String> args = new ArrayList<String>();
			args.add(this.getCommand(algorithm.toString()));
			args.add("-nx");
			args.add(manifest.toString());
			this.addTagFileArgs(args, manifest.getParentFile());

			builder = new ProcessBuilder(args);
		}
		else
		{
			throw new RuntimeException("Cannot determine if manifest is payload manifest or tag manifest");
		}
		
		//Set working directory to the package directory
		//File dir = this.getPackageDir(fileLocation, mountPath);
		builder.directory(manifest.getParentFile());
		//redirects stderror to stdout
		builder.redirectErrorStream(true);

		try
		{
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
			//if (exitValue != 0 || ! stringWriter.toString().isEmpty())
			if (exitValue != 0)
			{
				log.debug(MessageFormat.format("Exit value: {0}.  Output: {1}", exitValue, stringWriter));
				return new SimpleResult(false, stringWriter.toString());
			}
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
		return new SimpleResult(true);
	}
		
}
