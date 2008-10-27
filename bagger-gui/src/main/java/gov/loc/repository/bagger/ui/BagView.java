
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.*;
import gov.loc.repository.bagger.bag.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import javax.swing.SwingWorker;

//import org.springframework.richclient.progress.ProgressMonitor;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageComponentContext;
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
	private static final int BAG_CREATE = 0;
	private static final int BAG_OPEN = 1;
	private static final int BAG_VALIDATE = 2;
	private static final int BAG_ADD_DATA = 3;
	private static final int BAG_SAVE = 4;

	private Bagger bagger;
    private Bag bag;
    private File bagRootPath;
    private BagTree bagTree;
    private Collection<Project> userProjects;
    private Collection<Profile> userProfiles;
	private String username;
	private Contact user;
    private String formErrorsMessage = "!! Form errors exist.";

	private ProgressMonitor progressMonitor;
    private BagTreePanel bagTreePanel;
    private CompositePane bagDisplayPane;
    private BagInfoInputPane bagInfoInputPane;
    private BagTextPane infoFormMessagePane;
    private boolean enableDataButton = false;
    private JPanel bagButtonPanel;
    private JButton addDataButton;
    
    public void setBagger(Bagger bagger) {
        Assert.notNull(bagger, "The bagger property is required");
        display("BagView.setBagger: " );
        this.bagger = bagger;
    }

    @Override
    protected JComponent createControl() {
    	ApplicationWindow window = Application.instance().getActiveWindow();
    	JFrame f = window.getControl();
    	f.setForeground(Color.red);
        display("BagView.createControl - User Home Path: "+ System.getProperty("user.home"));
        display("BagView.createControl - message.properties getMessage: " + getMessage("bagName.description"));

    	if (bag == null) bag = new Bag();
    	bag.init();
    	initializeProfile();

    	JPanel topButtonPanel = createTopButtonPanel();
    	JScrollPane infoInputPane = createInfoInputPane();
        JPanel bagPanel = createBagPanel();

        JPanel mainPanel = new JPanel(new GridLayout(1,2,10,10));
        mainPanel.add(bagPanel);
        mainPanel.add(infoInputPane);
        
    	JPanel bagViewPanel = new JPanel(new BorderLayout(2, 2));
    	bagViewPanel.add(topButtonPanel, BorderLayout.NORTH);
    	bagViewPanel.add(mainPanel, BorderLayout.CENTER);
        
        return bagViewPanel;
    }
    
    private JPanel createTopButtonPanel() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        JFileChooser fc = new JFileChooser(selectFile);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        Action createAction = new FileAction(frame, fc, BAG_CREATE);
    	JButton createButton = new JButton("Create New Bag");
    	createButton.addActionListener(createAction);
    	buttonPanel.add(createButton);

        Action openAction = new FileAction(frame, fc, BAG_OPEN);
    	JButton openButton = new JButton("Open Existing Bag");
    	openButton.addActionListener(openAction);
    	buttonPanel.add(openButton);

    	JButton validateButton = new JButton("Validate Bag");
        validateButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 2922141723188929572L;
			public void actionPerformed(ActionEvent e) {
				// TODO: validate the current bag and output to zip if option is selected
				saveBag(bagRootPath);
            }
        });
        buttonPanel.add(validateButton);

        return buttonPanel;
    }
    
    private JPanel createBagPanel() {
    	bagButtonPanel = createBagButtonPanel();
    	bagTree = new BagTree();
    	bagTreePanel = new BagTreePanel(bagTree);
    	bagDisplayPane = new CompositePane(bag);

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        buildConstraints(glbc, 0, 0, 1, 1, 5, 30, GridBagConstraints.NONE, GridBagConstraints.NORTH);
        layout.setConstraints(bagButtonPanel, glbc);

        buildConstraints(glbc, 1, 0, 1, 1, 95, 30, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(bagTreePanel, glbc);

        buildConstraints(glbc, 0, 1, 2, 1, 100, 70, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        layout.setConstraints(bagDisplayPane, glbc);

        JPanel bagPanel = new JPanel(layout);
        bagPanel.add(bagButtonPanel);
    	bagPanel.add(bagTreePanel);
        bagPanel.add(bagDisplayPane);

        return bagPanel;
    }
    
    private JPanel createBagButtonPanel() {
    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        JFileChooser fc = new JFileChooser(selectFile);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        Action addAction = new FileAction(frame, fc, BAG_ADD_DATA);
    	addDataButton = new JButton("Add Data");
    	addDataButton.setEnabled(this.enableDataButton);
    	addDataButton.addActionListener(addAction);
        buttonPanel.add(addDataButton);

        return buttonPanel;
    }

    private JScrollPane createInfoInputPane() {
    	JPanel bagSettingsPanel = createBagSettingsPanel();
    	bagInfoInputPane = new BagInfoInputPane(bag, username, user, bagSettingsPanel);

    	// Create a panel for the form error messages and the update button
        JButton updatePropButton = new JButton("Save Updates");
        updatePropButton.setMnemonic('u');
        updatePropButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        updatePropButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = -6833185707352381008L;
			public void actionPerformed(ActionEvent e) {
                String messages = bagInfoInputPane.updateForms(bag);
                messages += updateMessages(messages);
                messages += updateProfile();
                bagDisplayPane.updateTabs(bag, messages);
            }
        });
        
        BorderLayout labelLayout = new BorderLayout();
        labelLayout.setHgap(10);
        JPanel infoLabelPanel = new JPanel(labelLayout);
        infoFormMessagePane = new BagTextPane("");
        if (bagInfoInputPane.hasFormErrors()) {
        	infoFormMessagePane.setMessage(formErrorsMessage);
        }
        Dimension labelDimension = bagInfoInputPane.getPreferredSize();
        int offset = updatePropButton.getWidth();
        if (offset == 0) offset = 80;
        labelDimension.setSize(labelDimension.getWidth()-offset, 25);
        infoFormMessagePane.setPreferredSize(labelDimension);
        infoLabelPanel.add(infoFormMessagePane, "Center");
        infoLabelPanel.add(updatePropButton, "East");

        // Combine the information panel with the forms pane
        GridBagLayout infoLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        buildConstraints(gbc, 0, 0, 1, 1, 10, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(infoLabelPanel, gbc);

        buildConstraints(gbc, 0, 1, 1, 1, 70, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagInfoInputPane, gbc);

        JPanel infoPanel = new JPanel(infoLayout);
        Border emptyBorder = new EmptyBorder(10, 10, 10, 10);
        infoPanel.setBorder(emptyBorder);
        infoPanel.add(infoLabelPanel);
        infoPanel.add(bagInfoInputPane);
        
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
        projectList.setVisibleRowCount(3);
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            	JList jlist = (JList)e.getSource();
            	String selected = (String) jlist.getSelectedValue();
            	display("valueChanged: " + selected);
            	if (selected.equalsIgnoreCase("eDeposit")) {
            		bag.setIsCopyright(true);
            		bag.getInfo().setIsCopyright(true);
            	} else {
            		bag.setIsCopyright(false);
            		bag.getInfo().setIsCopyright(false);
            	}
            	changeProject(selected);
            }
        });
    	String selected = (String) projectList.getSelectedValue();
    	if (selected.equalsIgnoreCase("eDeposit")) {
    		bag.setIsCopyright(true);
    	} else {
    		bag.setIsCopyright(false);
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
                	bag.setIsHoley(true);
                } else {
                	bag.setIsHoley(false);
                }
                String messages = bagInfoInputPane.updateForms(bag);
                messages += updateMessages(messages);
                messages += updateProfile();
                bagDisplayPane.updateTabs(bag, messages);
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
                	bag.setIsSerial(true);
                } else {
                	bag.setIsSerial(false);
                }
                String messages = bagInfoInputPane.updateForms(bag);
                messages += updateMessages(messages);
                messages += updateProfile();
                bagDisplayPane.updateTabs(bag, messages);
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
        
        JLabel filler = new JLabel("");
        buildConstraints(gbc, 0, 4, 2, 1, 1, 10, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        gridLayout.setConstraints(filler, gbc);

        JPanel checkPanel = new JPanel(gridLayout);
        checkPanel.add(projectLabel);
        checkPanel.add(projectPane);
        checkPanel.add(holeyLabel);
        checkPanel.add(holeyCheckbox);
        checkPanel.add(serialLabel);
        checkPanel.add(serialCheckbox);
        checkPanel.add(groupLabel);
        checkPanel.add(groupPanel);
        checkPanel.add(filler);

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
    	Project bagProject = bag.getProject();
    	if (bagProject == null) {
    		bagProject = (Project) projectArray[0];
    		bag.setProject(bagProject);
    	}

   		Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	if (a != null) this.username = a.getName();
    	if (this.username != null && !this.username.isEmpty()) {
        	display("BagView.initializeBag getAuthenticationUser:: " + this.username);
        	display("BagView.initializeBag projects: " + bagger.getProjects());
        	Collection<Profile> profiles = bagger.findProfiles(a.getName());
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
                       		Address address = org.getAddress();
                       		BagInfo bagInfo = bag.getInfo();
                       		BagOrganization bagOrg = bagInfo.getBagOrganization();
                       		bagOrg.setContact(profile.getContact());
                       		bagOrg.setOrgName(org.getName());
                       		bagOrg.setOrgAddress(address.toString(true));
                       		bagInfo.setBagOrganization(bagOrg);
                       		bag.setInfo(bagInfo);
                       		user = profile.getPerson();
                   		}
            		}
            	}
            	if (!found) {
            		System.out.println("profile does NOT exist: " + project.getId());
            		userProfiles.add(createProfile(project));
            	}
        	}
    	} else {
    		username = "user";
    		user = new Contact();
        	user.setContactType(new ContactType());
    		Organization org = new Organization();
    		org.setAddress(new Address());
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
    	contact.setContactType(new ContactType());
		contact.setOrganization(new Organization());
		profile.setContact(contact);
		return profile;
    }

    private void changeProject(String selected) {
        Object[] project_array = userProjects.toArray();
        for (int i=0; i < userProjects.size(); i++) {
        	Project project = (Project)project_array[i];
        	if (selected.equalsIgnoreCase(project.getName())) {
        		System.out.println("bagProject: " + project.getId());
        		bag.setProject(project);
        		Object[] profiles = userProfiles.toArray();
        		for (int j=0; j < profiles.length; j++) {
        			Profile profile = (Profile) profiles[j];
        			if (profile.getProjectId() == project.getId()) {
                   		Organization org = profile.getPerson().getOrganization();
                   		Address address = org.getAddress();
                   		BagInfo bagInfo = bag.getInfo();
                   		BagOrganization bagOrg = bagInfo.getBagOrganization();
                   		Contact contact = profile.getContact();
                   		if (contact == null) {
                   			contact = new Contact();
                        	contact.setContactType(new ContactType());
                   		}
                   		bagOrg.setContact(contact);
                   		bagOrg.setOrgName(org.getName());
                   		bagOrg.setOrgAddress(address.toString(true));
                   		bagInfo.setBagOrganization(bagOrg);
                   		bag.setInfo(bagInfo);
                   		user = profile.getPerson();
                   		bagInfoInputPane.populateForms(bag);
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
            case BAG_CREATE:
            case BAG_SAVE:
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
        	} else if (mode == BAG_CREATE || mode == BAG_SAVE) {
            	option = chooser.showSaveDialog(frame);
        	} else {
            	display("Unsupported mode: " + mode);
            	return;
        	}

            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
            	switch(mode) {
                case BAG_OPEN:
                	enableDataButton = true;
                	addDataButton.setEnabled(enableDataButton);
                	bagButtonPanel.invalidate();
                	// TODO: If zip read contents, else open bag and call createBag(file)
                	break;
                case BAG_ADD_DATA:
                	// TODO: call openBag and add contents to the current bag data directory
                    openBag(file);
                	break;
                case BAG_CREATE:
                	enableDataButton = true;
                	addDataButton.setEnabled(enableDataButton);
                	bagButtonPanel.invalidate();
                	// TODO: create a new empty bag populated with bagit.txt
                	bagRootPath = file;
                	break;
                case BAG_VALIDATE:
                case BAG_SAVE:
                	// TODO: save the currently open bag, if pre-existing bag, prompt for overwrite
                	bagRootPath = file;
                    saveBag(file);
                	break;
               	default:
                	display("The mode selected: " + mode + " is not supported.");
                }
            }
        }
    }
    
    private void openBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	File rootSrc = file.getAbsoluteFile();
        display("OpenFileAction.actionPerformed filePath: " + file.getPath() + " rootPath: " + rootSrc.getPath() );
        bag.setRootSrc(rootSrc);
        updateTree(file);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
        
    private void saveBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        display("BagView.SaveFileAction: " + file);
        String messages = "Creating the bag...";

        messages = bagInfoInputPane.updateForms(bag);
        messages += updateMessages(messages);
    	// TODO Break this down into multiple steps so that each step can send bag progress message to the console.
        // TODO What if file already exists?  Error or message to overwrite
        messages = bag.write(file);
    	updateBagTabs(messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private String updateProfile() {
    	String message = "";
    	Project project = bag.getProject();
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
    				bagger.storeProfile(profile);
    				message = "\nProfile for project: " + project.getName() + " has been saved.";
    			} catch (Exception e) {
    				message = "ERROR updating this profile: " + e.getMessage();
    				display(message);
    			}
    			profiles[i] = profile;
    		}
    	}
    	return message;
    }
    
    private void updateTree(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
//    	progressMonitor = Application.instance().getActiveWindow().getStatusBar().getProgressMonitor();
//    	progressMonitor.taskStarted("Bagger called", -1);
//    	rootFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    	String messages = new String();
    	bagTree.update(file);
    	bagTreePanel.update(bagTree);
    	bag.setRootTree(bagTree.getRootTree());
        messages = "Files have been added to the bag from: " + file.getName();
    	updateBagTabs(messages);        

//		rootFrame.setCursor(Cursor.getDefaultCursor());
//    	progressMonitor.done();
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private void updateBagTabs(String messages) {
    	bag.init();
    	bagDisplayPane.updateTabs(bag, messages);
    }
    
    private String updateMessages(String messages) {
        if (messages == null) {
            messages = "Organization and Contact information has been updated.";
            infoFormMessagePane.setMessage("");
            messages += "\n";
        } else {
            messages = "Organization and Contact information has form errors.";
            infoFormMessagePane.setMessage(formErrorsMessage);
        	messages += "\n";
        }
        infoFormMessagePane.invalidate();
        return messages;
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

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    public Bag getBag() {
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
}