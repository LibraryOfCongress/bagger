package gov.loc.repository.utilities;

import java.io.File;
import java.io.PrintWriter;

public class ManifestWriter {
	private File parentFile;
	private String algorithm;
	private PrintWriter writer = null;
	private File manifestFile = null;
	
	public void setParentPath(File file) throws Exception
	{
		if (! file.isDirectory())
		{
			throw new Exception(file.toString() + " is not a directory");
		}
		this.parentFile = file;
	}
	
	public void setAlgorithm(String algorithm)
	{
		this.algorithm = algorithm;
	}
	
	public File getManifestFile()
	{
		return this.manifestFile;
	}
	
	public void write(String file, String fixityValue) throws Exception
	{
		if (this.writer == null)
		{
			if (this.algorithm == null)
			{
				throw new Exception("Algorithm must be set");
			}
			if (this.parentFile == null)
			{
				throw new Exception("Parent path must be set");
			}
			this.manifestFile = new File(this.parentFile, "manifest-" + this.algorithm.toLowerCase() + ".txt");
			this.writer = new PrintWriter(this.manifestFile);					
		}
		this.writer.println(fixityValue + "  " + file);
	}
	
	public void close()
	{
		if (this.writer != null)
		{
			this.writer.close();
			this.writer = null;
		}
	}
}
