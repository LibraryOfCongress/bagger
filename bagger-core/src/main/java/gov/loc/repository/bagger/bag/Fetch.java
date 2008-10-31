package gov.loc.repository.bagger.bag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagger.FileEntity;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.impl.FetchTxtImpl;

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
public class Fetch extends FetchTxtImpl {
	private static final Log log = LogFactory.getLog(Fetch.class);

	List<FileEntity> uris;
	String fetchContent = null;
	private String name;
	private String content;

	public Fetch(BaggerBag bag) {
		super(bag);
	}
	
	public Fetch(BaggerBag bag, BagFile sourceBagFile) {
		super(bag, sourceBagFile);
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (fetchContent == null) {
			sb.append("<URI> ");
			sb.append("<CHECKSUM> ");
			sb.append("data/<FILENAME>");
			sb.append('\n');			
		} else {
			sb.append(fetchContent);
		}

		return sb.toString();
	}
	
	public String write(File rootDir) {
		String message = null;
		try
		{
			File file = new File(rootDir, name);
			Writer writer = new OutputStreamWriter(new FileOutputStream(file), AbstractBagConstants.BAG_ENCODING);
			writer.write(this.content);
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
