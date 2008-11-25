
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.*;
import gov.loc.repository.bagger.bag.*;
import gov.loc.repository.bagit.impl.AbstractBagConstants;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
//import javax.swing.SwingWorker;

//import org.springframework.richclient.progress.ProgressMonitor;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.command.support.GlobalCommandIds;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.progress.BusyIndicator;
import org.springframework.util.Assert;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagView extends AbstractView implements ApplicationListener {
	private static final Log log = LogFactory.getLog(BagView.class);
	private static final int BAG_NEW = 0;
	private static final int BAG_OPEN = 1;
	private static final int BAG_VALIDATE = 2;
	private static final int BAG_ADD_DATA = 3;
	private static final int BAG_CREATE = 4;

	private Bagger bagger;
    private BaggerBag baggerBag;
    private int bagCount = 1;
    private File bagRootPath;
    private BagTree bagTree;
    private Collection<Project> userProjects;
    private Collection<Profile> userProfiles;
	private String username;
	private Contact user;
	private String userHomeDir;
    private String formErrorsMessage = "!! Form errors exist.";

	private ProgressMonitor progressMonitor;
    private BagTreePanel bagTreePanel;
    private CompositePane bagDisplayPane;
    private BagInfoInputPane bagInfoInputPane;
    private BagTextPane infoFormMessagePane;
    private JPanel bagButtonPanel;
    private JPanel bagPanel;
    private JPanel topButtonPanel;
    private JButton addDataButton;
    private JButton saveButton;
    private JButton validateButton;
    private SaveExecutor saveExecutor = new SaveExecutor();
    private Color errorColor = new Color(255, 128, 128);
//    private Color infoColor = new Color(255,165,25);
	private Color infoColor = new Color(100, 100, 120);

    public void setBagger(Bagger bagger) {
        Assert.notNull(bagger, "The bagger property is required");
        display("BagView.setBagger: " );
        this.bagger = bagger;
    }

    @Override
    protected JComponent createControl() {
    	ApplicationWindow window = Application.instance().getActiveWindow();
    	JFrame f = window.getControl();
    	f.setBackground(Color.red);
    	this.userHomeDir = System.getProperty("user.home");
        display("BagView.createControl - User Home Path: "+ userHomeDir);
        display("BagView.createControl - message.properties getMessage: " + getMessage("bagName.description"));

    	if (baggerBag == null) baggerBag = new BaggerBag();
    	baggerBag.generate();
    	String bagName = baggerBag.getInfo().getBagName();
    	bagName += "" + this.bagCount;
		baggerBag.setName(bagName);
		baggerBag.getInfo().setBagName(bagName);

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
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        JFileChooser fc = new JFileChooser(selectFile);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        Color fgColor = new Color(250, 250, 250);
        Color bgColor = new Color(50, 50, 150);

    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    	buttonPanel.setBackground(bgColor);

        Action createAction = new FileAction(frame, fc, BAG_NEW);
    	JButton createButton = new JButton("Start New Bag");
    	createButton.addActionListener(createAction);
    	createButton.setOpaque(true);
    	createButton.setBackground(bgColor);
    	createButton.setForeground(fgColor);
    	buttonPanel.add(createButton);

        Action openAction = new FileAction(frame, fc, BAG_OPEN);
    	JButton openButton = new JButton("Open Existing Bag");
    	openButton.addActionListener(openAction);
    	openButton.setOpaque(true);
    	openButton.setBackground(bgColor);
    	openButton.setForeground(fgColor);
    	buttonPanel.add(openButton);

    	validateButton = new JButton("Validate Bag");
        validateButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 2922141723188929572L;
			public void actionPerformed(ActionEvent e) {
				validateBag();
            }
        });
        validateButton.setEnabled(false);
        validateButton.setOpaque(true);
    	validateButton.setBackground(bgColor);
    	validateButton.setForeground(fgColor);
        buttonPanel.add(validateButton);
        
        return buttonPanel;
    }
    
    private JPanel createBagPanel() {
    	bagButtonPanel = createBagButtonPanel();
    	bagTree = new BagTree();
    	bagTreePanel = new BagTreePanel(bagTree);
    	bagDisplayPane = new CompositePane(baggerBag);

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        buildConstraints(glbc, 0, 0, 1, 1, 5, 30, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagButtonPanel, glbc);

        buildConstraints(glbc, 1, 0, 1, 1, 95, 30, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(bagTreePanel, glbc);

        buildConstraints(glbc, 0, 1, 2, 1, 100, 70, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(bagDisplayPane, glbc);

        bagPanel = new JPanel(layout);
        bagPanel.add(bagButtonPanel);
    	bagPanel.add(bagTreePanel);
        bagPanel.add(bagDisplayPane);

        return bagPanel;
    }
    
    private JPanel createBagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new BorderLayout(5, 5));
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        JFileChooser fc = new JFileChooser(selectFile);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        Action addAction = new FileAction(frame, fc, BAG_ADD_DATA);
    	addDataButton = new JButton("Add Data");
    	addDataButton.setEnabled(false);
    	addDataButton.addActionListener(addAction);
        buttonPanel.add(addDataButton, BorderLayout.NORTH);

    	saveButton = new JButton("Save Bag");
        saveButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				createBag(bagRootPath);
                validateButton.setEnabled(true);
            	topButtonPanel.invalidate();
            }
        });
        saveButton.setEnabled(false);
        buttonPanel.add(saveButton, BorderLayout.SOUTH);
        
        return buttonPanel;
    }

    private JScrollPane createInfoInputPane() {
    	JPanel bagSettingsPanel = createBagSettingsPanel();
    	bagInfoInputPane = new BagInfoInputPane(baggerBag, username, user);

    	// Create a panel for the form error messages and the update button
        JButton nextButton = new JButton("Next");
        //nextButton.setBackground(infoColor);
        nextButton.setMnemonic(KeyEvent.VK_ENTER); // ALT+Enter
        //nextButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        nextButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
                int selected = bagInfoInputPane.getSelectedIndex();
                int count = bagInfoInputPane.getComponentCount();
                if (selected >= 0 && selected < count-1) {
                	bagInfoInputPane.setSelectedIndex(selected+1);
                } else {
                	bagInfoInputPane.setSelectedIndex(0);
                }
                bagInfoInputPane.verifyForms(baggerBag);
                updateProfile();
                if (bagInfoInputPane.hasFormErrors()) {
                	infoFormMessagePane.setMessage(formErrorsMessage);
                } else {
                	infoFormMessagePane.setMessage("");
                }
                bagInfoInputPane.invalidate();
                java.awt.Component comp = bagInfoInputPane.getComponent(0);
                comp.requestFocus();
                comp.transferFocus();
            }
        });
    	// Create a panel for the form error messages and the update button
        JButton updatePropButton = new JButton("Save Updates");
        //updatePropButton.setBackground(infoColor);
        updatePropButton.setMnemonic('u');
        //updatePropButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        updatePropButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = -6833185707352381008L;
			public void actionPerformed(ActionEvent e) {
                String messages = bagInfoInputPane.updateForms(baggerBag);
                messages += updateMessages(messages);
                bagInfoInputPane.updateSelected();
                messages += updateProfile();
                bagDisplayPane.updateTabs(baggerBag, messages);
            }
        });
        
        JPanel infoLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoFormMessagePane = new BagTextPane("");
        if (bagInfoInputPane.hasFormErrors()) {
        	infoFormMessagePane.setMessage(formErrorsMessage);
        }
        Dimension labelDimension = bagInfoInputPane.getPreferredSize();
        int offset = updatePropButton.getWidth();
        if (offset == 0) offset = 80;
        labelDimension.setSize(labelDimension.getWidth()-offset, 25);
        infoFormMessagePane.setPreferredSize(labelDimension);
        infoFormMessagePane.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        infoLabelPanel.add(infoFormMessagePane, "North");
        if (bagInfoInputPane.hasFormErrors()) {
            infoFormMessagePane.setBackground(errorColor);
        	//infoLabelPanel.setBackground(errorColor);
        } else {
            infoFormMessagePane.setBackground(infoColor);
        	//infoLabelPanel.setBackground(infoColor);
        }

        JLabel button = new JLabel("");
    	
        // Combine the information panel with the forms pane
        GridBagLayout infoLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        buildConstraints(gbc, 0, 0, 3, 1, 10, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(infoLabelPanel, gbc);

        buildConstraints(gbc, 0, 1, 3, 1, 50, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagInfoInputPane, gbc);

        buildConstraints(gbc, 0, 2, 1, 1, 80, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
        infoLayout.setConstraints(button, gbc);

        buildConstraints(gbc, 1, 2, 1, 1, 10, 0, GridBagConstraints.NONE, GridBagConstraints.EAST);
        infoLayout.setConstraints(nextButton, gbc);

        buildConstraints(gbc, 2, 2, 1, 1, 10, 0, GridBagConstraints.NONE, GridBagConstraints.EAST);
        infoLayout.setConstraints(updatePropButton, gbc);
        
        buildConstraints(gbc, 0, 3, 3, 1, 20, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagSettingsPanel, gbc);
        
        JPanel infoPanel = new JPanel(infoLayout);
    	//infoPanel.setBackground(new Color(200, 200, 200));
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        infoPanel.setBorder(emptyBorder);
        infoPanel.add(infoLabelPanel);
        infoPanel.add(bagInfoInputPane);
        infoPanel.add(button);
        infoPanel.add(nextButton);
        infoPanel.add(updatePropButton);
        infoPanel.add(bagSettingsPanel);
        
    	JScrollPane infoScrollPane = new JScrollPane();
    	infoScrollPane.setViewportView(infoPanel);
    	return infoScrollPane;
    }

    private JPanel createBagSettingsPanel() {
        Border border = new EmptyBorder(5, 5, 5, 5);

        // Project control
        JLabel projectLabel = new JLabel("Bag project: ");
        DefaultListModel listModel = new DefaultListModel();
        Object[] array = userProjects.toArray();
        for (int i=0; i < userProjects.size(); i++) listModel.addElement(((Project)array[i]).getName());
        JList projectList = new JList(listModel);
        projectList.setName("Bag Project");
        projectList.setSelectedIndex(0);
        projectList.setVisibleRowCount(2);
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            	JList jlist = (JList)e.getSource();
            	String selected = (String) jlist.getSelectedValue();
            	display("valueChanged: " + selected);
            	if (selected.equalsIgnoreCase("eDeposit")) {
            		baggerBag.setIsCopyright(true);
            		baggerBag.getInfo().setIsCopyright(true);
            	} else {
            		baggerBag.setIsCopyright(false);
            		baggerBag.getInfo().setIsCopyright(false);
            	}
            	changeProject(selected);
            }
        });
    	String selected = (String) projectList.getSelectedValue();
    	if (selected.equalsIgnoreCase("eDeposit")) {
    		baggerBag.setIsCopyright(true);
    	} else {
    		baggerBag.setIsCopyright(false);
    	}
        JScrollPane projectPane = new JScrollPane(projectList);

        // Checksum control
        JLabel groupLabel = new JLabel("Checksum Type: ");        
        JRadioButton md5Button = new JRadioButton("MD5");
        md5Button.setSelected(true);
        md5Button.setEnabled(true);
        JRadioButton sha1Button = new JRadioButton("SHA1");
        sha1Button.setSelected(false);
        sha1Button.setEnabled(false);
        ButtonGroup group = new ButtonGroup();
        group.add(md5Button);
        group.add(sha1Button);
        JPanel groupPanel = new JPanel(new FlowLayout());
        groupPanel.add(groupLabel);
        groupPanel.add(md5Button);
        groupPanel.add(sha1Button);
        groupPanel.setBorder(border);

        // Holey bag control
        JLabel holeyLabel = new JLabel("Holey Bag?: ");
        JCheckBox holeyCheckbox = new JCheckBox("Holey Bag");
        holeyCheckbox.setBorder(border);
        holeyCheckbox.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 75893358194076314L;
			public void actionPerformed(ActionEvent e) {
                JCheckBox cb = (JCheckBox)e.getSource();
                
                // Determine status
                boolean isSel = cb.isSelected();
                if (isSel) {
                	baggerBag.setIsHoley(true);
                } else {
                	baggerBag.setIsHoley(false);
                }
                String messages = bagInfoInputPane.updateForms(baggerBag);
                messages += updateMessages(messages);
                bagInfoInputPane.updateSelected();
                messages += updateProfile();
                bagDisplayPane.updateTabs(baggerBag, messages);
            }
        });

        // Bag is to be serialized control
        JLabel serialLabel = new JLabel("Serialize Bag?: ");
        JCheckBox serialCheckbox = new JCheckBox("Serial Bag");
        serialCheckbox.setBorder(border);
        serialCheckbox.setSelected(true);
        serialCheckbox.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = -9157876307330134254L;
			public void actionPerformed(ActionEvent e) {
                JCheckBox cb = (JCheckBox)e.getSource();
                
                // Determine status
                boolean isSel = cb.isSelected();
                if (isSel) {
                	baggerBag.setIsSerial(true);
                } else {
                	baggerBag.setIsSerial(false);
                }
                String messages = bagInfoInputPane.updateForms(baggerBag);
                messages += updateMessages(messages);
                bagInfoInputPane.updateSelected();
                messages += updateProfile();
                bagDisplayPane.updateTabs(baggerBag, messages);
            }
        });

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
        gridLayout.setConstraints(serialLabel, gbc);
        buildConstraints(gbc, 1, 2, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(serialCheckbox, gbc);

        buildConstraints(gbc, 0, 3, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(groupLabel, gbc);
        buildConstraints(gbc, 1, 3, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(groupPanel, gbc);
        
//        JLabel filler = new JLabel("");
//        buildConstraints(gbc, 0, 4, 2, 1, 1, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST);
//        gridLayout.setConstraints(filler, gbc);

        JPanel checkPanel = new JPanel(gridLayout);
        checkPanel.add(projectLabel);
        checkPanel.add(projectPane);
        checkPanel.add(holeyLabel);
        checkPanel.add(holeyCheckbox);
        checkPanel.add(serialLabel);
        checkPanel.add(serialCheckbox);
        checkPanel.add(groupLabel);
        checkPanel.add(groupPanel);
//        checkPanel.add(filler);

        return checkPanel;
    }
/* */
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
    	Project bagProject = baggerBag.getProject();
    	if (bagProject == null) {
    		bagProject = (Project) projectArray[0];
    		baggerBag.setProject(bagProject);
    	}
   		Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	if (a != null) this.username = a.getName();
    	else this.username = "user";
    	if (user == null) {
    		user = new Contact();
    		Organization org = new Organization();
    		user.setOrganization(org);    		
    	}
    	if (this.username != null && this.username.length() > 0) {
        	display("BagView.initializeBag getAuthenticationUser:: " + this.username);
        	display("BagView.initializeBag projects: " + bagger.getProjects());
        	Collection<Profile> profiles = bagger.findProfiles(this.username);
        	if (profiles == null) profiles = new ArrayList<Profile>();
        	userProfiles = profiles;
        	Object[] profileArray = profiles.toArray();
        	for (int p=0; p < projectArray.length; p++) {
        		Project project = (Project) projectArray[p];
        		boolean found = false;
            	for (int i=0; i < profileArray.length; i++) {
            		display("BagView.initializeBag profile:\n" + profileArray[i].toString());
            		Profile profile = (Profile) profileArray[i];
            		if (project.getId() == profile.getProject().getId()) {
            			found = true;
            			display("BagView.initializeBag profile exists for project: " + project.getId());
                   		if (project.getId() == bagProject.getId()) {
                       		Organization org = profile.getPerson().getOrganization();
                       		BagInfo bagInfo = baggerBag.getInfo();
                       		BagOrganization bagOrg = bagInfo.getBagOrganization();
                       		bagOrg.setContact(profile.getContact());
                       		bagOrg.setOrgName(org.getName());
                       		bagOrg.setOrgAddress(org.getAddress());
                       		bagInfo.setBagOrganization(bagOrg);
                       		baggerBag.setInfo(bagInfo);
                       		user = profile.getPerson();
                   		}
            		}
            	}
            	if (!found) {
            		log.info("profile does NOT exist: " + project.getId());
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
    		username = "user";
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

    private void changeProject(String selected) {
        bagInfoInputPane.verifyForms(baggerBag);
        updateProfile();

    	Object[] project_array = userProjects.toArray();
        for (int i=0; i < userProjects.size(); i++) {
        	Project project = (Project)project_array[i];
        	if (selected.equalsIgnoreCase(project.getName())) {
        		log.info("bagProject: " + project.getId());
        		baggerBag.setProject(project);
        		Object[] profiles = userProfiles.toArray();
        		for (int j=0; j < profiles.length; j++) {
        			Profile profile = (Profile) profiles[j];
        			if (profile.getProjectId() == project.getId()) {
        				Contact person = profile.getPerson();
        				if (person == null) person = new Contact();
                   		Organization org = person.getOrganization();
                   		if (org == null) org = new Organization();
                   		BagInfo bagInfo = baggerBag.getInfo();
                   		BagOrganization bagOrg = bagInfo.getBagOrganization();
                   		Contact contact = profile.getContact();
                   		if (contact == null) {
                   			contact = new Contact();
                   		}
                   		bagOrg.setContact(contact);
                   		bagOrg.setOrgName(org.getName());
                   		bagOrg.setOrgAddress(org.getAddress());
                   		bagInfo.setBagOrganization(bagOrg);
                   		baggerBag.setInfo(bagInfo);
                   		user = profile.getPerson();
                   		bagInfoInputPane.populateForms(baggerBag);
                   	}
        		}
        	}
        }
    }

    // This action creates and shows a modal save-file dialog.
    public class FileAction extends AbstractAction {
		private static final long serialVersionUID = -3466819146072877868L;
		JFileChooser chooser;
        JFrame frame;
        int mode;
    
        FileAction(JFrame frame, JFileChooser chooser, int mode) {
            super("File Chooser...");
            this.chooser = chooser;
            this.frame = frame;
            this.mode = mode;
            switch(mode) {
            case BAG_OPEN:
            case BAG_ADD_DATA:
                this.chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            	break;
            case BAG_NEW:
            case BAG_CREATE:
                this.chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            	break;
           	default:
            	display("Not handled.");
            }
        }
    
        public void actionPerformed(ActionEvent evt) {
        	int option;
        	if (mode == BAG_OPEN || mode == BAG_ADD_DATA) {
            	option = chooser.showOpenDialog(frame);
        	} else if (mode == BAG_NEW || mode == BAG_CREATE) {
            	option = chooser.showSaveDialog(frame);
        	} else {
            	display("Unsupported mode: " + mode);
            	return;
        	}

        	// These should all be directory selection only, user does not need 
        	// to type in bag name it is retrieved from the info form
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
            	switch(mode) {
                case BAG_NEW:
                	addDataButton.setEnabled(true);
                	bagButtonPanel.invalidate();
                	newBag(file);
                	break;
                case BAG_OPEN:
                	addDataButton.setEnabled(true);
                	saveButton.setEnabled(true);
                	bagButtonPanel.invalidate();
                	validateButton.setEnabled(true);
                	topButtonPanel.invalidate();
                	openExistingBag(file);
                	break;
                case BAG_ADD_DATA:
                    addBagData(file);
                    saveButton.setEnabled(true);
                    bagButtonPanel.invalidate();
                	validateButton.setEnabled(false);
                	topButtonPanel.invalidate();
                	break;
                case BAG_VALIDATE:
                	validateBag();
                	break;
                case BAG_CREATE:
                	bagRootPath = file;
                    createBag(file);
                    validateButton.setEnabled(true);
                	topButtonPanel.invalidate();
                	break;
               	default:
                	display("The mode selected: " + mode + " is not supported.");
                }
            }
        }
    }
    
	// TODO: create a new empty bag
    private void newBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	String messages = "";
        messages = bagInfoInputPane.updateForms(baggerBag);
    	bagRootPath = file;
    	log.info("BagView.newBag location: " + file.getAbsolutePath() );
    	try {
    		File rootDir = new File(file.getAbsolutePath(), baggerBag.getName());
    		baggerBag.setRootDir(rootDir);
        	messages += updateMessages(messages);
    	} catch (Exception e) {
        	messages += "\n" + "Failed to create new bag! " + e.getMessage();
    	}
    	this.bagCount++;
    	clearExistingBag(messages);
        log.info("BagView.newBag.getName: " + baggerBag.getName());
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
    
    private void openExistingBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	String messages = "";
		baggerBag.openBag(file);
		baggerBag.setName(file.getName());
		baggerBag.setRootDir(file);
		baggerBag.getInfo().setBagName(file.getName());
    	bagRootPath = file.getParentFile();

		File rootSrc = new File(file, AbstractBagConstants.DATA_DIRECTORY);
		bagTree.addParentNode(rootSrc);
		File[] listFiles = rootSrc.listFiles();
		for (int i=0; i<listFiles.length; i++) {
			File f = listFiles[i];
	    	BaggerFileEntity bfeSrc = new BaggerFileEntity(rootSrc, f, baggerBag.getRootDir());
	        bagTree.addTree(rootSrc, f, baggerBag.getRootDir());
	        baggerBag.addRootSrc(bfeSrc);
		}

        baggerBag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);
    	baggerBag.generate();

        bagInfoInputPane.populateForms(baggerBag);
        messages = bagInfoInputPane.updateForms(baggerBag);
        messages += updateMessages(messages);
        // TODO: need to figure out why validation field is not updating for valid input
        bagInfoInputPane.updateSelected();
        messages += updateProfile();
        messages += "Files have been added to the bag from: " + file.getName();
        bagInfoInputPane.update();
        //bagInfoInputPane.updateSelected();
    	bagDisplayPane.updateTabs(baggerBag, messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private void addBagData(File file) {
    	String messages = "";
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	File parentSrc = file.getParentFile().getAbsoluteFile();
    	log.info("BagView.addBagData parent: " + parentSrc.getAbsolutePath());
    	log.info("BagView.addBagData: " + file.getAbsolutePath());
    	log.info("BagView.BagRootDir: " + baggerBag.getRootDir());

    	baggerBag.addPayload(file);

    	BaggerFileEntity bfe = new BaggerFileEntity(parentSrc, file, baggerBag.getRootDir());
    	bagTree.addNodes(file);
        bagTree.addTree(parentSrc, file, baggerBag.getRootDir());
        baggerBag.addRootSrc(bfe);

        baggerBag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);
    	baggerBag.generate();

    	messages += "Files have been added to the bag from: " + file.getName();
    	bagDisplayPane.updateTabs(baggerBag, messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
        
	// TODO: validate the current bag and output to zip if option is selected
    private void validateBag() {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	String messages = "";
    	messages = bagInfoInputPane.updateForms(baggerBag);
    	messages += updateMessages(messages);
    	bagInfoInputPane.updateSelected();
    	messages += updateProfile();
		messages += baggerBag.validateForms();
		if (baggerBag.getIsValidForms()) {
			messages += baggerBag.validateAndBag();			
        }
    	bagDisplayPane.updateTabs(baggerBag, messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private void createBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        display("BagView.SaveFileAction: " + file);
        String messages = "Creating the bag...\n";

        messages = bagInfoInputPane.updateForms(baggerBag);
        messages += updateMessages(messages);
        bagInfoInputPane.updateSelected();
    	// TODO Break this down into multiple steps so that each step can send bag progress message to the console.
        if (!bagInfoInputPane.hasFormErrors()) {
            messages = baggerBag.write(file);
        }
    	bagDisplayPane.updateTabs(baggerBag, messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
    
    private void clearExistingBag(String messages) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	baggerBag = new BaggerBag();

    	bagTree = new BagTree();
        baggerBag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);
    	
    	baggerBag.generate();
    	String bagName = baggerBag.getInfo().getBagName();
    	bagName += "" + this.bagCount;
		baggerBag.setName(bagName);
		baggerBag.getInfo().setBagName(bagName);
    	initializeProfile();

    	bagInfoInputPane.populateForms(baggerBag);
        bagInfoInputPane.updateSelected();
        messages += updateProfile();
        bagInfoInputPane.update();
        //bagInfoInputPane.updateSelected();
        bagDisplayPane.updateTabs(baggerBag, messages);
        BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private String updateMessages(String messages) {
        if (!bagInfoInputPane.hasFormErrors() && (messages == null || messages.length() == 0)) {
            messages = "Organization and Contact information has been updated.";
            infoFormMessagePane.setBackground(infoColor);
            infoFormMessagePane.setMessage("");
            messages += "\n";
        } else {
            messages = "Organization and Contact information has form errors.";
            infoFormMessagePane.setBackground(errorColor);
            infoFormMessagePane.setMessage(formErrorsMessage);
        	messages += "\n";
        }
        infoFormMessagePane.invalidate();
        return messages;
    }

    private String updateProfile() {
    	String message = "\n";
    	Project project = baggerBag.getProject();
    	Object[] profiles = this.userProfiles.toArray();
    	for (int i=0; i < profiles.length; i++) {
    		Profile profile = (Profile) profiles[i];
    		if (profile.getProject().getId() == project.getId()) {
    			Contact contact = baggerBag.getInfo().getBagOrganization().getContact();
    			profile.setContact(contact);
    			profile.setContactId(contact.getId());
    			profile.setProject(project);
    			profile.setProjectId(project.getId());
    			profile.setPerson(user);
    			profile.setUsername(username);
    			try {
    				//bagger.storeProfile(profile);
    				message = "Profile for project: " + project.getName() + " has been saved.";
    				message += "\n";
    			} catch (Exception e) {
    				message = "ERROR updating this profile: " + e.getMessage();
    				display(message);
    			}
    			profiles[i] = profile;
    		}
    	}
    	return message;
    }
    
    public void onApplicationEvent(ApplicationEvent e) {
    	display("BagView.onApplicationEvent");
        if (e instanceof LifecycleApplicationEvent) {
        	display("BagView.onApplicationEvent.LifecycleApplicationEvent");
            LifecycleApplicationEvent le = (LifecycleApplicationEvent)e;
            if (le.getEventType() == LifecycleApplicationEvent.CREATED && le.objectIs(Profile.class)) {
            	// TODO: Add the newly created profile
            }
        } else {
        	display("BagView.onApplicationEvent.validate");
        }
    }

    public void setBag(BaggerBag baggerBag) {
        this.baggerBag = baggerBag;
    }

    public BaggerBag getBag() {
        return this.baggerBag;
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
    
    public void setBagNameToDisplay(String bagName) {
        this.baggerBag.setName(bagName);
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
	
    private void updateCommands() {
    	saveExecutor.setEnabled(true);
    }

    protected void registerLocalCommandExecutors(PageComponentContext context) {
        context.register(GlobalCommandIds.SAVE_AS, saveExecutor);
    }

    // Save the profiles to the database and write changes to disk
    private class SaveExecutor extends AbstractActionCommandExecutor {
        public void execute() {
        	String message = "\n";
        	// TODO: call the form and profile update for executing this
        	bagger.storeBaggerUpdates(userProfiles, userHomeDir);
        }
    }
}