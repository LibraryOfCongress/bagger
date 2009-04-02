
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.domain.BaggerValidationRulesSource;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.AbstractAction;
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
import javax.swing.JFileChooser;
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
import org.springframework.richclient.command.support.GlobalCommandIds;
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
	private int DEFAULT_WIDTH = 1024;
	private int DEFAULT_HEIGHT = 768;

	private Bagger bagger;
    private DefaultBag bag;
    private BaggerValidationRulesSource baggerRules;
    private int bagCount = 0;
    private File bagRootPath;
    private File tmpRootPath;
    private BagTree bagTree;
    private Collection<Project> userProjects;
    private Collection<Profile> userProfiles;
	private String username;
	private Contact user;
	private String userHomeDir;

    private BagTreePanel bagTreePanel;
    private CompositePane compositePane;
    private BagInfoInputPane bagInfoInputPane;
    private BagTextPane infoFormMessagePane;
    private JPanel bagButtonPanel;
    private JPanel bagPanel;
    private JPanel topButtonPanel;
    private JButton openButton;
    private JButton addDataButton;
    private JButton saveButton;
    private JButton saveAsButton;
    private JButton validateButton;
    private JButton updatePropButton;
    private JList projectList;
    private JCheckBox holeyCheckbox;
    private JPanel serializeGroupPanel;
    private JRadioButton noneButton;
    private JRadioButton zipButton;
    private JRadioButton tarButton;
    private SaveExecutor saveExecutor = new SaveExecutor();
    private Color errorColor = new Color(255, 128, 128);
	private Color infoColor = new Color(100, 100, 120);
	private Color buttonColor = new Color(100, 100, 120);

    public void setBagger(Bagger bagger) {
        Assert.notNull(bagger, "The bagger property is required");
        this.bagger = bagger;
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

    public void setBag(DefaultBag baggerBag) {
        this.bag = baggerBag;
    }

    public DefaultBag getBag() {
        return this.bag;
    }
    
    public void setBagTree(BagTree bagTree) {
        this.bagTree = bagTree;
    }

    public BagTree getBagTree() {
        return this.bagTree;
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
    	f.setBackground(Color.red);
    	this.userHomeDir = System.getProperty("user.home");
        display("BagView.createControl - User Home Path: "+ userHomeDir);

        ApplicationServices services = this.getApplicationServices();
        Object rulesSource = services.getService(org.springframework.rules.RulesSource.class);
        baggerRules = (BaggerValidationRulesSource) rulesSource;
		
		newDefaultBag(null);
    	initializeProfile();
    	updateCommands();
    	Color bgColor = new Color(20,20,100);

    	topButtonPanel = createTopButtonPanel();
    	topButtonPanel.setBackground(bgColor);
    	JScrollPane infoInputPane = createInfoInputPane();
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

    	JButton createButton = new JButton(getMessage("bag.button.create"));
    	createButton.addActionListener(new CreateNewBagHandler());
    	createButton.setOpaque(true);
    	createButton.setBackground(bgColor);
    	createButton.setForeground(fgColor);
    	createButton.setToolTipText(getMessage("bag.button.create.help"));
    	buttonPanel.add(createButton);

    	openButton = new JButton(getMessage("bag.button.open"));
    	openButton.addActionListener(new OpenBagHandler());
    	openButton.setEnabled(true);
    	openButton.setOpaque(true);
    	openButton.setBackground(bgColor);
    	openButton.setForeground(fgColor);
    	openButton.setToolTipText(getMessage("bag.button.open.help"));
    	buttonPanel.add(openButton);

    	validateButton = new JButton(getMessage("bag.button.validate"));
        validateButton.addActionListener(new ValidateBagHandler());
        validateButton.setEnabled(false);
        validateButton.setOpaque(true);
    	validateButton.setBackground(bgColor);
    	validateButton.setForeground(fgColor);
    	validateButton.setToolTipText(getMessage("bag.button.validate.help"));
        buttonPanel.add(validateButton);
        
        return buttonPanel;
    }
    
    private JPanel createBagPanel() {
    	bagButtonPanel = createBagButtonPanel();
    	bagTree = new BagTree();
    	bagTreePanel = new BagTreePanel(bagTree);
    	bagTreePanel.setToolTipText(this.getMessage("bagView.bagTree.help"));
    	compositePane = new CompositePane(this, getInitialConsoleMsg());
    	compositePane.setToolTipText(this.getMessage("compositePane.tab.help"));

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        buildConstraints(glbc, 0, 0, 1, 1, 5, 30, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagButtonPanel, glbc);

        buildConstraints(glbc, 1, 0, 1, 1, 95, 30, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(bagTreePanel, glbc);

        buildConstraints(glbc, 0, 1, 2, 1, 100, 70, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(compositePane, glbc);

        bagPanel = new JPanel(layout);
        bagPanel.add(bagButtonPanel);
    	bagPanel.add(bagTreePanel);
        bagPanel.add(compositePane);

        return bagPanel;
    }
    
    private JPanel createBagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));

    	addDataButton = new JButton(getMessage("bag.button.add"));
    	addDataButton.addActionListener(new AddDataHandler());
    	addDataButton.setEnabled(false);
    	addDataButton.setToolTipText(getMessage("bag.button.add.help"));
        buttonPanel.add(addDataButton, BorderLayout.NORTH);

        saveButton = new JButton(getMessage("bag.button.save"));
        saveButton.addActionListener(new SaveBagHandler());
        saveButton.setEnabled(false);
        saveButton.setToolTipText(getMessage("bag.button.save.help"));
        buttonPanel.add(saveButton, BorderLayout.CENTER);

    	saveAsButton = new JButton(getMessage("bag.button.saveas"));
    	saveAsButton.addActionListener(new SaveBagAsHandler());
        saveAsButton.setEnabled(false);
        saveAsButton.setToolTipText(getMessage("bag.button.saveas.help"));
        buttonPanel.add(saveAsButton, BorderLayout.SOUTH);
        
        return buttonPanel;
    }

    private JScrollPane createInfoInputPane() {
    	JPanel bagSettingsPanel = createBagSettingsPanel();
    	bagInfoInputPane = new BagInfoInputPane(this, username, user);
    	bagInfoInputPane.setToolTipText(this.getMessage("bagView.bagInfoInputPane.help"));
    	JScrollPane bagInfoScrollPane = new JScrollPane();
    	bagInfoScrollPane.setViewportView(bagInfoInputPane);
    	bagInfoScrollPane.setToolTipText(this.getMessage("bagView.bagInfoInputPane.help"));
    	
    	// Create a panel for the form error messages and the update button
        JButton nextButton = new JButton(getMessage("button.next"));
        nextButton.setMnemonic(KeyEvent.VK_ENTER);
        nextButton.addActionListener(new NextButtonHandler());
        nextButton.setToolTipText(getMessage("button.next.help"));

        // Create a panel for the form error messages and the update button
        updatePropButton = new JButton(getMessage("button.saveupdates"));
        buttonColor = updatePropButton.getBackground();
        updatePropButton.setMnemonic(KeyEvent.VK_S);
        updatePropButton.addActionListener(new UpdateBagHandler());
        updatePropButton.setToolTipText(getMessage("button.saveupdates.help"));
        updatePropButton.setEnabled(false);
        
        JPanel infoLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoFormMessagePane = new BagTextPane("");
        if (bagInfoInputPane.hasFormErrors(bag)) {
        	infoFormMessagePane.setMessage(getMessage("error.form"));
        }
        Dimension labelDimension = bagInfoInputPane.getPreferredSize();
        int offsetx = updatePropButton.getWidth();
        if (offsetx == 0) offsetx = 80;
        int offsety = 25;
        java.awt.Font font = infoFormMessagePane.getFont();
        if (font != null) {
            java.awt.FontMetrics fm = infoFormMessagePane.getFontMetrics(font);
            if (fm != null) offsety = 2*fm.getHeight();
        }
        labelDimension.setSize(labelDimension.getWidth()-offsetx, offsety);
        infoFormMessagePane.setPreferredSize(labelDimension);
        infoFormMessagePane.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        infoLabelPanel.add(infoFormMessagePane, "North");
        if (bagInfoInputPane.hasFormErrors(bag)) {
            infoFormMessagePane.setBackground(errorColor);
        } else {
            infoFormMessagePane.setBackground(infoColor);
        }

        JLabel button = new JLabel("");
    	
        // Combine the information panel with the forms pane
        GridBagLayout infoLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        buildConstraints(gbc, 0, 0, 3, 1, 10, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(infoLabelPanel, gbc);

        buildConstraints(gbc, 0, 1, 3, 1, 50, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagInfoScrollPane, gbc);

        buildConstraints(gbc, 0, 2, 1, 1, 80, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
        infoLayout.setConstraints(button, gbc);

        buildConstraints(gbc, 1, 2, 1, 1, 10, 0, GridBagConstraints.NONE, GridBagConstraints.EAST);
        infoLayout.setConstraints(nextButton, gbc);

        buildConstraints(gbc, 2, 2, 1, 1, 10, 0, GridBagConstraints.NONE, GridBagConstraints.EAST);
        infoLayout.setConstraints(updatePropButton, gbc);
        
        buildConstraints(gbc, 0, 3, 3, 1, 20, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagSettingsPanel, gbc);
        
        JPanel infoPanel = new JPanel(infoLayout);
        infoPanel.setToolTipText(this.getMessage("bagView.bagInfoInputPane.help"));
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        int width = 0;
        int height = 0;
        infoPanel.setBorder(emptyBorder);
        infoPanel.add(infoLabelPanel);
        height += (int) infoLabelPanel.getPreferredSize().getHeight();
        infoPanel.add(bagInfoScrollPane);
        width += (int) bagInfoScrollPane.getPreferredSize().getWidth();
        height += (int) bagInfoScrollPane.getPreferredSize().getHeight();
        infoPanel.add(button);
        infoPanel.add(nextButton);
        height += (int) updatePropButton.getPreferredSize().getHeight();
        infoPanel.add(updatePropButton);
        infoPanel.add(bagSettingsPanel);
        height += (int) bagSettingsPanel.getPreferredSize().getHeight();
        Dimension preferredSize = new Dimension(width, height);
        
    	JScrollPane infoScrollPane = new JScrollPane();
    	infoScrollPane.setToolTipText(this.getMessage("bagView.bagInfoInputPane.help"));
    	infoScrollPane.setPreferredSize(preferredSize);
    	infoScrollPane.setViewportView(infoPanel);
    	return infoScrollPane;
    }

    private JPanel createBagSettingsPanel() {
        Border border = new EmptyBorder(5, 5, 5, 5);

        // Project control
        JLabel projectLabel = new JLabel(getMessage("bag.label.project"));
        projectLabel.setToolTipText(getMessage("bag.projectlist.help"));
        DefaultListModel listModel = new DefaultListModel();
        Object[] array = userProjects.toArray();
        for (int i=0; i < userProjects.size(); i++) listModel.addElement(((Project)array[i]).getName());
        projectList = new JList(listModel);
        projectList.setName(getMessage("bag.label.projectlist"));
        projectList.setVisibleRowCount(2);
        projectList.setSelectedValue(getMessage("bag.project.noproject"), true);
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.addListSelectionListener(new ProjectListHandler());
        projectList.setToolTipText(getMessage("bag.projectlist.help"));
    	String selected = (String) projectList.getSelectedValue();
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(getMessage("bag.project.edeposit"))) {
    		bag.setIsEdeposit(true);
    	} else {
    		bag.setIsEdeposit(false);
    	}
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(getMessage("bag.project.ndnp"))) {
    		bag.setIsNdnp(true);
    	} else {
    		bag.setIsNdnp(false);
    	}
        JScrollPane projectPane = new JScrollPane(projectList);

        // Checksum control
        JLabel checksumLabel = new JLabel(getMessage("bag.label.checksumtype"));
        JRadioButton md5Button = new JRadioButton(getMessage("bag.checksumtype.md5"));
        md5Button.setSelected(true);
        md5Button.setEnabled(true);
        JRadioButton sha1Button = new JRadioButton(getMessage("bag.checksumtype.sha1"));
        sha1Button.setSelected(false);
        sha1Button.setEnabled(false);
        ButtonGroup checksumGroup = new ButtonGroup();
        checksumGroup.add(md5Button);
        checksumGroup.add(sha1Button);
        JPanel checksumGroupPanel = new JPanel(new FlowLayout());
        checksumGroupPanel.add(checksumLabel);
        checksumGroupPanel.add(md5Button);
        checksumGroupPanel.add(sha1Button);
        checksumGroupPanel.setBorder(border);

        // Holey bag control
        JLabel holeyLabel = new JLabel(getMessage("bag.label.isholey"));
        holeyLabel.setToolTipText(getMessage("bag.isholey.help"));
        holeyCheckbox = new JCheckBox(getMessage("bag.checkbox.isholey"));
        holeyCheckbox.setBorder(border);
        holeyCheckbox.setSelected(false);
        holeyCheckbox.addActionListener(new HoleyBagHandler());
        holeyCheckbox.setToolTipText(getMessage("bag.isholey.help"));

        // Bag is to be serialized control
        JLabel serializeLabel = new JLabel(getMessage("bag.label.ispackage"));
        serializeLabel.setToolTipText(getMessage("bag.serializetype.help"));
        noneButton = new JRadioButton(getMessage("bag.serializetype.none"));
        noneButton.setSelected(true);
        noneButton.setEnabled(false);
        AbstractAction serializeListener = new SerializeBagHandler();
        noneButton.addActionListener(serializeListener);
        noneButton.setToolTipText(getMessage("bag.serializetype.none.help"));

        zipButton = new JRadioButton(getMessage("bag.serializetype.zip"));
        zipButton.setSelected(false);
        zipButton.setEnabled(false);
        zipButton.addActionListener(serializeListener);
        zipButton.setToolTipText(getMessage("bag.serializetype.zip.help"));

        tarButton = new JRadioButton(getMessage("bag.serializetype.tar"));
        tarButton.setSelected(false);
        tarButton.setEnabled(false);
        tarButton.addActionListener(serializeListener);
        tarButton.setToolTipText(getMessage("bag.serializetype.tar.help"));

        ButtonGroup serializeGroup = new ButtonGroup();
        serializeGroup.add(noneButton);
        serializeGroup.add(zipButton);
        serializeGroup.add(tarButton);
        serializeGroupPanel = new JPanel(new FlowLayout());
        serializeGroupPanel.add(serializeLabel);
        serializeGroupPanel.add(noneButton);
        serializeGroupPanel.add(zipButton);
        serializeGroupPanel.add(tarButton);
        serializeGroupPanel.setBorder(border);
        serializeGroupPanel.setEnabled(false);
        serializeGroupPanel.setToolTipText(getMessage("bag.serializetype.help"));

        GridBagLayout gridLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        
        buildConstraints(gbc, 0, 0, 1, 1, 40, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(projectLabel, gbc);
        buildConstraints(gbc, 1, 0, 1, 1, 60, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(projectPane, gbc);

        buildConstraints(gbc, 0, 1, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(holeyLabel, gbc);
        buildConstraints(gbc, 1, 1, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(holeyCheckbox, gbc);

        buildConstraints(gbc, 0, 2, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(serializeLabel, gbc);
        buildConstraints(gbc, 1, 2, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(serializeGroupPanel, gbc);

        buildConstraints(gbc, 0, 3, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(checksumLabel, gbc);
        buildConstraints(gbc, 1, 3, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(checksumGroupPanel, gbc);
        
        JPanel checkPanel = new JPanel(gridLayout);
        checkPanel.add(projectLabel);
        checkPanel.add(projectPane);
        projectList.setEnabled(false);
        checkPanel.add(holeyLabel);
        checkPanel.add(holeyCheckbox);
        holeyCheckbox.setEnabled(false);
        checkPanel.add(serializeLabel);
        checkPanel.add(serializeGroupPanel);
        checkPanel.add(checksumLabel);
        checkPanel.add(checksumGroupPanel);
//      JLabel filler = new JLabel("");
//      buildConstraints(gbc, 0, 4, 2, 1, 1, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST);
//      gridLayout.setConstraints(filler, gbc);
//      checkPanel.add(filler);

        return checkPanel;
    }
    
    private void enableBagSettings(boolean b) {
        projectList.setEnabled(b);
        holeyCheckbox.setEnabled(b);
        serializeGroupPanel.setEnabled(b);
        zipButton.setEnabled(b);
        tarButton.setEnabled(b);
        noneButton.setEnabled(b);
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
    
    private void initializeProfile() {
   		userProjects = bagger.getProjects();
   		Object[] projectArray = userProjects.toArray();
    	Project bagProject = bag.getProject();
    	if (bagProject == null) {
    		bagProject = (Project) projectArray[0];
    		bag.setProject(bagProject);
    	}
   		Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	if (a != null) this.username = a.getName();
    	else this.username = getMessage("user.name");
    	if (user == null) {
    		user = new Contact();
    		Organization org = new Organization();
    		user.setOrganization(org);    		
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
                       		Organization org = profile.getPerson().getOrganization();
                       		DefaultBagInfo bagInfo = bag.getInfo();
                       		BaggerOrganization bagOrg = bagInfo.getBagOrganization();
                       		bagOrg.setContact(profile.getContact());
                       		bagOrg.setOrgName(org.getName());
                       		bagOrg.setOrgAddress(org.getAddress());
                       		bagInfo.setBagOrganization(bagOrg);
                       		bag.setInfo(bagInfo);
                       		user = profile.getPerson();
                   		}
            		}
            	}
            	if (!found) {
            		log.error("BagView.initializeProfile - profile does NOT exist: " + project.getId());
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
    		username = getMessage("user.name");
    		user = new Contact();
    		Organization org = new Organization();
    		user.setOrganization(org);
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
		profile.setPerson(user);
		profile.setUsername(username);
		Contact contact = new Contact();
		contact.setOrganization(new Organization());
		profile.setContact(contact);
		return profile;
    }

    private class ProjectListHandler implements ListSelectionListener {
    	public void valueChanged(ListSelectionEvent e) {
        	JList jlist = (JList)e.getSource();
        	String selected = (String) jlist.getSelectedValue();
        	display("BagView.projectList valueChanged: " + selected);
        	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(getMessage("bag.project.edeposit"))) {
        		bag.setIsEdeposit(true);
        	} else {
        		bag.setIsEdeposit(false);
        	}
        	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(getMessage("bag.project.ndnp"))) {
        		bag.setIsNdnp(true);
        	} else {
        		bag.setIsNdnp(false);
        	}
        	updateBaggerRules();
        	changeProject(selected);
        }
    }
    
    private void changeProject(String selected) {
        bagInfoInputPane.verifyForms(bag);
        updateProfile();

    	Object[] project_array = userProjects.toArray();
        for (int i=0; i < userProjects.size(); i++) {
        	Project project = (Project)project_array[i];
        	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(project.getName())) {
        		display("bagProject: " + project.getId());
        		bag.setProject(project);
        		Object[] profiles = userProfiles.toArray();
        		for (int j=0; j < profiles.length; j++) {
        			Profile profile = (Profile) profiles[j];
        			if (profile.getProjectId() == project.getId()) {
        				Contact person = profile.getPerson();
        				if (person == null) person = new Contact();
                   		Organization org = person.getOrganization();
                   		if (org == null) org = new Organization();
                   		DefaultBagInfo bagInfo = bag.getInfo();
                   		BaggerOrganization bagOrg = bagInfo.getBagOrganization();
                   		Contact contact = profile.getContact();
                   		if (contact == null) {
                   			contact = new Contact();
                   		}
                   		bagOrg.setContact(contact);
                   		bagOrg.setOrgName(org.getName());
                   		bagOrg.setOrgAddress(org.getAddress());
                   		bagInfo.setBagOrganization(bagOrg);
                   		bag.setInfo(bagInfo);
                   		user = profile.getPerson();
                   		bagInfoInputPane.populateForms(bag);
                   	}
        		}
        	}
        }
    }
    
    private String updateBaggerRules() {
        baggerRules.init(bag.getIsEdeposit(), bag.getIsNdnp(), bag.getIsHoley());
        String messages = "";
        bagInfoInputPane.populateForms(bag);
        messages = bagInfoInputPane.updateForms(bag);
        updateBagInfoInputPaneMessages(messages);
        bagInfoInputPane.update(bag);
        bag.updateStrategy();
        
        return messages;
    }

    private void newDefaultBag(File f) {
    	String bagName = "";
    	bag = new DefaultBag(f);
    	if (f == null) {
        	bagName = getMessage("bag.label.noname");
    	} else {
	    	bagName = f.getName();
    	}
		bag.setName(bagName);
    }

    private class CreateNewBagHandler extends AbstractAction {
		private static final long serialVersionUID = 2922141723188929572L;
		public void actionPerformed(ActionEvent e) {
			newBag();
	    	bag.setIsNewbag(true);
	    	addDataButton.setEnabled(true);
	    	updatePropButton.setEnabled(true);
	    	bagButtonPanel.invalidate();
        }
    }

    private void newBag() {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	String messages = "";
    	this.bagCount++;

    	bagInfoInputPane.enableForms(bag, true);
    	clearExistingBag(messages);
    	String bagName = getMessage("bag.label.name");
    	bagName += "" + this.bagCount;
		bag.setName(bagName);
		bag.setRootDir(bagRootPath);
    	messages = updateBaggerRules();
    	initializeProfile();
    	bag.copyFormToBag();
    	enableBagSettings(true);

    	bagInfoInputPane.populateForms(bag);
        messages = bagInfoInputPane.updateForms(bag);
        compositePane.setBag(bag);
        compositePane.updateCompositePaneTabs(bag, messages);
        updateBagInfoInputPaneMessages(messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private class OpenBagHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
	        File selectFile = new File(File.separator+".");
	        JFrame frame = new JFrame();
			JFileChooser fo = new JFileChooser(selectFile);
		    fo.setDialogType(JFileChooser.OPEN_DIALOG);
		    fo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		    if (bagRootPath != null) fo.setCurrentDirectory(bagRootPath.getParentFile());
			fo.setDialogTitle("Existing Bag Location");
        	int option = fo.showOpenDialog(frame);

            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fo.getSelectedFile();
                if (file == null) file = bagRootPath;
                openExistingBag(file);
                addDataButton.setEnabled(true);
                updatePropButton.setEnabled(true);
                saveButton.setEnabled(true);
                saveAsButton.setEnabled(true);
                bagButtonPanel.invalidate();
                validateButton.setEnabled(true);
                topButtonPanel.invalidate();
                bag.setIsNewbag(false);
            }
		}
    }
    
    private void openExistingBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	String messages = "";
    	bagInfoInputPane.enableForms(bag, true);
    	clearExistingBag(messages);
    	messages = updateBaggerRules();
    	messages = "";

		try {
	    	newDefaultBag(file);
		} catch (Exception ex) {
	    	newDefaultBag(null);
			log.error("BagView.openExistingBag DefaultBag: " + ex.getMessage());
        	messages +=  "Failed to create bag: " + ex.getMessage() + "\n";
		}
    	bag.copyBagToForm();
    	if (bag.getInfo().getBagSize().isEmpty()) {
        	bag.setSize(bag.getDataSize());
    	}
    	if (bag.getIsEdeposit()) {
    		messages += updateProject(getMessage("bag.project.edeposit"));
    	} else if (bag.getIsNdnp()) {
    		messages += updateProject(getMessage("bag.project.ndnp"));
    	} else {
    		messages += updateProject(getMessage("bag.project.noproject"));    		
    	}
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
		bagTree.addParentNode(rootSrc);
		bagTree.populateNodes(bag, rootSrc);
        bag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);
    	enableBagSettings(true);

    	messages += bag.validateForms(!bagInfoInputPane.hasValidBagForms(bag));
		messages += bag.completeBag();
		messages += bag.validateMetadata();
		bag.isSerialized(true);
        validateBag(messages);
        compositePane.setBag(bag);
        compositePane.updateCompositePaneTabs(bag, messages);
    	bagInfoInputPane.populateForms(bag);
    	messages += bagInfoInputPane.updateForms(bag);
    	updateBagInfoInputPaneMessages(messages);
   		bagInfoInputPane.updateSelected(bag);
        bagInfoInputPane.update(bag);

    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private class AddDataHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
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
                    addBagData(files);
                } else {
                	File file = fc.getSelectedFile();
                	addBagData(file, true);
                }
                saveAsButton.setEnabled(true);
                bagButtonPanel.invalidate();
            	validateButton.setEnabled(false);
            	topButtonPanel.invalidate();
            }
		}
    }

    private void addBagData(File[] files) {
    	if (files != null) {
        	for (int i=0; i < files.length; i++) {
        		log.info("BagView.addBagData[" + i + "] " + files[i].getName());
        		if (i < files.length-1) addBagData(files[i], false);
        		else addBagData(files[i], true);
        	}
    	}
    }

    private void addBagData(File file, boolean lastFileFlag) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	String messages = "";
    	File parentSrc = file.getParentFile().getAbsoluteFile();
    	try {
        	bag.getBag().addPayload(file);
        	if (lastFileFlag) bag.getBag().complete();
        	bagTree.addNodes(file);
            bagTree.addTree(parentSrc, file, bag.getRootDir());
    	} catch (Exception e) {
        	messages += "Failed to add file path: " + file.getName() + "\n";
    		log.error("BagView.addBagData: " + file.getAbsolutePath() + " error: " + e.getMessage());
    	}

        bag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);
    	bagInfoInputPane.populateForms(bag);
        bagInfoInputPane.update(bag);
    	messages += getMessage("bag.message.filesadded") + " " + file.getName() + "\n";
    	compositePane.setBag(bag);
    	compositePane.updateCompositePaneTabs(bag, messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private class ValidateBagHandler extends AbstractAction {
		private static final long serialVersionUID = 2922141723188929572L;
		public void actionPerformed(ActionEvent e) {
			validateBag("");
        }
    }

    private void validateBag(String messages) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	messages += bagInfoInputPane.updateForms(bag);
    	updateBagInfoInputPaneMessages(messages);
    	bagInfoInputPane.updateSelected(bag);
		messages += bag.validateBag(this.bag.getBag());
		compositePane.setBag(bag);
		compositePane.updateCompositePaneTabs(bag, messages);
        bagInfoInputPane.update(bag);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

	private void confirmWriteBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	if (bag.getSize() > DefaultBag.MAX_SIZE) {
	        		confirmAcceptBagSize();
	        	} else {
		        	bagRootPath = tmpRootPath;
	                writeBag(bagRootPath);
	                validateButton.setEnabled(true);
	            	topButtonPanel.invalidate();
	            	tmpRootPath = null;
	        	}
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(getMessage("bag.dialog.title.create"));
	    dialog.setConfirmationMessage(getMessage("bag.dialog.message.create"));
	    dialog.showDialog();
	}

	private void confirmAcceptBagSize() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	bagRootPath = tmpRootPath;
                writeBag(bagRootPath);
                validateButton.setEnabled(true);
            	topButtonPanel.invalidate();
            	tmpRootPath = null;
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(getMessage("bag.dialog.title.create"));
	    dialog.setConfirmationMessage(getMessage("bag.dialog.message.accept"));
	    dialog.showDialog();
	}

    private class SaveBagAsHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private FileFilter noFilter;
		private FileFilter zipFilter;
		private FileFilter tarFilter;
		public SaveBagAsHandler() {
			super();
			noFilter = new FileFilter() {
	            public boolean accept(File f) {
		              return f.isFile() || f.isDirectory();
	            }
	            public String getDescription() {
	            	return "";
	            }
			};
			zipFilter = new FileFilter() {
	            public boolean accept(File f) {
		              return f.getName().toLowerCase().endsWith("."+DefaultBag.ZIP_LABEL)
		                  || f.isDirectory();
	            }
	            public String getDescription() {
	            	return "*."+DefaultBag.ZIP_LABEL;
	            }
		    };
		    tarFilter = new FileFilter() {
	            public boolean accept(File f) {
	                return f.getName().toLowerCase().endsWith("."+DefaultBag.TAR_LABEL)
	                    || f.isDirectory();
	            }
	            public String getDescription() {
	            	return "*."+DefaultBag.TAR_LABEL;
	            }
	        };
		}

		public void actionPerformed(ActionEvent e) {
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
        	if (bag.getName() != null && !bag.getName().equalsIgnoreCase(getMessage("bag.label.noname"))) {
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
    	        		writeBag(bagRootPath);
    	        		if (bag.isSerialized()) saveButton.setEnabled(true);
    	        		validateButton.setEnabled(true);
    	        		topButtonPanel.invalidate();
    	        	}
            	}
            }
        }
    }
	
    private class SaveBagHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			writeBag(bagRootPath);
        }
    }
    
    private void writeBagErrorDialog(String msg) {
    	MessageDialog dialog = new MessageDialog(getMessage("bag.dialog.error"), msg);
	    dialog.showDialog();
    }

    private void writeBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        String messages = ""; //getMessage("bag.message.creating");
        bag.setRootDir(file);

        messages += bagInfoInputPane.updateForms(bag);
        updateBagInfoInputPaneMessages(messages);

        try {
            messages += bag.write(!bagInfoInputPane.hasValidBagForms(bag));
        } catch (Exception e) {
			bag.isSerialized(false);
			messages += "Error creating bag: " + file + "\n" + e.getMessage();
        	writeBagErrorDialog("Error creating bag: " + file + "\n" + e.getMessage());
        }
        if (bag.isSerialized()) {
            bagInfoInputPane.populateForms(bag);
            bagInfoInputPane.updateSelected(bag);
            bagInfoInputPane.update(bag);
            saveButton.setEnabled(false);
        }
        compositePane.setBag(bag);
        compositePane.updateCompositePaneTabs(bag, messages);
        BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private void clearExistingBag(String messages) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	bagInfoInputPane.enableForms(bag, false);
    	newDefaultBag(null);
    	this.baggerRules.clear();
    	bagTree = new BagTree();
    	bag.setIsNewbag(true);
        bag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);
    	enableBagSettings(false);
        noneButton.setSelected(true);
    	bagInfoInputPane.populateForms(bag);
        messages = bagInfoInputPane.updateForms(bag);
        updateBagInfoInputPaneMessages(messages);
        bagInfoInputPane.update(bag);
        compositePane.setBag(bag);
        compositePane.updateCompositePaneTabs(bag, messages);

    	addDataButton.setEnabled(false);
        updatePropButton.setEnabled(false);
    	saveButton.setEnabled(false);
    	saveAsButton.setEnabled(false);
    	validateButton.setEnabled(false);
    	bagButtonPanel.invalidate();
    	topButtonPanel.invalidate();
        BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
    
    private class UpdateBagHandler extends AbstractAction {
		private static final long serialVersionUID = -6833185707352381008L;
		public void actionPerformed(ActionEvent e) {
            String messages = bagInfoInputPane.updateForms(bag);
            updateBagInfoInputPaneMessages(messages);
            bagInfoInputPane.updateSelected(bag);
            messages += updateProfile();
            compositePane.setBag(bag);
            compositePane.updateCompositePaneTabs(bag, messages);
            bag.copyFormToBag();
        }
    } 

	private void updateBagInfoInputPaneMessages(String messages) {
		boolean isMessage = true;
		//isMessage = (messages == null || messages.length() == 0);
		display("BagView.updateMessages: " + messages);
        if (!bagInfoInputPane.hasFormErrors(bag) && isMessage) {
            messages += getMessage("bag.message.info.update") + "\n";
            infoFormMessagePane.setBackground(infoColor);
            infoFormMessagePane.setMessage("");
            updatePropButton.setBackground(buttonColor);
        } else {
            infoFormMessagePane.setBackground(errorColor);
            infoFormMessagePane.setMessage(getMessage("error.form"));
            updatePropButton.setBackground(errorColor);
        }
        updatePropButton.invalidate();
        infoFormMessagePane.invalidate();
    }

    private String updateProfile() {
    	String message = "";
    	Project project = bag.getProject();
    	if (project == null) return message;
    	Object[] profiles = this.userProfiles.toArray();
    	for (int i=0; i < profiles.length; i++) {
    		Profile profile = (Profile) profiles[i];
    		if (profile.getProject().getId() == project.getId()) {
    			Contact contact = bag.getInfo().getBagOrganization().getContact();
    			profile.setContact(contact);
    			profile.setContactId(contact.getId());
    			profile.setProject(project);
    			profile.setProjectId(project.getId());
    			profile.setPerson(user);
    			profile.setUsername(username);
    			try {
    				message = getMessage("profile.message.changed") + " " + project.getName() + "\n";
    			} catch (Exception e) {
    				message = getMessage("profile.message.error") + " " + e.getMessage() + "\n";
    				log.error(message);
    			}
    			profiles[i] = profile;
    		}
    	}
    	return message;
    }
    
    public String updateProject(String project) {
    	String messages = "";

   		Object[] projectArray = userProjects.toArray();
   		for (int i=0; i < projectArray.length; i++) {
   			Project bagProject = (Project) projectArray[i];
   			if (project != null && project.matches(bagProject.getName())) {
   				bag.setProject(bagProject);
   			}
   		}
   		messages += updateProfile();
    	if (bag.getIsEdeposit()) {
    		projectList.setSelectedValue(project, true);
    	} else if (bag.getIsNdnp()) {
    		projectList.setSelectedValue(project, true);
    	}
		return messages;
    }
    
    private class SerializeBagHandler extends AbstractAction {
    	private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			JRadioButton cb = (JRadioButton)e.getSource();
            boolean isSel = cb.isSelected();
            if (isSel) {
            	if (cb == noneButton) {
                	bag.setIsSerial(false);
                	bag.setSerialMode(DefaultBag.NO_MODE);
            	} else if (cb == zipButton) {
                	bag.setIsSerial(true);
                	bag.setSerialMode(DefaultBag.ZIP_MODE);
            	} else if (cb == tarButton) {
                	bag.setIsSerial(true);
                	bag.setSerialMode(DefaultBag.TAR_MODE);
            	} else {
                	bag.setIsSerial(false);
                	bag.setSerialMode(DefaultBag.NO_MODE);
            	}
            }
        }
    }

    private class HoleyBagHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
    		JCheckBox cb = (JCheckBox)e.getSource();
                
    		// Determine status
    		boolean isSelected = cb.isSelected();
    		if (isSelected) {
    			bag.setIsHoley(true);
    		} else {
    			bag.setIsHoley(false);
    		}
    		String messages = "";
    		updateBaggerRules();
    		bagInfoInputPane.updateSelected(bag);
    		compositePane.setBag(bag);
    		compositePane.updateCompositePaneTabs(bag, messages);
    	}
    }

    private class NextButtonHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
            int selected = bagInfoInputPane.getSelectedIndex();
            int count = bagInfoInputPane.getComponentCount();
            if (selected >= 0 && selected < count-1) {
            	bagInfoInputPane.setSelectedIndex(selected+1);
            } else {
            	bagInfoInputPane.setSelectedIndex(0);
            }
            bagInfoInputPane.verifyForms(bag);
            bagInfoInputPane.update(bag);
            updateProfile();
            if (bagInfoInputPane.hasFormErrors(bag)) {
            	infoFormMessagePane.setMessage(getMessage("error.form"));
                infoFormMessagePane.setBackground(errorColor);
            } else {
            	infoFormMessagePane.setMessage("");
                infoFormMessagePane.setBackground(infoColor);
            }
            bagInfoInputPane.invalidate();
            java.awt.Component comp = bagInfoInputPane.getComponent(0);
            comp.requestFocus();
            comp.transferFocus();
        }
    }

    private void updateCommands() {
    	saveExecutor.setEnabled(true);
    }

    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register(GlobalCommandIds.SAVE, saveExecutor);
    }

    // Save the profiles to the database and write changes to disk
    private class SaveExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	bagger.storeBaggerUpdates(userProfiles, userHomeDir);
			String message = getMessage("profile.message.saved") + " " + bag.getProject().getName() + "\n";
			display("SaveExecutor: " + message);
			compositePane.setBag(bag);
			compositePane.updateCompositePaneTabs(bag, message);
        }
    }

    public void onApplicationEvent(ApplicationEvent e) {
        if (e instanceof LifecycleApplicationEvent) {
        	//display("BagView.onApplicationEvent");
            LifecycleApplicationEvent le = (LifecycleApplicationEvent)e;
            if (le.getEventType() == LifecycleApplicationEvent.CREATED && le.objectIs(Profile.class)) {
            	// TODO: Add and insert the newly created profile
            }
        }
    }

    private String getInitialConsoleMsg() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(this.getMessage("consolepane.msg.help"));
    	buffer.append("\n\n");
    	buffer.append(this.getMessage("compositepane.message.ismetadata"));
    	buffer.append("\n");
    	buffer.append(this.getMessage("consolepane.ismetadata.help"));
    	buffer.append("\n\n");
    	buffer.append(this.getMessage("compositepane.message.iscomplete"));
    	buffer.append("\n");
    	buffer.append(this.getMessage("consolepane.iscomplete.help"));
    	buffer.append("\n\n");
    	buffer.append(this.getMessage("compositepane.message.isserialized"));
    	buffer.append("\n");
    	buffer.append(this.getMessage("consolepane.isserialized.help"));
    	buffer.append("\n\n");
    	buffer.append(this.getMessage("compositepane.message.isvalid"));
    	buffer.append("\n");
    	buffer.append(this.getMessage("consolepane.isvalid.help"));
    	return buffer.toString();
    }

}