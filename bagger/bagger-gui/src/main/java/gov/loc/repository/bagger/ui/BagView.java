
package gov.loc.repository.bagger.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.CompositeDialogPage;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TabbedDialogPage;
import org.springframework.richclient.dialog.AbstractDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

import gov.loc.repository.bagger.Address;
import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.bag.*;
import gov.loc.repository.bagger.util.RecursiveFileListIterator;
import com.ravnaandtines.ftp.*;

public class BagView extends AbstractView implements ApplicationListener {
	private static final Log log = LogFactory.getLog(BagView.class);

	private String username;
	private Bagger bagger;
    private Bag bag;
   	private JPanel bagView = null;
    private JTree bagsTree;
    private List<File> rootTree;
    private Collection<Project> userProjects;
    private File rootSrc = null;
    private JFrame rootFrame;
    private DefaultTreeModel bagsTreeModel;
    private OrganizationInfoForm bagInfoForm;

    private JPanel publisherPanel;
    private JTextField publisherField;
    private JScrollPane filePane;
    private JPanel filePanel;
    private JPanel mainPanel;
    private JPanel infoPanel;
    private JTabbedPane compositePane;
    private JScrollPane consoleScrollPane;
    private ConsolePane consolePane;
    private BagIt bagIt = null;
    private BagItPane bagItPane;
    private JScrollPane bagItScrollPane;
    private BagItInfo bagItInfo = null;
    private BagItInfoPane bagItInfoPane;
    private JScrollPane bagItInfoScrollPane;
    private Data data = null;
    private DataPane dataPane;
    private JScrollPane dataScrollPane;
    private Fetch fetch = null;
    private FetchPane fetchPane;
    private JScrollPane fetchScrollPane;
    private Manifest manifest = null;
    private ManifestPane manifestPane;
    private TagManifest tagManifest = null;
    private ManifestPane tagManifestPane;
    private JScrollPane manifestScrollPane;
    private JScrollPane tagManifestScrollPane;
    private Action openAction;
    private Action saveAction;
    private Action validateAction;
    private Action updatePropAction;
    private Action propAction;
    private Action ftpAction;
    private JButton openButton;
    private JButton saveButton;
    private JButton validateButton;
    private JButton updatePropButton;
    private JButton propButton;
    private JButton ftpButton;
    
    private ValidateExecutor validateExecutor = new ValidateExecutor();
    private FtpExecutor ftpExecutor = new FtpExecutor();
    private FtpPropertiesExecutor ftpPropertiesExecutor = new FtpPropertiesExecutor();
    private PropertiesExecutor propertiesExecutor = new PropertiesExecutor();
    private OrganizationPropertiesExecutor organizationPropertiesExecutor = new OrganizationPropertiesExecutor();

    public void setBagger(Bagger bagger) {
        Assert.notNull(bagger, "The bagger property is required");
        display("BagView.setBagger: " );
        this.bagger = bagger;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    private Bag getBag() {
        return this.bag;
    }
    
    public void setBagNameToDisplay(String bagName) {
        this.bag.setName(bagName);
    }

	public Dimension getMinimumSize() {
		return new Dimension(800, 100);
	}

	public Dimension getPreferredSize() {
		return new Dimension(1000, 800);
	}

	public void display(String s) {
		//log.debug(s);
		log.info(s);
	}
	
    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register(GlobalCommandIds.PROPERTIES, propertiesExecutor);
        context.register(GlobalCommandIds.PROPERTIES, validateExecutor);
    }

    private void resize(JFrame f) {
    	rootFrame = f;
        Dimension	sz = Toolkit.getDefaultToolkit().getScreenSize();
        int fud = 200;
        int width = sz.width-fud;
   	    int height = sz.height-fud;
   	    Dimension bd = new Dimension(width, height);
   	    display("BagView.createControl dimensions: " + bd.width + " x " + bd.height);
  	    f.setResizable(true);
  	    f.setLocation(100, 100);
  	    f.setSize( bd.width, bd.height );
    }

/* 
 * 
 * **************************************************************
 *                      ASCII Layout Here
 * **************************************************************
 * 
 */
    @Override
    protected JComponent createControl() {
    	resize(this.getActiveWindow().getControl());

        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setHgap(2);
        borderLayout.setVgap(2);
    	bagView = new JPanel(borderLayout);

        JPanel buttonPanel = createButtonPanel();

    	compositePane = new JTabbedPane();
        compositePane = createBagPane();

        mainPanel = createMainPanel();

        JPanel centerGrid = new JPanel(new GridLayout(1,2,10,10));
        centerGrid.add(mainPanel);
        centerGrid.add(compositePane);
        
        bagView.add(buttonPanel, BorderLayout.NORTH);
        bagView.add(centerGrid, BorderLayout.CENTER);

        return bagView;
    }
    
    private JPanel createButtonPanel() {
    	FlowLayout layout = new FlowLayout();
    	layout.setAlignment(FlowLayout.LEFT);
    	layout.setHgap(10);
    	layout.setVgap(10);
    	JPanel panel = new JPanel(layout);
        String filename = File.separator+".";
        File selectFile = new File(filename);
        rootTree = new ArrayList<File>();
        JFrame frame = new JFrame();
        JFileChooser fc = new JFileChooser(selectFile);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
        openAction = new OpenFileAction(frame, fc);
    	openButton = new JButton("Bag Data Chooser");
    	openButton.addActionListener(openAction);
        openButton.setMnemonic('o');

        saveAction = new SaveFileAction(frame, fc);
        saveButton = new JButton("Bag Creator");
        saveButton.addActionListener(saveAction);
        saveButton.setMnemonic('s');

        validateAction = new ValidateBagAction();
        validateButton = new JButton("Bag Validator");
        validateButton.addActionListener(validateAction);
        validateButton.setMnemonic('v');
        validateExecutor.setEnabled(true);

        ftpAction = new FtpAction();
        ftpButton = new JButton("Bag Transfer");
        ftpButton.addActionListener(ftpAction);
        ftpButton.setMnemonic('t');
        ftpExecutor.setEnabled(true);

        panel.add(openButton);
        panel.add(saveButton);
        panel.add(validateButton);
        panel.add(ftpButton);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
    	GridLayout gridLayout = new GridLayout(2,1,10,10);
    	gridLayout.setHgap(10);
    	gridLayout.setVgap(10);
        mainPanel = new JPanel(gridLayout);
        
/* */
        HierarchicalFormModel infoFormModel;
        infoFormModel = FormModelHelper.createCompoundFormModel(this.bag.getInfo());
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null));
/* */
        propAction = new PropertyAction();
        propButton = new JButton("Bag Properties");
        propButton.addActionListener(propAction);
        propButton.setMnemonic('p');
        propertiesExecutor.setEnabled(true);
        Border border = new EmptyBorder(5, 5, 5, 5);
        propButton.setBorder(border);
        
        updatePropAction = new UpdatePropertyAction();
        updatePropButton = new JButton("Save Updates");
        updatePropButton.addActionListener(updatePropAction);
        updatePropButton.setMnemonic('u');
        updatePropButton.setBorder(border);
        
        DefaultListModel listModel = new DefaultListModel();
        Object[] array = userProjects.toArray();
        for (int i=0; i < userProjects.size(); i++) listModel.addElement(((Project)array[i]).getName());
        JList projectList = new JList(listModel);
        projectList.setName("Bag Project");
        projectList.setSelectedIndex(0);
        projectList.setVisibleRowCount(1);
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            	JList jlist = (JList)e.getSource();
            	String selected = (String) jlist.getSelectedValue();
            	display("valueChanged: " + selected);
                Object[] project_array = userProjects.toArray();
                for (int i=0; i < userProjects.size(); i++) {
                	Project project = (Project)project_array[i];
                	if (selected.equalsIgnoreCase(project.getName())) {
                		bag.setProject(project);
                	}
                }
            	if (selected.equalsIgnoreCase("copyright")) {
            		bag.setIsCopyright(true);
            		//publisherPanel.show();
                    publisherField.setEnabled(true);
                    publisherField.setEditable(true);
                    //publisherPanel.invalidate();
                    //publisherPanel.repaint();
                    publisherField.invalidate();
                    publisherField.repaint();
                    //infoPanel.invalidate();
                    //infoPanel.repaint();
                    //mainPanel.invalidate();
                    //mainPanel.repaint();
            	} else {
            		bag.setIsCopyright(false);
                    publisherField.setEnabled(false);
                    publisherField.setEditable(false);
                    publisherField.invalidate();
                    publisherField.repaint();            		
            	}
            }
        });
        JPanel projectPane = new JPanel(new BorderLayout());
        JScrollPane listPane = new JScrollPane(projectList);
        projectPane.add(new JLabel("Bag project: "), BorderLayout.WEST);
        projectPane.add(listPane, BorderLayout.CENTER);
        
        JCheckBox holeyCheckbox = new JCheckBox("Holey Bag");
        holeyCheckbox.setBorder(border);
        
        JLabel groupLabel = new JLabel("Checksum Type: ");        
        JRadioButton md5Button = new JRadioButton("MD5");
        md5Button.setSelected(true);
        JRadioButton sha1Button = new JRadioButton("SHA1");
        sha1Button.setSelected(false);        
        ButtonGroup group = new ButtonGroup();
        group.add(md5Button);
        group.add(sha1Button);        
        JPanel groupPanel = new JPanel(new FlowLayout());
        groupPanel.add(groupLabel);
        groupPanel.add(md5Button);
        groupPanel.add(sha1Button);
        groupPanel.setBorder(border);
        
        publisherPanel = new JPanel(new BorderLayout());
        JLabel publisherLabel = new JLabel("Publisher: ");
        publisherField = new JTextField("");
        publisherField.setColumns(30);
        publisherField.setEditable(false);
        publisherField.setEnabled(false);
        publisherPanel.add(publisherLabel, BorderLayout.WEST);
        publisherPanel.add(publisherField, BorderLayout.CENTER);

        GridBagLayout infoLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        infoPanel = new JPanel(infoLayout);
        Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
        infoPanel.setBorder(emptyBorder);

        int row = 0;
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        infoLayout.setConstraints(projectPane, gbc);
        infoPanel.add(projectPane);

        gbc.gridx = 1;
        gbc.gridy = row-1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        infoLayout.setConstraints(propButton, gbc);
        infoPanel.add(propButton);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 10;
        gbc.weighty = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        infoLayout.setConstraints(bagInfoForm.getControl(), gbc);
        infoPanel.add(bagInfoForm.getControl());

        gbc.gridx = 1;
        gbc.gridy = row-1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;
        infoLayout.setConstraints(propButton, gbc);
        infoPanel.add(updatePropButton);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        infoLayout.setConstraints(holeyCheckbox, gbc);
        infoPanel.add(holeyCheckbox);

        gbc.gridx = 1;
        gbc.gridy = row-1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        infoLayout.setConstraints(groupPanel, gbc);
        infoPanel.add(groupPanel);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        infoLayout.setConstraints(publisherPanel, gbc);
        infoPanel.add(publisherPanel);

//      publisherPanel.hide();
        
        bagsTree = new JTree();
        bagsTree.setPreferredSize(getMinimumSize());      
        filePane = new JScrollPane();
        filePane.setViewportView(bagsTree);
        filePanel = new JPanel(new FlowLayout());
        filePanel.add(filePane);

        mainPanel.add(filePanel);
        mainPanel.add(infoPanel);

        return mainPanel;
    }

    private JTabbedPane createBagPane() {
    	createBag();
    	initializeBag();

    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane();
    	consoleScrollPane.setViewportView(consolePane);
    	compositePane.addTab("Console", consoleScrollPane);
    	
    	manifestScrollPane = new JScrollPane();
    	manifestPane = new ManifestPane();
    	manifestScrollPane.setViewportView(manifestPane);
    	compositePane.addTab("Manifest", manifestScrollPane);

    	tagManifestScrollPane = new JScrollPane();
    	tagManifestPane = new ManifestPane();
    	tagManifestScrollPane.setViewportView(tagManifestPane);
    	compositePane.addTab("TagManifest", tagManifestScrollPane);

    	bagItInfoScrollPane = new JScrollPane();
        bagItInfoPane = new BagItInfoPane();
        bagItInfoScrollPane.setViewportView(bagItInfoPane);
        compositePane.addTab("Bag It Info", bagItInfoScrollPane);

        dataScrollPane = new JScrollPane();
    	dataPane = new DataPane();
    	dataScrollPane.setViewportView(dataPane);
    	compositePane.addTab("Data", dataScrollPane);
    	
    	fetchScrollPane = new JScrollPane();
        fetchPane = new FetchPane();
    	fetchScrollPane.setViewportView(fetchPane);
    	if (this.bag.getIsHoley()) {
            compositePane.addTab("Fetch", fetchScrollPane);    		
    	}

        bagItScrollPane = new JScrollPane();
        bagItPane = new BagItPane();
        bagItScrollPane.setViewportView(bagItPane);
        compositePane.addTab("Bag It", bagItScrollPane);

    	return compositePane;
    }
    
    private JTabbedPane populateBagPane(String messages) {
    	createBag();

    	consoleScrollPane = new JScrollPane();
    	consolePane = new ConsolePane(bag, messages);
    	consoleScrollPane.setViewportView(consolePane);
    	compositePane.addTab("Console", consoleScrollPane);

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
    	compositePane.addTab("Manifest", manifestScrollPane);

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
    	compositePane.addTab("TagManifest", tagManifestScrollPane);

    	bagItInfoScrollPane = new JScrollPane();
        bagItInfoPane = new BagItInfoPane(bag.getInfo());
        bagItInfoScrollPane.setViewportView(bagItInfoPane);
        compositePane.addTab("Bag It Info", bagItInfoScrollPane);

        dataScrollPane = new JScrollPane();
    	dataPane = new DataPane(bag.getData());
    	dataScrollPane.setViewportView(dataPane);
    	compositePane.addTab("Data", dataScrollPane);

    	fetchScrollPane = new JScrollPane();
        fetchPane = new FetchPane(bag.getFetch());
    	fetchScrollPane.setViewportView(fetchPane);
    	if (this.bag.getIsHoley()) {
            compositePane.addTab("Fetch", fetchScrollPane);    		
    	}

        bagItScrollPane = new JScrollPane();
        bagItPane = new BagItPane(bag.getBagIt());
        bagItScrollPane.setViewportView(bagItPane);
        compositePane.addTab("Bag It", bagItScrollPane);

    	return compositePane;
    }

    private void createBag() {
        if (bag == null) bag = new Bag();
        if (fetch == null) fetch = new Fetch();
        else fetch = bag.getFetch();
        bag.setFetch(fetch);
        if (bagItInfo == null) bagItInfo = new  BagItInfo();
        else bagItInfo = bag.getInfo();
        bag.setInfo(bagItInfo);
        if (bagIt == null) bagIt = new BagIt();
        else bagIt = bag.getBagIt();
        bag.setBagIt(bagIt);
    	if (data == null) data = new Data();
    	else data = bag.getData();
    	data.setFiles(rootTree);
    	bag.setData(data);
    	if (rootSrc != null) bag.setRootSrc(rootSrc);
    	manifest = new Manifest(bag);
    	manifest.setType(ManifestType.MD5);
    	ArrayList<Manifest> mset = new ArrayList<Manifest>();
    	mset.add(manifest);
    	bag.setManifests(mset);
    	List<TagManifest> tagManifestList = bag.getTagManifests();
    	if (tagManifestList == null || tagManifestList.isEmpty()) {
        	ArrayList<TagManifest> tmset = new ArrayList<TagManifest>();
        	tagManifest = new TagManifest(bag);
        	tagManifest.setType(ManifestType.MD5);
        	tmset.add(tagManifest);    		
        	bag.setTagManifests(tmset);
    	}
    }

    private void initializeBag() {
    	Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	this.username = a.getName();
    	display("BagView.creatControl getAuthenticationUser:: " + this.username);
    	display("BagView.createControl projects: " + bagger.getProjects());
    	Collection<Profile> profiles = bagger.findProfiles(a.getName());
    	Object[] profileArray = profiles.toArray();
    	for (int i=0; i < profileArray.length; i++) {
    		display("BagView.createControl profile:\n" + profileArray[i].toString());
    		Profile profile = (Profile) profileArray[i];
    		userProjects = bagger.findProjects(profile.getPerson().getId());
    		Organization org = profile.getPerson().getOrganization();
    		Address address = org.getAddress();
    		bagItInfo = bag.getInfo();
    		BagOrganization bagOrg = bagItInfo.getBagOrganization();
    		bagOrg.setContact(profile.getContact());
    		bagOrg.setOrgName(org.getName());
    		bagOrg.setOrgAddress(address.toString(true));
    		bagItInfo.setBagOrganization(bagOrg);
    		bag.setInfo(bagItInfo);
    	}
    }
    	    
    private class OpenFileAction extends AbstractAction {
		private static final long serialVersionUID = -5915870395535673069L;
		JFrame frame;
        JFileChooser chooser;
    
        OpenFileAction(JFrame frame, JFileChooser chooser) {
            super("Open...");
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            this.chooser = chooser;
            this.frame = frame;
        }
    
        public void actionPerformed(ActionEvent evt) {
            // Show dialog; this method does not return until dialog is closed
        	int option = chooser.showOpenDialog(frame);
    
            // Get the selected file
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                rootSrc = file.getAbsoluteFile(); //file.getParentFile();
                display("OpenFileAction.actionPerformed filePath: " + file.getPath() + " rootPath: " + rootSrc.getPath() );
            	rootFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            	/* */
                bag = getBag();
                bagInfoForm.commit();
                BagItInfo newInfo = (BagItInfo)bagInfoForm.getFormObject();
                bag.setInfo(newInfo);
                setBag(bag);
            	/* */
                updateTree(file);
                bag.setRootDir(rootSrc);
            	rootFrame.setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    
 // This action creates and shows a modal save-file dialog.
    public class SaveFileAction extends AbstractAction {
		private static final long serialVersionUID = -3466819146072877868L;
		JFileChooser chooser;
        JFrame frame;
    
        SaveFileAction(JFrame frame, JFileChooser chooser) {
            super("Save As...");
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            this.chooser = chooser;
            this.frame = frame;
        }
    
        public void actionPerformed(ActionEvent evt) {
            // Show dialog; this method does not return until dialog is closed
        	int option = chooser.showSaveDialog(frame);
    
            // Get the selected file
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                display("BagView.SaveFileAction: " + file);
            	rootFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            	/* */
                bag = getBag();
                bagInfoForm.commit();
                BagItInfo newInfo = (BagItInfo)bagInfoForm.getFormObject();
                bag.setInfo(newInfo);
                setBag(bag);
            	/* */
                String errorMessages = bag.write(file);
            	rootFrame.setCursor(Cursor.getDefaultCursor());
            	updateTabs(errorMessages);
            	display("\nBagView.SaveFileAction ERROR: " + errorMessages);
            }
        }
    }

    private class ValidateBagAction extends AbstractAction {
		private static final long serialVersionUID = 2256331268073462469L;

		ValidateBagAction() {
            super("Validate...");
        }
    
        public void actionPerformed(ActionEvent e) {
            // Show dialog; this method does not return until dialog is closed
            if (validateExecutor.isEnabled()) {
                validateExecutor.execute();
            }
        }
    }

    private class PropertyAction extends AbstractAction {
		private static final long serialVersionUID = 7726904847537574542L;

		PropertyAction() {
            super("Organization Properties...");
        }
    
        public void actionPerformed(ActionEvent e) {
            // Show dialog; this method does not return until dialog is closed
            if (propertiesExecutor.isEnabled()) {
                propertiesExecutor.execute();
            }
        }
    }
    
    private class UpdatePropertyAction extends AbstractAction {
		private static final long serialVersionUID = 7203526831992572675L;

		UpdatePropertyAction() {
            super("Save Updated Properties...");
        }
    
        public void actionPerformed(ActionEvent e) {
        	String messages = new String();
            bag = getBag();
            bagInfoForm.commit();
            BagItInfo newInfo = (BagItInfo)bagInfoForm.getFormObject();
            bag.setInfo(newInfo);
            setBag(bag);
            messages = "Organization and Contact information has been updated.";

            updateTabs(messages);
        }
    }
    
    private class FtpAction extends AbstractAction {
		private static final long serialVersionUID = 5353357080228961994L;

		FtpAction() {
            super("Transfer...");
        }
    
        public void actionPerformed(ActionEvent e) {
            // Show dialog; this method does not return until dialog is closed
            if (ftpExecutor.isEnabled()) {
                ftpExecutor.execute();
            }
        }
    }

    /* */
    private void updateTree(File file) {
        String messages = new String();
    	if (filePane.getComponentCount() > 0) {
    		if (bagsTree != null && bagsTree.isShowing()) {
           	    bagsTree.invalidate();
    		}
    	}
        rootTree = new ArrayList<File>();
        createBagManagerTree(file);
        bagsTree.setPreferredSize(getMinimumSize());      
        filePane.setViewportView(bagsTree);
        messages = "The files to be included have been changed.";
    	updateTabs(messages);
        
    	bagView.validate();
    	bagView.repaint();
    }

    private void updateTabs(String messages) {
    	if (compositePane.getComponentCount() > 0) {
    		compositePane.removeAll();
            compositePane.invalidate();
            consolePane.invalidate();
            consoleScrollPane.invalidate();
    	}
        compositePane = populateBagPane(messages);
        consolePane.validate();
        consolePane.repaint();
        consoleScrollPane.validate();
        consoleScrollPane.repaint();
        compositePane.validate();
        compositePane.repaint();
    }    

    private void createBagManagerTree(File file) { 
    	rootFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	display("createBagManagerTree: rootTree");
    	RecursiveFileListIterator fit = new RecursiveFileListIterator(file);
    	for (Iterator<File> it=fit; it.hasNext(); ) {
            File f = it.next();
            rootTree.add(f);
            display(f.getAbsoluteFile().toString());
        }
    	display("bagsTree.getRootDir");
    	DefaultMutableTreeNode rootDir = new DefaultMutableTreeNode();
    	display("createBagManagerTree: fileTree");
		rootDir = addNodes(null, file);
		display("createBagManagerTree: files.size: " + rootDir.getChildCount());
		bagsTree = new JTree(rootDir);
		rootFrame.setCursor(Cursor.getDefaultCursor());
    }

	/** Add nodes from under "dir" into curTop. Highly recursive. */
	DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
    	//display("createBagManagerTree: addNodes: " + dir.toString());
		String curPath = dir.getPath();
		DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
		if (curTop != null) { // should only be null at root
			curTop.add(curDir);
		}
		Vector<String> ol = new Vector<String>();
		String[] tmp = dir.list();
		for (int i = 0; i < tmp.length; i++)
			ol.addElement(tmp[i]);

		Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
		File f;
		Vector<String> files = new Vector<String>();
		// Make two passes, one for Dirs and one for Files. This is #1.
		for (int i = 0; i < ol.size(); i++) {
			String thisObject = (String) ol.elementAt(i);
			String newPath;
			if (curPath.equals("."))
				newPath = thisObject;
			else
				newPath = curPath + File.separator + thisObject;
			if ((f = new File(newPath)).isDirectory())
				addNodes(curDir, f);
			else
				files.addElement(thisObject);
		}
		// Pass two: for files.
    	//display("createBagManagerTree: files.size: " + files.size());
		for (int fnum = 0; fnum < files.size(); fnum++)
			curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));

		return curDir;
	}

	private class ValidateExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            if (getBag() != null) {
            	display("ValidateExecutor");
                String messages = bag.validate();
            	display(messages);
            	updateTabs(messages);
            }
        }
    }

    private class PropertiesExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            if (getBag() != null) {
                organizationPropertiesExecutor.execute();
            }
        }
    }

    /**
     * Command to create a new Bag Organization and Contact information. 
     * Pops up a dialog which is reused by using the {@link CloseAction#HIDE}.
     *
     * @see ApplicationDialog
     * @see CloseAction
     */
      private class OrganizationPropertiesExecutor extends AbstractActionCommandExecutor {
        OrganizationGeneralForm organizationGeneralForm;
        OrganizationContactForm organizationContactForm;
        OrganizationInfoForm organizationInfoForm;

        private CompositeDialogPage compositePage;
        private TitledPageApplicationDialog dialog;
        private FormBackedDialogPage dialogPage;

	    protected AbstractDialogPage createOrganizationPanel() {
            HierarchicalFormModel organizationFormModel;
            BagOrganization bagOrganization = bagItInfo.getBagOrganization();
            organizationFormModel = FormModelHelper.createCompoundFormModel(bagOrganization);
            organizationGeneralForm = new OrganizationGeneralForm(FormModelHelper.createChildPageFormModel(organizationFormModel, null));

            HierarchicalFormModel contactFormModel;
            Contact contact = bagItInfo.getBagOrganization().getContact();
            contactFormModel = FormModelHelper.createCompoundFormModel(contact);
            organizationContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(contactFormModel, null));
            
            HierarchicalFormModel infoFormModel;
            infoFormModel = FormModelHelper.createCompoundFormModel(bagItInfo);
            organizationInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null));

            dialogPage = new FormBackedDialogPage(organizationGeneralForm);
            compositePage = new TabbedDialogPage("organizationProperties");
            compositePage.addForm(organizationGeneralForm);
            compositePage.addForm(organizationContactForm);
            compositePage.addForm(organizationInfoForm);
            
            return compositePage;
	    }

        private void createDialog() {
        	AbstractDialogPage abstractDialog = createOrganizationPanel();
                dialog = new TitledPageApplicationDialog(abstractDialog, getWindowControl(), CloseAction.HIDE) {        		
        	    protected void onAboutToShow() {
                	organizationGeneralForm.requestFocusInWindow();
                    setEnabled(dialogPage.isPageComplete());
                }

                protected boolean onFinish() {
                    String messages = new String();

                    organizationContactForm.commit();
                    Contact newContact = (Contact)organizationContactForm.getFormObject();

                	organizationInfoForm.commit();
                    BagItInfo newInfo = (BagItInfo)organizationInfoForm.getFormObject();

                    organizationGeneralForm.commit();
                    BagOrganization newOrganization = (BagOrganization)organizationGeneralForm.getFormObject();

                    bag = getBag();
                    newOrganization.setContact(newContact);
                    newInfo.setBagOrganization(newOrganization);
                    bag.setInfo(newInfo);
                    setBag(bag);
                    messages = "Organization and Contact information has been updated.";

                    updateTabs(messages);
                    
                    // TODO: Persist the organization updates to local or remote storage
                    return true;
                }
            };
        }

	    public void execute() {
            if (dialog == null) createDialog();

            //organizationGeneralForm.setFormObject(new BagOrganization());
            dialog.showDialog();
        }
    }
/* */
  	private class FtpExecutor extends AbstractActionCommandExecutor {
        public void execute() {
            if (getBag() != null) {
                ftpPropertiesExecutor.execute();
            }
        }
    }

    /**
     * Command to create a new Ftp transfer dialog. 
     * Pops up an ftp dialog which is reused by using the {@link CloseAction#HIDE}.
     *
     * @see ApplicationDialog
     * @see CloseAction
     */
      private class FtpPropertiesExecutor extends AbstractActionCommandExecutor {
    	  private boolean packFrame = false;
    	  private FtpFrame frame = null;

    	  public void execute() {
    		  frame = new FtpFrame();
    		  //Validate frames that have preset sizes
    		  //Pack frames that have useful preferred size info, e.g. from their layout
    		  if (packFrame)
    			  frame.pack();
    		  else
    			  frame.validate();
    		  //Center the window
    		  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    		  Dimension frameSize = frame.getSize();
    		  if (frameSize.height > screenSize.height)
    			  frameSize.height = screenSize.height;
    		  if (frameSize.width > screenSize.width)
    			  frameSize.width = screenSize.width;
    		  frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    		  frame.setVisible(true);
    	  }
    }
/* */
    public void onApplicationEvent(ApplicationEvent e) {
    	display("BagView.onApplicationEvent");
        if (e instanceof LifecycleApplicationEvent) {
        	display("BagView.onApplicationEvent.LifecycleApplicationEvent");
            LifecycleApplicationEvent le = (LifecycleApplicationEvent)e;
            if (le.getEventType() == LifecycleApplicationEvent.CREATED && le.objectIs(BagOrganization.class)) {
                if (bagsTree != null) {
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode)bagsTreeModel.getRoot();
                    root.add(new DefaultMutableTreeNode(le.getObject()));
                    bagsTreeModel.nodeStructureChanged(root);
                }
            }
        } else {
        	display("BagView.onApplicationEvent.validate");
        	bagView.repaint();
        }
    }
/* */
    public void componentClosed() {
    	display("closed");
    }

    public void componentFocusGained() {
    	display("gained");
    }

    public void componentFocusLost() {
    	display("lost");
    }

    public void componentOpened() {
    	display("opened");
    }

}