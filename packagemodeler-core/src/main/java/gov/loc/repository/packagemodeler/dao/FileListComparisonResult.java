package gov.loc.repository.packagemodeler.dao;

import gov.loc.repository.packagemodeler.packge.FileName;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileListComparisonResult {

	private static final Log log = LogFactory.getLog(FileListComparisonResult.class);
	
	/*
	 * A list of FileNames of the files that are in the source list, but not in the target list.
	 */
	public List<FileName> missingFromTargetList;

	/*
	 * A list of FileNames of the files that are in the target list, but not in the source list.
	 */
	public List<FileName> additionalInTargetList;

	/*
	 * A list of FileNames that are in both the source list and target list, but have mismatches between fixity values for a common fixity algorithm.
	 */
	public List<FileName> fixityMismatchList;
	
	/*
	 * A list of FileNames for files that can't be compared.
	 * The possible reasons for incomparability will depend on the nature of the source list and target list.
	 * However, one possible reason is that a file in both the source list and target list doesn't share a fixity algorithm.
	 * Note that changeable File Instances (i.e., File Instances without fixities) will not be included in this list.
	 */
	public List<FileName> incomparableList;
	
	public boolean isEqual()
	{
		if (this.missingFromTargetList.isEmpty() && this.additionalInTargetList.isEmpty() && this.fixityMismatchList.isEmpty() && this.incomparableList.isEmpty())
		{
			return true;
		}
		if (! this.missingFromTargetList.isEmpty())
		{
			log.debug("MissingFromTargetList is not empty");
		}
		if (! this.additionalInTargetList.isEmpty())
		{
			log.debug("AdditionalInTargetList is not empty");
		}
		if (! this.fixityMismatchList.isEmpty())
		{
			log.debug("FixityMismatchList is not empty");
		}
		if (! this.incomparableList.isEmpty())
		{
			log.debug("IncomparableList is not empty");
		}

		return false;
	}
}
