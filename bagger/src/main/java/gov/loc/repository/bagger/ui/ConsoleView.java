package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

public class ConsoleView extends AbstractView {

  private static ConsoleView instance;

  private ConsolePane consolePane;

  public ConsoleView() {
    instance = this;
  }

  @Override
  protected JComponent createControl() {
    consolePane = new ConsolePane(getInitialConsoleMsg());
    return consolePane;
  }

  @Override
  protected void registerLocalCommandExecutors(PageComponentContext context) {
    context.register("startCommand", BagView.getInstance().startExecutor);
    context.register("openCommand", BagView.getInstance().openExecutor);
    context.register("createBagInPlaceCommand", BagView.getInstance().createBagInPlaceExecutor);
    context.register("clearCommand", BagView.getInstance().clearExecutor);
    context.register("validateCommand", BagView.getInstance().validateExecutor);
    context.register("completeCommand", BagView.getInstance().completeExecutor);
    context.register("addDataCommand", BagView.getInstance().addDataExecutor);
    context.register("saveBagCommand", BagView.getInstance().saveBagExecutor);
    context.register("saveBagAsCommand", BagView.getInstance().saveBagAsExecutor);
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
