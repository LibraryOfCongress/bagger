package gov.loc.repository.bagger.bag;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagger.FileEntity;
import gov.loc.repository.bagger.util.MD5Checksum;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.impl.ManifestImpl;
import gov.loc.repository.bagit.BagFile;

/**
 *
|
|   manifest-md5.txt
|     CHECKSUM FILENAME
|
|    (93c53193ef96732c76e00b3fdd8f9dd3 data/Collection Overview.txt        )
|    (e9c5753d65b1ef5aeb281c0bb880c6c8 data/Seed List.txt                  )
|    (61c96810788283dc7be157b340e4eff4 data/gov-20060601-oth-050019.arc.gz )
|    (55c7c80c6635d5a4c8fe76a940bf353e data/gov-20060601-img-100002.arc.gz )
|
 *
 * @author Jon Steinbach
 */
public class BaggerManifest extends ManifestImpl {
	private static final Log log = LogFactory.getLog(BaggerManifest.class);

	private String type;

	private List<FileEntity> manifestList;
	
	private long totalSize = 0;	
	private int numFiles = 0;

	private BaggerBag baggerBag;
	private Data data;
	private long size;
	private String path;
	private String content;
	private String name;
	private String checksum;
	private String fname;
	
	public BaggerManifest(String name, BaggerBag bag) {
		super(name, bag);
		this.name = name;
		this.baggerBag = bag;
		this.data = baggerBag.getData();
		buildManifestList();			
	}
	
	public BaggerManifest(String name, BaggerBag bag, BagFile sourceBagFile) {
		super(name, bag, sourceBagFile);
		this.name = name;
		this.baggerBag = bag;
		this.data = baggerBag.getData();
		buildManifestList();
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
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public long getSize() {
		return this.size;
	}

	public void setContent(String data) {
		this.content = data;
	}

	public String getContent() {
		return this.content;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public String getChecksum() {
		return this.checksum;
	}

	public long getTotalSize() {
		return this.totalSize;
	}
	
	public int getNumFiles() {
		return this.numFiles;
	}
	
	private void buildManifestList() {
		log.debug("Manifest.buildManifestList begin...");
		// add data files to manifest
		totalSize = 0;
		numFiles = 0;
		Collection<BagFile> bagFileList = baggerBag.getPayloadFiles();
//		Object[] fileList = bagFileList.toArray();
		List<BaggerFileEntity> fileList = data.getFiles();
		manifestList = new ArrayList<FileEntity>();
		int size = fileList.size();
		log.info("buildManifestList: " + size);
		for (int i=0; i < size; i++) {
//			BagFile bagFile = (BagFile) fileList[i];
//			File file = new File(bagFile.getFilepath());
			BaggerFileEntity bfe = fileList.get(i);
			if (bfe.getIsIncluded()) {
				File file = bfe.getRootSrc();
				//System.out.println("buildManifestList: " + file.getAbsolutePath());
				FileEntity fileEntity = new FileEntity();
				fileEntity.setFile(file);
				numFiles++;
				long fileSize = file.length();
				totalSize += fileSize;
				fileEntity.setSize(fileSize);
				String filename = file.getAbsolutePath();
				try {
					String checksum = MD5Checksum.getMD5Checksum(filename);
					fileEntity.setChecksum(checksum);
					fileEntity.setName(bfe.getNormalizedName());
//					fileEntity.setName(filename);
					fileEntity.setPath(bfe.getRootParent().getAbsolutePath());
				} catch (Exception e) {
					log.error("Manifest.buildManifestList checksum: " + e);
				}
				manifestList.add(fileEntity);				
			}
		}
		log.debug("finished...Manifest.buildManifestList");
	}

	public void setType(String type) {
		this.type = type;
		this.fname = AbstractBagConstants.PAYLOAD_MANIFEST_PREFIX + type + AbstractBagConstants.PAYLOAD_MANIFEST_SUFFIX;
//		setName(this.fname);
	}

	public String getType() {
		return this.type;
	}

	public void setManifestList(List<FileEntity> list) {
		this.manifestList = list;
	}

	public List<FileEntity> getManifestList() {
		return this.manifestList;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Data getData() {
		return this.data;
	}
/* */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i < manifestList.size(); i++) {
			FileEntity fe = manifestList.get(i);
			sb.append(fe.getChecksum());
			sb.append("  " + AbstractBagConstants.DATA_DIRECTORY + "/");
			String filename = fe.getName();
			sb.append(filename);
			sb.append('\n');
		}

		return sb.toString();
	}
/* */
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
//		this.fromString(toString());
	}
}
