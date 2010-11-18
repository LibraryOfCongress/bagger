
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import gov.loc.repository.bagger.bag.BaggerManifest;

public class ManifestPane extends BagTextPane {
	private static final long serialVersionUID = 28997773979305889L;

	public static final String MANIFEST_PANE = "manifestPane";

    private JComponent manifest;

    public ManifestPane() {
	    super("");
    }

    public ManifestPane(String content) {
	    super(content);
    }

    protected JComponent createFormControl() {
	    return this;
	}

	public boolean requestFocusInWindow() {
	    return this.requestFocusInWindow();
	}
}
