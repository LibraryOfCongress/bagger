package gov.loc.repository.filefilters;

import java.io.File;
import java.io.FileFilter;

public class TiffFileFilter implements FileFilter {

	public boolean accept(File file) {
		if (file.toString().toLowerCase().endsWith(".tif"))
		{
			return false;
		}
		return true;
	}

}
