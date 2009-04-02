
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagger.bag.impl.DefaultBag;

public class ConsolePane extends JPanel {
	private static final long serialVersionUID = -4290352509246639528L;
	private static final Log log = LogFactory.getLog(ConsolePane.class);

	public static final String CONSOLE_PANE = "consolePane";
	private Dimension maxDimension = new Dimension(400, 400);
	private Dimension consoleDimension = new Dimension(300, 300);
	private Dimension preferredDimension = new Dimension(400, 380);
    private GridBagLayout layout = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();
    private String messages = new String();
    private Color textBackground = new Color(240, 240, 240);
	private Dimension formDimension = new Dimension(150, 25);
    private Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
    private Font font;

    private BagView parentView;
    private DefaultBag defaultBag;

    public ConsolePane(BagView bagView, String messages) {
        super();
        this.setLayout(layout);
        this.parentView = bagView;
        this.defaultBag = bagView.getBag();
        this.messages = messages;
        createFormControl();
    }

    public void setBag(DefaultBag bag) {
    	this.defaultBag = bag;
    }
    
    public DefaultBag getBag() {
    	return this.defaultBag;
    }
    
    public void setMessages(String messages) {
    	this.messages = messages;
    }
    
    public String getMessages() {
    	return this.messages;
    }

    protected JComponent createFormControl() {
    	this.setMaximumSize(maxDimension);
    	int row = 0;
    	createValidMetaDataLine(row++);
        createCompleteLine(row++);
        createBaggedLine(row++);
        createValidLine(row++);
        createConsoleArea();

        this.setBorder(emptyBorder);
        this.setPreferredSize(preferredDimension);
/* */    	
    	return this;
    }
    
    private void createValidMetaDataLine(int row) {
    	JLabel validMetaLabel = new JLabel(parentView.getPropertyMessage("compositePane.message.isMetadata"));
    	validMetaLabel.setToolTipText(parentView.getPropertyMessage("consolepane.ismetadata.help"));
    	font = validMetaLabel.getFont().deriveFont(Font.BOLD);
    	validMetaLabel.setFont(font);
    	validMetaLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validMetaLabel, gbc);
        this.add(validMetaLabel);
    	JLabel validMetaResult = new JLabel("no");
    	validMetaResult.setToolTipText(parentView.getPropertyMessage("consolepane.ismetadata.help"));
    	font = validMetaResult.getFont().deriveFont(Font.BOLD);
    	validMetaResult.setFont(font);
    	if (defaultBag != null) {
    		if (defaultBag.isValidMetadata()) validMetaResult.setText("yes");
    		else validMetaResult.setText("no");
    	}
    	validMetaResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validMetaResult, gbc);
        this.add(validMetaResult);    	
    }
    
    private void createCompleteLine(int row) {
        JLabel completeLabel = new JLabel(parentView.getPropertyMessage("compositePane.message.isComplete"));
        completeLabel.setToolTipText(parentView.getPropertyMessage("consolepane.iscomplete.help"));
    	font = completeLabel.getFont().deriveFont(Font.BOLD);
    	completeLabel.setFont(font);
    	completeLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(completeLabel, gbc);
        this.add(completeLabel);
    	JLabel completeResult = new JLabel("no");
    	completeResult.setToolTipText(parentView.getPropertyMessage("consolepane.iscomplete.help"));
    	font = completeResult.getFont().deriveFont(Font.BOLD);
    	completeResult.setFont(font);
    	if (defaultBag != null) {
    		if (defaultBag.isComplete()) completeResult.setText("yes");
    		else completeResult.setText("no");
    	}
    	completeResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(completeResult, gbc);
        this.add(completeResult);
    }
    
    private void createBaggedLine(int row) {
    	JLabel serializedLabel = new JLabel(parentView.getPropertyMessage("compositePane.message.isBagged"));
    	serializedLabel.setToolTipText(parentView.getPropertyMessage("consolepane.isbagged.help"));
    	font = serializedLabel.getFont().deriveFont(Font.BOLD);
    	serializedLabel.setFont(font);
    	serializedLabel.setPreferredSize(formDimension);
    	buildConstraints(gbc, 0, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	layout.setConstraints(serializedLabel, gbc);
    	this.add(serializedLabel);
    	JLabel serializedResult = new JLabel("no");
    	serializedResult.setToolTipText(parentView.getPropertyMessage("consolepane.isbagged.help"));
    	font = serializedResult.getFont().deriveFont(Font.BOLD);
    	serializedResult.setFont(font);
    	if (defaultBag != null) {
    		if (defaultBag.isSerialized()) serializedResult.setText("yes");
    		else serializedResult.setText("no");
    	}
    	serializedResult.setPreferredSize(formDimension);
    	buildConstraints(gbc, 1, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	layout.setConstraints(serializedResult, gbc);
    	this.add(serializedResult);
    }
    
    private void createValidLine(int row) {
    	JLabel validLabel = new JLabel(parentView.getPropertyMessage("compositePane.message.isValid"));
    	validLabel.setToolTipText(parentView.getPropertyMessage("consolepane.isvalid.help"));
    	font = validLabel.getFont().deriveFont(Font.BOLD);
    	validLabel.setFont(font);
    	validLabel.setPreferredSize(formDimension);
        buildConstraints(gbc, 0, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validLabel, gbc);
        this.add(validLabel);
    	JLabel validResult = new JLabel("no");
    	validResult.setToolTipText(parentView.getPropertyMessage("consolepane.isvalid.help"));
    	font = validResult.getFont().deriveFont(Font.BOLD);
    	validResult.setFont(font);
    	if (defaultBag != null) {
    		if (defaultBag.isValid()) validResult.setText("yes");
    		else validResult.setText("no");
    	}
    	validResult.setPreferredSize(formDimension);
        buildConstraints(gbc, 1, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(validResult, gbc);
        this.add(validResult);
    }
    
    private void createConsoleArea() {
    	String text = "";
    	if (this.messages != null) text = this.messages;
    	//for (int i=0; i<1500; i++) { text += "" + i%10 + ""; }
    	int consoleWidth = 600;
        int consoleHeight = 400;
        int textRows = 20;
        int textCols = 60;

        JTextArea serializedArea = new JTextArea(text);
        serializedArea.setToolTipText(parentView.getPropertyMessage("consolepane.msg.help"));
        Font textFont = serializedArea.getFont();
        FontMetrics fm = serializedArea.getFontMetrics(textFont);
        int fontHeight = fm.getHeight();
        int fontWidth = fm.charWidth('M');
        textCols = consoleWidth / fontWidth;
        textRows = getRowCount(text, textCols);
        if (fontHeight > 0 && textRows > 0) consoleHeight = fontHeight * textRows;

    	serializedArea.setEditable(false);
    	serializedArea.setLineWrap(true);
    	//serializedArea.setRows(textRows);
    	//serializedArea.setColumns(textCols);
    	serializedArea.setBackground(textBackground);
    	serializedArea.setWrapStyleWord(true);
    	serializedArea.setAutoscrolls(true);
        consoleDimension = new Dimension(consoleWidth, consoleHeight);
    	serializedArea.setPreferredSize(consoleDimension);
    	serializedArea.setBorder(BorderFactory.createLineBorder(Color.black));
        buildConstraints(gbc, 0, 4, 2, 4, 10, 10, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        JScrollPane serializedPane = new JScrollPane(serializedArea);
        layout.setConstraints(serializedPane, gbc);
        this.add(serializedPane);
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

    private int getRowCount(String text, int rowWidth) {
    	int rows = 0;
    	try {
        	java.io.StringReader reader = new java.io.StringReader(text);
        	java.io.LineNumberReader ln = new java.io.LineNumberReader(reader);
        	String line = ln.readLine();
            while (line != null) {
            	if (line.length() > rowWidth) {
            		int length = line.length() / rowWidth;
            		rows += length;
            	} else {
                	rows++;
            	}
            	line = ln.readLine();
            }
        	if (line != null && line.length() > rowWidth) {
        		int length = line.length() / rowWidth;
        		rows += length;
        	}
    	} catch (Exception e) {
    		log.error("ConsolePane.getRowCount: " + e.getMessage());
    	}
    	
    	return rows;
    }
}
