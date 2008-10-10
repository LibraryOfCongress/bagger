
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import gov.loc.repository.bagger.bag.Bag;

public class ConsolePane extends JPanel {
	private static final long serialVersionUID = -4290352509246639528L;

	public static final String CONSOLE_PANE = "consolePane";
	private Dimension dimension = new Dimension(100, 300);
	//private GridLayout layout = new GridLayout(0, 2, 10, 10);
    private GridBagLayout layout = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();
    private String messages = new String();

    private Bag bag;

    public ConsolePane() {
        super();
        this.setLayout(layout);
        createFormControl();
    }

    public ConsolePane(Bag bag) {
        super();
        this.setLayout(layout);
        this.bag = bag;
        createFormControl();
    }
    
    public ConsolePane(Bag bag, String messages) {
        super();
        this.setLayout(layout);
        this.bag = bag;
        this.messages = messages;
        createFormControl();
    }

    public void setBag(Bag bag) {
    	this.bag = bag;
    }
    
    public Bag getBag() {
    	return this.bag;
    }
    
    public void setMessages(String messages) {
    	this.messages = messages;
    }
    
    public String getMessages() {
    	return this.messages;
    }

    protected JComponent createFormControl() {
    	this.setMaximumSize(dimension);
    	Dimension formDimension = new Dimension(150, 25);
    	Dimension consoleDimension = new Dimension(300, 400);
        Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
/* */
    	JLabel completeLabel = new JLabel("Is Complete? ");
    	Font font = completeLabel.getFont().deriveFont(Font.BOLD);
    	completeLabel.setFont(font);
    	completeLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, 0, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(completeLabel, gbc);
        this.add(completeLabel);

        JCheckBox completeResult = new JCheckBox("");
    	if (bag != null) {
    		if (bag.getIsComplete()) completeResult.setSelected(true);
    		else completeResult.setSelected(false);
    	}
    	completeResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, 0, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(completeResult, gbc);
        this.add(completeResult);

    	JLabel validLabel = new JLabel("Is Valid? ");
    	font = validLabel.getFont().deriveFont(Font.BOLD);
    	validLabel.setFont(font);
    	validLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, 1, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validLabel, gbc);
        this.add(validLabel);

        JCheckBox validResult = new JCheckBox("");
    	if (bag != null) {
    		if (bag.getIsValid()) validResult.setSelected(true);
    		else validResult.setSelected(false);
    	}
    	validResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, 1, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validResult, gbc);
        this.add(validResult);

    	JLabel validMetaLabel = new JLabel("Is Valid Metadata? ");
    	font = validMetaLabel.getFont().deriveFont(Font.BOLD);
    	validMetaLabel.setFont(font);
    	validMetaLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, 2, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validMetaLabel, gbc);
        this.add(validMetaLabel);

        JCheckBox validMetaResult = new JCheckBox("");
    	if (bag != null) {
    		if (bag.getIsValidMetadata()) validMetaResult.setSelected(true);
    		else validMetaResult.setSelected(false);
    	}
    	validMetaResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validMetaResult, gbc);
        this.add(validMetaResult);

        if (bag == null || (bag != null && bag.getIsSerial())) {
        	JLabel serializedLabel = new JLabel("Is Packaged? ");
        	font = serializedLabel.getFont().deriveFont(Font.BOLD);
        	serializedLabel.setFont(font);
        	serializedLabel.setPreferredSize(formDimension);
            buildConstraints(gbc, 0, 3, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
            layout.setConstraints(serializedLabel, gbc);
            this.add(serializedLabel);

            JCheckBox serializedResult = new JCheckBox("");
        	if (bag != null) {
        		if (bag.getIsSerialized()) serializedResult.setSelected(true);
        		else serializedResult.setSelected(false);
        	}
        	serializedResult.setPreferredSize(formDimension);
            buildConstraints(gbc, 1, 3, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
            layout.setConstraints(serializedResult, gbc);
            this.add(serializedResult);        	
        }
    	JTextArea serializedArea = new JTextArea(this.messages);
    	if (bag != null) serializedArea.append("");
    	serializedArea.setEditable(false);
    	serializedArea.setLineWrap(true);
    	serializedArea.setRows(20);
    	serializedArea.setColumns(10);
    	serializedArea.setBackground(Color.LIGHT_GRAY);
    	serializedArea.setWrapStyleWord(true);
    	serializedArea.setAutoscrolls(true);
    	serializedArea.setPreferredSize(consoleDimension);
    	serializedArea.setBorder(BorderFactory.createLineBorder(Color.black));
        buildConstraints(gbc, 0, 4, 2, 4, 10, 10, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        layout.setConstraints(serializedArea, gbc);
        this.add(serializedArea);

        this.setBorder(emptyBorder);
/* */    	
    	return this;
    }

    private void buildConstraints(GridBagConstraints gbc,int x, int y, int w, int h, int wx, int wy, int fill, int anchor) {
    	gbc.gridx = x; // start cell in a row
    	gbc.gridy = y; // start cell in a column
    	gbc.gridwidth = w; // how many column does the control occupy in the row
    	gbc.gridheight = h; // how many column does the control occupy in the column
    	gbc.weightx = wx; // relative horizontal size
    	gbc.weighty = wy; // relative vertical size
    	gbc.fill = fill; // the way how the control fills cells
    	gbc.anchor = anchor; // alignment
    }

    public boolean requestFocusInWindow() {
        return this.requestFocusInWindow();
    }
}
