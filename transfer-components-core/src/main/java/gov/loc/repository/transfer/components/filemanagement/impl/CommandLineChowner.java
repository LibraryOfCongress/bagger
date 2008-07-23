package gov.loc.repository.transfer.components.filemanagement.impl;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.Chowner;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.utilities.OperatingSystemHelper;
import gov.loc.repository.utilities.ProcessBuilderWrapper;
import gov.loc.repository.utilities.ProcessBuilderWrapper.ProcessBuilderResult;

@Component("commandLineChowner")
public class CommandLineChowner implements Chowner {

	private static final Log log = LogFactory.getLog(CommandLineChowner.class);

	protected ProcessBuilderWrapper pb;
	
	@Autowired
	public CommandLineChowner(ProcessBuilderWrapper pb) {
		this.pb = pb;
	}
	
	@Override
	public void changeOwner(CopyDescription copyDescription) {
		
		if (OperatingSystemHelper.isWindows())
		{
			log.warn("Not changing permissions, since OS is Windows.");
			return;
		}
		
		String user = copyDescription.additionalParameters.get(Chowner.USER_KEY);
		String group = copyDescription.additionalParameters.get(Chowner.GROUP_KEY);
		
		String commandLine = MessageFormat.format("find {0} ! -user {1} -o ! -group {2} | xargs -r chown {1}:{2}", copyDescription.destMountPath, user, group);
		log.debug("Commandline is " + commandLine);
		ProcessBuilderResult result = pb.execute(commandLine);
		if (result.getExitValue() != 0)
		{
			log.error(MessageFormat.format("{0} returned {1}.  Output was {2}", commandLine, result.getExitValue(), result.getOutput()));
			throw new RuntimeException("Changing owner failed");
		}

	}


}
