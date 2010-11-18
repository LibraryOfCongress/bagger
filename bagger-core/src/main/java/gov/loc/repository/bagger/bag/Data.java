package gov.loc.repository.bagger.bag;

import java.util.List;
import java.io.File;
import javax.swing.JTree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagger.FileEntity;

/**
 *
|
\--- data/
     |
     |   Collection Overview.txt
     |    (... narrative description ...                                   )
     |
     |   Seed List.txt
     |    (... list of crawler starting point URLs ...                     )
     ....
 *
 * @author Jon Steinbach
 */
public class Data extends FileEntity {
	private static final Log log = LogFactory.getLog(Data.class);

	private List<BaggerFileEntity> files;
	private long sizeFiles = 0;
	private int numFiles = 0;

	private JTree tree;

	public void setData(JTree tree) {
		this.tree = tree;
	}

	public JTree getData() {
		return this.tree;
	}

	public void setFiles(List<BaggerFileEntity> files) {
		this.files = files;
	}

	public List<BaggerFileEntity> getFiles() {
		return this.files;
	}
	
	public void setSizeFiles(long size) {
		this.sizeFiles = size;
	}
	
	public long getSizeFiles() {
		return this.sizeFiles;
	}
	
	public void setNumFiles(int num) {
		this.numFiles = num;
	}
	
	public int getNumFiles() {
		return this.numFiles;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.files == null) {
		} else {
			sb.append(AbstractBagConstants.DATA_DIRECTORY + "/");
			sb.append('\n');
	    	for (int i=0; i < files.size(); i++) {
	    		BaggerFileEntity bfe = files.get(i);
	    		if (bfe.getIsIncluded()) {
		    		sb.append(bfe.toString());
		    		sb.append('\n');	    			
	    		}
	    	}
		}
		return sb.toString();
	}

	public void writeData() {
		this.fromString(toString());
	}
}
