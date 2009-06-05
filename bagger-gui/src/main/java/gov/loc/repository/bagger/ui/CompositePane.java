
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.Color;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import gov.loc.repository.bagger.bag.impl.DefaultBag;

public class CompositePane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	public static final String COMPOSITE_PANE = "compositePane";
    private String messages = new String();
    private BagView parentView;
    private DefaultBag bag;
    private JScrollPane consoleScrollPane;
    private ConsolePane consolePane;
	private Dimension preferredDimension = new Dimension(400, 380);
	private Color selectedColor = Color.lightGray; //new Color(180, 180, 200);
	private Color unselectedColor = Color.black; //new Color(180, 180, 160);

    public CompositePane(BagView bagView, String message) {
        super();
        this.parentView = bagView;
        this.bag = bagView.getBag();
        populateBagPane(message);
    }

    private void init() {
    	this.setPreferredSize(preferredDimension);
        ChangeListener changeListener = new ChangeListener() {
        	public void stateChanged(ChangeEvent changeEvent) {
        		JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int count = sourceTabbedPane.getTabCount();
                int selected = sourceTabbedPane.getSelectedIndex();
                for (int i = 0; i < count; ++i) {
                    Color c = (i == selected) ? unselectedColor : selectedColor;
                    sourceTabbedPane.setBackgroundAt(i, c);
                    sourceTabbedPane.setForegroundAt(i, c);
                }
        	}
        };
        this.addChangeListener(changeListener);
    }

    public void setBag(DefaultBag bag) {
    	this.bag = bag;
    }

    public DefaultBag getBag() {
    	return this.bag;
    }

    public void setMessages(String messages) {
    	this.messages = messages;
    }

    public String getMessages() {
    	return this.messages;
    }

    public void populateBagPane(String messages) {
    	if (messages == null) messages = "";
    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane(parentView, messages);
    	consoleScrollPane.setViewportView(consolePane);
    	this.addTab(parentView.getPropertyMessage("compositePane.tab.console"), consoleScrollPane);
    	consoleScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.console.help"));
    	consoleScrollPane.setForeground(unselectedColor);

        init();
    }

    // setBag must be called before updateTabs is called
    public void updateMessages(String messages) {
    	setBag(bag);
    	if (this.getComponentCount() > 0) {
    		this.removeAll();
            this.invalidate();
            consolePane.invalidate();
            consoleScrollPane.invalidate();
    	}
        /* */
    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane(parentView, messages);
    	consoleScrollPane.setViewportView(consolePane);
    	this.addTab(parentView.getPropertyMessage("compositePane.tab.console"), consoleScrollPane);
    	consoleScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.console.help"));
    	consoleScrollPane.setForeground(unselectedColor);
    	/* */
        consolePane.invalidate();
        consoleScrollPane.invalidate();
        this.invalidate();
    }
    
    // setBag must be called before updateTabs is called
    public void updateCompositePaneTabs(DefaultBag bag, String messages) {
        setBag(bag);
    	if (bag.isSerialized()) {
            messages += "\n";
            messages += parentView.getPropertyMessage("compositePane.message.files.total") + " " + bag.getDataNumber();
            messages += "\n";
            long fsize = bag.getDataSize();
            if (fsize > DefaultBag.GB) {
            	fsize /= DefaultBag.GB;
                messages += parentView.getPropertyMessage("compositePane.message.files.size") + " " + parentView.getPropertyMessage("compositePane.message.files.gb") + " " + fsize;
            } else if (fsize > DefaultBag.MB) {
            	fsize /= DefaultBag.MB;
                messages += parentView.getPropertyMessage("compositePane.message.files.size") + " " + parentView.getPropertyMessage("compositePane.message.files.mb") + " " + fsize;
            } else if (fsize > DefaultBag.KB) {
            	fsize /= DefaultBag.KB;
                messages += parentView.getPropertyMessage("compositePane.message.files.size") + " " + parentView.getPropertyMessage("compositePane.message.files.kb") + " " + fsize;
            } else {
                messages += parentView.getPropertyMessage("compositePane.message.files.size") + " " + parentView.getPropertyMessage("compositePane.message.files.tiny");
            }
    	}
    	if (this.getComponentCount() > 0) {
    		this.removeAll();
            this.invalidate();
            consolePane.invalidate();
            consoleScrollPane.invalidate();
    	}
        populateBagPane(messages);
        consolePane.invalidate();
        consoleScrollPane.invalidate();
        this.invalidate();
    }
}
