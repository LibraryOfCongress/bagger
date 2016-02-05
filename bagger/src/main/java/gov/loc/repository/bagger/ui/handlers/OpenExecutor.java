package gov.loc.repository.bagger.ui.handlers;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

import gov.loc.repository.bagger.ui.BagView;

public class OpenExecutor extends AbstractActionCommandExecutor {
  BagView bagView;

  public OpenExecutor(BagView bagView) {
    super();
    setEnabled(true);
    this.bagView = bagView;
  }

  @Override
  public void execute() {
    bagView.openBagHandler.openBag();

  }

}
