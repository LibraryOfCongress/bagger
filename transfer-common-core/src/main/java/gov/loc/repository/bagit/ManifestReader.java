package gov.loc.repository.bagit;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManifestReader implements Iterator<ManifestReader.FileFixity> {

	private static final Log log = LogFactory.getLog(ManifestReader.class);
	
	private File file = null;
	private BufferedReader reader = null;
	private FileFixity next = null;
	
	public ManifestReader(File file) {
		this.file = file;
		try
		{
			this.reader = new BufferedReader(new FileReader(file));
			this.setNext();
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
		
	public String getAlgorithm()
	{
		return ManifestHelper.getAlgorithm(this.file);
	}
	
	public String getBasePath()
	{
		return ManifestHelper.getBasePath(this.file);
	}
		
	public boolean hasNext() {
		if (this.next == null)
		{
			return false;
		}
		return true;
	}	
	
	private void setNext()
	{
		try
		{
			while(true)
			{
				String line = this.reader.readLine();
				if (line == null)
				{
					this.next = null;
					return;
				}
				String[] splitString = line.split("  +");
				if (splitString.length == 2)
				{
					this.next = new FileFixity(splitString[1], splitString[0]);
					return;
				}						
				
			}
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
				
	}
	
	public FileFixity next() {
		if (this.next == null)
		{
			throw new NoSuchElementException();
		}
		FileFixity returnFileFixity = this.next;
		this.setNext();
		return returnFileFixity;
		
	}
	
	public void remove() {
		throw new UnsupportedOperationException();		
	}	
	
	public void close()
	{
		try
		{
			if (this.reader != null)
			{
				this.reader.close();
			}
		}
		catch(IOException ex)
		{
			log.error(ex);
		}
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
