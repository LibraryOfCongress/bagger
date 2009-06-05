
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;

public class NextButtonHandler extends AbstractAction {
	private static final Log log = LogFactory.getLog(NextButtonHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;
	
	public NextButtonHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		this.bag = bagView.getBag();

        int selected = bagView.bagInfoInputPane.getSelectedIndex();
        int count = bagView.bagInfoInputPane.getComponentCount();
        if (selected >= 0 && selected < count-1) {
        	bagView.bagInfoInputPane.setSelectedIndex(selected+1);
        } else {
        	bagView.bagInfoInputPane.setSelectedIndex(0);
        }
        bagView.bagInfoInputPane.verifyForms(bag);
        bagView.bagInfoInputPane.update(bag);
        bagView.updateProfile();
        if (bagView.bagInfoInputPane.hasFormErrors(bag)) {
        	bagView.infoFormMessagePane.setMessage(bagView.getPropertyMessage("error.form"));
        	bagView.infoFormMessagePane.setBackground(bagView.errorColor);
        } else {
        	bagView.infoFormMessagePane.setMessage("");
        	bagView.infoFormMessagePane.setBackground(bagView.infoColor);
        }
        bagView.bagInfoInputPane.invalidate();
        java.awt.Component comp = bagView.bagInfoInputPane.getComponent(0);
        comp.requestFocus();
        comp.transferFocus();
	}
}
