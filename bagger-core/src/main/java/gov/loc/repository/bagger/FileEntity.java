package gov.loc.repository.bagger;

import gov.loc.repository.bagit.bag.BagHelper;

import java.io.File;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple JavaBean domain object adds a file property to <code>BaseEntity</code>.
 * Used as a base class for objects needing these properties.
 *
 * @author Jon Steinbach
 */
public class FileEntity extends BaseEntity {
	private static final Log log = LogFactory.getLog(FileEntity.class);

	private File file;
	
	private String path;
	
	private String name;

	private String data;
	
	private String checksum;

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}

	public void fromString(String data) {
		this.data = data;
	}

	public String toString() {
		return this.data;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public String getChecksum() {
		return this.checksum;
	}

	public void write(File rootDir) {
		try
		{
			File file = new File(rootDir, name);
			Writer writer = new OutputStreamWriter(new FileOutputStream(file), BagHelper.BAG_ENCODING);
			writer.write(this.data);
			writer.close();
			this.setFile(file);
		}
		catch(IOException e)
		{
			log.error("EXCEPTION: FileEntity.write: " + e.getMessage());
		}
	}
}
