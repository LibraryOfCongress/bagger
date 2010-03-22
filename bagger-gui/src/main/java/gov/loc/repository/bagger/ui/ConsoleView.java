package gov.loc.repository.bagger.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

public class ConsoleView  extends AbstractView{

	@Override
	protected JComponent createControl() {
		JPanel consolePane = new JPanel(new BorderLayout(2, 2));
		CompositePane compositePane= BagView.instance.compositePane;
		compositePane.setToolTipText(BagView.instance.getPropertyMessage("compositePane.tab.help"));
		consolePane.add(compositePane);
        return consolePane;
	}
	
	 protected void registerLocalCommandExecutors(PageComponentContext context) {
	    	context.register("startCommand", BagView.instance.startExecutor);
	    	context.register("openCommand", BagView.instance.openExecutor);
	    	context.register("createBagInPlaceCommand", BagView.instance.createBagInPlaceExecutor);
	    	context.register("clearCommand", BagView.instance.clearExecutor);
	    	context.register("validateCommand", BagView.instance.validateExecutor);
	    	context.register("completeCommand", BagView.instance.completeExecutor);
	    	context.register("addDataCommand", BagView.instance.addDataExecutor);
	    	context.register("saveBagCommand", BagView.instance.saveBagExecutor);
	    	context.register("saveBagAsCommand", BagView.instance.saveBagAsExecutor);
	    }
}
