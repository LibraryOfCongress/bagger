
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
	private static final int BAG_WRITE = 4;
	private static final int BAG_CLEAR = 5;
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
    private CompositePane bagDisplayPane;
    private BagInfoInputPane bagInfoInputPane;
    private BagTextPane infoFormMessagePane;
    private JPanel bagButtonPanel;
    private JPanel bagPanel;
    private JPanel topButtonPanel;
    private JButton addDataButton;
    private JButton saveButton;
    private JButton clearButton;
    private JButton validateButton;
    private JButton updatePropButton;
    private JList projectList;
    private JCheckBox holeyCheckbox;
    private SaveExecutor saveExecutor = new SaveExecutor();
    private Color errorColor = new Color(255, 128, 128);
	private Color infoColor = new Color(100, 100, 120);
	private Color buttonColor = new Color(100, 100, 120);

    public void setBagger(Bagger bagger) {
        Assert.notNull(bagger, "The bagger property is required");
        display("BagView.setBagger: " );
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
		//log.info(s);
	}

	public String getPropertyMessage(String property) {
    	return getMessage(property);
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
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        Color fgColor = new Color(250, 250, 250);
        Color bgColor = new Color(50, 50, 150);

    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    	buttonPanel.setBackground(bgColor);

        JFileChooser fc = new JFileChooser(selectFile);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("New Bag Location");
        Action createAction = new FileAction(frame, fc, BAG_NEW);
    	JButton createButton = new JButton(getMessage("bag.button.create"));
    	createButton.addActionListener(createAction);
    	createButton.setOpaque(true);
    	createButton.setBackground(bgColor);
    	createButton.setForeground(fgColor);
    	buttonPanel.add(createButton);

    	JFileChooser fo = new JFileChooser(selectFile);
        fo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	fo.setDialogTitle("Existing Bag Location");
        Action openAction = new FileAction(frame, fo, BAG_OPEN);
    	JButton openButton = new JButton(getMessage("bag.button.open"));
    	openButton.addActionListener(openAction);
    	openButton.setOpaque(true);
    	openButton.setBackground(bgColor);
    	openButton.setForeground(fgColor);
    	buttonPanel.add(openButton);

    	clearButton = new JButton(getMessage("bag.button.clear"));
    	clearButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 2922141723188929572L;
			public void actionPerformed(ActionEvent e) {
				confirmClearBag();
            }
        });
    	clearButton.setEnabled(false);
    	clearButton.setOpaque(true);
    	clearButton.setBackground(bgColor);
    	clearButton.setForeground(fgColor);
        buttonPanel.add(clearButton);

    	validateButton = new JButton(getMessage("bag.button.validate"));
        validateButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 2922141723188929572L;
			public void actionPerformed(ActionEvent e) {
				validateBag("");
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
    	bagDisplayPane = new CompositePane(this);

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

        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setDialogTitle("Add File or Directory");
        Action addAction = new FileAction(frame, fc, BAG_ADD_DATA);
    	addDataButton = new JButton(getMessage("bag.button.add"));
    	addDataButton.setEnabled(false);
    	addDataButton.addActionListener(addAction);
        buttonPanel.add(addDataButton, BorderLayout.NORTH);

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	if (bag.getIsNewbag())
            fc.setDialogTitle("Save");
    	else
            fc.setDialogTitle("Save As");
        Action saveAction = new FileAction(frame, fc, BAG_WRITE);
    	saveButton = new JButton(getMessage("bag.button.save"));
    	saveButton.addActionListener(saveAction);
        saveButton.setEnabled(false);
        buttonPanel.add(saveButton, BorderLayout.SOUTH);
        
        return buttonPanel;
    }

    private JScrollPane createInfoInputPane() {
    	JPanel bagSettingsPanel = createBagSettingsPanel();
    	bagInfoInputPane = new BagInfoInputPane(this, username, user);

    	// Create a panel for the form error messages and the update button
        JButton nextButton = new JButton(getMessage("button.next"));
        nextButton.setMnemonic(KeyEvent.VK_ENTER);
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
                bagInfoInputPane.verifyForms();
                bagInfoInputPane.update();
                updateProfile();
                if (bagInfoInputPane.hasFormErrors()) {
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
        });
    	// Create a panel for the form error messages and the update button
        updatePropButton = new JButton(getMessage("button.saveUpdates"));
        buttonColor = updatePropButton.getBackground();
        updatePropButton.setMnemonic('u');
        updatePropButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = -6833185707352381008L;
			public void actionPerformed(ActionEvent e) {
                String messages = bagInfoInputPane.updateForms();
                updateMessages(messages);
                bagInfoInputPane.updateSelected();
                messages += updateProfile();
                bagDisplayPane.updateBagPaneTabs(messages);
                bag.copyFormToBag(bag.getBag());
            }
        });
        
        JPanel infoLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        infoFormMessagePane = new BagTextPane("");
        if (bagInfoInputPane.hasFormErrors()) {
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
        if (bagInfoInputPane.hasFormErrors()) {
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
        JLabel projectLabel = new JLabel(getMessage("bag.label.project"));
        DefaultListModel listModel = new DefaultListModel();
        Object[] array = userProjects.toArray();
        for (int i=0; i < userProjects.size(); i++) listModel.addElement(((Project)array[i]).getName());
        projectList = new JList(listModel);
        projectList.setName(getMessage("bag.label.projectList"));
        projectList.setVisibleRowCount(2);
        projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projectList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
            	JList jlist = (JList)e.getSource();
            	String selected = (String) jlist.getSelectedValue();
            	display("BagView.projectList valueChanged: " + selected);
            	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(getMessage("bag.project.edeposit"))) {
            		bag.setIsEdeposit(true);
            	} else {
            		bag.setIsEdeposit(false);
            	}
            	updateBaggerRules();
            	changeProject(selected);
            }
        });
    	String selected = (String) projectList.getSelectedValue();
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(getMessage("bag.project.edeposit"))) {
    		bag.setIsEdeposit(true);
    	} else {
    		bag.setIsEdeposit(false);
    	}
        JScrollPane projectPane = new JScrollPane(projectList);

        // Checksum control
        JLabel checksumLabel = new JLabel(getMessage("bag.label.checksumType"));
        JRadioButton md5Button = new JRadioButton(getMessage("bag.checksumType.md5"));
        md5Button.setSelected(true);
        md5Button.setEnabled(true);
        JRadioButton sha1Button = new JRadioButton(getMessage("bag.checksumType.sha1"));
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
        JLabel holeyLabel = new JLabel(getMessage("bag.label.isHoley"));
        holeyCheckbox = new JCheckBox(getMessage("bag.checkbox.isHoley"));
        holeyCheckbox.setBorder(border);
        holeyCheckbox.addActionListener(new AbstractAction() {
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
                bagInfoInputPane.updateSelected();
                messages += updateProfile();
                bagDisplayPane.updateBagPaneTabs(messages);
            }
        });

        // Bag is to be serialized control
        // Checksum control
        JLabel serializeLabel = new JLabel(getMessage("bag.label.isPackage"));
        JRadioButton noneButton = new JRadioButton(getMessage("bag.serializeType.none"));
        noneButton.setSelected(false);
        noneButton.setEnabled(true);
        noneButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1;
			public void actionPerformed(ActionEvent e) {
				JRadioButton cb = (JRadioButton)e.getSource();
                boolean isSel = cb.isSelected();
                if (isSel) {
                	bag.setIsSerial(false);
                	bag.setSerialMode(DefaultBag.NO_MODE);
                }
            }
        });
        JRadioButton zipButton = new JRadioButton(getMessage("bag.serializeType.zip"));
        zipButton.setSelected(false);
        zipButton.setEnabled(true);
        zipButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1;
			public void actionPerformed(ActionEvent e) {
				JRadioButton cb = (JRadioButton)e.getSource();
                boolean isSel = cb.isSelected();
                if (isSel) {
                	bag.setIsSerial(true);
                	bag.setSerialMode(DefaultBag.ZIP_MODE);
                }
            }
        });

        JRadioButton tarButton = new JRadioButton(getMessage("bag.serializeType.tar"));
        tarButton.setSelected(false);
        tarButton.setEnabled(true);
        tarButton.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1;
			public void actionPerformed(ActionEvent e) {
				JRadioButton cb = (JRadioButton)e.getSource();
                boolean isSel = cb.isSelected();
                if (isSel) {
                	bag.setIsSerial(true);
                	bag.setSerialMode(DefaultBag.TAR_MODE);
                }
            }
        });
        ButtonGroup serializeGroup = new ButtonGroup();
        serializeGroup.add(noneButton);
        serializeGroup.add(zipButton);
        serializeGroup.add(tarButton);
        JPanel serializeGroupPanel = new JPanel(new FlowLayout());
        serializeGroupPanel.add(serializeLabel);
        serializeGroupPanel.add(noneButton);
        serializeGroupPanel.add(zipButton);
        serializeGroupPanel.add(tarButton);
        serializeGroupPanel.setBorder(border);
        // TODO
        serializeGroupPanel.setEnabled(false);

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

    private void changeProject(String selected) {
        bagInfoInputPane.verifyForms();
        updateProfile();

    	Object[] project_array = userProjects.toArray();
        for (int i=0; i < userProjects.size(); i++) {
        	Project project = (Project)project_array[i];
        	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(project.getName())) {
        		log.debug("bagProject: " + project.getId());
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
        messages = bagInfoInputPane.updateForms();
        updateMessages(messages);
        bagInfoInputPane.update();
        
        return messages;
    }

    // This action creates and shows a modal save-file dialog.
    public class FileAction extends AbstractAction {
		private static final long serialVersionUID = -3466819146072877868L;
		JFileChooser chooser;
        JFrame frame;
        int mode;
    
        FileAction(JFrame frame, JFileChooser chooser, int mode) {
            super(getMessage("file.chooser"));
        	this.chooser = chooser;
            this.frame = frame;
            this.mode = mode;

            switch(mode) {
            case BAG_OPEN:
                this.chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                break;
            case BAG_ADD_DATA:
                this.chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            	this.chooser.setMultiSelectionEnabled(true);
            	break;
            case BAG_NEW:
            	this.chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            	break;
            case BAG_WRITE:
            	this.chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            	break;
           	default:
            	log.error("BagView.FileAction - mode not handled: " + mode);
            }
        }
    
        public void actionPerformed(ActionEvent evt) {
        	int option;
        	if (mode == BAG_OPEN || mode == BAG_NEW) {
        		// Open an existing bag directory, zip/tar file or 
        		// set the location of a new bag
            	option = chooser.showOpenDialog(frame);
        	} else if (mode == BAG_ADD_DATA) {
        		// Add a directory, file or multiple files to the bag
            	option = chooser.showOpenDialog(frame);
        	} else if (mode == BAG_WRITE) {
            	if (bag.getIsNewbag())
                    chooser.setDialogTitle("Save");
            	else
                    chooser.setDialogTitle("Save As");
            	chooser.setSelectedFile(bag.getRootDir());
            	chooser.setCurrentDirectory(bagRootPath);
//            	if (!bag.getIsNewbag()) option = chooser.showSaveDialog(frame);
//            	else option = JFileChooser.APPROVE_OPTION;
            	option = chooser.showSaveDialog(frame);
        	} else {
            	log.error("BagView.FileAction unsupported mode: " + mode);
            	return;
        	}

        	// These should all be directory selection only, user does not need 
        	// to type in bag name it is retrieved from the info form
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
            	switch(mode) {
                case BAG_NEW:
                	addDataButton.setEnabled(true);
                	clearButton.setEnabled(true);
                	bagButtonPanel.invalidate();
                	newBag(file);
                	bag.setIsNewbag(true);
                	break;
                case BAG_OPEN:
                	addDataButton.setEnabled(true);
                	saveButton.setEnabled(true);
                	clearButton.setEnabled(true);
                	bagButtonPanel.invalidate();
                	validateButton.setEnabled(true);
                	topButtonPanel.invalidate();
                	openExistingBag(file);
                	bag.setIsNewbag(false);
                	break;
                case BAG_ADD_DATA:
                    File[] files = chooser.getSelectedFiles();
                    if (files.length >0) {
                        addBagData(files);
                    } else {
                    	file = chooser.getSelectedFile();
                    	addBagData(file);
                    }
                    saveButton.setEnabled(true);
                    bagButtonPanel.invalidate();
                	validateButton.setEnabled(false);
                	topButtonPanel.invalidate();
                	break;
                case BAG_VALIDATE:
                	validateBag("");
                	break;
                case BAG_CLEAR:
                	confirmClearBag();
                	bag.setIsNewbag(true);
                	break;
                case BAG_WRITE:
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
        	        		writeBag(file);
        	        		validateButton.setEnabled(true);
        	        		topButtonPanel.invalidate();
        	        	}
                	}
                	break;
               	default:
                	log.error("BagView.FileAction - The mode selected: " + mode + " is not supported.");
                }
            }
        }
    }
    
    private void newBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	display("BagView.newBag location: " + file.getAbsolutePath() );
    	String messages = "";
    	bagRootPath = file;
    	this.bagCount++;

    	bagInfoInputPane.enableForms(true);
    	clearExistingBag(messages);
    	String bagName = getMessage("bag.label.name");
    	bagName += "" + this.bagCount;
		bag.setName(bagName);
		bag.getInfo().setBagName(bagName);
    	messages = updateBaggerRules();
    	initializeProfile();

    	bag.copyFormToBag(bag.getBag());
    	try {
    		File rootDir = new File(file.getAbsolutePath(), bag.getName());
    		bag.setRootDir(rootDir);
    	} catch (Exception e) {
        	messages += getMessage("error.bag.create") + " " + e.getMessage() + "\n";
    	}
    	enableBagSettings(true);

    	bagInfoInputPane.populateForms(bag);
        messages = bagInfoInputPane.updateForms();
        bagInfoInputPane.update();
    	bagDisplayPane.setBag(bag);
        bagDisplayPane.updateBagPaneTabs(messages);
    	updateMessages(messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
    
    private void openExistingBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	String messages = "";

    	bagInfoInputPane.enableForms(true);
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
    	bag.setRootDir(file);
		bagRootPath = file.getParentFile();
		File rootSrc = new File(file, bag.getDataDirectory());
		bagTree.addParentNode(rootSrc);
		bagTree.populateNodes(bag, rootSrc);
        bag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);
    	enableBagSettings(true);

		bagDisplayPane.setBag(bag);
    	bagDisplayPane.updateBagPaneTabs(messages);
    	// TODO: click validate button rather than automatically validate
    	//validateBag(messages);
    	bag.setSize(bag.getDataSize());
    	bagInfoInputPane.populateForms(bag);

    	messages += bagInfoInputPane.updateForms();
   		updateMessages(messages);
   		bagInfoInputPane.updateSelected();
        bagInfoInputPane.update();

    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private void addBagData(File[] files) {
    	if (files != null) {
        	for (int i=0; i < files.length; i++) {
        		log.info("BagView.addBagData[" + i + "] " + files[i].getName());
        		addBagData(files[i]);
        	}
    	}
    }

    private void addBagData(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	String messages = "";
    	File parentSrc = file.getParentFile().getAbsoluteFile();
    	try {
        	bag.getBag().addPayload(file);
        	bag.getBag().complete();
        	bagTree.addNodes(file);
            bagTree.addTree(parentSrc, file, bag.getRootDir());
    	} catch (Exception e) {
        	messages += "Failed to add file path: " + file.getName() + "\n";
    		log.error("BagView.addBagData: " + file.getAbsolutePath() + " error: " + e.getMessage());
    	}

        bag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);

    	bagInfoInputPane.populateForms(bag);
        bagInfoInputPane.update();
    	messages += getMessage("bag.message.filesAdded") + " " + file.getName() + "\n";
    	bagDisplayPane.setBag(bag);
    	bagDisplayPane.updateBagPaneTabs(messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
        
    private void validateBag(String messages) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	messages += bagInfoInputPane.updateForms();
   		updateMessages(messages);
    	bagInfoInputPane.updateSelected();
    	//messages += updateProfile();
		messages += bag.validateBag(this.bag.getBag());
		bagDisplayPane.setBag(bag);
    	bagDisplayPane.updateBagPaneTabs(messages);
        bagInfoInputPane.update();
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }

    private void writeBag(File file) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	display("BagView.SaveFileAction: " + file);
        String messages = getMessage("bag.message.creating");

        messages = bagInfoInputPane.updateForms();
        updateMessages(messages);
        bagInfoInputPane.updateSelected();
        if (!bagInfoInputPane.hasFormErrors()) {
        	if (file.getName().equalsIgnoreCase(bag.getName())) {
        		file = file.getParentFile();
        	}
            messages = bag.write(!bagInfoInputPane.hasFormErrors(), file);
        }
    	bagDisplayPane.setBag(bag);
    	bagDisplayPane.updateBagPaneTabs(messages);
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
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

    private void clearExistingBag(String messages) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	//messages = "";
    	newDefaultBag(null);
    	this.baggerRules.clear();
    	bagTree = new BagTree();
        bag.setRootTree(bagTree.getRootTree());
    	bagTreePanel.refresh(bagTree);
    	enableBagSettings(false);
    	bagInfoInputPane.populateForms(bag);
        messages = bagInfoInputPane.updateForms();
        updateMessages(messages);
        bagInfoInputPane.update();
    	bagDisplayPane.setBag(bag);
        bagDisplayPane.updateBagPaneTabs(messages);
        BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
    
    private void confirmClearBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	String messages = "";
	        	bagInfoInputPane.enableForms(false);
                clearExistingBag(messages);

            	addDataButton.setEnabled(false);
            	saveButton.setEnabled(false);
            	clearButton.setEnabled(false);
            	bagButtonPanel.invalidate();
            	validateButton.setEnabled(false);
            	topButtonPanel.invalidate();
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(getMessage("bag.dialog.title.clear"));
	    dialog.setConfirmationMessage(getMessage("bag.dialog.message.clear"));
	    dialog.showDialog();
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

	private void updateMessages(String messages) {
		log.debug("BagView.updateMessages: " + messages);
        if (!bagInfoInputPane.hasFormErrors() && (messages == null || messages.length() == 0)) {
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
    				//bagger.storeProfile(profile);
    				message = getMessage("profile.message.saved") + " " + project.getName() + "\n";
    			} catch (Exception e) {
    				message = getMessage("profile.message.error") + " " + e.getMessage() + "\n";
    				log.error(message);
    			}
    			profiles[i] = profile;
    		}
    	}
    	return message;
    }
    
    public void onApplicationEvent(ApplicationEvent e) {
        if (e instanceof LifecycleApplicationEvent) {
        	display("BagView.onApplicationEvent.LifecycleApplicationEvent");
            LifecycleApplicationEvent le = (LifecycleApplicationEvent)e;
            if (le.getEventType() == LifecycleApplicationEvent.CREATED && le.objectIs(Profile.class)) {
            	// TODO: Add and insert the newly created profile
            }
        } else {
        	display("BagView.onApplicationEvent.validate");
        }
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
    
    public void setBagNameToDisplay(String bagName) {
        this.bag.setName(bagName);
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
        }
    }
}