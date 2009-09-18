
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

public class SaveBagExecutor extends AbstractActionCommandExecutor {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public SaveBagExecutor(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void execute() {
		if (bagView.bagRootPath.exists()) {
			bagView.tmpRootPath = bagView.bagRootPath;
			bagView.saveBagHandler.confirmWriteBag();
		} else {
			bagView.saveBagHandler.saveBag(bagView.bagRootPath);
		}
	}

}
