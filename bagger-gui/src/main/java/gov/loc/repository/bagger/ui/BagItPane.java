
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import gov.loc.repository.bagger.bag.BagIt;

public class BagItPane extends BagTextPane {
	private static final long serialVersionUID = -3869406857748461748L;

	public static final String BAGIT_PANE = "bagItPane";

    private BagIt bagIt;

    public BagItPane() {
        super("BagIt");
    }

    public BagItPane(BagIt bagIt) {
        super(bagIt.toString());
        this.bagIt = bagIt;
    }
    
    protected JComponent createFormControl() {
        return this;
    }

    public boolean requestFocusInWindow() {
        return this.requestFocusInWindow();
    }
}
