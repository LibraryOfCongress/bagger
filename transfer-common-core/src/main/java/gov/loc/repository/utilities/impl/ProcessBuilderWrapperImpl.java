package gov.loc.repository.utilities.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import gov.loc.repository.utilities.OperatingSystemHelper;
import gov.loc.repository.utilities.ProcessBuilderWrapper;

@Component("processBuilderWrapper")
public class ProcessBuilderWrapperImpl implements ProcessBuilderWrapper {

	public ProcessBuilderResult execute(String commandLine) {
		return this.execute(new File("."), commandLine);
	}
	
	public ProcessBuilderResult execute(File directory, String commandLine) {
		try
		{
			List<String> commandList = new ArrayList<String>();
			if (OperatingSystemHelper.isLinux() || OperatingSystemHelper.isSolaris())
			{
				commandList.add("/bin/sh");
				commandList.add("-c");
			}
			else if (OperatingSystemHelper.isWindows())
			{
				commandList.add("cmd");
				commandList.add("/C");
				
			}
			else
			{
				throw new RuntimeException("OS not supported");
			}
			commandList.add(commandLine);
			
			//Check free space
			ProcessBuilder builder = new ProcessBuilder(commandList);		
			//Set working directory to the package directory
			builder.directory(directory);		
			//redirects stderror to stdout
			builder.redirectErrorStream(true);
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuffer buf = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null)
			{
				if (buf.length() != 0)
				{
					buf.append("\n");
				}
				buf.append(line);
			}
			String output = buf.toString();
			int exitValue = process.waitFor();
			if (exitValue != 0)
			{
				throw new RuntimeException(MessageFormat.format("{0} returned {1}.  Output was {2}", commandLine, exitValue, output));
			}
			return new ProcessBuilderResult(exitValue, output);
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

}
