package gov.loc.repository.bagger.bag;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagger.FileEntity;
import gov.loc.repository.bagger.util.MD5Checksum;
import gov.loc.repository.bagit.impl.AbstractBagConstants;

/**
 *
|
|   tagmanifest-sha1.txt
|     CHECKSUM NON-DATA-FILENAME
|
|    (e9c5753d65b1ef5aeb281c0bb880c6c8 manifest-md5.txt                  )
|    (93c53193ef96732c76e00b3fdd8f9dd3 bagit.txt                         )
|    (61c96810788283dc7be157b340e4eff4 bag-info.txt                    )
|    (55c7c80c6635d5a4c8fe76a940bf353e fetch.txt                         )
|
 *
 * @author Jon Steinbach
 */
public class BaggerTagManifest extends FileEntity {
	private static final Log log = LogFactory.getLog(BaggerTagManifest.class);

	private String type;

	private List<FileEntity> tagManifestList;

	private BaggerBag baggerBag;

	private String fname;

	public BaggerTagManifest() {
		super();
	}

	public BaggerTagManifest(BaggerBag baggerBag) {
		super();
		this.baggerBag = baggerBag;
		buildTagManifestList();
	}

	private void buildTagManifestList() {
		// add data files to manifest
		List<File> fileList = new ArrayList<File>();
		File bagItFile = new File(baggerBag.getRootDir(), AbstractBagConstants.BAGIT_TXT);
		fileList.add(bagItFile);
		File bagInfoFile = new File(baggerBag.getRootDir(), AbstractBagConstants.BAGINFO_TXT);
		fileList.add(bagInfoFile);
    	if (this.baggerBag.getIsHoley()) {
    		File fetchFile = new File(baggerBag.getRootDir(), AbstractBagConstants.FETCH_TXT);
    		fileList.add(fetchFile);
    	}
		List<BaggerManifest> mlist = this.baggerBag.getBaggerManifests();
		for (int m=0; m < mlist.size(); m++) {
			BaggerManifest manifest = mlist.get(m);
			File mfile = new File(baggerBag.getRootDir(), manifest.getName());
			fileList.add(mfile);
		}

		//log.info("TagManifest.buildTagManifestList::" + fileList.size() );
		tagManifestList = new ArrayList<FileEntity>();
		for (int i=0; i < fileList.size(); i++) {
			File file = fileList.get(i);
			if (file != null && file.exists()) {
				//log.info("TagManifest.fileList: " + file.getName() );
				FileEntity fileEntity = new FileEntity();
				fileEntity.setFile(file);
				String filename = file.getAbsolutePath();
				//log.info("TagManifest.tagManifestList filename::" + filename );
				try {
					String checksum = MD5Checksum.getMD5Checksum(filename);
					fileEntity.setChecksum(checksum);
					fileEntity.setName(filename);
					fileEntity.setPath(file.getParent());
				} catch (Exception e) {
					log.error("Manifest.buildTagManifestList checksum: " + e);
				}
				tagManifestList.add(fileEntity);				
			}
		}
		//log.info("TagManifest.tagManifestList::" + tagManifestList.size() );
	}

	public void setType(String type) {
		this.type = type;
		this.fname = AbstractBagConstants.TAG_MANIFEST_PREFIX + type + AbstractBagConstants.TAG_MANIFEST_SUFFIX;
		setName(this.fname);
	}

	public String getType() {
		return this.type;
	}

	public void setTagManifestList(List<FileEntity> list) {
		this.tagManifestList = list;
	}

	public List<FileEntity> getTagManifestList() {
		return this.tagManifestList;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		File parent = this.baggerBag.getRootDir();
		for (int i=0; i < tagManifestList.size(); i++) {
			FileEntity fe = tagManifestList.get(i);
			sb.append(fe.getChecksum());
			sb.append("  ");
			String filename = fe.getName();
			try {
				if (parent != null && filename != null) filename = FilenameHelper.removeBasePath(parent.getAbsolutePath(), fe.getName());
			} catch (Exception e) {
				log.error("BaggerTagmanifest.toString: " + e.getMessage());
			}
			sb.append(filename);
			sb.append('\n');
		}

		//log.info("TagManifest.toString:: " + sb.toString());
		this.fromString(sb.toString());
		return sb.toString();
	}

	public void writeData() {
		//log.info("TagManifest.writeData");
		buildTagManifestList();
		this.fromString(toString());
	}
}
