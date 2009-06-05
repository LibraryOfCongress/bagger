package gov.loc.repository.bagger.ui;

import java.text.MessageFormat;

import gov.loc.repository.bagit.ProgressListener;

public class PrintingProgressListener implements ProgressListener {

	@Override
	public void reportProgress(String activity, String item, int count,
			int total) {
		System.out.println(MessageFormat.format("{0} {1} ({2} of {3})", activity, item, count, total));
	}

}
