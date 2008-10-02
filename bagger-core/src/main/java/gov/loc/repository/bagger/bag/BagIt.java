package gov.loc.repository.bagger.bag;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagger.FileEntity;
import gov.loc.repository.bagit.bag.impl.BagGeneratorVerifierImpl;


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
|    (BagIt-version: 0.95                                                   )
|    (Tag-File-Character-Encoding: UTF-8                                   )
|
 *
 * @author Jon Steinbach
 */
public class BagIt extends FileEntity {
	private static final Log log = LogFactory.getLog(BagIt.class);

	private String versionLabel = "BagIt-version: ";
	private String version = BagGeneratorVerifierImpl.VERSION;
	private String encodingLabel = "Tag-File-Character-Encoding: ";
	private String encoding = "UTF-8"; // Currently the only encoding type allowed for meta-data files

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

	public void writeData() {
		this.fromString(toString());
	}
}
