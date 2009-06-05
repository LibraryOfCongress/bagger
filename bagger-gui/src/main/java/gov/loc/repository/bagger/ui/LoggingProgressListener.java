package gov.loc.repository.bagger.ui;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.ProgressListener;

public class LoggingProgressListener implements ProgressListener {

    private static final Log log = LogFactory.getLog(LoggingProgressListener.class);

	
	@Override
	public void reportProgress(String activity, String item, int count,
			int total) {
		log.debug(MessageFormat.format("{0} {1} ({2} of {3})", activity, item, count, total));
	}

}
