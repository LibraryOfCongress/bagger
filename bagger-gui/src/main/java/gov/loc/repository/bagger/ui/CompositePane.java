
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;

import gov.loc.repository.bagger.Address;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.Bag;
import gov.loc.repository.bagger.bag.BagInfo;
import gov.loc.repository.bagger.bag.BagIt;
import gov.loc.repository.bagger.bag.BagOrganization;
import gov.loc.repository.bagger.bag.Data;
import gov.loc.repository.bagger.bag.Fetch;
import gov.loc.repository.bagger.bag.Manifest;
import gov.loc.repository.bagger.bag.ManifestType;
import gov.loc.repository.bagger.bag.TagManifest;

public class CompositePane extends JTabbedPane {

	public static final String COMPOSITE_PANE = "compositePane";
    private String messages = new String();
    private Bag bag;
    private JTree bagsTree;
//    private List<File> rootTree;
//    private File rootSrc = null;
    private JScrollPane consoleScrollPane;
    private ConsolePane consolePane;
    private BagItPane bagItPane;
    private JScrollPane bagItScrollPane;
    private BagInfoPane bagInfoPane;
    private JScrollPane bagInfoScrollPane;
    private DataPane dataPane;
    private JScrollPane dataScrollPane;
    private FetchPane fetchPane;
    private JScrollPane fetchScrollPane;
    private ManifestPane manifestPane;
    private ManifestPane tagManifestPane;
    private JScrollPane manifestScrollPane;
    private JScrollPane tagManifestScrollPane;

    public CompositePane(Bag bag) {
        super();
        this.bag = bag;
        createBagPane();
    }

    public CompositePane(Bag bag, List<File> rootTree, File rootSrc, String messages) {
        super();
        this.bag = bag;
//        this.rootTree = rootTree;
//        this.rootSrc = rootSrc;
        this.messages = messages;
        populateBagPane(messages);
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

    public void createBagPane() {
//    	createBag();
//    	initializeBag();
    	if (bagsTree == null) System.out.println("createBagPane: NULL");

    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane();
    	consoleScrollPane.setViewportView(consolePane);
    	this.addTab("Console", consoleScrollPane);
    	
    	manifestScrollPane = new JScrollPane();
    	manifestPane = new ManifestPane();
    	manifestScrollPane.setViewportView(manifestPane);
    	this.addTab("Manifest", manifestScrollPane);

    	tagManifestScrollPane = new JScrollPane();
    	tagManifestPane = new ManifestPane();
    	tagManifestScrollPane.setViewportView(tagManifestPane);
    	this.addTab("TagManifest", tagManifestScrollPane);

    	bagInfoScrollPane = new JScrollPane();
        bagInfoPane = new BagInfoPane();
        bagInfoScrollPane.setViewportView(bagInfoPane);
        this.addTab("Bag It Info", bagInfoScrollPane);

        dataScrollPane = new JScrollPane();
    	dataPane = new DataPane();
    	dataScrollPane.setViewportView(dataPane);
    	this.addTab("Data", dataScrollPane);
    	
    	fetchScrollPane = new JScrollPane();
        fetchPane = new FetchPane();
    	fetchScrollPane.setViewportView(fetchPane);
    	if (this.bag.getIsHoley()) {
            this.addTab("Fetch", fetchScrollPane);    		
    	}

        bagItScrollPane = new JScrollPane();
        bagItPane = new BagItPane();
        bagItScrollPane.setViewportView(bagItPane);
        this.addTab("Bag It", bagItScrollPane);
    }
    
    public void populateBagPane(String messages) {
//    	createBag();

    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane(bag, messages);
    	consoleScrollPane.setViewportView(consolePane);
    	this.addTab("Console", consoleScrollPane);

    	String mcontent = new String();
    	if (bag.getManifests() != null && bag.getManifests().size() > 0) {
        	List<Manifest> manifests = bag.getManifests();
    		StringBuffer sb = new StringBuffer();
        	for (int i=0; i < manifests.size(); i++) {
        		sb.append(manifests.get(i).getName());
        		sb.append('\n');
        		sb.append(manifests.get(i).toString());
        		sb.append('\n');
        	}
        	mcontent = sb.toString();
    	}
    	manifestScrollPane = new JScrollPane();
    	manifestPane = new ManifestPane(mcontent);
    	manifestScrollPane.setViewportView(manifestPane);
    	this.addTab("Manifest", manifestScrollPane);

    	String tmcontent = new String();
    	if (bag.getTagManifests() != null && bag.getTagManifests().size() > 0) {
        	List<TagManifest> tagManifests = bag.getTagManifests();
    		StringBuffer sb = new StringBuffer();
        	for (int i=0; i < tagManifests.size(); i++) {
        		sb.append(tagManifests.get(i).getName());
        		sb.append('\n');
        		sb.append(tagManifests.get(i).toString());
        		sb.append('\n');
        	}
        	tmcontent = sb.toString();
    	}
    	tagManifestScrollPane = new JScrollPane();
    	tagManifestPane = new ManifestPane(tmcontent);
    	tagManifestScrollPane.setViewportView(tagManifestPane);
    	this.addTab("TagManifest", tagManifestScrollPane);

    	bagInfoScrollPane = new JScrollPane();
        bagInfoPane = new BagInfoPane(bag.getInfo());
        bagInfoScrollPane.setViewportView(bagInfoPane);
        this.addTab("Bag It Info", bagInfoScrollPane);

        dataScrollPane = new JScrollPane();
    	dataPane = new DataPane(bag.getData());
    	dataScrollPane.setViewportView(dataPane);
    	this.addTab("Data", dataScrollPane);

    	fetchScrollPane = new JScrollPane();
        fetchPane = new FetchPane(bag.getFetch());
    	fetchScrollPane.setViewportView(fetchPane);
    	if (this.bag.getIsHoley()) {
            this.addTab("Fetch", fetchScrollPane);    		
    	}

        bagItScrollPane = new JScrollPane();
        bagItPane = new BagItPane(bag.getBagIt());
        bagItScrollPane.setViewportView(bagItPane);
        this.addTab("Bag It", bagItScrollPane);
    }

    // setBag must be called before updateTabs is called
    public void updateMessages(Bag bag, String messages) {
    	setBag(bag);
    	if (this.getComponentCount() > 0) {
    		this.removeAll();
            this.invalidate();
            consolePane.invalidate();
            consoleScrollPane.invalidate();
    	}
        /* */
    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane(bag, messages);
    	consoleScrollPane.setViewportView(consolePane);
    	this.addTab("Console", consoleScrollPane);
    	manifestScrollPane.setViewportView(manifestPane);
    	this.addTab("Manifest", manifestScrollPane);
    	this.addTab("TagManifest", tagManifestScrollPane);
        this.addTab("Bag It Info", bagInfoScrollPane);
    	this.addTab("Data", dataScrollPane);
    	if (this.bag.getIsHoley()) {
            this.addTab("Fetch", fetchScrollPane);    		
    	}
        this.addTab("Bag It", bagItScrollPane);
    	/* */
        consolePane.validate();
        consolePane.repaint();
        consoleScrollPane.validate();
        consoleScrollPane.repaint();
        this.validate();
        this.repaint();
    }
    
    // setBag must be called before updateTabs is called
    public void updateTabs(Bag bag, String messages) {
    	setBag(bag);
    	if (this.getComponentCount() > 0) {
    		this.removeAll();
            this.invalidate();
            consolePane.invalidate();
            consoleScrollPane.invalidate();
    	}
        populateBagPane(messages);
        consolePane.validate();
        consolePane.repaint();
        consoleScrollPane.validate();
        consoleScrollPane.repaint();
        this.validate();
        this.repaint();
    }

    public boolean requestFocusInWindow() {
        return this.requestFocusInWindow();
    }
}
