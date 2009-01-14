
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import gov.loc.repository.bagger.bag.BaggerBag;

public class ConsolePane extends JPanel {
	private static final long serialVersionUID = -4290352509246639528L;

	public static final String CONSOLE_PANE = "consolePane";
	private Dimension maxDimension = new Dimension(400, 400);
	private Dimension preferredDimension = new Dimension(400, 200);
    private GridBagLayout layout = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();
    private String messages = new String();
    private Color textBackground = new Color(240, 240, 240);

    private BagView parentView;
    private BaggerBag baggerBag;

    public ConsolePane(BagView bagView) {
        super();
        this.setLayout(layout);
        this.parentView = bagView;
        this.baggerBag = bagView.getBag();
        createFormControl();
    }
    
    public ConsolePane(BagView bagView, String messages) {
        super();
        this.setLayout(layout);
        this.parentView = bagView;
        this.baggerBag = bagView.getBag();
        this.messages = messages;
        createFormControl();
    }

    public void setBag(BaggerBag baggerBag) {
    	this.baggerBag = baggerBag;
    }
    
    public BaggerBag getBag() {
    	return this.baggerBag;
    }
    
    public void setMessages(String messages) {
    	this.messages = messages;
    }
    
    public String getMessages() {
    	return this.messages;
    }

    protected JComponent createFormControl() {
    	this.setMaximumSize(maxDimension);
    	Dimension formDimension = new Dimension(150, 25);
    	Dimension consoleDimension = new Dimension(300, 200);
        Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
/* */
    	JLabel completeLabel = new JLabel(parentView.getPropertyMessage("compositePane.message.isComplete"));
    	Font font = completeLabel.getFont().deriveFont(Font.BOLD);
    	completeLabel.setFont(font);
    	completeLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, 0, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(completeLabel, gbc);
        this.add(completeLabel);

    	JLabel completeResult = new JLabel("no");
    	font = completeResult.getFont().deriveFont(Font.BOLD);
    	completeResult.setFont(font);
    	if (baggerBag != null) {
    		if (baggerBag.getIsComplete()) completeResult.setText("yes");
    		else completeResult.setText("no");
    	}
    	completeResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, 0, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(completeResult, gbc);
        this.add(completeResult);

    	JLabel validLabel = new JLabel(parentView.getPropertyMessage("compositePane.message.isValid"));
    	font = validLabel.getFont().deriveFont(Font.BOLD);
    	validLabel.setFont(font);
    	validLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, 1, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validLabel, gbc);
        this.add(validLabel);

    	JLabel validResult = new JLabel("no");
    	font = validResult.getFont().deriveFont(Font.BOLD);
    	validResult.setFont(font);
    	if (baggerBag != null) {
    		if (baggerBag.getIsValid()) validResult.setText("yes");
    		else validResult.setText("no");
    	}
    	validResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, 1, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validResult, gbc);
        this.add(validResult);

    	JLabel validMetaLabel = new JLabel(parentView.getPropertyMessage("compositePane.message.isMetadata"));
    	font = validMetaLabel.getFont().deriveFont(Font.BOLD);
    	validMetaLabel.setFont(font);
    	validMetaLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, 2, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validMetaLabel, gbc);
        this.add(validMetaLabel);

    	JLabel validMetaResult = new JLabel("no");
    	font = validMetaResult.getFont().deriveFont(Font.BOLD);
    	validMetaResult.setFont(font);
    	if (baggerBag != null) {
    		if (baggerBag.getIsValidMetadata()) validMetaResult.setText("yes");
    		else validMetaResult.setText("no");
    	}
    	validMetaResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validMetaResult, gbc);
        this.add(validMetaResult);

        if (baggerBag == null || (baggerBag != null && baggerBag.getIsSerial())) {
        	JLabel serializedLabel = new JLabel(parentView.getPropertyMessage("compositePane.message.isPackaged"));
        	font = serializedLabel.getFont().deriveFont(Font.BOLD);
        	serializedLabel.setFont(font);
        	serializedLabel.setPreferredSize(formDimension);
            buildConstraints(gbc, 0, 3, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
            layout.setConstraints(serializedLabel, gbc);
            this.add(serializedLabel);

        	JLabel serializedResult = new JLabel("no");
        	font = serializedResult.getFont().deriveFont(Font.BOLD);
        	serializedResult.setFont(font);
        	if (baggerBag != null) {
        		if (baggerBag.getIsSerialized()) serializedResult.setText("yes");
        		else serializedResult.setText("no");
        	}
        	serializedResult.setPreferredSize(formDimension);
            buildConstraints(gbc, 1, 3, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
            layout.setConstraints(serializedResult, gbc);
            this.add(serializedResult);        	
        }
    	JTextArea serializedArea = new JTextArea(this.messages);
    	if (baggerBag != null) serializedArea.append("");
    	serializedArea.setEditable(false);
    	serializedArea.setLineWrap(true);
    	serializedArea.setRows(20);
    	serializedArea.setColumns(10);
    	serializedArea.setBackground(textBackground);
    	serializedArea.setWrapStyleWord(true);
    	serializedArea.setAutoscrolls(true);
    	serializedArea.setPreferredSize(consoleDimension);
    	serializedArea.setBorder(BorderFactory.createLineBorder(Color.black));
        buildConstraints(gbc, 0, 4, 2, 4, 10, 10, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        JScrollPane serializedPane = new JScrollPane(serializedArea);
        layout.setConstraints(serializedPane, gbc);
        this.add(serializedPane);

        this.setBorder(emptyBorder);
        this.setPreferredSize(preferredDimension);
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
