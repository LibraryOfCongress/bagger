
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import gov.loc.repository.bagger.bag.Fetch;

public class FetchPane extends BagTextPane {
	private static final long serialVersionUID = -2018135461133794623L;

	public static final String FETCH_PANE = "fetchPane";

	private JComponent fetch;

	public FetchPane() {
	    super("BagIt");
	}

	public FetchPane(Fetch fetch) {
	    super(fetch.toString());
	}

	protected JComponent createFormControl() {
	    return this;
	}

	public boolean requestFocusInWindow() {
	    return this.requestFocusInWindow();
	}
}
