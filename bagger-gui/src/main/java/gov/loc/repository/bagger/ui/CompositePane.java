
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
    private BagTextPane bagItPane;
    private JScrollPane bagItScrollPane;
    private BagTextPane bagInfoPane;
    private JScrollPane bagInfoScrollPane;
    private BagTextPane dataPane;
    private JScrollPane dataScrollPane;
    private BagTextPane fetchPane;
    private JScrollPane fetchScrollPane;
    private BagTextPane manifestPane;
    private JScrollPane manifestScrollPane;
    private BagTextPane tagManifestPane;
    private JScrollPane tagManifestScrollPane;
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

    	String mcontent = bag.getManifestContent();
    	manifestScrollPane = new JScrollPane();
    	manifestPane = new BagTextPane(mcontent);
    	manifestScrollPane.setViewportView(manifestPane);
    	this.addTab(parentView.getPropertyMessage("compositePane.tab.manifest"), manifestScrollPane);
    	manifestScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.manifest.help"));
    	manifestScrollPane.setForeground(selectedColor);

    	String tmcontent = bag.getTagManifestContent();
    	tagManifestScrollPane = new JScrollPane();
    	tagManifestPane = new BagTextPane(tmcontent);
    	tagManifestScrollPane.setViewportView(tagManifestPane);
    	this.addTab(parentView.getPropertyMessage("compositePane.tab.tagmanifest"), tagManifestScrollPane);
    	tagManifestScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.tagmanifest.help"));
    	tagManifestScrollPane.setForeground(selectedColor);

        bagInfoScrollPane = new JScrollPane();
    	String bagInfoContent = bag.getBagInfoContent();
        bagInfoPane = new BagTextPane(bagInfoContent);
        bagInfoScrollPane.setViewportView(bagInfoPane);
        this.addTab(parentView.getPropertyMessage("compositePane.tab.baginfo"), bagInfoScrollPane);
        bagInfoScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.baginfo.help"));
        bagInfoScrollPane.setForeground(selectedColor);

        bagItScrollPane = new JScrollPane();
        String bagItContent = bag.getBagItContent();
        bagItPane = new BagTextPane(bagItContent);
        bagItScrollPane.setViewportView(bagItPane);
        this.addTab(parentView.getPropertyMessage("compositePane.tab.bagit"), bagItScrollPane);
        bagItScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.bagit.help"));
        bagItScrollPane.setForeground(selectedColor);

    	fetchScrollPane = new JScrollPane();
    	String fetchContent = bag.getFetchContent();
        fetchPane = new BagTextPane(fetchContent);
    	fetchScrollPane.setViewportView(fetchPane);
    	if (this.bag.getIsHoley()) {
            this.addTab(parentView.getPropertyMessage("compositePane.tab.fetch"), fetchScrollPane);    		
    	}
    	fetchScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.fetch.help"));
    	fetchScrollPane.setForeground(selectedColor);

    	dataScrollPane = new JScrollPane();
    	String dataContent = bag.getDataContent();
    	dataPane = new BagTextPane(dataContent);
    	dataScrollPane.setViewportView(dataPane);
    	if (!this.bag.getIsHoley()) {
        	this.addTab(parentView.getPropertyMessage("compositePane.tab.data"), dataScrollPane);
    	}
    	dataScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.data.help"));
    	dataScrollPane.setForeground(selectedColor);

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
    	manifestScrollPane.setViewportView(manifestPane);
    	this.addTab(parentView.getPropertyMessage("compositePane.tab.manifest"), manifestScrollPane);
    	manifestScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.manifest.help"));
    	manifestScrollPane.setForeground(selectedColor);
    	this.addTab(parentView.getPropertyMessage("compositePane.tab.tagmanifest"), tagManifestScrollPane);
    	tagManifestScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.tagmanifest.help"));
    	tagManifestScrollPane.setForeground(selectedColor);
        this.addTab(parentView.getPropertyMessage("compositePane.tab.baginfo"), bagInfoScrollPane);
    	bagInfoScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.baginfo.help"));
        bagInfoScrollPane.setForeground(selectedColor);
        this.addTab(parentView.getPropertyMessage("compositePane.tab.bagit"), bagItScrollPane);
    	bagItScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.bagit.help"));
        bagItScrollPane.setForeground(selectedColor);
    	if (this.bag.getIsHoley()) {
            this.addTab(parentView.getPropertyMessage("compositePane.tab.fetch"), fetchScrollPane);    		
            fetchScrollPane.setForeground(selectedColor);
        	fetchScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.fetch.help"));
    	}
    	if (!this.bag.getIsHoley()) {
        	this.addTab(parentView.getPropertyMessage("compositePane.tab.data"), dataScrollPane);
        	dataScrollPane.setForeground(selectedColor);
        	dataScrollPane.setToolTipText(parentView.getPropertyMessage("compositePane.tab.data.help"));
    	}
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
