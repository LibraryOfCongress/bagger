
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.ProjectBagInfo;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.ProjectProfile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerFileEntity;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.BaggerProfile;
import gov.loc.repository.bagger.domain.BaggerValidationRulesSource;
import gov.loc.repository.bagger.ui.handlers.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Cancellable;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.writer.Writer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
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
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
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
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.image.ImageSource;
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
    public ProgressMonitor progressMonitor;
    private JTextArea taskOutput;
    private Timer timer;
    public LongTask task;
    public Cancellable longRunningProcess = null;
    public Writer bagWriter = null;

	private Bagger bagger;
    private DefaultBag bag;
    public BaggerValidationRulesSource baggerRules;
    public int bagCount = 0;
    public boolean clearAfterSaving = false;
    public File bagRootPath;
    public File tmpRootPath;
    public File parentSrc;
    public Collection<Project> userProjects;
    public Collection<Profile> userProfiles;
    public Collection<ProjectProfile> userProjectProfiles;
    public BaggerProfile baggerProfile = new BaggerProfile();
    private ProjectBagInfo projectBagInfo = new ProjectBagInfo();
    public String username;
    public Contact projectContact;
	private String userHomeDir;
	public boolean validateOnSave = false;

	public BagTree bagPayloadTree;
	public BagTreePanel bagPayloadTreePanel;
	public BagTree bagTagFileTree;
	public BagTreePanel bagTagFileTreePanel;
	public CompositePane compositePane;
	public TagFilesFrame tagFilesFrame;
	public TagManifestPane tagManifestPane;
	public BagInfoInputPane bagInfoInputPane;
	public JPanel bagSettingsPanel;
	public InfoFormsPane infoInputPane;
	public JPanel bagButtonPanel;
	public JPanel bagTagButtonPanel;
	public JPanel bagPanel;
	public JPanel topButtonPanel;
	public JButton openButton;
	public JButton createSkeletonButton;
	public JButton addDataButton;
    public JButton removeDataButton;
    public JButton saveButton;
    public JButton saveAsButton;
    public JButton completeButton;
    public JButton validateButton;
    public JButton closeButton;
    public JButton showTagButton;
    public JButton addTagFileButton;
    public JButton removeTagFileButton;
    public JButton updateTagTreeButton;
    public JButton updatePropButton;
    public SaveBagFrame saveBagFrame;
    public NewBagFrame newBagFrame;
    public NewBagInPlaceFrame newBagInPlaceFrame;
    public JTextField bagNameField;
    public JComboBox bagVersionList;
    public JLabel bagVersionValue = new JLabel(Version.V0_96.versionString);
    public JComboBox projectList;
    public JButton newProjectButton;
    public JCheckBox defaultProject;
    public JCheckBox holeyCheckbox;
    public JLabel holeyValue;
    public JLabel serializeLabel;
    public JPanel serializeGroupPanel;
    public JLabel serializeValue;
    public JRadioButton noneButton;
    public JRadioButton zipButton;
    public JRadioButton tarButton;
    public JRadioButton tarGzButton;
    public JRadioButton tarBz2Button;
    public FileFilter noFilter;
    public FileFilter zipFilter;
    public FileFilter tarFilter;

    public CreateBagInPlaceHandler createBagInPlaceHandler;
    public CreateBagInPlaceExecutor createBagInPlaceExecutor = new CreateBagInPlaceExecutor(this);
    public ClearBagHandler clearBagHandler;
    public ClearBagExecutor clearExecutor = new ClearBagExecutor(this);
    public ValidateBagHandler validateBagHandler;
    public ValidateExecutor validateExecutor = new ValidateExecutor(this);
    public CompleteBagHandler completeBagHandler;
    public CompleteExecutor completeExecutor = new CompleteExecutor(this);
    public RemoveDataHandler removeDataHandler;
    public RemoveTagFileHandler removeTagFileHandler;
    public AddDataHandler addDataHandler;
    public AddDataExecutor addDataExecutor = new AddDataExecutor();
    public AddTagFileHandler addTagFileHandler;
    public SaveBagHandler saveBagHandler;
    public SaveBagExecutor saveBagExecutor = new SaveBagExecutor(this);
    public StartNewBagHandler startNewBagHandler;
	public StartExecutor startExecutor = new StartExecutor(this);
	public OpenBagHandler openBagHandler;
    public OpenExecutor openExecutor = new OpenExecutor(this);
    public SaveBagAsHandler saveBagAsHandler;
    public SaveBagAsExecutor saveBagAsExecutor = new SaveBagAsExecutor(this);

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
        this.bag = baggerBag;
        this.bag.getInfo().setBag(baggerBag);
    }

    public DefaultBag getBag() {
        return this.bag;
    }
    
    public void setBaggerProfile(BaggerProfile profile) {
    	this.baggerProfile = profile;
    }
    
    public BaggerProfile getBaggerProfile() {
    	return this.baggerProfile;
    }
    
    public void setProjectBagInfo(ProjectBagInfo projBagInfo) {
    	this.projectBagInfo = projBagInfo;
    }
    
    public ProjectBagInfo getProjectBagInfo() {
    	return this.projectBagInfo;
    }
    
    public boolean projectExists(Project project) {
    	Collection<Project> projectList = this.userProjects;
		for (Iterator<Project> iter = projectList.iterator(); iter.hasNext();) {
			Project p = (Project) iter.next();
			if (p.getName().equalsIgnoreCase(project.getName())) {
				return true;
			}
		}
    	return false;
    }

    public void addProject(Project project) {
    	this.userProjects.add(project);

    	projectList.addItem(project.getName());
    	projectList.invalidate();
    	this.updateProject(project.getName());
    	bagger.storeProject(project);
    	bag.setProject(project);
    	bag.getInfo().setLcProject(project.getName());
    	ProjectProfile projectProfile = new ProjectProfile();
    	projectProfile.setProjectId(project.getId());
    	projectProfile.setFieldName(DefaultBagInfo.FIELD_LC_PROJECT);
    	projectProfile.setFieldValue(bag.getInfo().getLcProject());
    	projectProfile.setIsRequired(true);
    	projectProfile.setIsValueRequired(true);
    	userProjectProfiles.add(projectProfile);
		baggerProfile.addField(projectProfile.getFieldName(), projectProfile.getFieldValue(), projectProfile.getIsRequired(), !projectProfile.getIsValueRequired(), false);
		this.bagInfoInputPane.updateProject(this);
		this.bagInfoInputPane.populateForms(bag, true);
    }

    public void addProjectField(BagInfoField field) {
    	if (field.isRequired() || field.isRequiredvalue() || !field.getValue().trim().isEmpty()) {
        	log.debug("BagView.addProjectField: " + field);
    		Project project = bag.getProject();
    		ProjectProfile projectProfile = new ProjectProfile();
	    	projectProfile.setProjectId(project.getId());
	    	projectProfile.setFieldName(field.getLabel());
	    	projectProfile.setFieldValue(field.getValue());
	    	projectProfile.setIsRequired(field.isRequired());
	    	projectProfile.setIsValueRequired(field.isRequiredvalue());
	    	userProjectProfiles.add(projectProfile);
			baggerProfile.addField(projectProfile.getFieldName(), projectProfile.getFieldValue(), projectProfile.getIsRequired(), !projectProfile.getIsValueRequired(), false);
    	}
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

	public ImageIcon getPropertyImage(String name) {
		ImageSource source = this.getImageSource();
        Image image = source.getImage(name);
        ImageIcon imageIcon = new ImageIcon(image);
        return imageIcon;
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
		
    	Color bgColor = new Color(20,20,100);
    	topButtonPanel = createTopButtonPanel();
    	topButtonPanel.setBackground(bgColor);

    	clearBagHandler.newDefaultBag(null);
    	initializeProfile();
    	updateCommands();

    	infoInputPane = new InfoFormsPane(this);
    	enableSettings(false);
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
    	startNewBagHandler = new StartNewBagHandler(this);
    	createButton.addActionListener(startNewBagHandler);
    	createButton.setOpaque(true);
    	createButton.setBackground(bgColor);
    	createButton.setForeground(fgColor);
    	createButton.setToolTipText(getPropertyMessage("bag.button.create.help"));
    	buttonPanel.add(createButton);

    	openButton = new JButton(getPropertyMessage("bag.button.open"));
    	openBagHandler = new OpenBagHandler(this);
    	openButton.addActionListener(openBagHandler);
    	openButton.setEnabled(true);
    	openButton.setOpaque(true);
    	openButton.setBackground(bgColor);
    	openButton.setForeground(fgColor);
    	openButton.setToolTipText(getPropertyMessage("bag.button.open.help"));
    	buttonPanel.add(openButton);

    	createSkeletonButton = new JButton(getPropertyMessage("bag.button.createskeleton"));
    	createBagInPlaceHandler = new CreateBagInPlaceHandler(this);
    	createSkeletonButton.addActionListener(createBagInPlaceHandler);
    	createSkeletonButton.setEnabled(true);
    	createSkeletonButton.setOpaque(true);
    	createSkeletonButton.setBackground(bgColor);
    	createSkeletonButton.setForeground(fgColor);
    	createSkeletonButton.setToolTipText(getPropertyMessage("bag.button.createskeleton.help"));
    	buttonPanel.add(createSkeletonButton);

        saveButton = new JButton(getPropertyMessage("bag.button.save"));
        saveBagHandler = new SaveBagHandler(this);
        saveButton.addActionListener(saveBagHandler);
        saveButton.setEnabled(false);
        saveButton.setOpaque(true);
        saveButton.setBackground(bgColor);
        saveButton.setForeground(fgColor);
        saveButton.setToolTipText(getPropertyMessage("bag.button.save.help"));
        buttonPanel.add(saveButton);

    	saveAsButton = new JButton(getPropertyMessage("bag.button.saveas"));
    	saveBagAsHandler = new SaveBagAsHandler(this);
    	saveAsButton.addActionListener(saveBagAsHandler);
        saveAsButton.setEnabled(false);
        saveAsButton.setOpaque(true);
        saveAsButton.setBackground(bgColor);
        saveAsButton.setForeground(fgColor);
        saveAsButton.setToolTipText(getPropertyMessage("bag.button.saveas.help"));
        saveBagFrame = new SaveBagFrame(this, getPropertyMessage("bag.frame.save"));
        buttonPanel.add(saveAsButton);

        completeButton = new JButton(getPropertyMessage("bag.button.complete"));
    	completeBagHandler = new CompleteBagHandler(this);
        completeButton.addActionListener(completeBagHandler);
        completeButton.setEnabled(false);
        completeButton.setOpaque(true);
        completeButton.setBackground(bgColor);
        completeButton.setForeground(fgColor);
        completeButton.setToolTipText(getPropertyMessage("bag.button.complete.help"));
        buttonPanel.add(completeButton);
        
        validateButton = new JButton(getPropertyMessage("bag.button.validate"));
    	validateBagHandler = new ValidateBagHandler(this);
        validateButton.addActionListener(validateBagHandler);
        validateButton.setEnabled(false);
        validateButton.setOpaque(true);
    	validateButton.setBackground(bgColor);
    	validateButton.setForeground(fgColor);
    	validateButton.setToolTipText(getPropertyMessage("bag.button.validate.help"));
        buttonPanel.add(validateButton);
        
        closeButton = new JButton(getPropertyMessage("bag.button.clear"));
    	clearBagHandler = new ClearBagHandler(this);
    	closeButton.addActionListener(clearBagHandler);
    	closeButton.setEnabled(false);
    	closeButton.setOpaque(true);
    	closeButton.setBackground(bgColor);
    	closeButton.setForeground(fgColor);
    	closeButton.setToolTipText(getPropertyMessage("bag.button.clear.help"));
        buttonPanel.add(closeButton);

        return buttonPanel;
    }
    
    private JPanel createBagPanel() {
    	EmptyBorder border = new EmptyBorder(2, 1, 5, 1);
    	bagButtonPanel = createBagButtonPanel();

    	bagTagButtonPanel = createBagTagButtonPanel();

    	bagPayloadTree = new BagTree(this, AbstractBagConstants.DATA_DIRECTORY, true);
    	bagPayloadTree.setEnabled(false);
    	bagPayloadTreePanel = new BagTreePanel(bagPayloadTree);
    	bagPayloadTreePanel.setEnabled(false);
    	bagPayloadTreePanel.setBorder(border);
    	bagPayloadTreePanel.setToolTipText(getPropertyMessage("bagTree.help"));

    	bagTagFileTree = new BagTree(this, getPropertyMessage("bag.label.noname"), false);
    	bagTagFileTree.setEnabled(false);
    	bagTagFileTreePanel = new BagTreePanel(bagTagFileTree);
    	bagTagFileTreePanel.setEnabled(false);
    	bagTagFileTreePanel.setBorder(border);
    	bagTagFileTreePanel.setToolTipText(getPropertyMessage("bagTree.help"));

    	tagManifestPane = new TagManifestPane(this);
    	tagManifestPane.setToolTipText(getPropertyMessage("compositePane.tab.help"));
		tagFilesFrame = new TagFilesFrame(getActiveWindow().getControl(), getPropertyMessage("bagView.tagFrame.title"));
		tagFilesFrame.addComponents(tagManifestPane);
    	
    	compositePane = new CompositePane(this, getInitialConsoleMsg());
    	compositePane.setToolTipText(getPropertyMessage("compositePane.tab.help"));

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();
        int row = 0;
        int colspan = 1;
        bagPanel = new JPanel(layout);

        buildConstraints(glbc, 0, row, colspan, 1, 1, 10, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagButtonPanel, glbc);
        bagPanel.add(bagButtonPanel);
        buildConstraints(glbc, 1, row, colspan, 1, 90, 40, GridBagConstraints.BOTH, GridBagConstraints.EAST);
        layout.setConstraints(bagPayloadTreePanel, glbc);
    	bagPanel.add(bagPayloadTreePanel);
        row++;
        buildConstraints(glbc, 0, row, colspan, 1, 1, 10, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagTagButtonPanel, glbc);
    	bagPanel.add(bagTagButtonPanel);
        buildConstraints(glbc, 1, row, colspan, 1, 90, 20, GridBagConstraints.BOTH, GridBagConstraints.EAST);
        layout.setConstraints(bagTagFileTreePanel, glbc);
    	bagPanel.add(bagTagFileTreePanel);
        row++;
        colspan = 3;
        buildConstraints(glbc, 0, row, colspan, 1, 1, 30, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(compositePane, glbc);
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

    	removeDataButton = new JButton(getPropertyMessage("bag.button.remove"));
    	removeDataHandler = new RemoveDataHandler(this);
    	removeDataButton.addActionListener(removeDataHandler);
    	removeDataButton.setEnabled(false);
    	removeDataButton.setToolTipText(getPropertyMessage("bag.button.remove.help"));
    	buttonPanel.add(removeDataButton, BorderLayout.CENTER);

        return buttonPanel;
    }
    
    private JPanel createBagTagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));

    	showTagButton = new JButton(getPropertyMessage("bag.tagbutton.show"));
    	showTagButton.addActionListener(new ShowTagFilesHandler(this));
    	showTagButton.setEnabled(false);
    	showTagButton.setToolTipText(getPropertyMessage("bag.tagbutton.show.help"));
    	buttonPanel.add(showTagButton, BorderLayout.NORTH);

    	addTagFileButton = new JButton(getPropertyMessage("bag.tagbutton.add"));
    	addTagFileHandler = new AddTagFileHandler(this);
    	addTagFileButton.addActionListener(addTagFileHandler);
    	addTagFileButton.setEnabled(false);
    	addTagFileButton.setToolTipText(getPropertyMessage("bag.tagbutton.add.help"));
    	buttonPanel.add(addTagFileButton, BorderLayout.CENTER);
    	
    	removeTagFileButton = new JButton(getPropertyMessage("bag.tagbutton.remove"));
    	removeTagFileHandler = new RemoveTagFileHandler(this);
    	removeTagFileButton.addActionListener(removeTagFileHandler);
    	removeTagFileButton.setEnabled(false);
    	removeTagFileButton.setToolTipText(getPropertyMessage("bag.tagbutton.remove.help"));
    	buttonPanel.add(removeTagFileButton, BorderLayout.SOUTH);

        return buttonPanel;
    }

    public void enableBagSettings(boolean b) {
    	bagPayloadTree.setEnabled(b);
    	bagPayloadTreePanel.setEnabled(b);
    	bagTagFileTree.setEnabled(b);
    	bagTagFileTreePanel.setEnabled(b);
        projectList.setEnabled(b);
        newProjectButton.setEnabled(b);
    	bagInfoInputPane.setEnabled(b);
        defaultProject.setEnabled(b);
        infoInputPane.saveButton.setEnabled(b);
        infoInputPane.loadDefaultsButton.setEnabled(b);
        infoInputPane.clearDefaultsButton.setEnabled(b);
        holeyCheckbox.setEnabled(false);
        serializeGroupPanel.setEnabled(false);
        zipButton.setEnabled(false);
        tarButton.setEnabled(false);
        tarGzButton.setEnabled(false);
        tarBz2Button.setEnabled(false);
        noneButton.setEnabled(false);
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
   		log.debug("userProjects: " + userProjects);
   		userProjectProfiles = bagger.getProjectProfiles();
    	Collection<ProjectProfile> projectProfileMap = userProjectProfiles;
		Object[] reqs = bag.getInfo().getRequiredStrings();
		for (Iterator<ProjectProfile> iter = projectProfileMap.iterator(); iter.hasNext();) {
			ProjectProfile projectProfile = (ProjectProfile) iter.next();
			log.debug("initializeProfile: " + projectProfile);
			if (projectProfile.getIsRequired()) {
				if (!bag.getInfo().getRequiredSet().contains(projectProfile.getFieldName())) {
					List<Object> list = new ArrayList<Object>();
					for (int i=0; i < reqs.length; i++) {list.add(reqs[i]);}
					list.add(projectProfile.getFieldName());
					bag.getInfo().setRequiredStrings(list.toArray());
				}
			}
		}

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
            		log.debug("loadProfiles profile: " + profile);
            		if (project.getId() == profile.getProject().getId()) {
            			found = true;
            			log.debug("projectId: " + project.getId() + ", bagId: " + bagProject.getId());
                   		if (project.getId() == bagProject.getId()) {
                   			// TODO: user is org contact
                       		DefaultBagInfo bagInfo = bag.getInfo();
                       		BaggerOrganization bagOrg = bagInfo.getBagOrganization();
                       		bagOrg.setContact(profile.getContact());
                       		Organization contactOrg = profile.getContact().getOrganization();
                       		bagOrg.setSourceOrganization(contactOrg.getName());
                       		bagOrg.setOrganizationAddress(contactOrg.getAddress());
                       		bagInfo.setBagOrganization(bagOrg);
                       		bag.setInfo(bagInfo);
                       		projectContact = profile.getPerson();
                       		baggerProfile.setOrganization(bagOrg);
                	   		baggerProfile.setSourceCountact(profile.getContact());
                       		baggerProfile.setToContact(projectContact);
                       		log.debug("InitProfiles: " + bagOrg);
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
        baggerRules.init(bag.isEdeposit(), bag.isNdnp(), !bag.isNoProject(), bag.isHoley());
        String messages = "";
        bag.updateStrategy();
        
        return messages;
    }

    public void dropBagTagFile(List<File> files) {
    	addTagFileHandler.addTagFiles(files);
    }

    public class AddDataExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	addData();
        }
    }

    private class AddDataHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
    	private LongTask task;

    	public void actionPerformed(ActionEvent e) {
    		addData();
       	}
    	
    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
        	statusBarEnd();
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
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
        	bagPayloadTreePanel.refresh(bagPayloadTree);
        	compositePane.setBag(bag);
        	compositePane.updateCompositePaneTabs(bag, getPropertyMessage("bag.message.filesadded"));

    		saveAsButton.setEnabled(true);
            saveBagAsExecutor.setEnabled(true);
            removeDataButton.setEnabled(true);
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
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        parentSrc = file.getParentFile().getAbsoluteFile();
        try {
        	bag.getBag().addFileToPayload(file);
        	boolean alreadyExists = bagPayloadTree.addNodes(file, false);
        	if (alreadyExists) {
        	    showWarningErrorDialog("Warning - file already exists", "File: " + file.getName() + "\n" + "already exists in bag.");
        	}
        } catch (Exception e) {
        	log.error("BagView.addBagData: " + e);
    	    showWarningErrorDialog("Error - file not added", "Error adding bag file: " + file + "\ndue to:\n" + e.getMessage());
        }
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
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
        	bagPayloadTreePanel.refresh(bagPayloadTree);
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
        	compositePane.setBag(bag);
        	compositePane.updateCompositePaneTabs(bag, getPropertyMessage("bag.message.filesadded"));

            saveAsButton.setEnabled(true);
            saveBagAsExecutor.setEnabled(true);
            removeDataButton.setEnabled(true);
            bagButtonPanel.invalidate();
        	topButtonPanel.invalidate();
    	}
    }

    public void showWarningErrorDialog(String title, String msg) {
    	MessageDialog dialog = new MessageDialog(title, msg);
	    dialog.showDialog();
    }

    public void enableSettings(boolean b) {
        bagNameField.setEnabled(b);
        bagVersionValue.setEnabled(b);
        projectList.setEnabled(b);
        newProjectButton.setEnabled(b);
        holeyValue.setEnabled(b);
        serializeValue.setEnabled(b);
        infoInputPane.saveButton.setEnabled(b);
        infoInputPane.loadDefaultsButton.setEnabled(b);
        infoInputPane.clearDefaultsButton.setEnabled(b);
    }

    private void updateCommands() {
    	startExecutor.setEnabled(true);
    	openExecutor.setEnabled(true);
    	createBagInPlaceExecutor.setEnabled(true);
    	clearExecutor.setEnabled(false);
        validateExecutor.setEnabled(false);
        completeExecutor.setEnabled(false);
        addDataExecutor.setEnabled(false);
        saveBagExecutor.setEnabled(false);
        saveBagAsExecutor.setEnabled(false);
    }

    protected void registerLocalCommandExecutors(PageComponentContext context) {
    	context.register("startCommand", startExecutor);
    	context.register("openCommand", openExecutor);
    	context.register("createBagInPlaceCommand", createBagInPlaceExecutor);
    	context.register("clearCommand", clearExecutor);
    	context.register("validateCommand", validateExecutor);
    	context.register("completeCommand", completeExecutor);
    	context.register("addDataCommand", addDataExecutor);
    	context.register("saveBagCommand", saveBagExecutor);
    	context.register("saveBagAsCommand", saveBagAsExecutor);
    }

    public String loadProfiles() {
    	try {
        	String message = bagger.loadProfiles();
        	this.username = getPropertyMessage("user.name");
        	Project project = bag.getProject();
        	//this.projectBagInfo = bagger.loadProjectBagInfo(project.getId());
        	//bag.parseBagInfoDefaults(projectBagInfo.getDefaults());
        	this.initializeProfile();
        	Object[] array = userProjects.toArray();
        	boolean b = true;
        	for (int i=0; i < userProjects.size(); i++) {
        		String name = ((Project)array[i]).getName();
        		for (int j=0; j < projectList.getModel().getSize(); j++) {
        			String proj = (String) projectList.getModel().getElementAt(j);
            		if (name.trim().equalsIgnoreCase(proj.trim())) {
            			b = false;
            			break;
            		}
        		}
        		if (b) { projectList.addItem(name);	}
        		b = true;
        	}
        	projectList.invalidate();
        	bagInfoInputPane.updateProject(this);
        	bagInfoInputPane.populateForms(bag, true);
            bagInfoInputPane.update(bag);
    		compositePane.updateCompositePaneTabs(bag, message);
        	return message;
    	} catch (Exception e) {
    	    showWarningErrorDialog("Error Dialog", "Error trying to load project defaults:\n" + e.getMessage());
    		return null;
    	}
    }

    public String clearProfiles() {
    	String message = "";
    	ArrayList<Profile> newProfiles = new ArrayList<Profile>();
    	Object[] profiles = userProfiles.toArray();
    	for (int j=0; j < profiles.length; j++) {
    		Profile profile = (Profile) profiles[j];
    		Contact person = new Contact();
    		person.setOrganization(new Organization());
    		profile.setPerson(person);
    		Contact contact = new Contact();
    		contact.setOrganization(new Organization());
    		profile.setContact(contact);
    		newProfiles.add(profile);
    		if (j == 0) {
    			DefaultBagInfo bagInfo = bag.getInfo();
    	   		BaggerOrganization bagOrg = new BaggerOrganization();
    	   		bagOrg.setContact(contact);
    	   		bagInfo.setBagOrganization(bagOrg);
    	   		bag.setInfo(bagInfo);
    	   		projectContact = profile.getPerson();
    	   		baggerProfile.setOrganization(bagOrg);
    	   		baggerProfile.setSourceCountact(profile.getContact());
    	   		baggerProfile.setToContact(projectContact);
    		}
    	}
    	userProfiles = newProfiles;
   		bagInfoInputPane.populateForms(bag, true);
   		bagInfoInputPane.update(bag);
		compositePane.updateCompositePaneTabs(bag, message);
    	return message;
    }

    public String saveProfiles() {
    	try {
        	Project bagProject = bag.getProject();
        	if (bagProject == null) bagProject = new Project();
    		projectBagInfo.setProjectId(bagProject.getId());
    		String defaults = "";
    		HashMap<String, BagInfoField> fieldMap = baggerProfile.getProfileMap();
    		if (fieldMap != null && !fieldMap.isEmpty()) {
    			Set<String> keys = fieldMap.keySet();
    			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
    				String key = (String) iter.next();
    				BagInfoField val = fieldMap.get(key);
    				defaults += key + "=" + val;
    				if (iter.hasNext()) defaults += ", ";
    			}
            }
    		//projectBagInfo.setDefaults(defaults);
    		String messages = bagger.storeBaggerUpdates(userProfiles, userProjects, userProjectProfiles, projectBagInfo, userHomeDir);
    		if (messages != null) {
        	    showWarningErrorDialog("Error Dialog", "Error trying to store project defaults:\n" + messages);
        	    return null;
    		}

    		String message = getPropertyMessage("profile.message.saved") + " " + bag.getProject().getName() + "\n";
    		compositePane.updateCompositePaneTabs(bag, message);
    	    showWarningErrorDialog("Project Defaults Stored", message);
    		return message;
    	} catch (Exception e) {
    	    showWarningErrorDialog("Error Dialog", "Error trying to store project defaults:\n" + e.getMessage());
    		return null;
    	}
    }

    public void updateManifestPane() {
        bagTagFileTree = new BagTree(this, bag.getName(), false);
        Collection<BagFile> tags = bag.getBag().getTags();
        for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
            bagTagFileTree.addNode(bf.getFilepath());
        }
        bagTagFileTreePanel.refresh(bagTagFileTree);
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
    		projectList.setSelectedItem(projectName);
    		bag.isNoProject(true);
    	} else {
    		projectList.setSelectedItem(projectName);
      		bag.isNoProject(false);
    	}
    	setBag(bag);
		return messages;
    }

    public String updateProfile() {
    	String message = "";
    	Project project = bag.getProject();
    	if (project == null) return message;
    	Object[] profiles = this.userProfiles.toArray();
    	Collection<Profile> profileList = new ArrayList<Profile>();
    	for (int i=0; i < profiles.length; i++) {
    		Profile profile = (Profile) profiles[i];
    		if (profile.getProject().getId() == project.getId()) {
    			BaggerOrganization org = bag.getInfo().getBagOrganization();
    			log.debug("updateProfile: " + org);
    			Contact orgContact = bag.getInfo().getBagOrganization().getContact();
    			orgContact.getOrganization().setName(org.getSourceOrganization());
    			orgContact.getOrganization().setAddress(org.getOrganizationAddress());
    			profile.setContact(orgContact);
    			profile.setContactId(orgContact.getId());
    			profile.setProject(project);
    			profile.setProjectId(project.getId());
    			profile.setPerson(this.projectContact);
    			profile.setUsername(this.username);
    			message = getPropertyMessage("profile.message.changed") + " " + project.getName() + "\n";
    			profiles[i] = profile;
    		}
    		profileList.add(profile);
    	}
    	userProfiles = profileList;
    	return message;
    }
/*    
    public void updateTreePanels() {
    	try {
    		bag.getInfo().setBag(bag);
    		bagTagFileTree = new BagTree(this, bag.getName(), false);
    		Collection<BagFile> tags = bag.getBag().getTags();
    		for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
    			BagFile bf = it.next();
    			bagTagFileTree.addNode(bf.getFilepath());
    		}
    		bagTagFileTreePanel.refresh(bagTagFileTree);
    		tagManifestPane.updateCompositePaneTabs(bag);
    	} catch (Exception e) {
    	}
    }
*/
    public void onApplicationEvent(ApplicationEvent e) {
    	log.info("BagView.onApplicationEvent: " + e);
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
    	buffer.append(getPropertyMessage("consolepane.status.help"));
    	buffer.append("\n\n");
    	return buffer.toString();
    }

    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            progressMonitor.setProgress(task.getCurrent().intValue());
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
                if (longRunningProcess != null) longRunningProcess.cancel();
                if (task.isDone()) {
                    taskOutput.append("Task completed.\n");
                } else {
                    taskOutput.append("Task canceled.\n");

                }
            }
        }
    }

    public void statusBarBegin(Progress progress, String message, Long size) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        task = new LongTask();
        task.setLengthOfTask(size);
        progress.setTask(task);
        task.setProgress(progress);

        taskOutput = new JTextArea(5, 20);
        timer = new Timer(ONE_SECOND, new TimerListener());

        progressMonitor = new ProgressMonitor(this.getControl(),
                "Running a Long Task", "", 0, task.getLengthOfTask().intValue());
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(1 * ONE_SECOND);
        task.setMonitor(progressMonitor);

        task.go();
        timer.start();
    }
    
    public void statusBarEnd() {
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());    	
    }

}
