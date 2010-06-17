package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

public class ConsoleView extends AbstractView {

	public static ConsoleView instance;
	
	private ConsolePane consolePane;

	public ConsoleView() {
		instance = this;
	}

	@Override
	protected JComponent createControl() {
		consolePane =  new ConsolePane(getInitialConsoleMsg());
		return consolePane;
	}

	protected void registerLocalCommandExecutors(PageComponentContext context) {
		context.register("startCommand", BagView.instance.startExecutor);
		context.register("openCommand", BagView.instance.openExecutor);
		context.register("createBagInPlaceCommand",
				BagView.instance.createBagInPlaceExecutor);
		context.register("clearCommand", BagView.instance.clearExecutor);
		context.register("validateCommand", BagView.instance.validateExecutor);
		context.register("completeCommand", BagView.instance.completeExecutor);
		context.register("addDataCommand", BagView.instance.addDataExecutor);
		context.register("saveBagCommand", BagView.instance.saveBagExecutor);
		context
				.register("saveBagAsCommand",
						BagView.instance.saveBagAsExecutor);
	}

	public static ConsoleView getInstance() {
		return instance;
	}
	
	public void addConsoleMessages(String messages) {
		consolePane.addConsoleMessages(messages);
    }
	
	public void clearConsoleMessages() {
		consolePane.clearConsoleMessages();
    }
	
	private String getInitialConsoleMsg() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(getMessage("consolepane.msg.help"));
    	buffer.append("\n\n");
    	buffer.append(getMessage("consolepane.status.help"));
    	buffer.append("\n\n");
    	return buffer.toString();
    }
}
