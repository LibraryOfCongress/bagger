package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

public class AddDataExecutor extends AbstractActionCommandExecutor {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public AddDataExecutor(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void execute() {
		bagView.addDataHandler.addData();
	}

}
