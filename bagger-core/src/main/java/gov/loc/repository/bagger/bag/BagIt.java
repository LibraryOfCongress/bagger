package gov.loc.repository.bagger.bag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagger.FileEntity;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.impl.BagItTxtImpl;

/**
 * The high-level BagIt business interface.
 *
 * <p>This is basically a data access object.
 * Bagger doesn't have a dedicated business facade.
 *
 *
examplebag/
|
|   bagit.txt
|    (BagIt-version: M.N                                                   )
|    (Tag-File-Character-Encoding: UTF-<N>                                   )
|
 *
 * @author Jon Steinbach
 */
public class BagIt extends BagItTxtImpl {
	private static final Log log = LogFactory.getLog(BagIt.class);

	private String versionLabel = "BagIt-version: ";
	private String version = "0.96";
	private String encodingLabel = "Tag-File-Character-Encoding: ";
	private String encoding = AbstractBagConstants.BAG_ENCODING; // Currently the only encoding type allowed for meta-data files
	private String name;
	private String content;

	public BagIt(BagFile bagFile, BagConstants bagConstants) {
		super(bagFile, bagConstants);
	}
	
	public BagIt(BagConstants bagConstants) {
		super(bagConstants);
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return this.encoding;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public void setContent(String data) {
		this.content = data;
	}

	public String getContent() {
		return this.content;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.versionLabel);
		sb.append(this.version);
		sb.append('\n');
		sb.append(this.encodingLabel);
		sb.append(this.encoding);
		sb.append('\n');

		return sb.toString();
	}

	public String write(File rootDir) {
		String message = null;
		try
		{
			File file = new File(rootDir, name);
			Writer writer = new OutputStreamWriter(new FileOutputStream(file), AbstractBagConstants.BAG_ENCODING);
			writer.write(this.toString());
			writer.close();
//			this.setFile(file);
		}
		catch(IOException e)
		{
			message = e.getMessage();
			log.error("EXCEPTION: FileEntity.write: " + e.getMessage());
		}
		return message;
	}

	public void writeData() {
		this.getContent();
	}
}
