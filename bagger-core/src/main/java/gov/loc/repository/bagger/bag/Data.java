package gov.loc.repository.bagger.bag;

import java.util.List;
import java.io.File;
import javax.swing.JTree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.bag.BagHelper;
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

	private List<File> files;
	private List<FileEntity> data;

	private JTree tree;

	public void setData(JTree tree) {
		this.tree = tree;
	}

	public JTree getData() {
		return this.tree;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

	public List<File> getFiles() {
		return this.files;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.files == null) {
		} else {
			sb.append(BagHelper.DATA_DIRECTORY + "/");
			sb.append('\n');
	    	for (int i=0; i < files.size(); i++) {
	    		sb.append(files.get(i).toString());
	    		sb.append('\n');
	    	}
		}
		return sb.toString();
	}

	public void writeData() {
		this.fromString(toString());
	}
}
