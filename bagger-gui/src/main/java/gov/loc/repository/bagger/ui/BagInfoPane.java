
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import gov.loc.repository.bagger.bag.BagInfo;

public class BagInfoPane extends BagTextPane {
	private static final long serialVersionUID = 5993030113524119051L;

	public static final String BAGINFO_PANE = "bagInfoPane";

    private BagInfo bagInfo;

    public BagInfoPane() {
        super("BagInfo");
    }

    public BagInfoPane(BagInfo bagInfo) {
        super(bagInfo.toString());
        this.bagInfo = bagInfo;
    }

    protected JComponent createFormControl() {
        return this;
    }

    public boolean requestFocusInWindow() {
        return this.requestFocusInWindow();
    }
}