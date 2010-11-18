
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;

import gov.loc.repository.bagger.bag.BagOrganization;

public class BagOrganizationPane extends JPanel {
	private static final long serialVersionUID = 6086173640024935562L;

	public static final String BAG_ORGANIZATION_PANE = "bagOrganizationPane";

	private BagOrganization bagOrganization;

	protected JComponent createFormControl() {
	    return this;
	}
	
	public void setBagOrganization(BagOrganization bagOrganization) {
		this.bagOrganization = bagOrganization;
	}
	
	public BagOrganization getBagOrganization() {
		return this.bagOrganization;
	}

	public boolean requestFocusInWindow() {
	    return this.requestFocusInWindow();
	}
}
