package gov.loc.repository.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.service.Memento;
import gov.loc.repository.service.MementoStore;
import gov.loc.repository.service.ServiceConstants;
import gov.loc.repository.utilities.ConfigurationFactory;

public class FileSystemMementoStore implements MementoStore {

	private static final Log log = LogFactory.getLog(FileSystemMementoStore.class);	
	private static final String PREFIX = "Task";
	private static final String EXTENSION = "obj";
	
	private File storeDirectory;
	
	public FileSystemMementoStore() throws Exception {
		String storeDirectoryName = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME).getString("filesystemmementostore.directory");
		this.storeDirectory = new File(storeDirectoryName);
		log.debug("MementoStore directory is " + this.storeDirectory.getAbsolutePath());
		if (this.storeDirectory.exists())
		{
			if(! this.storeDirectory.isDirectory())
			{
				throw new Exception(MessageFormat.format("MementoStore directory {0} is not a directory", this.storeDirectory));
			}
		}
		else
		{
			FileUtils.forceMkdir(this.storeDirectory);
		}
	}	

	public File getStoreDirectory()
	{
		return this.storeDirectory;
	}
	
	private File getFile(int key)
	{
		return new File(this.storeDirectory, PREFIX + key + "." + EXTENSION);
	}
	
	private int getKey(File file) throws Exception
	{
		int start = file.toString().lastIndexOf(PREFIX);
		if (start == -1)
		{
			throw new Exception(MessageFormat.format("Prefix {0} not found in {1}", PREFIX, file));
		}
		int end = file.toString().lastIndexOf(EXTENSION);
		if (end == -1)
		{
			throw new Exception(MessageFormat.format("Extension {0} not found in {1}", EXTENSION, file));
		}
		String key = file.toString().substring(start + PREFIX.length(), end-1);
		return Integer.parseInt(key);
	}
	
	public void delete(int key) throws Exception {
		FileUtils.forceDelete(this.getFile(key));

	}

	public Memento get(int key) throws Exception {
		
		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(new FileInputStream(this.getFile(key)));
			return (Memento)in.readObject();
		}
		finally
		{
			if (in != null)
			{
				in.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, Memento> getMementoMap() throws Exception {
		Map<Integer, Memento> mementoMap = new HashMap<Integer, Memento>();
		Collection<File> fileList = FileUtils.listFiles(this.storeDirectory, new String[] {EXTENSION}, false);
		for(File file : fileList)
		{
			int key = this.getKey(file);
			mementoMap.put(key, this.get(key));
		}
		return mementoMap;
	}

	public void put(int key, Memento memento) throws Exception {
		ObjectOutputStream out = null;
		try
		{
			out = new ObjectOutputStream(new FileOutputStream(this.getFile(key)));
			out.writeObject(memento);
			out.flush();
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

}
