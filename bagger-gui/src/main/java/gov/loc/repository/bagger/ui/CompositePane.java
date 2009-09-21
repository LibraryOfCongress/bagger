
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.Color;

import javax.swing.JScrollPane;

import gov.loc.repository.bagger.bag.impl.DefaultBag;

public class CompositePane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	public static final String COMPOSITE_PANE = "compositePane";
    private BagView bagView;
    private DefaultBag bag;
    private ConsolePane consolePane = null;
	private Dimension preferredDimension = new Dimension(600, 380);
	private Color unselectedColor = Color.black; //new Color(180, 180, 160);

    public CompositePane(BagView bagView, String message) {
        super();
        this.bagView = bagView;
        this.bag = bagView.getBag();
        populateBagPane(bag, message);
    }

    public void setBag(DefaultBag bag) {
    	this.bag = bag;
    }

    public DefaultBag getBag() {
    	return this.bag;
    }

    public void populateBagPane(DefaultBag bag, String messages) {
    	if (messages == null) messages = "";
		consolePane = new ConsolePane(bagView, bag, messages);
    	this.setViewportView(consolePane);
    	this.setToolTipText(bagView.getPropertyMessage("compositePane.tab.console.help"));
    	this.setForeground(unselectedColor);
    	this.setPreferredSize(preferredDimension);
        consolePane.invalidate();
    }

    public void updateCompositePaneTabs(DefaultBag bag, String messages) {
        setBag(bag);
    	if (messages == null) messages = "";
    	populateBagPane(bag, messages);
        this.invalidate();
    }
    
    private String getSizeMsg() {
    	String messages = "";
        if (bag.isSerialized()) {
            messages += "\n";
            messages += bagView.getPropertyMessage("compositePane.message.files.total") + " " + bag.getDataNumber();
            messages += "\n";
            long fsize = bag.getDataSize();
            if (fsize > DefaultBag.GB) {
            	fsize /= DefaultBag.GB;
                messages += bagView.getPropertyMessage("compositePane.message.files.size") + " " + bagView.getPropertyMessage("compositePane.message.files.gb") + " " + fsize;
            } else if (fsize > DefaultBag.MB) {
            	fsize /= DefaultBag.MB;
                messages += bagView.getPropertyMessage("compositePane.message.files.size") + " " + bagView.getPropertyMessage("compositePane.message.files.mb") + " " + fsize;
            } else if (fsize > DefaultBag.KB) {
            	fsize /= DefaultBag.KB;
                messages += bagView.getPropertyMessage("compositePane.message.files.size") + " " + bagView.getPropertyMessage("compositePane.message.files.kb") + " " + fsize;
            } else {
                messages += bagView.getPropertyMessage("compositePane.message.files.size") + " " + bagView.getPropertyMessage("compositePane.message.files.tiny");
            }
    	}
        return messages;
    }
}
