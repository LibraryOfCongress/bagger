package gov.loc.repository.bagger.bag;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagger.FileEntity;

/**
 *
|
|   fetch.txt
|	 URL LENGTH FILENAME
|
|    (http://WB20.Stanford.Edu/gov-06-2006-ARC/gov-20060601-oth-050019.arc.gz
|        26583985 data/gov-20060601-oth-050019.arc.gz                      )
|    (http://WB20.Stanford.Edu/gov-06-2006-ARC/gov-20060601-img-100002.arc.gz
|        99509720 data/gov-20060601-img-100002.arc.gz                      )
|    ( ..................................................................... )
|
 *
 * @author Jon Steinbach
 */
public class Fetch extends FileEntity {
	private static final Log log = LogFactory.getLog(Fetch.class);

	List<FileEntity> uris;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<URI> ");
		sb.append("<CHECKSUM> ");
		sb.append("data/<FILENAME>");
		sb.append('\n');

		return sb.toString();
	}

	public void writeData() {
		this.fromString(toString());
	}
}
