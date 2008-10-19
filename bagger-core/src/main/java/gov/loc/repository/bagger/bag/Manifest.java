package gov.loc.repository.bagger.bag;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagger.FileEntity;
import gov.loc.repository.bagger.util.MD5Checksum;
import gov.loc.repository.bagit.bag.BagHelper;

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
public class Manifest extends FileEntity {
	private static final Log log = LogFactory.getLog(Manifest.class);

	private String type;

	private List<FileEntity> manifestList;
	
	private long totalSize = 0;
	
	private int numFiles = 0;

	private Bag bag;
	
	private Data data;

	private String fname;

	public Manifest() {
		super();
	}

	public Manifest(Bag bag) {
		super();
		this.bag = bag;
		this.data = bag.getData();
		buildManifestList();
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
		List<File> fileList = data.getFiles();
		manifestList = new ArrayList<FileEntity>();
		for (int i=0; i < fileList.size(); i++) {
			File file = fileList.get(i);
			FileEntity fileEntity = new FileEntity();
			fileEntity.setFile(file);
			numFiles++;
			long fileSize = file.length();
			totalSize += fileSize;
			fileEntity.setSize(fileSize);
			String filename = file.getAbsolutePath();
			File parent = this.bag.getRootSrc();
			if (parent != null) log.debug("Manifest.buildManifestList parent: " + parent.getAbsolutePath() + ", filename: " + filename);
			try {
				String checksum = MD5Checksum.getMD5Checksum(filename);
				fileEntity.setChecksum(checksum);
				fileEntity.setName(filename);
				fileEntity.setPath(file.getParent());
			} catch (Exception e) {
				log.error("Manifest.buildManifestList checksum: " + e);
			}
			manifestList.add(fileEntity);
		}
		log.debug("finished...Manifest.buildManifestList");
	}

	public void setType(String type) {
		this.type = type;
		this.fname = BagHelper.MANIFEST_PREFIX + type + BagHelper.MANIFEST_SUFFIX;
		setName(this.fname);
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

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		File parent = this.bag.getRootSrc();
		for (int i=0; i < manifestList.size(); i++) {
			FileEntity fe = manifestList.get(i);
			sb.append(fe.getChecksum());
			sb.append("  " + BagHelper.DATA_DIRECTORY + "/");
			String filename = fe.getName();
			if (parent != null) filename = FilenameHelper.removeBasePath(parent.getAbsolutePath(), fe.getName());
			sb.append(filename);
			sb.append('\n');
		}

		return sb.toString();
	}

	public void writeData() {
		this.fromString(toString());
	}
}
