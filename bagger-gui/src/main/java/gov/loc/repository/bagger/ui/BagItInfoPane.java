
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import gov.loc.repository.bagger.bag.BagItInfo;

public class BagItInfoPane extends BagTextPane {
	private static final long serialVersionUID = 5993030113524119051L;

	public static final String BAGITINFO_PANE = "bagItInfoPane";

    private BagItInfo info;

    public BagItInfoPane() {
        super("BagItInfo");
    }

    public BagItInfoPane(BagItInfo bagItInfo) {
        super(bagItInfo.toString());
        this.info = bagItInfo;
    }

    protected JComponent createFormControl() {
        return this;
    }

    public boolean requestFocusInWindow() {
        return this.requestFocusInWindow();
    }
}