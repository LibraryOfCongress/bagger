package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import gov.loc.repository.transfer.components.filemanagement.impl.ByFileDirectoryCopier.FileCopier;

@Component("commonsIOLocalFileCopier")
public class CommonsIOLocalFileCopier implements FileCopier {

	@Override
	public void copy(File srcFile, File destFile) {
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

	}

}
