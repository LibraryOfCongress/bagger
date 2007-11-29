package gov.loc.repository.utilities;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ManifestReader implements Iterator<ManifestReader.FileFixity> {

	private File file = null;
	private BufferedReader reader = null;
	private String nextLine = null;
	
	public void setFile(File file) throws Exception
	{
		this.file = file;
		this.reader = new BufferedReader(new FileReader(file));
		this.nextLine = this.reader.readLine();
	}
	
	public String getAlgorithm() throws Exception
	{
		return ManifestHelper.getAlgorithm(this.file);
	}
	
	public String getBasePath()
	{
		return ManifestHelper.getBasePath(this.file);
	}
		
	public boolean hasNext() {
		if (this.nextLine == null)
		{
			return false;
		}
		return true;
	}	
	
	public FileFixity next() {
		if (this.nextLine == null)
		{
			throw new NoSuchElementException();
		}
		String[] splitString = this.nextLine.split("  +");
		if (splitString.length != 2)
		{
			return null;
		}						
		try
		{
			this.nextLine = this.reader.readLine();
		}
		catch(IOException ex)
		{
			this.nextLine = null;
		}
		return new FileFixity(splitString[1], splitString[0]);
		
	}
	
	public void remove() {
		throw new UnsupportedOperationException();		
	}	
	
	public void close() throws IOException
	{
		if (this.reader != null)
		{
			this.reader.close();
		}
	}
	
	protected void finalize() throws IOException
	{
		this.close();
	}
	
	public class FileFixity
	{
		private String file;
		private String fixityValue;
		
		public FileFixity(String file, String fixityValue)
		{
			this.file = file;
			this.fixityValue = fixityValue;
		}
			
		public FileFixity()
		{			
		}
		
		public void setFile(String file) {
			this.file = file;
		}
		public String getFile() {
			return file;
		}
		public void setFixityValue(String fixityValue) {
			this.fixityValue = fixityValue;
		}
		public String getFixityValue() {
			return fixityValue;
		}
		
		
	}
	
}
