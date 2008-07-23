package gov.loc.repository.transfer.components.filemanagement.impl;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.Chmoder;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.utilities.OperatingSystemHelper;
import gov.loc.repository.utilities.ProcessBuilderWrapper;
import gov.loc.repository.utilities.ProcessBuilderWrapper.ProcessBuilderResult;

@Component("commandLineChmoder")
public class CommandLineChmoder implements Chmoder {

	private static final Log log = LogFactory.getLog(CommandLineChmoder.class);

	protected ProcessBuilderWrapper pb;
	
	@Autowired
	public CommandLineChmoder(ProcessBuilderWrapper pb) {
		this.pb = pb;
	}
	
	@Override
	public void changePermissions(CopyDescription copyDescription) {
		
		if (OperatingSystemHelper.isWindows())
		{
			log.warn("Not changing owner, since OS is Windows.");
			return;
		}
		
		String filePermissions = copyDescription.additionalParameters.get(Chmoder.FILE_PERMISSIONS_KEY);
		String dirPermissions = copyDescription.additionalParameters.get(Chmoder.DIR_PERMISSIONS_KEY);
		
		String commandLine = MessageFormat.format("find {0} -type d ! -perm {1} | xargs -r chmod {1}", copyDescription.destMountPath, dirPermissions);
		log.debug("Commandline is " + commandLine);
		ProcessBuilderResult result = pb.execute(commandLine);
		if (result.getExitValue() != 0)
		{
			log.error(MessageFormat.format("{0} returned {1}.  Output was {2}", commandLine, result.getExitValue(), result.getOutput()));
			throw new RuntimeException("Changing permissions for directory failed");
		}

		commandLine = MessageFormat.format("find {0} -type f ! -perm {1} | xargs -r chmod {1}", copyDescription.destMountPath, filePermissions);
		log.debug("Commandline is " + commandLine);
		result = pb.execute(commandLine);
		if (result.getExitValue() != 0)
		{
			log.error(MessageFormat.format("{0} returned {1}.  Output was {2}", commandLine, result.getExitValue(), result.getOutput()));
			throw new RuntimeException("Changing permissions for files failed");
		}

	}

}
