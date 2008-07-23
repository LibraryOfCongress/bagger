package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;

import gov.loc.repository.transfer.components.AbstractComponent;
import gov.loc.repository.transfer.components.filemanagement.ZFSFileSystemCreator;
import gov.loc.repository.utilities.ProcessBuilderWrapper;
import gov.loc.repository.utilities.ProcessBuilderWrapper.ProcessBuilderResult;

public class ZFSFileSystemCreatorImpl extends AbstractComponent implements
		ZFSFileSystemCreator {

	private String pool;
	private ProcessBuilderWrapper pb;
	
	public ZFSFileSystemCreatorImpl(String pool, ProcessBuilderWrapper pb)
	{
		this.pool = pool;
		this.pb = pb;
	}
	
	@Override
	protected String getComponentName() {
		return COMPONENT_NAME;
	}

	@Override
	public void create(String mountPath, Long size) throws Exception {
		File mountFile = new File(mountPath);
		String path = mountFile.getParent();

		String checkSizeCommandLine = "zfs get -o value -H -p avail " + this.pool + path;
		ProcessBuilderResult checkSizeResult = pb.execute(new File("."), checkSizeCommandLine);
		if (checkSizeResult.getExitValue() != 0)
		{
			throw new RuntimeException(MessageFormat.format("Commandline {0} returned {1}.  The output was: {2}", checkSizeCommandLine, checkSizeResult.getExitValue(), checkSizeResult.getExitValue()));
		}
		if (Long.parseLong(checkSizeResult.getOutput()) < size)
		{
			throw new RuntimeException(MessageFormat.format("{0} space is needed, but only {1} is available.", size, checkSizeResult.getOutput()));
		}
		
		String createCommandLine = "zfs create " + this.pool + mountPath;
		ProcessBuilderResult createResult = pb.execute(new File("."), createCommandLine);
		if (createResult.getExitValue() != 0)
		{
			throw new RuntimeException(MessageFormat.format("Commandline {0} returned {1}.  The output was: {2}", createCommandLine, createResult.getExitValue(), createResult.getExitValue()));
		}
				
	}

}
