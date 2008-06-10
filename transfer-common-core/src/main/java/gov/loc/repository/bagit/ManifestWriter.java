package gov.loc.repository.bagit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ManifestWriter {
	//private File parentDir;
	//private String algorithm;
	private PrintWriter writer = null;
	private File manifestFile = null;
	
	public ManifestWriter(File manifestFile) {

		if (manifestFile == null)
		{
			throw new RuntimeException("Manifest file may not be null");
		}
		this.manifestFile = manifestFile;
		
		String algorithm = ManifestHelper.getAlgorithm(manifestFile);
		if (algorithm == null)
		{
			throw new RuntimeException("Algorithm not found in manifest file name");
		}
		try
		{
			this.writer = new PrintWriter(this.manifestFile);
		}
		catch(FileNotFoundException ex)
		{
			throw new RuntimeException(ex);
		}
				
	}
		
	public File getManifestFile()
	{
		return this.manifestFile;
	}
	
	public void write(String file, String fixityValue)
	{
		if (this.writer == null)
		{
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
