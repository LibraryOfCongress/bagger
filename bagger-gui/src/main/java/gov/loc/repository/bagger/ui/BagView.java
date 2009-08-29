
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
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Cancellable;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;
import gov.loc.repository.bagit.verify.impl.ParallelManifestChecksumVerifier;
import gov.loc.repository.bagit.verify.impl.ValidVerifierImpl;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.TarBz2Writer;
import gov.loc.repository.bagit.writer.impl.TarGzWriter;
import gov.loc.repository.bagit.writer.impl.TarWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.swing.tree.DefaultMutableTreeNode;
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
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.progress.BusyIndicator;
import org.springframework.util.Assert;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.dao.DataAccessException;
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
    private Cancellable longRunningProcess = null;
    private Writer bagWriter = null;

	private Bagger bagger;
    private DefaultBag bag;
    private BaggerValidationRulesSource baggerRules;
    public int bagCount = 0;
    public File bagRootPath;
    public File tmpRootPath;
    public File parentSrc;
    public Collection<Project> userProjects;
    public Collection<Profile> userProfiles;
    public Collection<ProjectProfile> userProjectProfiles;
    private BaggerProfile baggerProfile = new BaggerProfile();
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
    public JButton clearButton;
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

    private CreateBagInPlaceHandler createBagInPlaceHandler;
    public CreateBagInPlaceExecutor createBagInPlaceExecutor = new CreateBagInPlaceExecutor();
    private ClearBagHandler clearBagHandler;
    public ClearBagExecutor clearExecutor = new ClearBagExecutor();
    private ValidateBagHandler validateBagHandler;
    public ValidateExecutor validateExecutor = new ValidateExecutor();
    private CompleteBagHandler completeBagHandler;
    public CompleteExecutor completeExecutor = new CompleteExecutor();
    private AddDataHandler addDataHandler;
    public AddDataExecutor addDataExecutor = new AddDataExecutor();
    private SaveBagHandler saveBagHandler;
    public SaveBagExecutor saveBagExecutor = new SaveBagExecutor();
	public StartExecutor startExecutor = new StartExecutor();
    public OpenExecutor openExecutor = new OpenExecutor();
    public RemoveDataExecutor removeDataExecutor = new RemoveDataExecutor();
    public SaveBagAsExecutor saveBagAsExecutor = new SaveBagAsExecutor(this);
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
    
    public String saveProfiles() {
    	String message = storeProfile();
    	return message;
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
    	createButton.addActionListener(new StartNewBagHandler(this));
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

    	createSkeletonButton = new JButton(getPropertyMessage("bag.button.createskeleton"));
    	createBagInPlaceHandler = new CreateBagInPlaceHandler();
    	createSkeletonButton.addActionListener(createBagInPlaceHandler);
    	createSkeletonButton.setEnabled(true);
    	createSkeletonButton.setOpaque(true);
    	createSkeletonButton.setBackground(bgColor);
    	createSkeletonButton.setForeground(fgColor);
    	createSkeletonButton.setToolTipText(getPropertyMessage("bag.button.createskeleton.help"));
    	buttonPanel.add(createSkeletonButton);

        saveButton = new JButton(getPropertyMessage("bag.button.save"));
        saveBagHandler = new SaveBagHandler();
        saveButton.addActionListener(saveBagHandler);
        saveButton.setEnabled(false);
        saveButton.setOpaque(true);
        saveButton.setBackground(bgColor);
        saveButton.setForeground(fgColor);
        saveButton.setToolTipText(getPropertyMessage("bag.button.save.help"));
        buttonPanel.add(saveButton);

    	saveAsButton = new JButton(getPropertyMessage("bag.button.saveas"));
    	saveAsButton.addActionListener(new SaveBagAsHandler(this));
        saveAsButton.setEnabled(false);
        saveAsButton.setOpaque(true);
        saveAsButton.setBackground(bgColor);
        saveAsButton.setForeground(fgColor);
        saveAsButton.setToolTipText(getPropertyMessage("bag.button.saveas.help"));
        saveBagFrame = new SaveBagFrame(this, getPropertyMessage("bag.frame.save"));
        buttonPanel.add(saveAsButton);

        completeButton = new JButton(getPropertyMessage("bag.button.complete"));
    	completeBagHandler = new CompleteBagHandler();
        completeButton.addActionListener(completeBagHandler);
        completeButton.setEnabled(false);
        completeButton.setOpaque(true);
        completeButton.setBackground(bgColor);
        completeButton.setForeground(fgColor);
        completeButton.setToolTipText(getPropertyMessage("bag.button.complete.help"));
        buttonPanel.add(completeButton);
        
        validateButton = new JButton(getPropertyMessage("bag.button.validate"));
    	validateBagHandler = new ValidateBagHandler();
        validateButton.addActionListener(validateBagHandler);
        validateButton.setEnabled(false);
        validateButton.setOpaque(true);
    	validateButton.setBackground(bgColor);
    	validateButton.setForeground(fgColor);
    	validateButton.setToolTipText(getPropertyMessage("bag.button.validate.help"));
        buttonPanel.add(validateButton);
        
        clearButton = new JButton(getPropertyMessage("bag.button.clear"));
    	clearBagHandler = new ClearBagHandler();
    	clearButton.addActionListener(clearBagHandler);
    	clearButton.setEnabled(false);
    	clearButton.setOpaque(true);
    	clearButton.setBackground(bgColor);
    	clearButton.setForeground(fgColor);
    	clearButton.setToolTipText(getPropertyMessage("bag.button.clear.help"));
        buttonPanel.add(clearButton);

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
        buildConstraints(glbc, 0, row, colspan, 1, 1, 20, GridBagConstraints.BOTH, GridBagConstraints.WEST);
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
    	removeDataButton.addActionListener(new RemoveDataHandler());
    	removeDataButton.setEnabled(false);
    	removeDataButton.setToolTipText(getPropertyMessage("bag.button.remove.help"));
    	buttonPanel.add(removeDataButton, BorderLayout.CENTER);

        return buttonPanel;
    }
    
    private JPanel createBagTagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));

    	showTagButton = new JButton(getPropertyMessage("bag.tagbutton.show"));
    	showTagButton.addActionListener(new ShowTagFilesHandler());
    	showTagButton.setEnabled(false);
    	showTagButton.setToolTipText(getPropertyMessage("bag.tagbutton.show.help"));
    	buttonPanel.add(showTagButton, BorderLayout.NORTH);

    	addTagFileButton = new JButton(getPropertyMessage("bag.tagbutton.add"));
    	addTagFileButton.addActionListener(new AddTagFileHandler());
    	addTagFileButton.setEnabled(false);
    	addTagFileButton.setToolTipText(getPropertyMessage("bag.tagbutton.add.help"));
    	buttonPanel.add(addTagFileButton, BorderLayout.CENTER);
    	
    	removeTagFileButton = new JButton(getPropertyMessage("bag.tagbutton.remove"));
    	removeTagFileButton.addActionListener(new RemoveTagFileHandler());
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
        holeyValue.setText("false");
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
					List<Object> list = new ArrayList();
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
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
            compositePane.updateCompositePaneTabs(bag, "Tag files changed.");
    	}
    }

    private class RemoveTagFileExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	removeTagFile();
        }
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

    	TreePath[] paths = bagPayloadTree.getCheckingPaths(); //bagPayloadTree.getSelectionPaths();
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
    			log.debug("removeData filePath: " + fileName);
        		if (fileName != null && !fileName.isEmpty()) {
            		try {
            			b.removeBagFile(fileName);
            			if (node instanceof MutableTreeNode) {
            				model.removeNodeFromParent((MutableTreeNode)node);
            			} else {
            				DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(node);
            				model.removeNodeFromParent((MutableTreeNode)aNode);
            			}
            		} catch (Exception e) {
            			try {
                			b.removePayloadDirectory(fileName);
                			if (node instanceof MutableTreeNode) {
                				model.removeNodeFromParent((MutableTreeNode)node);
                			} else {
                				DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(node);
                				model.removeNodeFromParent((MutableTreeNode)aNode);
                			}
            			} catch (Exception ex) {
                			message = "Error trying to remove: " + fileName + "\n";
                			showWarningErrorDialog("Error - file not removed", message + ex.getMessage());
            			}
            		}
        		}
        	}
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
            compositePane.updateCompositePaneTabs(bag, "Payload data removed.");
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

    public class ValidateExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	validateBag();
        }
    }

    private class ValidateBagHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
    	private LongTask task;

    	public void actionPerformed(ActionEvent e) {
    		validateBag();
    	}

    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
        	while (!task.canceled && !task.done) {
                try {
                    Thread.sleep(1000); //sleep for a second
                    /* */
            		CompleteVerifierImpl completeVerifier = new CompleteVerifierImpl();
            		completeVerifier.addProgressListener(task);
            		
            		ParallelManifestChecksumVerifier manifestVerifier = new ParallelManifestChecksumVerifier();
            		manifestVerifier.addProgressListener(task);
            		
            		ValidVerifierImpl validVerifier = new ValidVerifierImpl(completeVerifier, manifestVerifier);
            		validVerifier.addProgressListener(task);
            		longRunningProcess = validVerifier;
            		/* */
                    String messages = bag.validateBag(validVerifier);
            	    if (messages != null && !messages.trim().isEmpty()) {
            	    	showWarningErrorDialog("Warning - validation failed", "Validation result: " + messages);
            	    	task.current = task.lengthOfTask;
            	    }
            	    else {
            	    	showWarningErrorDialog("Validation Dialog", "Validation successful.");
            	    	task.current = task.lengthOfTask;
            	    }
                	setBag(bag);
                	compositePane.updateCompositePaneTabs(bag, messages);
                    if (task.current >= task.lengthOfTask) {
                        task.done = true;
                        task.current = task.lengthOfTask;
                    }
                    task.statMessage = "Completed " + task.current +
                                  " out of " + task.lengthOfTask + ".";
                } catch (InterruptedException e) {
                	e.printStackTrace();
                	task.current = task.lengthOfTask;
            	    showWarningErrorDialog("Warning - validation interrupted", "Error trying validate bag: " + e.getMessage());
                }
            }
        	statusBarEnd();
    	}
    }

    private void validateBag() {
    	statusBarBegin(validateBagHandler, "Validating bag...", 1L);
    }

    public class CompleteExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	completeBag();
        }
    }

    private class CompleteBagHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
    	private LongTask task;

    	public void actionPerformed(ActionEvent e) {
     		completeBag();
    	}

    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
        	while (!task.canceled && !task.done) {
                try {
                    Thread.sleep(1000); //sleep for a second
                    /* */
                    CompleteVerifierImpl completeVerifier = new CompleteVerifierImpl();
                    completeVerifier.addProgressListener(task);
            		longRunningProcess = completeVerifier;
            		/*             		 */
            		Bag completeBag = bag.getBag();
                    String messages = bag.completeBag(completeVerifier, completeBag);
            	    if (messages != null && !messages.trim().isEmpty()) {
            	    	showWarningErrorDialog("Warning - incomplete", "Is complete result: " + messages);
            	    	task.current = task.lengthOfTask;
            	    }
            	    else {
            	    	showWarningErrorDialog("Is Complete Dialog", "Bag is complete.");
            	    	task.current = task.lengthOfTask;
            	    }
                	setBag(bag);
                	compositePane.updateCompositePaneTabs(bag, messages);
                    if (task.current >= task.lengthOfTask) {
                        task.done = true;
                        task.current = task.lengthOfTask;
                    }
                    task.statMessage = "Completed " + task.current +
                                  " out of " + task.lengthOfTask + ".";
                } catch (InterruptedException e) {
                	e.printStackTrace();
                	task.current = task.lengthOfTask;
            	    showWarningErrorDialog("Warning - complete check interrupted", "Error checking bag completeness: " + e.getMessage());
                }
            }
        	statusBarEnd();
    	}
    }

    public void completeBag() {
    	statusBarBegin(completeBagHandler, "Checking if complete...", 1L);
    }

    public class SaveBagAsExecutor extends AbstractActionCommandExecutor {
	   	BagView bagView;

	   	public SaveBagAsExecutor(BagView bagView) {
	   		this.bagView = bagView;
	   	}

	   	public void execute() {
//        	saveBagAs();
	        saveBagFrame = new SaveBagFrame(bagView, getPropertyMessage("bag.frame.save"));
	        saveBagFrame.setBag(bag);
			saveBagFrame.setVisible(true);
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
        bag.setName(file.getName());
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
        	}
    	}
        String fileName = bagFile.getName(); //bagFile.getAbsolutePath();
        bagNameField.setText(fileName);
        this.getControl().invalidate();
        bagNameField.setCaretPosition(fileName.length()-1);
        enableSettings(true);
    }

    private void confirmWriteBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	    	boolean isCancel = true;
	        protected void onConfirm() {
	        	if (bag.getSize() > DefaultBag.MAX_SIZE) {
	        		confirmAcceptBagSize();
	        	} else {
		        	bagRootPath = tmpRootPath;
		        	saveBag(bagRootPath);
	        	}
	        }
	        protected void onCancel() {
        		super.onCancel();
	        	if (isCancel) {
	        		cancelWriteBag();
	        		isCancel = false;
	        	}
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(getPropertyMessage("bag.dialog.title.create"));
	    dialog.setConfirmationMessage(getPropertyMessage("bag.dialog.message.create"));
	    dialog.showDialog();
	}

    private void cancelWriteBag() {
    	saveBagAs();
    }

    private void confirmAcceptBagSize() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	bagRootPath = tmpRootPath;
	        	saveBag(bagRootPath);
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(getPropertyMessage("bag.dialog.title.create"));
	    dialog.setConfirmationMessage(getPropertyMessage("bag.dialog.message.accept"));
	    dialog.showDialog();
	}

	public class SaveBagExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	if (bagRootPath.exists()) {
        		tmpRootPath = bagRootPath;
                confirmWriteBag();
        	} else {
        		saveBag(bagRootPath);
        	}
        }
    }

    private class SaveBagHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
       	private LongTask task;

       	public void actionPerformed(ActionEvent e) {
        	if (bagRootPath.exists()) {
        		tmpRootPath = bagRootPath;
                confirmWriteBag();
        	} else {
        		saveBag(bagRootPath);
        	}
       	}

    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
        	while (!task.canceled && !task.done) {
                try {
                    Thread.sleep(1000); //sleep for a second
                    /* */
                    short mode = bag.getSerialMode();
                    if (mode == DefaultBag.NO_MODE) {
                    	bagWriter = new FileSystemWriter(bag.getBagFactory());
                    } else if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
                    	bagWriter = new ZipWriter(bag.getBagFactory());
                    } else if (mode == DefaultBag.TAR_MODE) {
                    	bagWriter = new TarWriter(bag.getBagFactory());
                    } else if (mode == DefaultBag.TAR_GZ_MODE) {
                    	bagWriter = new TarGzWriter(bag.getBagFactory());
                    } else if (mode == DefaultBag.TAR_BZ2_MODE) {
                    	bagWriter = new TarBz2Writer(bag.getBagFactory());
					}
        			bagWriter.addProgressListener(task);
            		longRunningProcess = bagWriter;
                    String messages = bag.write(bagWriter);

                    if (task.current >= task.lengthOfTask) {
                        task.done = true;
                        task.current = task.lengthOfTask;
                    }
                    if (messages != null && !messages.trim().isEmpty()) showWarningErrorDialog("Warning - bag not saved", "Problem saving bag:\n" + messages);
                    else showWarningErrorDialog("Bag saved", "Bag saved successfully.\n" );
                    if (bag.isSerialized()) {
                        if (progressMonitor.isCanceled() || task.isDone()) {
                            progressMonitor.close();
                        }
                        bag.isValidateOnSave(validateOnSave);
                		if (bag.isValidateOnSave()) {
                			validateBag();
                		}
                    	// Open the newly created bag, e.g. if zip file is
                    	// saved as a filesystem it will display correctly
                    	statusBarEnd();
        				File bagFile = bag.getBagFileName();
        				log.info("BagView.openExistingBag: " + bagFile);
        				openExistingBag(bagFile);
        				// If openExistingBag is not used then do this
                    	/*
                    	bag.getInfo().createExistingFieldMap(true);
                    	bag.copyBagToForm();
                        bagInfoInputPane.populateForms(bag, true);
                        bagInfoInputPane.update(bag);
                    	compositePane.updateCompositePaneTabs(bag, messages);
                    	updateManifestPane();
                         */
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
        	            bagButtonPanel.invalidate();
        	            clearButton.setEnabled(true);
        	            validateButton.setEnabled(true);
        	            completeButton.setEnabled(true);
        	            clearExecutor.setEnabled(true);
        	            validateExecutor.setEnabled(true);
        	            topButtonPanel.invalidate();
        	            bag.isNewbag(false);
                    } else {
                        compositePane.updateCompositePaneTabs(bag, messages);
                        updateManifestPane();
                    }
                } catch (InterruptedException e) {
                	task.done = true;
        			bag.isSerialized(false);
        			if (longRunningProcess.isCancelled()) {
            			showWarningErrorDialog("Save cancelled", "Save cancelled.");
        			} else {
            			showWarningErrorDialog("Warning - save interrupted", "Problem saving bag: " + bagRootPath + "\n" + e.getMessage());
        			}
                	e.printStackTrace();
                } catch (Exception e) {
                	task.done = true;
        			bag.isSerialized(false);
        			if (longRunningProcess.isCancelled()) {
            			showWarningErrorDialog("Save cancelled", "Save cancelled.");
        			} else {
            			showWarningErrorDialog("Error - bag not saved", "Error saving bag: " + bagRootPath + "\n" + e.getMessage());
        			}
                	e.printStackTrace();
                }
        	}
        	statusBarEnd();
    	}
    }

    public void saveBag(File file) {
        bag.setRootDir(file);
        statusBarBegin(saveBagHandler, "Writing bag...", 1L);
    }

    public void showWarningErrorDialog(String title, String msg) {
    	MessageDialog dialog = new MessageDialog(title, msg);
	    dialog.showDialog();
    }

    private class ClearBagExecutor extends AbstractActionCommandExecutor {
        public void execute() {
    		clearExistingBag(getPropertyMessage("compositePane.message.clear"));
        }
    }

    private class ClearBagHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;
	   	
    	public void actionPerformed(ActionEvent e) {
    		clearExistingBag(getPropertyMessage("compositePane.message.clear"));
       	}
    }

    public void clearExistingBag(String messages) {
    	bagInfoInputPane.enableForms(bag, false);
    	newDefaultBag(null);
    	bag.getInfo().setFieldMap(null);
    	bag.getInfo().setProfileMap(null);
        holeyCheckbox.setSelected(false);
        holeyValue.setText("false");
        this.baggerRules.clear();
    	bag.isNewbag(true);
    	bagPayloadTree = new BagTree(this, AbstractBagConstants.DATA_DIRECTORY, true);
    	bagPayloadTreePanel.refresh(bagPayloadTree);
    	bagTagFileTree = new BagTree(this, getPropertyMessage("bag.label.noname"), false);
    	bagTagFileTreePanel.refresh(bagTagFileTree);
    	enableBagSettings(false);
        noneButton.setSelected(true);
    	addDataButton.setEnabled(false);
    	addDataExecutor.setEnabled(false);
        updatePropButton.setEnabled(false);
    	saveButton.setEnabled(false);
    	saveBagExecutor.setEnabled(false);
    	saveAsButton.setEnabled(false);
    	saveBagAsExecutor.setEnabled(false);
    	removeDataButton.setEnabled(false);
    	showTagButton.setEnabled(false);
    	addTagFileButton.setEnabled(false);
    	removeTagFileButton.setEnabled(false);
    	clearButton.setEnabled(false);
    	validateButton.setEnabled(false);
    	completeButton.setEnabled(false);
    	clearExecutor.setEnabled(false);
    	validateExecutor.setEnabled(false);
    	completeExecutor.setEnabled(false);
    	bagButtonPanel.invalidate();
    	topButtonPanel.invalidate();

        bagNameField.setText(bag.getName());
        enableSettings(false);
		bagInfoInputPane.populateForms(bag, false);
        compositePane.updateCompositePaneTabs(bag, messages);
    }
    
    private void enableSettings(boolean b) {
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
        removeDataExecutor.setEnabled(false);
        saveBagExecutor.setEnabled(false);
        saveBagAsExecutor.setEnabled(false);
        addTagFileExecutor.setEnabled(false);
        removeTagFileExecutor.setEnabled(false);
        saveProfileExecutor.setEnabled(true);
    }

    protected void registerLocalCommandExecutors(PageComponentContext context) {
    	context.register("startCommand", startExecutor);
    	context.register("openCommand", openExecutor);
    	context.register("createBagInPlaceCommand", createBagInPlaceExecutor);
    	context.register("clearCommand", clearExecutor);
    	context.register("validateCommand", validateExecutor);
    	context.register("completeCommand", completeExecutor);
    	context.register("addDataCommand", addDataExecutor);
    	context.register("removeDataCommand", removeDataExecutor);
    	context.register("saveBagCommand", saveBagExecutor);
    	context.register("saveBagAsCommand", saveBagAsExecutor);
    	context.register("addTagFileCommand", addTagFileExecutor);
    	context.register("removeTagFileCommand", removeTagFileExecutor);
    	context.register("saveProfileCommand", saveProfileExecutor);
    }

    private class SaveProfileExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	storeProfile();
        }
    }

    public String storeProfile() {
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

    public void newDefaultBag(File f) {
    	String bagName = "";
    	bag = new DefaultBag(f, bagVersionValue.getText());
    	bag.isClear(true);
    	if (f == null) {
        	bagName = getPropertyMessage("bag.label.noname");
    	} else {
	    	bagName = f.getName();
	        String fileName = f.getAbsolutePath();
	        bagNameField.setText(fileName);
	        bagNameField.setCaretPosition(fileName.length()-1);
	        enableSettings(true);
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
	   	BagView bagView;
	   	
	   	public StartNewBagHandler(BagView bagView) {
	   		this.bagView = bagView;
	   	}

    	public void actionPerformed(ActionEvent e) {
    		newBag();
       	}
    }

    private void newBag() {
        newBagFrame = new NewBagFrame(this, getPropertyMessage("bag.frame.new"));
        newBagFrame.setBag(bag);
        newBagFrame.setVisible(true);
    }

    public void createNewBag() {
    	String messages = "";
    	bagCount++;

    	bagInfoInputPane.enableForms(bag, true);
    	clearExistingBag(messages);
        bagVersionList.setSelectedItem(bagVersionValue.getText());
        bagVersionValue.setText(bagVersionValue.getText());

    	String bagName = getPropertyMessage("bag.label.noname");
		bag.setName(bagName);
        bagNameField.setText(bagName);
        bagNameField.setCaretPosition(bagName.length()-1);
        enableSettings(false);
		bag.setRootDir(bagRootPath);
		messages = updateBaggerRules();
    	initializeProfile();

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
        bag.isClear(false);
		bag.getInfo().setBag(bag);
		bagInfoInputPane.populateForms(bag, true);
        //bagInfoInputPane.updateSelected(bag);
        compositePane.updateCompositePaneTabs(bag, messages);

		bag.isNewbag(true);
    	addDataButton.setEnabled(true);
    	addDataExecutor.setEnabled(true);
    	addTagFileButton.setEnabled(true);
    	clearButton.setEnabled(true);
    	removeTagFileButton.setEnabled(true);
    	bagButtonPanel.invalidate();
    }

    private class CreateBagInPlaceExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	createBagInPlace();
        }
    }

    private class CreateBagInPlaceHandler extends AbstractAction implements Progress {
       	private static final long serialVersionUID = 1L;
    	private LongTask task;
    	BagView bagView;
    	DefaultBag bag;

    	public void actionPerformed(ActionEvent e) {
    		this.bag = getBag();
    		createBagInPlace();
    	}

    	public void setBagView(BagView bagView) {
    		this.bag = bagView.getBag();
    	}

    	public void setTask(LongTask task) {
    		this.task = task;
    	}

    	public void execute() {
        	statusBarEnd();
    	}
    }

    public void createBagInPlace() {
        newBagInPlaceFrame = new NewBagInPlaceFrame(this, getPropertyMessage("bag.frame.newbaginplace"));
        newBagInPlaceFrame.setBag(bag);
        newBagInPlaceFrame.setVisible(true);
    }

    public void createPreBag(File data) {
    	String messages = "";
    	clearExistingBag(messages);
    	try {
    		bag.createPreBag(data, this.bagVersionValue.getText());
    	} catch (Exception e) {
    	    showWarningErrorDialog("Error - bagging in place", "No file or directory selection was made!\n");
    		return;
    	}
        bag.getInfo().setBag(bag);
    	bag.getBag().addFileToPayload(data);
    	boolean alreadyExists = bagPayloadTree.addNodes(data, false);
    	bagPayloadTreePanel.refresh(bagPayloadTree);
    	/* */
    	File bagDir = data.getParentFile();
    	String bagFileName = "bag_" + data.getName();
    	bag.isClear(false);
        bag.setName(bagFileName);
        bagNameField.invalidate();
        File bagFile = new File(bagDir, bagFileName);
		save(bagFile);
    	/* */
    	compositePane.setBag(bag);
    	compositePane.updateCompositePaneTabs(bag, getPropertyMessage("bag.message.filesadded"));
        updateManifestPane();
    	enableBagSettings(true);
		bag.isSerialized(true);
		String msgs = bag.validateMetadata();
		if (msgs != null) {
			if (messages != null) messages += msgs;
			else messages = msgs;
		}
		bag.getInfo().setBag(bag);
        Bag b = bag.getBag();
    	bagTagFileTree = new BagTree(this, bag.getName(), false);
        Collection<BagFile> tags = b.getTags();
        for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
            bagTagFileTree.addNode(bf.getFilepath());
        }
        bagTagFileTreePanel.refresh(bagTagFileTree);
    	bagInfoInputPane.populateForms(bag, true);
        compositePane.updateCompositePaneTabs(bag, messages);
/* */
        addDataButton.setEnabled(true);
        addDataExecutor.setEnabled(true);
        updatePropButton.setEnabled(false);
        saveButton.setEnabled(false);
        saveBagExecutor.setEnabled(false);
        saveAsButton.setEnabled(true);
        saveBagAsExecutor.setEnabled(true);
        removeDataExecutor.setEnabled(true);
        removeDataButton.setEnabled(true);
        addTagFileButton.setEnabled(true);
        removeTagFileButton.setEnabled(true);
        showTagButton.setEnabled(true);
        bagButtonPanel.invalidate();
        clearButton.setEnabled(true);
        validateButton.setEnabled(true);
        completeButton.setEnabled(true);
        validateExecutor.setEnabled(true);
        bagButtonPanel.invalidate();
        topButtonPanel.invalidate();
        bag.isNewbag(true);

        statusBarEnd();
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
            bagButtonPanel.invalidate();
            clearButton.setEnabled(true);
            validateButton.setEnabled(true);
            completeButton.setEnabled(true);
            validateExecutor.setEnabled(true);
            topButtonPanel.invalidate();
            bag.isNewbag(false);
        }
    }

    private void openExistingBag(File file) {
    	String messages = "";
    	bagInfoInputPane.enableForms(bag, true);
    	clearExistingBag(messages);
    	messages = "";

		try {
	    	newDefaultBag(file);
		} catch (Exception ex) {
			log.error("openExistingBag DefaultBag: " + ex.getMessage());
        	messages +=  "Failed to create bag: " + ex.getMessage() + "\n";
    	    //showWarningErrorDialog("Warning - file not opened", "Error trying to open file: " + file + "\n" + ex.getMessage());
    	    return;
		}
        bagVersionValue.setText(bag.getVersion());
        bagVersionList.setSelectedItem(bagVersionValue.getText());
        String fileName = file.getName();
        bagNameField.setText(file.getAbsolutePath());
        enableSettings(true);
        bagNameField.invalidate();

        /* */
    	String s = file.getName();
	    int i = s.lastIndexOf('.');
	    if (i > 0 && i < s.length() - 1) {
	    	String sub = s.substring(i + 1).toLowerCase();
	    	if (sub.contains("gz")) {
	    		serializeValue.setText(DefaultBag.TAR_GZ_LABEL);
	    		tarGzButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_GZ_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains("bz2")) {
	    		serializeValue.setText(DefaultBag.TAR_BZ2_LABEL);
	    		tarBz2Button.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_BZ2_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains(DefaultBag.TAR_LABEL)) {
	    		serializeValue.setText(DefaultBag.TAR_LABEL);
	    		tarButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains(DefaultBag.ZIP_LABEL)) {
	    		serializeValue.setText(DefaultBag.ZIP_LABEL);
	    		zipButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.ZIP_MODE);
	    		bag.isSerial(true);
	    	} else {
	    		serializeValue.setText(DefaultBag.NO_LABEL);
	    		noneButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.NO_MODE);
	    		bag.isSerial(false);
	    	}
	    } else {
    		serializeValue.setText(DefaultBag.NO_LABEL);
    		noneButton.setSelected(true);
    		bag.setSerialMode(DefaultBag.NO_MODE);
    		bag.isSerial(false);
	    }
	    serializeValue.invalidate();
	    /* */
	    bag.isClear(false);
        bag.getInfo().setBag(bag);
    	bag.copyBagToForm();
    	baggerProfile = new BaggerProfile();
	    if (!bag.getInfo().getLcProject().isEmpty()){
    		String name = bag.getInfo().getLcProject().trim();
    		Project project = new Project();
    		project.setName(name);
    		if (!projectExists(project)) {
        		addProject(project);
    		}
    		messages += updateProject(name);
    		bag.isNoProject(false);
    	} else {
    		messages += updateProject(getPropertyMessage("bag.project.noproject"));
    		bag.isNoProject(true);
    	}
	    // TODO: if LC-Project field exists then open Project Profile and
	    // add LC-Project to the baggerProfile map or modify it
		bag.getInfo().createExistingFieldMap(true);
    	baggerProfile.setOrganization(bag.getInfo().getBagOrganization());
    	if (bag.getInfo().getBagSize() != null && bag.getInfo().getBagSize().isEmpty()) {
        	bag.setSize(bag.getDataSize());
    	} 
	    if (bag.isHoley()) {
	        holeyCheckbox.setSelected(true);
	        holeyValue.setText("true");
	    }
    	bag.copyBagToForm();
	    if (bag.getProject() != null && bag.getProject().getIsDefault()) {
	    	defaultProject.setSelected(true);
	    } else {
	    	defaultProject.setSelected(false);
	    }
		messages = updateBaggerRules();
    	bagRootPath = file;
    	bag.setRootDir(bagRootPath);
		File rootSrc = new File(file, bag.getDataDirectory());
    	if (bag.getBag().getFetchTxt() != null) {
    		rootSrc = new File(file, bag.getBag().getFetchTxt().getFilepath());
    	} else {
    		rootSrc = new File(file, bag.getDataDirectory());
    	}
    	bagPayloadTree = new BagTree(this, AbstractBagConstants.DATA_DIRECTORY, true);
		bagPayloadTree.populateNodes(bag, rootSrc, true);
        bagPayloadTreePanel.refresh(bagPayloadTree);
        updateManifestPane();
    	enableBagSettings(true);
		bag.isSerialized(true);
		String msgs = bag.validateMetadata();
		if (msgs != null) {
			if (messages != null) messages += msgs;
			else messages = msgs;
		}
		bag.getInfo().setBag(bag);
		bagInfoInputPane.updateProject(this);
    	bagInfoInputPane.populateForms(bag, true);
        compositePane.updateCompositePaneTabs(bag, messages);

    	statusBarEnd();
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
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
            compositePane.updateCompositePaneTabs(bag, "Tag file added.");
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
            		if (node != null) {
                		if (node instanceof MutableTreeNode) {
                    		b.removeBagFile(node.toString());
            				model.removeNodeFromParent((MutableTreeNode)node);
            			} else {
                    		b.removeBagFile((String)node);
            				DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(node);
            				model.removeNodeFromParent((MutableTreeNode)aNode);
            			}
            		}
    			} catch (Exception e) {
            	    message += "Error trying to remove file: " + node + "\n";
            	    showWarningErrorDialog("Error - file not removed", "Error trying to remove file: " + node + "\n" + e.getMessage());
    			}
        	}
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
            compositePane.updateCompositePaneTabs(bag, "Tag file removed.");
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
    		projectList.setSelectedItem(projectName);
    		bag.isNoProject(true);
    	} else {
    		projectList.setSelectedItem(projectName);
      		bag.isNoProject(false);
    	}
    	setBag(bag);
		return messages;
    }

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
    	buffer.append(getPropertyMessage("compositePane.message.isComplete"));
    	buffer.append("\n");
    	buffer.append(getPropertyMessage("consolepane.iscomplete.help"));
    	buffer.append("\n\n");
    	buffer.append(getPropertyMessage("compositePane.message.isValid"));
    	buffer.append("\n");
    	buffer.append(getPropertyMessage("consolepane.isvalid.help"));
    	buffer.append("\n\n");
    	buffer.append(getPropertyMessage("compositePane.message.isMetadata"));
    	buffer.append("\n");
    	buffer.append(getPropertyMessage("consolepane.ismetadata.help"));
    	buffer.append("\n\n");
//    	buffer.append(getPropertyMessage("compositePane.message.isSerialized"));
//    	buffer.append("\n");
//   	buffer.append(getPropertyMessage("consolepane.isserialized.help"));
//    	buffer.append("\n\n");
    	return buffer.toString();
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
