
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.NewBagFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

public class SaveBagExecutor extends AbstractActionCommandExecutor {
	private static final Log log = LogFactory.getLog(SaveBagExecutor.class);
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
