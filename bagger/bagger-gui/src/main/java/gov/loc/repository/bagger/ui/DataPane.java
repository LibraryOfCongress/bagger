
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import gov.loc.repository.bagger.bag.Data;

public class DataPane extends BagTextPane {
	private static final long serialVersionUID = -414005711048973058L;

	public static final String DATA_PANE = "dataPane";

    private JComponent data;

    public DataPane() {
        super("Data");
    }

    public DataPane(Data data) {
        super(data.toString());
    }

    protected JComponent createFormControl() {
        return this;
    }

    public boolean requestFocusInWindow() {
        return this.requestFocusInWindow();
    }
}