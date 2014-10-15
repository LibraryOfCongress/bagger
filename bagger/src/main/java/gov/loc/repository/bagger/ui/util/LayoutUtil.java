package gov.loc.repository.bagger.ui.util;

import java.awt.GridBagConstraints;

public class LayoutUtil {

	public static GridBagConstraints buildGridBagConstraints(int x, int y, int w, int h,
			int wx, int wy, int fill, int anchor) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x; // start cell in a row
		gbc.gridy = y; // start cell in a column
		gbc.gridwidth = w; // how many column does the control occupy in the row
		gbc.gridheight = h; // how many column does the control occupy in the
							// column
		gbc.weightx = wx; // relative horizontal size
		gbc.weighty = wy; // relative vertical size
		gbc.fill = fill; // the way how the control fills cells
		gbc.anchor = anchor; // alignment
		
		return gbc;
	}

}
