package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;
import gov.loc.repository.bagit.verify.impl.ParallelManifestChecksumVerifier;
import gov.loc.repository.bagit.verify.impl.ValidVerifierImpl;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateBagHandler extends AbstractAction implements Progress {
  private static final long serialVersionUID = 1L;
  protected static final Logger log = LoggerFactory.getLogger(ValidateBagHandler.class);
  private BagView bagView;
  private String messages;

  public ValidateBagHandler(BagView bagView) {
    super();
    this.bagView = bagView;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    validateBag();
  }

  public void validateBag() {
    bagView.statusBarBegin(this, "Validating bag...", "verifying file checksum");
  }

  @Override
  public void execute() {
    DefaultBag bag = bagView.getBag();
    try {
      CompleteVerifierImpl completeVerifier = new CompleteVerifierImpl();

      ParallelManifestChecksumVerifier manifestVerifier = new ParallelManifestChecksumVerifier();

      ValidVerifierImpl validVerifier = new ValidVerifierImpl(completeVerifier, manifestVerifier);
      validVerifier.addProgressListener(bagView.task);
      bagView.longRunningProcess = validVerifier;
      /* */
      messages = bag.validateBag(validVerifier);

      if (messages != null && !messages.trim().isEmpty()) {
        bagView.showWarningErrorDialog("Warning - validation failed", "Validation result: " + messages);
      }
      else {
        bagView.showWarningErrorDialog("Validation Dialog", "Validation successful.");
      }

      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          ApplicationContextUtil.addConsoleMessage(messages);
        }
      });
    }
    catch (Exception e) {
      log.error("Failed to validate bag", e);
      if (bagView.longRunningProcess.isCancelled()) {
        bagView.showWarningErrorDialog("Validation cancelled", "Validation cancelled.");
      }
      else {
        bagView.showWarningErrorDialog("Warning - validation interrupted", "Error trying validate bag: " + e.getMessage());
      }
    }
    finally {
      bagView.task.done();
      bagView.statusBarEnd();
    }
  }
}
