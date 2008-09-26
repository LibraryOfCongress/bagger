
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
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import gov.loc.repository.bagger.bag.Bag;

public class ConsolePane extends JPanel {
	private static final long serialVersionUID = -4290352509246639528L;

	public static final String CONSOLE_PANE = "consolePane";
	private Dimension dimension = new Dimension(300, 300);
	//private GridLayout layout = new GridLayout(0, 2, 10, 10);
    private GridBagLayout layout = new GridBagLayout();
    private GridBagConstraints gbc = new GridBagConstraints();

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
    
    public void setBag(Bag bag) {
    	this.bag = bag;
    }
    
    public Bag getBag() {
    	return this.bag;
    }

    protected JComponent createFormControl() {
    	this.setMaximumSize(dimension);
    	Dimension formDimension = new Dimension(150, 50);
        Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
/* */
    	JLabel completeLabel = new JLabel("Is Complete?: ");
    	Font font = completeLabel.getFont().deriveFont(Font.BOLD);
    	completeLabel.setFont(font);
    	completeLabel.setPreferredSize(formDimension);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        layout.setConstraints(completeLabel, gbc);
        this.add(completeLabel);

    	JLabel completeResult = new JLabel("");
    	if (bag != null) {
    		completeResult.setText(""+bag.getIsComplete());
    		if (bag.getIsComplete()) completeResult.setForeground(Color.green);
    		else completeResult.setForeground(Color.red);
    	}
    	font = completeResult.getFont().deriveFont(Font.BOLD);
    	completeResult.setFont(font);
    	completeResult.setPreferredSize(formDimension);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        layout.setConstraints(completeResult, gbc);
        this.add(completeResult);

    	JLabel validLabel = new JLabel("Is Valid?: ");
    	font = validLabel.getFont().deriveFont(Font.BOLD);
    	validLabel.setFont(font);
    	validLabel.setPreferredSize(formDimension);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        layout.setConstraints(validLabel, gbc);
        this.add(validLabel);

    	JLabel validResult = new JLabel("");
    	if (bag != null) {
    		validResult.setText(""+bag.getIsValid());
    		if (bag.getIsValid()) validResult.setForeground(Color.green);
    		else validResult.setForeground(Color.red);
    	}
    	font = validResult.getFont().deriveFont(Font.BOLD);
    	validResult.setFont(font);
    	validResult.setPreferredSize(formDimension);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        layout.setConstraints(validResult, gbc);
        this.add(validResult);

    	JLabel serializedLabel = new JLabel("Is Packaged?: ");
    	font = serializedLabel.getFont().deriveFont(Font.BOLD);
    	serializedLabel.setFont(font);
    	serializedLabel.setPreferredSize(formDimension);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        layout.setConstraints(serializedLabel, gbc);
        this.add(serializedLabel);

    	JLabel serializedResult = new JLabel("");
    	if (bag != null) {
    		serializedResult.setText(""+bag.getIsSerialized());
    		if (bag.getIsSerialized()) serializedResult.setForeground(Color.green);
    		else serializedResult.setForeground(Color.red);
    	}
    	font = serializedResult.getFont().deriveFont(Font.BOLD);
    	serializedResult.setFont(font);
    	serializedResult.setPreferredSize(formDimension);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        layout.setConstraints(serializedResult, gbc);
        this.add(serializedResult);

    	JTextArea serializedArea = new JTextArea("");
    	if (bag != null) serializedArea.append("");
    	serializedArea.setEditable(false);
    	serializedArea.setLineWrap(false);
    	serializedArea.setRows(20);
    	serializedArea.setColumns(10);
    	serializedArea.setBackground(Color.white);
    	serializedArea.setPreferredSize(new Dimension(500, 500));
    	serializedArea.setBorder(BorderFactory.createLineBorder(Color.black));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.gridheight = 4;
        gbc.weightx = 10;
        gbc.weighty = 10;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(serializedArea, gbc);
        this.add(serializedArea);

        this.setBorder(emptyBorder);
/* */

/* 
    	add(completeLabel);
    	add(completeResult);
    	add(validLabel);
    	add(validResult);
    	add(serializedLabel);
    	add(serializedResult);
    	add(serializedArea);
 */
    	
    	return this;
    }

    public boolean requestFocusInWindow() {
        return this.requestFocusInWindow();
    }
}
