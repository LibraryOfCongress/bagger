package gov.loc.repository.utilities;

import java.io.File;
import java.util.Map;

public interface ProcessBuilderWrapper {
	
	ProcessBuilderResult execute(File directory, String commandLine, Map<String, String> env);
	
	ProcessBuilderResult execute(File directory, String commandLine);

	ProcessBuilderResult execute(String commandLine);
	
	
	
	public class ProcessBuilderResult
	{
		int exitValue;
		String output;

		public ProcessBuilderResult(int exitValue, String output)
		{
			this.exitValue = exitValue;
			this.output = output;
		}
		
		public int getExitValue()
		{
			return this.exitValue;
		}
		
		public String getOutput()
		{
			return this.output;
		}
	}
	
}
