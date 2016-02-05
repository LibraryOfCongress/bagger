package gov.loc.repository.bagger.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;

import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;

public class ProfilesView extends AbstractView {

  @Override
  protected JComponent createControl() {
    JPanel bagViewPanel = new JPanel(new BorderLayout(2, 2));
    InfoFormsPane infoPanel = ApplicationContextUtil.getBagView().infoInputPane;
    bagViewPanel.add(infoPanel);
    return bagViewPanel;
  }

  @Override
  protected void registerLocalCommandExecutors(PageComponentContext context) {
    context.register("startCommand", ApplicationContextUtil.getBagView().startExecutor);
    context.register("openCommand", ApplicationContextUtil.getBagView().openExecutor);
    context.register("createBagInPlaceCommand", ApplicationContextUtil.getBagView().createBagInPlaceExecutor);
    context.register("clearCommand", ApplicationContextUtil.getBagView().clearExecutor);
    context.register("validateCommand", ApplicationContextUtil.getBagView().validateExecutor);
    context.register("completeCommand", ApplicationContextUtil.getBagView().completeExecutor);
    context.register("addDataCommand", ApplicationContextUtil.getBagView().addDataExecutor);
    context.register("saveBagCommand", ApplicationContextUtil.getBagView().saveBagExecutor);
    context.register("saveBagAsCommand", ApplicationContextUtil.getBagView().saveBagAsExecutor);
  }
}
