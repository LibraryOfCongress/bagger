package gov.loc.repository.bagger.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

public class ProfilesView extends AbstractView {

  @Override
  protected JComponent createControl() {
    JPanel bagViewPanel = new JPanel(new BorderLayout(2, 2));
    InfoFormsPane infoPanel = BagView.getInstance().infoInputPane;
    bagViewPanel.add(infoPanel);
    return bagViewPanel;
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
}
