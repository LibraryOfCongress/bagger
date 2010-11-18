
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

public class StartExecutor extends AbstractActionCommandExecutor {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public StartExecutor(BagView bagView) {
		super();
		setEnabled(true);
		this.bagView = bagView;
	}

	public void execute() {
		bagView.startNewBagHandler.newBag();
	}

}
