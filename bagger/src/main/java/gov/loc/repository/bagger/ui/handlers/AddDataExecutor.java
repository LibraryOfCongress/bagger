package gov.loc.repository.bagger.ui.handlers;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

import gov.loc.repository.bagger.ui.BagView;

public class AddDataExecutor extends AbstractActionCommandExecutor {
  BagView bagView;

  public AddDataExecutor(BagView bagView) {
    super();
    this.bagView = bagView;
  }

  @Override
  public void execute() {
    bagView.addDataHandler.addData();
  }

}
