
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
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.progress.BusyIndicator;
//import org.springframework.richclient.progress.ProgressMonitor;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

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

	private ApplicationWindow window;
	private ProgressMonitor progressMonitor;
	private String username;
	private Bagger bagger;
    private Bag bag;
   	private JPanel bagView = null;
    private BagTree bagsTree;
    private List<File> rootTree;
    private Collection<Project> userProjects;
    private File rootSrc = null;
    private JFrame rootFrame;
    private DefaultTreeModel bagsTreeModel;
    private OrganizationInfoForm bagInfoForm;
    private OrganizationGeneralForm organizationGeneralForm;
    private OrganizationContactForm organizationContactForm;

    private JScrollPane filePane;
    private JPanel filePanel;
    private JPanel mainPanel;
    private JPanel infoPanel;
    private BagTextPane infoMessagePane;
    private JTabbedPane infoPane;
    private JPanel infoLabelPanel;
    private CompositePane compositePane;
    private BagIt bagIt = null;
    private BagInfo bagInfo = null;
    private Data data = null;
    private Fetch fetch = null;
    private Manifest manifest = null;
    private TagManifest tagManifest = null;
    private Action openAction;
    private Action saveAction;
    private Action ftpAction;
    private JButton openButton;
    private JButton saveButton;
    private JButton updatePropButton;
    private JButton ftpButton;
    
    private FtpExecutor ftpExecutor = new FtpExecutor();
    private FtpPropertiesExecutor ftpPropertiesExecutor = new FtpPropertiesExecutor();

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
		return new Dimension(1024, 768);
	}

	public Dimension getPreferredSize() {
		return new Dimension(1024, 768);
	}

	public void display(String s) {
		//log.debug(s);
		log.info(s);
	}
	
    protected void registerLocalCommandExecutors(PageComponentContext context) {
    }

    private void resize(JFrame f) {
    	rootFrame = f;
        Dimension	sz = Toolkit.getDefaultToolkit().getScreenSize();
        int margin = 100;
        int width = sz.width-margin;
   	    int height = sz.height-margin;
   	    Dimension bd = new Dimension(width, height);

   	    bd = this.getPreferredSize();
   	    display("BagView.createControl dimensions: " + bd.width + " x " + bd.height);
  	    f.setResizable(true);
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
    	window = Application.instance().getActiveWindow();

    	resize(this.getActiveWindow().getControl());

        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setHgap(2);
        borderLayout.setVgap(2);
    	bagView = new JPanel(borderLayout);

        JPanel buttonPanel = createButtonPanel();

        compositePane = createBagPane();
        mainPanel = createMainPanel();

        JPanel centerGrid = new JPanel(new GridLayout(1,2,10,10));
        centerGrid.add(mainPanel);
        centerGrid.add(compositePane);
        
        bagView.add(buttonPanel, BorderLayout.NORTH);
        bagView.add(centerGrid, BorderLayout.CENTER);
        
        display("getMessage: " + getMessage("bagName.description"));

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

        ftpAction = new FtpAction();
        ftpButton = new JButton("Bag Transfer");
        ftpButton.addActionListener(ftpAction);
        ftpButton.setMnemonic('t');
        ftpExecutor.setEnabled(true);

        panel.add(openButton);
        panel.add(saveButton);
//        panel.add(ftpButton);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
    	// The file selection tree
    	bagsTree = new BagTree();
        filePane = new JScrollPane();
        filePane.setViewportView(bagsTree);
        filePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        filePanel = new JPanel(new FlowLayout());
        filePanel.setPreferredSize(bagsTree.getPreferredSize());
        filePanel.add(filePane);

        // The bag information panels and controls
    	JScrollPane infoScrollPane = createInfoPane();

    	GridBagLayout gridLayout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        buildConstraints(glbc, 0, 0, 1, 1, 0, 10, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        gridLayout.setConstraints(filePanel, glbc);

        buildConstraints(glbc, 0, 1, 1, 1, 0, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        gridLayout.setConstraints(infoScrollPane, glbc);

        mainPanel = new JPanel(gridLayout);
        mainPanel.add(filePanel);
        mainPanel.add(infoScrollPane);

        return mainPanel;
    }
    
    private JScrollPane createInfoPane() {
    	// Create a panel for the form error messages and the update button
        Action updatePropAction = new UpdatePropertyAction();
        updatePropButton = new JButton("Save Updates");
        updatePropButton.addActionListener(updatePropAction);
        updatePropButton.setMnemonic('u');
        updatePropButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        BorderLayout labelLayout = new BorderLayout();
        labelLayout.setHgap(10);
        infoLabelPanel = new JPanel(labelLayout);
        infoMessagePane = new BagTextPane("");
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors()) {
        	infoMessagePane.setMessage("Form errors exist.");        	
        }
        Dimension labelDimension = infoPane.getPreferredSize();
        int offset = updatePropButton.getWidth();
        if (offset == 0) offset = 80;
        labelDimension.setSize(labelDimension.getWidth()-offset, 25);
        infoMessagePane.setPreferredSize(labelDimension);
        infoLabelPanel.add(infoMessagePane, "Center");
        infoLabelPanel.add(updatePropButton, "East");

        // Define the information forms
    	HierarchicalFormModel organizationFormModel;
        BagOrganization bagOrganization = bagInfo.getBagOrganization();
        organizationFormModel = FormModelHelper.createCompoundFormModel(bagOrganization);
        organizationGeneralForm = new OrganizationGeneralForm(FormModelHelper.createChildPageFormModel(organizationFormModel, null));

        HierarchicalFormModel contactFormModel;
        Contact contact = bagInfo.getBagOrganization().getContact();
        if (contact == null) contact = new Contact();
        contactFormModel = FormModelHelper.createCompoundFormModel(contact);
        organizationContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(contactFormModel, null));
        
        HierarchicalFormModel infoFormModel;
        infoFormModel = FormModelHelper.createCompoundFormModel(bagInfo);
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null));

        // Create a tabbed pane for the information forms and checkbox panel
        infoPane = new JTabbedPane();
        infoPane.addTab("Information", bagInfoForm.getControl());
        infoPane.addTab("Organization", organizationGeneralForm.getControl());
        infoPane.addTab("Contact", organizationContactForm.getControl());
        infoPane.addTab("Controls", createCheckboxPanel());
        infoPane.setPreferredSize(bagInfoForm.getControl().getPreferredSize());

        // Combine the information panel with the forms pane
        GridBagLayout infoLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        buildConstraints(gbc, 0, 0, 1, 1, 10, 20, GridBagConstraints.NONE, GridBagConstraints.WEST);
        infoLayout.setConstraints(infoLabelPanel, gbc);

        buildConstraints(gbc, 0, 1, 1, 1, 70, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(infoPane, gbc);

    	infoPanel = new JPanel(infoLayout);
        Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
        infoPanel.setBorder(emptyBorder);
        infoPanel.add(infoLabelPanel);
        infoPanel.add(infoPane);
        
    	JScrollPane infoScrollPane = new JScrollPane();
    	infoScrollPane.setViewportView(infoPanel);
    	return infoScrollPane;
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
    
    private JPanel createCheckboxPanel() {
        Border border = new EmptyBorder(5, 5, 5, 5);
    	GridLayout gridLayout = new GridLayout(3,1,10,10);
    	gridLayout.setHgap(10);
    	gridLayout.setVgap(10);
        JPanel checkPanel = new JPanel(gridLayout);

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
            	} else {
            		bag.setIsCopyright(false);
            	}
            	log.info("BagView.setIsCopyright: " + bag.getIsCopyright());
            }
        });
        JPanel projectPane = new JPanel(new BorderLayout());
        JScrollPane listPane = new JScrollPane(projectList);
        /* */
    	String selected = (String) projectList.getSelectedValue();
    	if (selected.equalsIgnoreCase("copyright")) {
    		bag.setIsCopyright(true);
    	} else {
    		bag.setIsCopyright(false);
    	}
    	log.info("BagView.setIsCopyright: " + bag.getIsCopyright());
    	/* */
        projectPane.add(new JLabel("Bag project: "), BorderLayout.WEST);
        projectPane.add(listPane, BorderLayout.CENTER);
        
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

        JCheckBox holeyCheckbox = new JCheckBox("Holey Bag");
        holeyCheckbox.setBorder(border);

        checkPanel.add(projectPane);
        checkPanel.add(holeyCheckbox);
        checkPanel.add(groupPanel);

        return checkPanel;
    }

    private CompositePane createBagPane() {
    	createBag();
    	initializeBag();
    	if (bagsTree == null) display("createBagPane: NULL");
    	compositePane = new CompositePane(bag);
    	return compositePane;
    }
    
    private void createBag() {
        if (bag == null) bag = new Bag();
        if (fetch == null) fetch = new Fetch();
        else fetch = bag.getFetch();
        bag.setFetch(fetch);
        if (bagInfo == null) bagInfo = new BagInfo(bag);
        else bagInfo = bag.getInfo();
        bag.setInfo(bagInfo);
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
    	if (a != null) {
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
        		bagInfo = bag.getInfo();
        		BagOrganization bagOrg = bagInfo.getBagOrganization();
        		bagOrg.setContact(profile.getContact());
        		bagOrg.setOrgName(org.getName());
        		bagOrg.setOrgAddress(address.toString(true));
        		bagInfo.setBagOrganization(bagOrg);
        		bag.setInfo(bagInfo);
        	}
    	} else {
    		userProjects = bagger.getProjects();
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
                openBag(file);
            }
        }
    }
    
    private void openBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	rootFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        rootSrc = file.getAbsoluteFile(); //file.getParentFile();
        display("OpenFileAction.actionPerformed filePath: " + file.getPath() + " rootPath: " + rootSrc.getPath() );
        String messages = "Adding " + file.getPath() + " to the bag.";
        compositePane.updateMessages(bag, messages);
    	/* */
        bag = getBag();
        if (!organizationGeneralForm.hasErrors() && !organizationContactForm.hasErrors() && !bagInfoForm.hasErrors()) {
            try {
                bagInfoForm.commit();
                organizationContactForm.commit();
                organizationGeneralForm.commit();            
            } catch (Exception e) {
            	display("openBag exception: " + e.getMessage());
            }        	
        }
        BagInfo newInfo = (BagInfo)bagInfoForm.getFormObject();
        bag.setInfo(newInfo);
        setBag(bag);
    	/* */
        updateTree(file);
        bag.setRootDir(rootSrc);
    	rootFrame.setCursor(Cursor.getDefaultCursor());
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
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
                saveBag(file);
            }
        }
    }
    
    private void saveBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	rootFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        display("BagView.SaveFileAction: " + file);
        String messages = "Creating the bag...";
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors()) {
        	messages = "The Bag Information Form contains errors.";
            compositePane.updateMessages(bag, messages);
        } else {
            try {
                bagInfoForm.commit();
                organizationContactForm.commit();
                organizationGeneralForm.commit();            
                bag = getBag();
                BagInfo newInfo = (BagInfo)bagInfoForm.getFormObject();
                bag.setInfo(newInfo);
                setBag(bag);
            	// TODO Break this down into multiple steps so that each step can send bag progress message to the console.
                // TODO What if file already exists?  Error or message to overwrite
                messages = bag.write(file);
               	display("\nBagView.SaveFileAction: " + messages);
            	updateTabs(messages);
            } catch (Exception e) {
            	display("saveBag exception: " + e.getMessage());
            }        	
        }
    	rootFrame.setCursor(Cursor.getDefaultCursor());
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private class UpdatePropertyAction extends AbstractAction {
		private static final long serialVersionUID = 7203526831992572675L;

		UpdatePropertyAction() {
            super("Save Updated Properties...");
        }
    
        public void actionPerformed(ActionEvent e) {
            String messages = new String();

            if (!organizationContactForm.hasErrors()) {
                organizationContactForm.commit();            	
            }
            Contact newContact = (Contact)organizationContactForm.getFormObject();

            if (!bagInfoForm.hasErrors()) {
                bagInfoForm.commit();            	
            }
            BagInfo newInfo = (BagInfo)bagInfoForm.getFormObject();

            if (!organizationGeneralForm.hasErrors()) {
                organizationGeneralForm.commit();            	
            }
            BagOrganization newOrganization = (BagOrganization)organizationGeneralForm.getFormObject();

            bag = getBag();
            newOrganization.setContact(newContact);
            newInfo.setBagOrganization(newOrganization);
            bag.setInfo(newInfo);
            setBag(bag);
            messages = "Organization and Contact information has been updated.";

            if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors()) {
            	messages += "\nBag Information form errors exist.";
            	infoMessagePane.setMessage("Form errors exist");
            } else {
            	infoMessagePane.setMessage("");
            }
            infoMessagePane.invalidate();
            infoLabelPanel.invalidate();
            compositePane.updateTabs(bag, messages);
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
        filePane.setViewportView(bagsTree);
        filePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messages = "The files to be bagged have been updated.";
    	updateTabs(messages);
        
    	bagView.validate();
    	bagView.repaint();
    }

    private void updateTabs(String messages) {
    	createBag();
    	compositePane.updateTabs(bag, messages);
    }    

    private void createBagManagerTree(File file) { 
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
//    	progressMonitor = window.getStatusBar().getProgressMonitor();
//    	progressMonitor.taskStarted("Bagger called", -1);
    	rootFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	display("createBagManagerTree: rootTree");
    	RecursiveFileListIterator fit = new RecursiveFileListIterator(file);
    	for (Iterator<File> it=fit; it.hasNext(); ) {
            File f = it.next();
            rootTree.add(f);
            //display(f.getAbsoluteFile().toString());
        }
		bagsTree = new BagTree(file);
		bagsTree.requestFocus();
		rootFrame.setCursor(Cursor.getDefaultCursor());
//    	progressMonitor.done();
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
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
                	bagsTreeModel = bagsTree.getBagTreeModel();
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