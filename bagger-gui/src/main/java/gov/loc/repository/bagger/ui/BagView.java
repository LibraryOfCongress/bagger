
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagger.bag.BaggerFileEntity;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.domain.BaggerValidationRulesSource;
//import gov.loc.repository.bagger.ui.handlers.*;
//import gov.loc.repository.bagger.ui.handlers.AddDataHandler;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;
import gov.loc.repository.bagit.verify.impl.ParallelManifestChecksumVerifier;
import gov.loc.repository.bagit.verify.impl.ValidVerifierImpl;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JComponent;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JFrame;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.progress.BusyIndicator;
import org.springframework.util.Assert;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagView extends AbstractView implements ApplicationListener {
	private static final Log log = LogFactory.getLog(BagView.class);
    public final static int ONE_SECOND = 1000;
	private int DEFAULT_WIDTH = 1024;
	private int DEFAULT_HEIGHT = 768;
    private ProgressMonitor progressMonitor;
    private JTextArea taskOutput;
    private Timer timer;
    private LongTask task;

	private Bagger bagger;
    private DefaultBag bag;
    private BaggerValidationRulesSource baggerRules;
    public int bagCount = 0;
    public File bagRootPath;
    public File tmpRootPath;
    public File parentSrc;
    public File addBagDataFile;
    public Collection<Project> userProjects;
    public Collection<Profile> userProfiles;
    public String username;
    public String bagVersion = null;
    public Contact projectContact;
	private String userHomeDir;

	public BagTree bagPayloadTree;
	public BagTreePanel bagPayloadTreePanel;
	public BagTree bagTagFileTree;
	public BagTreePanel bagTagFileTreePanel;
	public CompositePane compositePane;
	public TagFilesFrame tagFilesFrame;
	public TagManifestPane tagManifestPane;
	public BagInfoInputPane bagInfoInputPane;
	public InfoFormsPane infoInputPane;
	public BagTextPane infoFormMessagePane;
	public JPanel bagButtonPanel;
	public JPanel bagTagButtonPanel;
	public JPanel bagPanel;
	public JPanel topButtonPanel;
	public JButton openButton;
	public JButton createDataButton;
	public JButton addDataButton;
    public JButton removeDataButton;
    public JButton saveButton;
    public JButton saveAsButton;
    public JButton updateTreeButton;
    public JButton completeButton;
    public JButton validateButton;
    public JButton showTagButton;
    public JButton addTagFileButton;
    public JButton removeTagFileButton;
    public JButton updateTagTreeButton;
    public JButton updatePropButton;
    public SaveBagFrame saveBagFrame;
    public JTextField bagNameField;
    public JComboBox bagVersionList;
    public JComboBox projectList;
    public JCheckBox defaultProject;
    public JCheckBox holeyCheckbox;
    public JLabel serializeLabel;
    public JPanel serializeGroupPanel;
    public JRadioButton noneButton;
    public JRadioButton zipButton;
    public JRadioButton tarButton;
    public FileFilter noFilter;
    public FileFilter zipFilter;
    public FileFilter tarFilter;

    private CreateDataBagHandler createDataBagHandler;
    private ValidateBagHandler validateBagHandler;
    private AddDataHandler addDataHandler;
    private SaveBagHandler saveBagHandler;
	public StartExecutor startExecutor = new StartExecutor();
    public OpenExecutor openExecutor = new OpenExecutor();
    public CreateDataExecutor createDataExecutor = new CreateDataExecutor();
    public AddDataExecutor addDataExecutor = new AddDataExecutor();
    public RemoveDataExecutor removeDataExecutor = new RemoveDataExecutor();
    public SaveBagExecutor saveBagExecutor = new SaveBagExecutor();
    public SaveBagAsExecutor saveBagAsExecutor = new SaveBagAsExecutor();
    public ValidateExecutor validateExecutor = new ValidateExecutor();
    public CompleteExecutor completeExecutor = new CompleteExecutor();
    public UpdateTreeExecutor updateTreeExecutor = new UpdateTreeExecutor();
    public AddTagFileExecutor addTagFileExecutor = new AddTagFileExecutor();
    public RemoveTagFileExecutor removeTagFileExecutor = new RemoveTagFileExecutor();
    public SaveProfileExecutor saveProfileExecutor = new SaveProfileExecutor();

    public Color errorColor = new Color(255, 128, 128);
	public Color infoColor = new Color(120, 120, 120);

    public void setBagger(Bagger bagger) {
        Assert.notNull(bagger, "The bagger property is required");
        this.bagger = bagger;
    }
    
    public Bagger getBagger() {
    	return this.bagger;
    }
    
    public void setBag(DefaultBag baggerBag) {
        Assert.notNull(baggerBag, "The bag property is required");
        this.bag = baggerBag;
    }

    public DefaultBag getBag() {
        return this.bag;
    }
    
	public Dimension getMinimumSize() {
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public Dimension getPreferredSize() {
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public void display(String s) {
		//log.debug(s);
		log.info(s);
	}

	public String getPropertyMessage(String property) {
    	return getMessage(property);
    }

    public void setBagPayloadTree(BagTree bagTree) {
        this.bagPayloadTree = bagTree;
    }

    public BagTree getBagPayloadTree() {
        return this.bagPayloadTree;
    }
    
    public void setBagTagFileTree(BagTree bagTree) {
    	this.bagTagFileTree = bagTree;
    }
    
    public BagTree getBagTagFileTree() {
    	return this.bagTagFileTree;
    }

    public void setProfiles(Collection<Profile> profiles) {
    	this.userProfiles = profiles;
    }
    
    public Collection<Profile> getProfiles() {
    	return this.userProfiles;
    }
    
    @Override
    // This populates the default view descriptor declared as the startingPageId
    // property in the richclient-application-context.xml file.
    protected JComponent createControl() {
    	ApplicationWindow window = Application.instance().getActiveWindow();
    	JFrame f = window.getControl();
    	//Application.instance().getApplicationContext().
    	f.setBackground(Color.red);
    	this.userHomeDir = System.getProperty("user.home");
        display("createControl - User Home Path: "+ userHomeDir);

        ApplicationServices services = this.getApplicationServices();
        Object rulesSource = services.getService(org.springframework.rules.RulesSource.class);
        baggerRules = (BaggerValidationRulesSource) rulesSource;
		
		newDefaultBag(null);
    	initializeProfile();
    	updateCommands();
    	Color bgColor = new Color(20,20,100);

    	topButtonPanel = createTopButtonPanel();
    	topButtonPanel.setBackground(bgColor);
    	infoInputPane = new InfoFormsPane(this);
        JPanel bagPanel = createBagPanel();

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        buildConstraints(glbc, 0, 0, 1, 1, 50, 100, GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        layout.setConstraints(bagPanel, glbc);

        buildConstraints(glbc, 1, 0, 1, 1, 50, 100, GridBagConstraints.BOTH, GridBagConstraints.NORTH);
        layout.setConstraints(infoInputPane, glbc);

        JPanel mainPanel = new JPanel(layout);
        mainPanel.add(bagPanel);
        mainPanel.add(infoInputPane);
        
    	JPanel bagViewPanel = new JPanel(new BorderLayout(2, 2));
        bagViewPanel.setBackground(bgColor);
    	bagViewPanel.add(topButtonPanel, BorderLayout.NORTH);
    	bagViewPanel.add(mainPanel, BorderLayout.CENTER);
        return bagViewPanel;
    }
    
    private JPanel createTopButtonPanel() {
        Color fgColor = new Color(250, 250, 250);
        Color bgColor = new Color(50, 50, 150);

    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    	buttonPanel.setBackground(bgColor);

    	JButton createButton = new JButton(getPropertyMessage("bag.button.create"));
    	createButton.addActionListener(new StartNewBagHandler());
    	createButton.setOpaque(true);
    	createButton.setBackground(bgColor);
    	createButton.setForeground(fgColor);
    	createButton.setToolTipText(getPropertyMessage("bag.button.create.help"));
    	buttonPanel.add(createButton);

    	openButton = new JButton(getPropertyMessage("bag.button.open"));
    	//openBagHandler = new OpenBagHandler(this);
    	openButton.addActionListener(new OpenBagHandler());
    	openButton.setEnabled(true);
    	openButton.setOpaque(true);
    	openButton.setBackground(bgColor);
    	openButton.setForeground(fgColor);
    	openButton.setToolTipText(getPropertyMessage("bag.button.open.help"));
    	buttonPanel.add(openButton);

    	createDataButton = new JButton(getPropertyMessage("bag.button.createdata"));
    	createDataBagHandler = new CreateDataBagHandler();
    	createDataButton.addActionListener(createDataBagHandler);
    	createDataButton.setEnabled(true);
    	createDataButton.setOpaque(true);
    	createDataButton.setBackground(bgColor);
    	createDataButton.setForeground(fgColor);
    	createDataButton.setToolTipText(getPropertyMessage("bag.button.createdata.help"));
    	buttonPanel.add(createDataButton);

    	validateButton = new JButton(getPropertyMessage("bag.button.validate"));
    	validateBagHandler = new ValidateBagHandler();
        validateButton.addActionListener(validateBagHandler);
        validateButton.setEnabled(false);
        validateButton.setOpaque(true);
    	validateButton.setBackground(bgColor);
    	validateButton.setForeground(fgColor);
    	validateButton.setToolTipText(getPropertyMessage("bag.button.validate.help"));
        buttonPanel.add(validateButton);
        
        completeButton = new JButton(getPropertyMessage("bag.button.complete"));
        completeButton.addActionListener(new CompleteBagHandler());
        completeButton.setEnabled(false);
        completeButton.setOpaque(true);
        completeButton.setBackground(bgColor);
        completeButton.setForeground(fgColor);
        completeButton.setToolTipText(getPropertyMessage("bag.button.complete.help"));
        buttonPanel.add(completeButton);
        
        return buttonPanel;
    }
    
    private JPanel createBagPanel() {
    	bagButtonPanel = createBagButtonPanel();

    	bagTagButtonPanel = createBagTagButtonPanel();

    	bagPayloadTree = new BagTree(this, AbstractBagConstants.DATA_DIRECTORY, true);
    	bagPayloadTree.setEnabled(false);
    	bagPayloadTreePanel = new BagTreePanel(bagPayloadTree);
    	bagPayloadTreePanel.setEnabled(false);
    	bagPayloadTreePanel.setToolTipText(getPropertyMessage("bagTree.help"));

    	bagTagFileTree = new BagTree(this, getPropertyMessage("bag.label.noname"), false);
    	bagTagFileTree.setEnabled(false);
    	bagTagFileTreePanel = new BagTreePanel(bagTagFileTree);
    	bagTagFileTreePanel.setEnabled(false);
    	bagTagFileTreePanel.setToolTipText(getPropertyMessage("bagTree.help"));

    	compositePane = new CompositePane(this, getInitialConsoleMsg());
    	compositePane.setToolTipText(getPropertyMessage("compositePane.tab.help"));    	

    	tagManifestPane = new TagManifestPane(this);
    	tagManifestPane.setToolTipText(getPropertyMessage("compositePane.tab.help"));
		tagFilesFrame = new TagFilesFrame(getActiveWindow().getControl(), getPropertyMessage("tagFrame.title"));
		tagFilesFrame.addComponents(tagManifestPane);
    	
    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        buildConstraints(glbc, 0, 0, 1, 1, 5, 10, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagButtonPanel, glbc);

        buildConstraints(glbc, 1, 0, 1, 1, 35, 40, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(bagPayloadTreePanel, glbc);

        buildConstraints(glbc, 0, 1, 1, 1, 5, 10, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagTagButtonPanel, glbc);

        buildConstraints(glbc, 1, 1, 1, 1, 35, 20, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(bagTagFileTreePanel, glbc);
        
        buildConstraints(glbc, 0, 2, 2, 1, 40, 40, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(compositePane, glbc);

        bagPanel = new JPanel(layout);
        bagPanel.add(bagButtonPanel);
    	bagPanel.add(bagPayloadTreePanel);
    	bagPanel.add(bagTagButtonPanel);
    	bagPanel.add(bagTagFileTreePanel);
        bagPanel.add(compositePane);

        return bagPanel;
    }
    
    private JPanel createBagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));

    	addDataButton = new JButton(getPropertyMessage("bag.button.add"));
    	addDataHandler = new AddDataHandler();
    	addDataButton.addActionListener(addDataHandler);
    	addDataButton.setEnabled(false);
    	addDataButton.setToolTipText(getPropertyMessage("bag.button.add.help"));
        buttonPanel.add(addDataButton, BorderLayout.NORTH);

    	JPanel savePanel = new JPanel(new BorderLayout(5, 5));
    	removeDataButton = new JButton(getPropertyMessage("bag.button.remove"));
    	removeDataButton.addActionListener(new RemoveDataHandler());
    	removeDataButton.setEnabled(false);
    	removeDataButton.setToolTipText(getPropertyMessage("bag.button.remove.help"));
    	savePanel.add(removeDataButton, BorderLayout.NORTH);
    	
        saveButton = new JButton(getPropertyMessage("bag.button.save"));
        saveBagHandler = new SaveBagHandler();
        saveButton.addActionListener(saveBagHandler);
        saveButton.setEnabled(false);
        saveButton.setToolTipText(getPropertyMessage("bag.button.save.help"));
        savePanel.add(saveButton, BorderLayout.CENTER);

    	saveAsButton = new JButton(getPropertyMessage("bag.button.saveas"));
    	saveAsButton.addActionListener(new SaveBagAsHandler(this));
        saveAsButton.setEnabled(false);
        saveAsButton.setToolTipText(getPropertyMessage("bag.button.saveas.help"));
        saveBagFrame = new SaveBagFrame(this, getPropertyMessage("bag.frame.save"));
        savePanel.add(saveAsButton, BorderLayout.SOUTH);

        buttonPanel.add(savePanel, BorderLayout.CENTER);
        
        updateTreeButton = new JButton(getPropertyMessage("bag.button.updatetree"));
        updateTreeButton.addActionListener(new UpdateTreeHandler());
        updateTreeButton.setEnabled(false);
        updateTreeButton.setToolTipText(getPropertyMessage("bag.button.updatetree.help"));
        buttonPanel.add(updateTreeButton, BorderLayout.SOUTH);
        
        return buttonPanel;
    }
    
    private JPanel createBagTagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));

    	showTagButton = new JButton(getPropertyMessage("bag.tagbutton.show"));
    	showTagButton.addActionListener(new ShowTagFilesHandler());
    	showTagButton.setEnabled(false);
    	showTagButton.setToolTipText(getPropertyMessage("bag.tagbutton.show.help"));
    	buttonPanel.add(showTagButton, BorderLayout.NORTH);

    	JPanel panel = new JPanel(new BorderLayout(5, 5));
    	addTagFileButton = new JButton(getPropertyMessage("bag.tagbutton.add"));
    	addTagFileButton.addActionListener(new AddTagFileHandler());
    	addTagFileButton.setEnabled(false);
    	addTagFileButton.setToolTipText(getPropertyMessage("bag.tagbutton.add.help"));
    	panel.add(addTagFileButton, BorderLayout.NORTH);
    	
    	removeTagFileButton = new JButton(getPropertyMessage("bag.tagbutton.remove"));
    	removeTagFileButton.addActionListener(new RemoveTagFileHandler());
    	removeTagFileButton.setEnabled(false);
    	removeTagFileButton.setToolTipText(getPropertyMessage("bag.tagbutton.remove.help"));
    	panel.add(removeTagFileButton, BorderLayout.SOUTH);

    	buttonPanel.add(panel, BorderLayout.CENTER);

    	updateTagTreeButton = new JButton(getPropertyMessage("bag.tagbutton.update"));
    	updateTagTreeButton.addActionListener(new UpdateTagTreeHandler());
    	updateTagTreeButton.setEnabled(false);
    	updateTagTreeButton.setToolTipText(getPropertyMessage("bag.tagbutton.update.help"));
        buttonPanel.add(updateTagTreeButton, BorderLayout.SOUTH);

        return buttonPanel;
    }

    public void enableBagSettings(boolean b) {
    	bagPayloadTree.setEnabled(b);
    	bagPayloadTreePanel.setEnabled(b);
    	bagTagFileTree.setEnabled(b);
    	bagTagFileTreePanel.setEnabled(b);
        projectList.setEnabled(b);
    	bagInfoInputPane.setEnabled(b);
        defaultProject.setEnabled(b);
        holeyCheckbox.setEnabled(b);
        serializeGroupPanel.setEnabled(b);
        zipButton.setEnabled(b);
        tarButton.setEnabled(b);
        noneButton.setEnabled(b);
    }

    public void buildConstraints(GridBagConstraints gbc,int x, int y, int w, int h, int wx, int wy, int fill, int anchor) {
    	gbc.gridx = x; // start cell in a row
    	gbc.gridy = y; // start cell in a column
    	gbc.gridwidth = w; // how many column does the control occupy in the row
    	gbc.gridheight = h; // how many column does the control occupy in the column
    	gbc.weightx = wx; // relative horizontal size
    	gbc.weighty = wy; // relative vertical size
    	gbc.fill = fill; // the way how the control fills cells
    	gbc.anchor = anchor; // alignment
    }
    
    public void initializeProfile() {
   		userProjects = bagger.getProjects();
   		Object[] projectArray = userProjects.toArray();
    	Project bagProject = bag.getProject();
    	if (bagProject == null) {
    		for (int i=0; i < projectArray.length; i++) {
        		bagProject = (Project) projectArray[i];
        		if (bagProject.getIsDefault()) {
            		bag.setProject(bagProject);
            		break;
        		}
    		}
    	}
   		Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	if (a != null) this.username = a.getName();
    	else this.username = getPropertyMessage("user.name");
    	if (projectContact == null) {
    		projectContact = new Contact();
    		Organization org = new Organization();
    		projectContact.setOrganization(org);
    	}
    	if (this.username != null && this.username.length() > 0) {
        	Collection<Profile> profiles = bagger.findProfiles(this.username);
        	if (profiles == null) profiles = new ArrayList<Profile>();
        	userProfiles = profiles;
        	Object[] profileArray = profiles.toArray();
        	for (int p=0; p < projectArray.length; p++) {
        		Project project = (Project) projectArray[p];
        		boolean found = false;
            	for (int i=0; i < profileArray.length; i++) {
            		Profile profile = (Profile) profileArray[i];
            		if (project.getId() == profile.getProject().getId()) {
            			found = true;
                   		if (project.getId() == bagProject.getId()) {
                   			// TODO: user is org contact
                       		Organization org = profile.getPerson().getOrganization();
                       		DefaultBagInfo bagInfo = bag.getInfo();
                       		BaggerOrganization bagOrg = bagInfo.getBagOrganization();
                       		bagOrg.setContact(profile.getContact());
                       		bagOrg.setSourceOrganization(org.getName());
                       		bagOrg.setOrganizationAddress(org.getAddress());
                       		bagInfo.setBagOrganization(bagOrg);
                       		bag.setInfo(bagInfo);
                       		projectContact = profile.getPerson();
                   		}
            		}
            	}
            	if (!found) {
            		log.error("initializeProfile - profile does NOT exist: " + project.getId());
            		userProfiles.add(createProfile(project));
            	}
            	if (userProjects == null || userProjects.isEmpty()) {
            		userProjects = bagger.getProjects();
            		Object[] projList = userProjects.toArray();
            		for (int i=0; i < projList.length; i++) {
            			Project proj = (Project) projList[i];
            			userProfiles.add(createProfile(proj));
            		}
            	}
        	}
    	} else {
    		username = getPropertyMessage("user.name");
    		projectContact = new Contact();
    		Organization org = new Organization();
    		projectContact.setOrganization(org);
    		userProfiles = new ArrayList<Profile>();
    		userProjects = bagger.getProjects();
    		Object[] projList = userProjects.toArray();
    		for (int i=0; i < projList.length; i++) {
    			Project project = (Project) projList[i];
    			userProfiles.add(createProfile(project));
    		}
    	}
    }
    
    public String updateProfile() {
    	String message = "";
    	Project project = bag.getProject();
    	if (project == null) return message;
    	Object[] profiles = this.userProfiles.toArray();
    	for (int i=0; i < profiles.length; i++) {
    		Profile profile = (Profile) profiles[i];
    		if (profile.getProject().getId() == project.getId()) {
    			Contact orgContact = bag.getInfo().getBagOrganization().getContact();
    			profile.setContact(orgContact);
    			profile.setContactId(orgContact.getId());
    			profile.setProject(project);
    			profile.setProjectId(project.getId());
    			profile.setPerson(this.projectContact);
    			profile.setUsername(this.username);
    			message = getPropertyMessage("profile.message.changed") + " " + project.getName() + "\n";
    			profiles[i] = profile;
    		}
    	}
    	return message;
    }

    private Profile createProfile(Project project) {
		Profile profile = new Profile();
		profile.setProject(project);
		profile.setProjectId(project.getId());
		profile.setPerson(projectContact);
		profile.setUsername(username);
		Contact contact = new Contact();
		contact.setOrganization(new Organization());
		profile.setContact(contact);
		return profile;
    }

    public String updateBaggerRules() {
        baggerRules.init(bag.getIsEdeposit(), bag.getIsNdnp(), !bag.getIsNoProject(), bag.getIsHoley());
        String messages = "";
        bagInfoInputPane.populateForms(bag, false);
        messages = bagInfoInputPane.updateForms(bag);
        updateBagInfoInputPaneMessages(messages);
        bagInfoInputPane.update(bag);
        bag.updateStrategy();
        
        return messages;
    }

    private class AddTagFileExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	addTagFile();
        }
    }

    public void dropBagTagFile(List<File> files) {
    	if (bagTagFileTree.isEnabled()) {
    		if (files != null) {
    			for (int i=0; i < files.size(); i++) {
    				//log.info("addBagData[" + i + "] " + files.get(i).getName());
    	            bag.addTagFile(files.get(i));
    			}
	        	bagTagFileTree = new BagTree(this, bag.getName(), false);
	            Bag b = bag.getBag();
	            Collection<BagFile> tags = b.getTags();
	            for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
	            	BagFile bf = it.next();
	                bagTagFileTree.addNode(bf.getFilepath());
	            }
	            bagTagFileTreePanel.refresh(bagTagFileTree);
    		}
    	}
    }

    private class RemoveTagFileExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	removeTagFile();
        }
    }

    public void newDefaultBag(File f) {
    	String bagName = "";
    	bag = new DefaultBag(f);
    	if (f == null) {
        	bagName = getPropertyMessage("bag.label.noname");
    	} else {
	    	bagName = f.getName();
	        String fileName = f.getAbsolutePath();
	        bagNameField.setText(fileName);
	        bagNameField.setCaretPosition(fileName.length());
    	}
		bag.setName(bagName);
    }

    private class StartExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	newBag();
        }
    }

    private class StartNewBagHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;
    	private LongTask task;

    	public void actionPerformed(ActionEvent e) {
    		newBag();
       	}
    }

    private void newBag() {
    	String messages = "";
    	bagCount++;

    	bagInfoInputPane.enableForms(bag, true);
    	clearExistingBag(messages);

    	String bagName = getPropertyMessage("bag.label.noname");
		bag.setName(bagName);
        bagNameField.setText(bagName);
        bagNameField.setCaretPosition(bagName.length());
		bag.setRootDir(bagRootPath);
		messages = updateBaggerRules();
    	initializeProfile();
    	bag.copyFormToBag();

        // TODO: populate tag file names into bagTagFileTree
        Bag b = bag.getBag();
    	bagTagFileTree = new BagTree(this, bag.getName(), false);
        Collection<BagFile> tags = b.getTags();
        for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
            bagTagFileTree.addNode(bf.getFilepath());
        }
        bagTagFileTreePanel.refresh(bagTagFileTree);
        showTagButton.setEnabled(true);
    	enableBagSettings(true);
		bag.getInfo().setBag(bag);
		//bag.getInfo().createStandardFieldList(true);
    	bagInfoInputPane.populateForms(bag, true);
        messages += bagInfoInputPane.updateForms(bag);
        updateBagInfoInputPaneMessages(messages);
        bagInfoInputPane.updateSelected(bag);
        compositePane.updateCompositePaneTabs(bag, messages);
        tagManifestPane.updateCompositePaneTabs(bag);

    	bag.setIsNewbag(true);
    	addDataButton.setEnabled(true);
    	addDataExecutor.setEnabled(true);
    	//updatePropButton.setEnabled(false);
    	addTagFileButton.setEnabled(true);
    	removeTagFileButton.setEnabled(true);
    	bagButtonPanel.invalidate();
    }

    private class CreateDataExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	createDataBag();
        }    	
    }

    private class CreateDataBagHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
    	private LongTask task;
    	BagView bagView;
    	DefaultBag bag;

    	public void actionPerformed(ActionEvent e) {
    		this.bag = getBag();
    		createDataBag();
    	}

    	public void setBagView(BagView bagView) {
    		this.bag = bagView.getBag();
    	}

    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
        	while (!task.canceled && !task.done) {
                try {
                    Thread.sleep(1000); //sleep for a second
                    task.current += Math.random() * 100; //make some progress

                    // TODO: execute create data bag task

                	statusBarEnd();
                    task.current++;
                    if (task.current >= task.lengthOfTask) {
                        task.done = true;
                        task.current = task.lengthOfTask;
                    }
                    task.statMessage = "Completed " + task.current +
                                  " out of " + task.lengthOfTask + ".";
                } catch (InterruptedException e) {
                	e.printStackTrace();
                }
            }
    	}
    }

    public void createDataBag() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
		JFileChooser fo = new JFileChooser(selectFile);
		fo.setDialogType(JFileChooser.OPEN_DIALOG);
    	fo.addChoosableFileFilter(noFilter);
		fo.setFileFilter(noFilter);
	    fo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    if (bagRootPath != null) fo.setCurrentDirectory(bagRootPath.getParentFile());
		fo.setDialogTitle("Existing Bag Location");
    	int option = fo.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File data = fo.getSelectedFile();
            createDataBag(bag, data);
        }
    }

    private void createDataBag(DefaultBag bag, File data) {
    	statusBarBegin(createDataBagHandler, "Opening existing bag...", 1);

    	newBag();
    	File bagDirectory = data.getParentFile();
		File bagFile = new File(bagDirectory, bag.getName());
		bagRootPath = bagDirectory;
		bag.setRootDir(bagRootPath);
        String fileName = bagFile.getAbsolutePath();
        bagNameField.setText(fileName);
        bagNameField.setCaretPosition(fileName.length());

        File[] files = data.listFiles();
        if (files != null) {
        	for (int i=0; i < files.length; i++) {
        		log.info("addBagData[" + i + "] " + files[i].getName());
        		if (i < files.length-1) addBagData(files[i], false);
        		else addBagData(files[i], true);
        	}
        }

        saveAsButton.setEnabled(true);
        saveBagAsExecutor.setEnabled(true);
        removeDataButton.setEnabled(true);
        updateTreeButton.setEnabled(true);
        updateTreeExecutor.setEnabled(true);
        bagButtonPanel.invalidate();
        topButtonPanel.invalidate();

        statusBarEnd();
    }

    public class RemoveDataExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	removeData();
        }
    }

    private class RemoveDataHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		removeData();
       	}

    }

    public String removeData() {
    	String message = "";
    	Bag b = bag.getBag();

    	log.debug("Bag payload size pre: " + b.getPayload().size());

    	TreePath[] paths = bagPayloadTree.getSelectionPaths();
    	if (paths != null) {
    		DefaultTreeModel model = (DefaultTreeModel)bagPayloadTree.getModel();
        	for (int i=0; i < paths.length; i++) {
        		TreePath path = paths[i];
        		Object node = path.getLastPathComponent();
        		log.debug("removeData: " + path.toString());
        		log.debug("removeData pathCount: " + path.getPathCount());
        		File filePath = null;
        		String fileName = null;
        		if (path.getPathCount() > 0) {
        			filePath = new File(""+path.getPathComponent(0));
            		for (int j=1; j<path.getPathCount(); j++) {
            			filePath = new File(filePath, ""+path.getPathComponent(j));
            			log.debug("\t" + filePath);
            		}
        		}
    			if (filePath != null) fileName = BaggerFileEntity.normalize(filePath.getPath());
    			log.debug("FilePath: " + fileName);
    			try {
            		if (fileName != null && !fileName.isEmpty()) b.removeBagFile(fileName);
            	    model.removeNodeFromParent((MutableTreeNode)node);
    			} catch (Exception e) {
            	    message += "Error trying to remove file: " + fileName + "\n";
            	    showWarningErrorDialog("Error trying to remove file: " + fileName + "\n" + e.getMessage());
    			}
        	}
        	bagPayloadTree.removeSelectionPaths(paths);
        	bagPayloadTreePanel.refresh(bagPayloadTree);

        	bag.setBag(b);
        	setBag(bag);
    	}

    	return message;
    }

    public class AddDataExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	addData();
        }
    }

    private class AddDataHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
    	private LongTask task;

    	public AddDataHandler() {
    		super();
    	}
    	
    	public void actionPerformed(ActionEvent e) {
    		addData();
       	}
    	
    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
            //Fake a long task,
            //making a random amount of progress every second.
        	display("execute");
        	int i = 0;
        	while (!task.canceled && !task.done) {
                try {
                    Thread.sleep(1000); //sleep for a second
                    //i = task.current;
                    //task.current++;
                    //task.current += Math.random() * 100;
                    // TODO: add data task
                	try {
                    	bag.getBag().addFileToPayload(addBagDataFile);
                		// TODO: make complete after adding files
                    	//if (lastFileFlag) bag.getBag().complete();
                    	bagPayloadTree.addNodes(addBagDataFile);
                    	bagPayloadTree.addTree(parentSrc, addBagDataFile, bag.getRootDir());
                	} catch (Exception e) {
                		showWarningErrorDialog("Error trying to add file: " + addBagDataFile.getAbsolutePath() + "\n" + e.getMessage());
                		log.error("BagView.addBagData: " + addBagDataFile.getAbsolutePath() + " error: " + e.getMessage());
                	}

                    task.current++;
                	statusBarEnd();
                    if (task.current >= task.lengthOfTask) {
                        task.done = true;
                        task.current = task.lengthOfTask;
                    }
                    task.statMessage = "Completed " + task.current +
                                  " out of " + task.lengthOfTask + ".";
                } catch (InterruptedException e) {
                	e.printStackTrace();
                }
            }
    	}
    }

    public void addData() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        JFileChooser fc = new JFileChooser(selectFile);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
    	fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setDialogTitle("Add File or Directory");
    	int option = fc.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File[] files = fc.getSelectedFiles();
            if (files != null && files.length >0) {
            	display("addBagDataFiles");
                addBagData(files);
            } else {
            	File file = fc.getSelectedFile();
            	display("addBagDataFile");
            	addBagData(file, true);
            }
            saveAsButton.setEnabled(true);
            saveBagAsExecutor.setEnabled(true);
            removeDataButton.setEnabled(true);
            updateTreeButton.setEnabled(true);
            updateTreeExecutor.setEnabled(true);
            bagButtonPanel.invalidate();
        	topButtonPanel.invalidate();
        }
    }

    public void addBagData(File[] files) {
    	if (files != null) {
        	for (int i=0; i < files.length; i++) {
        		log.info("addBagData[" + i + "] " + files[i].getName());
        		if (i < files.length-1) addBagData(files[i], false);
        		else addBagData(files[i], true);
        	}
    	}
    }

    public void addBagData(File file, boolean lastFileFlag) {
    	String messages = "";
        parentSrc = file.getParentFile().getAbsoluteFile();
        addBagDataFile = file;
        // TODO: handle an invalid file error
    	statusBarBegin(addDataHandler, "Adding data...", 1);
/*     	try {
        	bag.getBag().addFileToPayload(file);
    		// TODO: make complete after adding files
        	//if (lastFileFlag) bag.getBag().complete();
        	bagPayloadTree.addNodes(file);
        	bagPayloadTree.addTree(parentSrc, file, bag.getRootDir());
    	} catch (Exception e) {
           	writeBagErrorDialog("Error trying to add file: " + addBagDataFile.getAbsolutePath() + "\n" + e.getMessage());
        	messages += "Failed to add file path: " + file.getName() + "\n";
    		log.error("BagView.addBagData: " + file.getAbsolutePath() + " error: " + e.getMessage());
    	}
 */
        //bag.setRootTree(bagPayloadTree.getRootTree());
    	bagPayloadTreePanel.refresh(bagPayloadTree);
        bagInfoInputPane.verifyForms(bag);
    	bagInfoInputPane.populateForms(bag, true);
        bagInfoInputPane.update(bag);
    	messages += getPropertyMessage("bag.message.filesadded") + " " + file.getName() + "\n";
    	compositePane.setBag(bag);
    	compositePane.updateCompositePaneTabs(bag, messages);
    	tagManifestPane.updateCompositePaneTabs(bag);
    }

    public void dropBagPayloadData(List<File> files) {
    	if (bagPayloadTree.isEnabled()) {
        	if (files != null) {
            	for (int i=0; i < files.size(); i++) {
            		//log.info("addBagData[" + i + "] " + files.get(i).getName());
            		if (i < files.size()-1) addBagData(files.get(i), false);
            		else addBagData(files.get(i), true);
            	}
        	}
            saveAsButton.setEnabled(true);
            saveBagAsExecutor.setEnabled(true);
            removeDataButton.setEnabled(true);
            updateTreeButton.setEnabled(true);
            updateTreeExecutor.setEnabled(true);
            bagButtonPanel.invalidate();
        	topButtonPanel.invalidate();
    	}
    }

    public class ValidateExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	validateBag("");
        }
    }

    private class ValidateBagHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
    	private LongTask task;

    	public void actionPerformed(ActionEvent e) {
    		validateBag("");
    	}

    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
        	while (!task.canceled && !task.done) {
                try {
                    Thread.sleep(1000); //sleep for a second
//                    task.current += Math.random() * 100;

                    /* */
            		CompleteVerifierImpl completeVerifier = new CompleteVerifierImpl();
//            		completeVerifier.setCancelIndicator(new DummyCancelIndicator(5));
            		completeVerifier.setCancelIndicator(task);
            		completeVerifier.addProgressListener(new PrintingProgressListener());
            		//assertNull(bag.verify(completeVerifier));
            		
            		ParallelManifestChecksumVerifier manifestVerifier = new ParallelManifestChecksumVerifier();
            		manifestVerifier.setCancelIndicator(task);
            		manifestVerifier.addProgressListener(task);
            		//assertNull(manifestVerifier.verify(bag.getPayloadManifests(), bag));
            		
            		ValidVerifierImpl validVerifier = new ValidVerifierImpl(completeVerifier, manifestVerifier);
            		validVerifier.setCancelIndicator(task);
            		validVerifier.addProgressListener(task);
            		//assertNull(bag.verify(validVerifier));
            		/* */
                    String messages = bag.validateBag(validVerifier);
                    log.debug("ValidateBagHanlder.execute");
                    task.current++;
                    if (task.current >= task.lengthOfTask) {
                        task.done = true;
                        task.current = task.lengthOfTask;
                    }
                    task.statMessage = "Completed " + task.current +
                                  " out of " + task.lengthOfTask + ".";
                } catch (InterruptedException e) {
                	e.printStackTrace();
                }
            	statusBarEnd();
            }
    	}
    }

    private void validateBag(String messages) {
    	messages += bagInfoInputPane.updateForms(bag);
    	updateBagInfoInputPaneMessages(messages);
    	bagInfoInputPane.updateSelected(bag);
    	statusBarBegin(validateBagHandler, "Validating bag...", 1);
    	//String msg = bag.validateBag(validVerifier);

    	setBag(bag);
    	compositePane.updateCompositePaneTabs(bag, messages);
    	tagManifestPane.updateCompositePaneTabs(bag);
    	bagInfoInputPane.update(bag);
    	statusBarEnd();
    }

    public class CompleteExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	completeBag("");
        }
    }

    private class CompleteBagHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
     		completeBag("");
    	}

    }

    public void completeBag(String messages) {
    	ActualTask actualTask = new ActualTask();
    	statusBarBegin(actualTask, "Completing bag...", 1000);
    	messages += bagInfoInputPane.updateForms(bag);
    	updateBagInfoInputPaneMessages(messages);
    	bagInfoInputPane.updateSelected(bag);
		messages += bag.completeBag();
		compositePane.updateCompositePaneTabs(bag, messages);
		tagManifestPane.updateCompositePaneTabs(bag);
        bagInfoInputPane.update(bag);
    	statusBarEnd();
    }

    public class UpdateTreeExecutor extends AbstractActionCommandExecutor {
		public void execute() {
			updateTree();
		}
	}

    private class UpdateTreeHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		updateTree();
       	}
    }

    public void updateTree() {
    	removeData();
    }

    public class SaveBagAsExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	saveBagAs();
        }
    }

	private class SaveBagAsHandler extends AbstractAction {
	   	private static final long serialVersionUID = 1L;
	   	BagView bagView;
	   	
	   	public SaveBagAsHandler(BagView bagView) {
	   		this.bagView = bagView;
	   	}

		public void actionPerformed(ActionEvent e) {
	        saveBagFrame = new SaveBagFrame(bagView, getPropertyMessage("bag.frame.save"));
	        saveBagFrame.setBag(bag);
			saveBagFrame.setVisible(true);
	   	}

	}

    public void saveBagAs() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        JFileChooser fs = new JFileChooser(selectFile);
    	fs.setDialogType(JFileChooser.SAVE_DIALOG);
    	fs.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fs.addChoosableFileFilter(noFilter);
    	fs.addChoosableFileFilter(zipFilter);
        fs.addChoosableFileFilter(tarFilter);
        fs.setDialogTitle("Save Bag As");
    	fs.setCurrentDirectory(bag.getRootDir());
    	if (bag.getName() != null && !bag.getName().equalsIgnoreCase(getPropertyMessage("bag.label.noname"))) {
    		String selectedName = bag.getName();
    		if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
    			selectedName += "."+DefaultBag.ZIP_LABEL;
    			fs.setFileFilter(zipFilter);
    		}
    		else if (bag.getSerialMode() == DefaultBag.TAR_MODE) {
    			selectedName += "."+DefaultBag.TAR_LABEL;
    			fs.setFileFilter(tarFilter);
    		}
    		else {
    			fs.setFileFilter(noFilter);
    		}
    		fs.setSelectedFile(new File(selectedName));
    	}
    	int	option = fs.showSaveDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fs.getSelectedFile();
            save(file);
        }
    }

    public void save(File file) {
        if (file == null) file = bagRootPath;
		File bagFile = new File(file, bag.getName());
    	if (bagFile.exists()) {
    		tmpRootPath = file;
            confirmWriteBag();
    	} else {
        	if (bag.getSize() > DefaultBag.MAX_SIZE) {
        		tmpRootPath = file;
        		confirmAcceptBagSize();
        	} else {
        		bagRootPath = file;
        		saveBag(bagRootPath);
        		if (bag.isSerialized()) saveButton.setEnabled(true);
        		validateButton.setEnabled(true);
        		validateExecutor.setEnabled(true);
        		completeButton.setEnabled(true);
        		completeExecutor.setEnabled(true);
        		topButtonPanel.invalidate();
        	}
    	}
        String fileName = bagFile.getAbsolutePath();
        bagNameField.setText(fileName);
        bagNameField.setCaretPosition(fileName.length());
    }

    private void confirmWriteBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	if (bag.getSize() > DefaultBag.MAX_SIZE) {
	        		confirmAcceptBagSize();
	        	} else {
		        	bagRootPath = tmpRootPath;
		        	saveBag(bagRootPath);
	                validateButton.setEnabled(true);
	                completeButton.setEnabled(true);
	                validateExecutor.setEnabled(true);
	                completeExecutor.setEnabled(true);
	            	topButtonPanel.invalidate();
	            	tmpRootPath = null;
	        	}
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(getPropertyMessage("bag.dialog.title.create"));
	    dialog.setConfirmationMessage(getPropertyMessage("bag.dialog.message.create"));
	    dialog.showDialog();
	}

	private void confirmAcceptBagSize() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	bagRootPath = tmpRootPath;
	        	saveBag(bagRootPath);
	        	validateButton.setEnabled(true);
	        	completeButton.setEnabled(true);
	        	validateExecutor.setEnabled(true);
	        	completeExecutor.setEnabled(true);
	        	topButtonPanel.invalidate();
	        	tmpRootPath = null;
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(getPropertyMessage("bag.dialog.title.create"));
	    dialog.setConfirmationMessage(getPropertyMessage("bag.dialog.message.accept"));
	    dialog.showDialog();
	}

	public class SaveBagExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	saveBag(bagRootPath);
        }
    }

    private class SaveBagHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
       	private LongTask task;

       	public void actionPerformed(ActionEvent e) {
    		saveBag(bagRootPath);
       	}

    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
        	while (!task.canceled && !task.done) {
                try {
                    Thread.sleep(1000); //sleep for a second
//                    task.current += Math.random() * 100;

                    String messages = bag.write(task, task);

            		log.debug("ValidateBagHanlder.execute");
                    task.current++;
                    if (task.current >= task.lengthOfTask) {
                        task.done = true;
                        task.current = task.lengthOfTask;
                    }
                    task.statMessage = "Completed " + task.current +
                                  " out of " + task.lengthOfTask + ".";
                } catch (InterruptedException e) {
        			bag.isSerialized(false);
        			showWarningErrorDialog("Error saving bag: " + bagRootPath + "\n" + e.getMessage());
                	e.printStackTrace();
                } catch (Exception e) {
        			bag.isSerialized(false);
        			showWarningErrorDialog("Error saving bag: " + bagRootPath + "\n" + e.getMessage());
                	e.printStackTrace();
                }
            	statusBarEnd();
            }
    	}
    }

    public void saveBag(File file) {
        String messages = ""; //getMessage("bag.message.creating");
        bag.setRootDir(file);
        messages += bagInfoInputPane.updateForms(bag);
        updateBagInfoInputPaneMessages(messages);
        statusBarBegin(saveBagHandler, "Writing bag...", 1);
/*
        try {
            messages += bag.write(!bagInfoInputPane.hasValidBagForms(bag));
        } catch (Exception e) {
			bag.isSerialized(false);
			messages += "Error creating bag: " + file + "\n" + e.getMessage();
			showWarningErrorDialog("Error creating bag: " + file + "\n" + e.getMessage());
        }
 */
        if (bag.isSerialized()) {
            bagInfoInputPane.populateForms(bag, true);
            bagInfoInputPane.updateSelected(bag);
            bagInfoInputPane.update(bag);
            saveButton.setEnabled(false);
            saveBagExecutor.setEnabled(false);
        }
        compositePane.updateCompositePaneTabs(bag, messages);
        tagManifestPane.updateCompositePaneTabs(bag);
//    	statusBarEnd();
    }

    private void showWarningErrorDialog(String msg) {
    	MessageDialog dialog = new MessageDialog(getPropertyMessage("bag.dialog.error"), msg);
	    dialog.showDialog();
    }

    public void clearExistingBag(String messages) {
    	bagInfoInputPane.enableForms(bag, false);
    	newDefaultBag(null);
    	bag.getInfo().setFieldList(null);
        //projectList.setSelectedItem(getPropertyMessage("bag.project.noproject"));
        holeyCheckbox.setSelected(false);
        this.baggerRules.clear();
    	bag.setIsNewbag(true);
    	bagPayloadTree = new BagTree(this, AbstractBagConstants.DATA_DIRECTORY, true);
    	bagPayloadTreePanel.refresh(bagPayloadTree);
    	bagTagFileTree = new BagTree(this, getPropertyMessage("bag.label.noname"), false);
    	bagTagFileTreePanel.refresh(bagTagFileTree);
    	enableBagSettings(false);
        noneButton.setSelected(true);
        // TODO: clear out bag info form values from existing bag
/*
        bag.setInfo(new DefaultBagInfo(bag));
    	bagInfoInputPane.populateForms(bag, false);
    	messages = bagInfoInputPane.updateForms(bag);
        updateBagInfoInputPaneMessages(messages);
        bagInfoInputPane.update(bag);
        compositePane.updateCompositePaneTabs(bag, messages);
        tagManifestPane.updateCompositePaneTabs(bag);
*/
    	addDataButton.setEnabled(false);
    	addDataExecutor.setEnabled(false);
        updatePropButton.setEnabled(false);
    	saveButton.setEnabled(false);
    	saveBagExecutor.setEnabled(false);
    	saveAsButton.setEnabled(false);
    	saveBagAsExecutor.setEnabled(false);
    	removeDataButton.setEnabled(false);
    	updateTreeButton.setEnabled(false);
    	updateTreeExecutor.setEnabled(false);
    	showTagButton.setEnabled(false);
    	addTagFileButton.setEnabled(false);
    	removeTagFileButton.setEnabled(false);
    	validateButton.setEnabled(false);
    	completeButton.setEnabled(false);
    	validateExecutor.setEnabled(false);
    	completeExecutor.setEnabled(false);
    	bagButtonPanel.invalidate();
    	topButtonPanel.invalidate();
    }
    
    public void updateBagInfo() {
    	log.debug("updateBagInfo");
    	String messages = bagInfoInputPane.verifyForms(bag);
        updateBagInfoInputPaneMessages(messages);
        compositePane.updateCompositePaneTabs(bag, messages);
        tagManifestPane.updateCompositePaneTabs(bag);
        bag.updateBagInfo();
        //updatePropButton.setEnabled(false);
    }

    public void updateBagFetchTxt() {
        String messages = bagInfoInputPane.updateForms(bag);
        updateBagInfoInputPaneMessages(messages);
        bagInfoInputPane.updateSelected(bag);
        messages += updateProfile();
        compositePane.updateCompositePaneTabs(bag, messages);
        tagManifestPane.updateCompositePaneTabs(bag);
        bag.updateFetchTxt();
        //updatePropButton.setEnabled(false);
    }

    public void updateBagInfoInputPaneMessages(String messages) {
		boolean isMessage = true;
		display("updateMessages: " + messages);
        if (!bagInfoInputPane.hasFormErrors(bag) && isMessage) {
            messages += getPropertyMessage("bag.message.info.update") + "\n";
            infoFormMessagePane.setBackground(infoColor);
            infoFormMessagePane.setMessage("");
            updatePropButton.setEnabled(true);
        } else {
            infoFormMessagePane.setBackground(errorColor);
            infoFormMessagePane.setMessage(getPropertyMessage("error.form"));
        }
        updatePropButton.invalidate();
        infoFormMessagePane.invalidate();
    }

    private void updateCommands() {
    	startExecutor.setEnabled(true);
    	openExecutor.setEnabled(true);
    	createDataExecutor.setEnabled(true);
        validateExecutor.setEnabled(false);
        completeExecutor.setEnabled(false);
        addDataExecutor.setEnabled(false);
        removeDataExecutor.setEnabled(false);
        saveBagExecutor.setEnabled(false);
        saveBagAsExecutor.setEnabled(false);
        updateTreeExecutor.setEnabled(false);
        addTagFileExecutor.setEnabled(false);
        removeTagFileExecutor.setEnabled(false);
        saveProfileExecutor.setEnabled(true);
    }

    protected void registerLocalCommandExecutors(PageComponentContext context) {
    	context.register("startCommand", startExecutor);
    	context.register("openCommand", openExecutor);
    	context.register("createDataCommand", createDataExecutor);
    	context.register("validateCommand", validateExecutor);
    	context.register("completeCommand", completeExecutor);
    	context.register("addDataCommand", addDataExecutor);
    	context.register("removeDataCommand", removeDataExecutor);
    	context.register("saveBagCommand", saveBagExecutor);
    	context.register("saveBagAsCommand", saveBagAsExecutor);
    	context.register("updateTreeCommand", updateTreeExecutor);
    	context.register("addTagFileCommand", addTagFileExecutor);
    	context.register("removeTagFileCommand", removeTagFileExecutor);
    	context.register("saveProfileCommand", saveProfileExecutor);
    }

    private class SaveProfileExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	saveProfile();
        }    	
    }

    private void saveProfile() {
    	bagger.storeBaggerUpdates(userProfiles, userHomeDir);
		String message = getPropertyMessage("profile.message.saved") + " " + bag.getProject().getName() + "\n";
		display("SaveProfileExecutor: " + message);
		compositePane.updateCompositePaneTabs(bag, message);
		tagManifestPane.updateCompositePaneTabs(bag);
    }

    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            progressMonitor.setProgress(task.getCurrent());
            String s = task.getMessage();
            if (s != null) {
                progressMonitor.setNote(s);
                taskOutput.append(s + "\n");
                taskOutput.setCaretPosition(
                    taskOutput.getDocument().getLength());
            }
            if (progressMonitor.isCanceled() || task.isDone()) {
                progressMonitor.close();
                task.stop();
                Toolkit.getDefaultToolkit().beep();
                timer.stop();
                if (task.isDone()) {
                    taskOutput.append("Task completed.\n");
                } else {
                    taskOutput.append("Task canceled.\n");

                }
            }
        }
    }

    private class OpenExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	openBag();
        }    	
    }
    
    private class OpenBagHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		openBag();
    	}

    }

    private void openBag() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
		JFileChooser fo = new JFileChooser(selectFile);
		fo.setDialogType(JFileChooser.OPEN_DIALOG);
    	fo.addChoosableFileFilter(noFilter);
    	fo.addChoosableFileFilter(zipFilter);
        fo.addChoosableFileFilter(tarFilter);
		fo.setFileFilter(noFilter);
	    fo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    if (bagRootPath != null) fo.setCurrentDirectory(bagRootPath.getParentFile());
		fo.setDialogTitle("Existing Bag Location");
    	int option = fo.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fo.getSelectedFile();
            if (file == null) file = bagRootPath;
            openExistingBag(file);
            addDataButton.setEnabled(true);
            addDataExecutor.setEnabled(true);
            updatePropButton.setEnabled(false);
            saveButton.setEnabled(true);
            saveBagExecutor.setEnabled(true);
            saveAsButton.setEnabled(true);
            removeDataExecutor.setEnabled(true);
            removeDataButton.setEnabled(true);
            addTagFileButton.setEnabled(true);
            removeTagFileButton.setEnabled(true);
            showTagButton.setEnabled(true);
            saveBagAsExecutor.setEnabled(true);
            updateTreeButton.setEnabled(true);
            updateTreeExecutor.setEnabled(true);
            bagButtonPanel.invalidate();
            validateButton.setEnabled(true);
            completeButton.setEnabled(true);
            validateExecutor.setEnabled(true);
            topButtonPanel.invalidate();
            bag.setIsNewbag(false);
        }
    }

    private void openExistingBag(File file) {
    	ActualTask actualTask = new ActualTask();
    	statusBarBegin(actualTask, "Opening existing bag...", 1);
    	String messages = "";
    	bagInfoInputPane.enableForms(bag, true);
    	clearExistingBag(messages);
    	messages = "";

		try {
	    	newDefaultBag(file);
		} catch (Exception ex) {
	    	newDefaultBag(null);
			log.error("openExistingBag DefaultBag: " + ex.getMessage());
        	messages +=  "Failed to create bag: " + ex.getMessage() + "\n";
		}
        bagVersion = bag.getVersion();
        bagVersionList.setSelectedItem(bagVersion);
        String fileName = file.getAbsolutePath();
        bagNameField.setText(fileName);
        bagNameField.setCaretPosition(fileName.length());
        bagNameField.invalidate();

		bag.getInfo().createExistingFieldList(true);
    	bag.copyBagToForm();
    	if (bag.getInfo().getBagSize().isEmpty()) {
        	bag.setSize(bag.getDataSize());
    	}
	    if (bag.getIsHoley()) {
	        holeyCheckbox.setSelected(true);
	    }
	    if (!bag.getInfo().getLcProject().isEmpty()){
    		messages += updateProject(bag.getInfo().getLcProject());
    		bag.setIsNoProject(false);
    	} else {
    		messages += updateProject(getPropertyMessage("bag.project.noproject"));
    		bag.setIsNoProject(true);
    	}
	    if (bag.getProject() != null && bag.getProject().getIsDefault()) {
	    	defaultProject.setSelected(true);
	    } else {
	    	defaultProject.setSelected(false);
	    }
		messages = updateBaggerRules();

    	String s = file.getName();
		noneButton.setSelected(true);
	    int i = s.lastIndexOf('.');
	    if (i > 0 && i < s.length() - 1) {
		      if (s.substring(i + 1).toLowerCase().equals(DefaultBag.TAR_LABEL)) {
		    	  tarButton.setSelected(true);
		    	  bag.setSerialMode(DefaultBag.TAR_MODE);
		    	  bag.setIsSerial(true);
		      } else if (s.substring(i + 1).toLowerCase().equals(DefaultBag.ZIP_LABEL)) {
		    	  zipButton.setSelected(true);
		    	  bag.setSerialMode(DefaultBag.ZIP_MODE);
		    	  bag.setIsSerial(true);
		      }
	    }

    	bagRootPath = file;
    	bag.setRootDir(bagRootPath);
		File rootSrc = new File(file, bag.getDataDirectory());
    	if (bag.getBag().getFetchTxt() != null) {
    		rootSrc = new File(file, bag.getBag().getFetchTxt().getFilepath());
    	} else {
    		rootSrc = new File(file, bag.getDataDirectory());
    	}
    	bagPayloadTree = new BagTree(this, AbstractBagConstants.DATA_DIRECTORY, true);
		bagPayloadTree.addParentNode(rootSrc);
		bagPayloadTree.populateNodes(bag, rootSrc);
        bagPayloadTreePanel.refresh(bagPayloadTree);
        Bag b = bag.getBag();
        Collection<BagFile> payload = b.getPayload();
        log.debug("Payload size: " + payload.size());
        for (Iterator<BagFile> it=payload.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
        	log.debug("" + bf.getFilepath());
        }

        bagTagFileTree = new BagTree(this, bag.getName(), false);
        Collection<BagFile> tags = b.getTags();
        for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
            bagTagFileTree.addNode(bf.getFilepath());
        }
        bagTagFileTreePanel.refresh(bagTagFileTree);
    	enableBagSettings(true);

    	messages += bag.validateForms(!bagInfoInputPane.hasValidBagForms(bag));
		messages += bag.completeBag();
		messages += bag.validateMetadata();
		bag.isSerialized(true);
		validateBag(messages);

		bag.getInfo().setBag(bag);
    	bagInfoInputPane.populateForms(bag, true);
    	messages += bagInfoInputPane.updateForms(bag);
    	updateBagInfoInputPaneMessages(messages);
   		bagInfoInputPane.updateSelected(bag);
        compositePane.updateCompositePaneTabs(bag, messages);
   		tagManifestPane.updateCompositePaneTabs(bag);

    	statusBarEnd();
    }

    private class ShowTagFilesHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		showTagFiles();
       	}
    }
    
    public void showTagFiles() {
    	tagManifestPane.updateCompositePaneTabs(bag);
    	tagFilesFrame.addComponents(tagManifestPane);
    	tagFilesFrame.setVisible(true);
    	tagFilesFrame.setAlwaysOnTop(true);
    }

    private class UpdateTagTreeHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		updateTagTree();
       	}
    }

    public void updateTagTree() {
    	tagManifestPane.updateCompositePaneTabs(bag);
    }

    private class AddTagFileHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		addTagFile();
       	}
    }

    public void addTagFile() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
		JFileChooser fo = new JFileChooser(selectFile);
		fo.setDialogType(JFileChooser.OPEN_DIALOG);
	    fo.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    if (bagRootPath != null) fo.setCurrentDirectory(bagRootPath.getParentFile());
		fo.setDialogTitle("Tag File Chooser");
    	int option = fo.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fo.getSelectedFile();
            bag.addTagFile(file);
        	bagTagFileTree = new BagTree(this, bag.getName(), false);
            Bag b = bag.getBag();
            Collection<BagFile> tags = b.getTags();
            for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
            	BagFile bf = it.next();
                bagTagFileTree.addNode(bf.getFilepath());
            }
            bagTagFileTreePanel.refresh(bagTagFileTree);
        }
    }

    private class RemoveTagFileHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		removeTagFile();
       	}
    }

    public void removeTagFile() {
    	String message = "";
    	Bag b = bag.getBag();

    	TreePath[] paths = bagTagFileTree.getSelectionPaths();
    	if (paths != null) {
    		DefaultTreeModel model = (DefaultTreeModel)bagTagFileTree.getModel();
        	for (int i=0; i < paths.length; i++) {
        		TreePath path = paths[i];
        		Object node = path.getLastPathComponent();
    			try {
            		if (node != null) b.removeBagFile((String)node);
            	    model.removeNodeFromParent((MutableTreeNode)node);
    			} catch (Exception e) {
            	    message += "Error trying to remove file: " + node + "\n";
            	    showWarningErrorDialog("Error trying to remove file: " + node + "\n" + e.getMessage());
    			}
        	}
        	bagTagFileTree.removeSelectionPaths(paths);
        	bagTagFileTreePanel.refresh(bagTagFileTree);
    	}
    }

    public String updateProject(String projectName) {
    	String messages = "";

   		Object[] projectArray = userProjects.toArray();
   		for (int i=0; i < projectArray.length; i++) {
   			Project bagProject = (Project) projectArray[i];
   			if (projectName != null && projectName.matches(bagProject.getName())) {
   				bag.setProject(bagProject);
   			}
   		}
   		messages += updateProfile();
    	if (projectName.equalsIgnoreCase(getPropertyMessage("bag.project.noproject"))) {
    		bag.setIsNoProject(true);
    	} else if (bag.getIsEdeposit()) {
    		projectList.setSelectedItem(projectName);
    		bag.setIsNoProject(false);
    	} else if (bag.getIsNdnp()) {
    		projectList.setSelectedItem(projectName);
    		bag.setIsNoProject(false);
    	} else {
      		bag.setIsNoProject(false);
    	}
    	setBag(bag);
		return messages;
    }

    public void onApplicationEvent(ApplicationEvent e) {
        if (e instanceof LifecycleApplicationEvent) {
        	//display("onApplicationEvent");
            LifecycleApplicationEvent le = (LifecycleApplicationEvent)e;
            if (le.getEventType() == LifecycleApplicationEvent.CREATED && le.objectIs(Profile.class)) {
            	// TODO: Add and insert the newly created profile from wizard
            	Profile profile = (Profile) le.getObject();
            	userProfiles.add(profile);
            }
        }
    }

    private String getInitialConsoleMsg() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(getPropertyMessage("consolepane.msg.help"));
    	buffer.append("\n\n");
    	buffer.append(getPropertyMessage("compositePane.message.isMetadata"));
    	buffer.append("\n");
    	buffer.append(getPropertyMessage("consolepane.ismetadata.help"));
    	buffer.append("\n\n");
    	buffer.append(getPropertyMessage("compositePane.message.isComplete"));
    	buffer.append("\n");
    	buffer.append(getPropertyMessage("consolepane.iscomplete.help"));
    	buffer.append("\n\n");
    	buffer.append(getPropertyMessage("compositePane.message.isSerialized"));
    	buffer.append("\n");
    	buffer.append(getPropertyMessage("consolepane.isserialized.help"));
    	buffer.append("\n\n");
    	buffer.append(getPropertyMessage("compositePane.message.isValid"));
    	buffer.append("\n");
    	buffer.append(getPropertyMessage("consolepane.isvalid.help"));
    	return buffer.toString();
    }

    public void statusBarBegin(Progress progress, String message, int size) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        task = new LongTask();
        task.setLengthOfTask(size);
        progress.setTask(task);
        task.setProgress(progress);

        taskOutput = new JTextArea(5, 20);
        timer = new Timer(ONE_SECOND, new TimerListener());

        progressMonitor = new ProgressMonitor(this.getControl(),
                "Running a Long Task",
                "", 0, task.getLengthOfTask());
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(1 * ONE_SECOND);

        task.go();
        timer.start();
    }
    
    public void statusBarEnd() {
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());    	
    }

}
