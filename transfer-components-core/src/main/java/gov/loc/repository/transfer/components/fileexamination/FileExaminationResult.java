package gov.loc.repository.transfer.components.fileexamination;

import java.text.MessageFormat;
import java.util.Date;

public class FileExaminationResult {
	public String relativeFilename = null;

	/*
	 * Absence of a fixityValue indicates that the file is not present or cannot be read.
	 */
	public String fixityValue = null;
	
	public String fixityAlgorithm = null;
	
	/*
	 * Size in bytes.
	 */
	public Long size = null;
	
	public Date modifiedDate = null;
	
	@Override
	public String toString() {
		return MessageFormat.format("Relative filename is {0}.  Fixity ({1}) is {2}.  Size is {3}.  Modified date {4}.", relativeFilename, fixityAlgorithm, fixityValue, size, modifiedDate);
	}	
}
