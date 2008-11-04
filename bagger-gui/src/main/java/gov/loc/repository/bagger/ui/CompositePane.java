
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.Color;
import java.io.File;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
//import javax.swing.JTree;
import javax.swing.UIManager;

import gov.loc.repository.bagger.bag.BaggerBag;
import gov.loc.repository.bagger.bag.BaggerTagManifest;
import gov.loc.repository.bagger.bag.BaggerManifest;

public class CompositePane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	public static final String COMPOSITE_PANE = "compositePane";
    private String messages = new String();
    private BaggerBag baggerBag;
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
	private Dimension preferredDimension = new Dimension(400, 400);
	private Color selectedColor = new Color(180, 180, 200);
	private Color unselectedColor = Color.black; //new Color(180, 180, 160);

    public CompositePane(BaggerBag baggerBag) {
        super();
        this.baggerBag = baggerBag;
        init();
        createBagPane();
    }

    public CompositePane(BaggerBag baggerBag, List<File> rootTree, File rootSrc, String messages) {
        super();
        init();
        this.baggerBag = baggerBag;
        this.messages = messages;
        populateBagPane(messages);
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

    public void createBagPane() {
    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane();
    	consoleScrollPane.setViewportView(consolePane);
    	this.addTab("Console", consoleScrollPane);
    	consolePane.setForeground(unselectedColor);
    	consoleScrollPane.setForeground(unselectedColor);
    	
    	manifestScrollPane = new JScrollPane();
    	manifestPane = new ManifestPane();
    	manifestScrollPane.setViewportView(manifestPane);
    	this.addTab("Manifest", manifestScrollPane);
    	manifestPane.setForeground(selectedColor);
    	manifestScrollPane.setForeground(selectedColor);

    	tagManifestScrollPane = new JScrollPane();
    	tagManifestPane = new ManifestPane();
    	tagManifestScrollPane.setViewportView(tagManifestPane);
    	this.addTab("TagManifest", tagManifestScrollPane);
    	tagManifestPane.setForeground(selectedColor);
    	tagManifestScrollPane.setForeground(selectedColor);

    	bagInfoScrollPane = new JScrollPane();
        bagInfoPane = new BagInfoPane();
        bagInfoScrollPane.setViewportView(bagInfoPane);
        this.addTab("Bag It Info", bagInfoScrollPane);
        bagInfoPane.setForeground(selectedColor);
        bagInfoScrollPane.setForeground(selectedColor);

        dataScrollPane = new JScrollPane();
    	dataPane = new DataPane();
    	dataScrollPane.setViewportView(dataPane);
    	this.addTab("Data", dataScrollPane);
    	dataPane.setForeground(selectedColor);
    	dataScrollPane.setForeground(selectedColor);
    	
    	fetchScrollPane = new JScrollPane();
        fetchPane = new FetchPane();
    	fetchScrollPane.setViewportView(fetchPane);
    	if (this.baggerBag.getIsHoley()) {
            this.addTab("Fetch", fetchScrollPane);    		
    	}
    	fetchPane.setForeground(selectedColor);
    	fetchScrollPane.setForeground(selectedColor);

        bagItScrollPane = new JScrollPane();
        bagItPane = new BagItPane();
        bagItScrollPane.setViewportView(bagItPane);
        this.addTab("Bag It", bagItScrollPane);
        bagItPane.setForeground(selectedColor);
        bagItScrollPane.setForeground(selectedColor);
    }
    
    public void populateBagPane(String messages) {
//    	createBag();

    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane(baggerBag, messages);
    	consoleScrollPane.setViewportView(consolePane);
    	this.addTab("Console", consoleScrollPane);
    	consolePane.setForeground(selectedColor);
    	consoleScrollPane.setForeground(unselectedColor);

    	String mcontent = new String();
    	if (baggerBag.getBaggerManifests() != null && baggerBag.getBaggerManifests().size() > 0) {
        	List<BaggerManifest> manifests = baggerBag.getBaggerManifests();
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
    	manifestPane.setForeground(selectedColor);
    	manifestScrollPane.setForeground(selectedColor);

    	String tmcontent = new String();
    	if (baggerBag.getTagManifests() != null && baggerBag.getBaggerTagManifests().size() > 0) {
        	List<BaggerTagManifest> tagManifests = baggerBag.getBaggerTagManifests();
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
    	tagManifestPane.setForeground(selectedColor);
    	tagManifestScrollPane.setForeground(selectedColor);

    	bagInfoScrollPane = new JScrollPane();
        bagInfoPane = new BagInfoPane(baggerBag.getInfo());
        bagInfoScrollPane.setViewportView(bagInfoPane);
        this.addTab("Bag It Info", bagInfoScrollPane);
        bagInfoPane.setForeground(selectedColor);
        bagInfoScrollPane.setForeground(selectedColor);

        dataScrollPane = new JScrollPane();
    	dataPane = new DataPane(baggerBag.getData());
    	dataScrollPane.setViewportView(dataPane);
    	this.addTab("Data", dataScrollPane);
    	dataPane.setForeground(selectedColor);
    	dataScrollPane.setForeground(selectedColor);

    	fetchScrollPane = new JScrollPane();
        fetchPane = new FetchPane(baggerBag.getFetch());
    	fetchScrollPane.setViewportView(fetchPane);
    	if (this.baggerBag.getIsHoley()) {
            this.addTab("Fetch", fetchScrollPane);    		
            fetchPane.setForeground(selectedColor);
        	fetchScrollPane.setForeground(selectedColor);
    	}

        bagItScrollPane = new JScrollPane();
        bagItPane = new BagItPane(baggerBag.getBagIt());
        bagItScrollPane.setViewportView(bagItPane);
        this.addTab("Bag It", bagItScrollPane);
        bagItPane.setForeground(selectedColor);
        bagItScrollPane.setForeground(selectedColor);
    }

    // setBag must be called before updateTabs is called
    public void updateMessages(BaggerBag baggerBag, String messages) {
    	setBag(baggerBag);
    	if (this.getComponentCount() > 0) {
    		this.removeAll();
            this.invalidate();
            consolePane.invalidate();
            consoleScrollPane.invalidate();
    	}
        /* */
    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane(baggerBag, messages);
    	consoleScrollPane.setViewportView(consolePane);
    	this.addTab("Console", consoleScrollPane);
    	consoleScrollPane.setForeground(unselectedColor);
    	manifestScrollPane.setViewportView(manifestPane);
    	this.addTab("Manifest", manifestScrollPane);
    	manifestScrollPane.setForeground(selectedColor);
    	this.addTab("TagManifest", tagManifestScrollPane);
    	tagManifestScrollPane.setForeground(selectedColor);
        this.addTab("Bag It Info", bagInfoScrollPane);
        bagInfoScrollPane.setForeground(selectedColor);
    	this.addTab("Data", dataScrollPane);
    	dataScrollPane.setForeground(selectedColor);
    	if (this.baggerBag.getIsHoley()) {
            this.addTab("Fetch", fetchScrollPane);    		
            fetchScrollPane.setForeground(selectedColor);
    	}
        this.addTab("Bag It", bagItScrollPane);
        bagItScrollPane.setForeground(selectedColor);
    	/* */
        consolePane.invalidate();
        consoleScrollPane.invalidate();
        this.invalidate();
    }
    
    // setBag must be called before updateTabs is called
    public void updateTabs(BaggerBag baggerBag, String messages) {
        messages += "\n";
        messages += "Number of files added: " + baggerBag.getData().getNumFiles();
        messages += "\n";
        long fsize = baggerBag.getData().getSizeFiles();
        if (fsize > BaggerBag.MB) {
        	fsize /= BaggerBag.MB;
            messages += "Total size of files added (MB): " + fsize;
        } else if (fsize > BaggerBag.KB) {
        	fsize /= BaggerBag.KB;
            messages += "Total size of files added (KB): " + fsize;
        } else {
            messages += "Total size of files added: 0-1 KB";
        }

        setBag(baggerBag);
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
