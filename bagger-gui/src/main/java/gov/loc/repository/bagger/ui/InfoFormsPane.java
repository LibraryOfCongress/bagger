
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.ProfileField;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.handlers.ClearFieldHandler;
import gov.loc.repository.bagger.ui.handlers.HoleyBagHandler;
import gov.loc.repository.bagger.ui.handlers.LoadFieldHandler;
import gov.loc.repository.bagger.ui.handlers.ProjectListHandler;
import gov.loc.repository.bagger.ui.handlers.SaveFieldHandler;
import gov.loc.repository.bagger.ui.handlers.SerializeBagHandler;
import gov.loc.repository.bagger.ui.handlers.UpdateBagHandler;
import gov.loc.repository.bagit.BagFactory.Version;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

public class InfoFormsPane extends JScrollPane {
	private static final long serialVersionUID = -5988111446773491301L;
    private BagView bagView;
    private DefaultBag bag;
	private JPanel bagSettingsPanel;
	private JPanel buttonPanel;
    private JPanel infoPanel;
    protected JPanel serializeGroupPanel;
    private JScrollPane bagInfoScrollPane;
	private NewProjectFrame newProjectFrame;

    public BagInfoInputPane bagInfoInputPane;
	public UpdateBagHandler updateBagHandler;
    public SerializeBagHandler serializeBagHandler;

	protected JTextField bagNameField;
    protected JButton saveButton;
    protected JButton loadDefaultsButton;
    protected JButton clearDefaultsButton;
	public JButton updatePropButton;
    public JButton newProjectButton;
    public JButton removeProjectButton;
	protected JLabel bagVersionValue = new JLabel(Version.V0_96.versionString);
    public JLabel holeyValue;
    public JLabel serializeLabel;
    public JLabel serializeValue;
	protected JComboBox bagVersionList;
	protected JComboBox profileList;
    public JCheckBox holeyCheckbox;
    public JCheckBox defaultProject;
    public JRadioButton noneButton;
    public JRadioButton zipButton;
    public JRadioButton tarButton;
    public JRadioButton tarGzButton;
    public JRadioButton tarBz2Button;
    public FileFilter noFilter;
    public FileFilter zipFilter;
    public FileFilter tarFilter;
	private JButton btnEditProfile;
	private JButton saveProfiles;
	private JButton showAllDefaultFeildsButton;
	private JButton addCustomFieldButton;
	private JButton removeProfileBtn;

    public InfoFormsPane(BagView bagView) {
    	super();
		this.bagView = bagView;
		this.bag = bagView.getBag();
		bag.getInfo().setBag(bag);
    	createScrollPane(false);
    }

    private void createScrollPane(boolean enabled) {
    	buttonPanel = createButtonPanel(enabled);

    	bagInfoInputPane = new BagInfoInputPane(bagView, bagView.bagProject.username, bagView.bagProject.projectContact, false);
    	bagInfoInputPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	bagInfoInputPane.setEnabled(false);
    	bagSettingsPanel = createSettingsPanel();
    	bagInfoScrollPane = new JScrollPane();
    	bagInfoScrollPane.setViewportView(bagInfoInputPane);
    	bagInfoScrollPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));

        // Create a panel for the form error messages and the update button
        updatePropButton = new JButton(bagView.getPropertyMessage("button.saveupdates"));
        updatePropButton.setMnemonic(KeyEvent.VK_S);
        updateBagHandler = new UpdateBagHandler(bagView);
        updatePropButton.addActionListener(updateBagHandler);
        updatePropButton.setToolTipText(bagView.getPropertyMessage("button.saveupdates.help"));
        updatePropButton.setEnabled(false);
        
        GridBagLayout infoLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;
        bagView.buildConstraints(gbc, 0, row, 3, 1, 1, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagSettingsPanel, gbc);
        row++;
//        bagView.buildConstraints(gbc, 0, row, 3, 1, 1, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
//        infoLayout.setConstraints(buttonPanel, gbc);
//        row++;
        bagView.buildConstraints(gbc, 0, row, 3, 1, 1, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagInfoScrollPane, gbc);
        
        infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        infoPanel.setBorder(emptyBorder);
        infoPanel.add(bagSettingsPanel, BorderLayout.NORTH);
        //infoPanel.add(buttonPanel, BorderLayout.CENTER);
        infoPanel.add(bagInfoScrollPane, BorderLayout.SOUTH);
    	this.setViewportView(infoPanel);
    }

    private JPanel createSettingsPanel() {
    	JPanel panel = new JPanel();
    	Border border = new EmptyBorder(2, 1, 2, 1);

    	JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		//setContentPane(contentPane);
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.NORTH);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel pane = new JPanel();
		mainPanel.add(pane,BorderLayout.CENTER);
		GridBagLayout gbl_pane = new GridBagLayout();
		gbl_pane.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
		gbl_pane.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_pane.columnWeights = new double[]{0.0, 1.0, 3.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_pane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pane.setLayout(gbl_pane);
		
		JLabel lblBagName = new JLabel(bagView.getPropertyMessage("bag.label.name"));
		GridBagConstraints gbc_lblBagName = new GridBagConstraints();
		gbc_lblBagName.insets = new Insets(0, 0, 5, 5);
		gbc_lblBagName.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblBagName.gridx = 0;
		gbc_lblBagName.gridy = 0;
		pane.add(lblBagName, gbc_lblBagName);
		
		bagNameField = new JTextField(bagView.getPropertyMessage("bag.label.noname"));
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridwidth = 2;
		gbc_textField_1.anchor = GridBagConstraints.EAST;
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 0;
		pane.add(bagNameField, gbc_textField_1);
		bagNameField.setColumns(10);
		
		JLabel lblSelectProfile = new JLabel("Select Profile:");
		lblSelectProfile.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblSelectProfile = new GridBagConstraints();
		gbc_lblSelectProfile.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectProfile.anchor = GridBagConstraints.WEST;
		gbc_lblSelectProfile.gridx = 0;
		gbc_lblSelectProfile.gridy = 1;
		pane.add(lblSelectProfile, gbc_lblSelectProfile);
		
		lblSelectProfile.setToolTipText(bagView.getPropertyMessage("bag.projectlist.help"));
    	ArrayList<String> listModel = new ArrayList<String>();
		Set<String> projectKeys = bagView.bagProject.userProfiles.keySet();
		for (Iterator<String> iter = projectKeys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Profile p =bagView.bagProject.userProfiles.get(key);
			if (p != null) listModel.add(p.getName());
		}
		
		profileList = new JComboBox(listModel.toArray());
    	profileList.setName(bagView.getPropertyMessage("bag.label.projectlist"));
    	profileList.setSelectedItem(bagView.getPropertyMessage("bag.project.noproject"));
    	profileList.addActionListener(new ProjectListHandler(bagView));
    	profileList.setToolTipText(bagView.getPropertyMessage("bag.projectlist.help"));
    	profileList.setEnabled(false);
    	String selected = (String) profileList.getSelectedItem();
    	
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 1;
		pane.add(profileList, gbc_comboBox);
		
		JLabel bagVersionLabel = new JLabel(bagView.getPropertyMessage("bag.label.version"));
    	bagVersionLabel.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
    	ArrayList<String> versionModel = new ArrayList<String>();
    	Version[] vals = Version.values();
    	for (int i=0; i < vals.length; i++) {
    		versionModel.add(vals[i].versionString);
    	}
		GridBagConstraints gbc_lblBagVersion = new GridBagConstraints();
		gbc_lblBagVersion.anchor = GridBagConstraints.WEST;
		gbc_lblBagVersion.insets = new Insets(0, 0, 5, 5);
		gbc_lblBagVersion.gridx = 0;
		gbc_lblBagVersion.gridy = 2;
		pane.add(bagVersionLabel, gbc_lblBagVersion);
		
		bagVersionValue = new JLabel(Version.V0_96.versionString);
    	bagVersionValue.setText(Version.V0_96.versionString);
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.WEST;
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 1;
		gbc_label_2.gridy = 2;
		pane.add(bagVersionValue, gbc_label_2);
		
		JLabel holeyLabel = new JLabel(bagView.getPropertyMessage("bag.label.isholey"));
    	holeyLabel.setToolTipText(bagView.getPropertyMessage("bag.isholey.help"));
		GridBagConstraints gbc_lblHoleyBag = new GridBagConstraints();
		gbc_lblHoleyBag.anchor = GridBagConstraints.WEST;
		gbc_lblHoleyBag.insets = new Insets(0, 0, 5, 5);
		gbc_lblHoleyBag.gridx = 0;
		gbc_lblHoleyBag.gridy = 3;
		pane.add(holeyLabel, gbc_lblHoleyBag);
		
		holeyValue = new JLabel("false");
    	holeyCheckbox = new JCheckBox(bagView.getPropertyMessage("bag.checkbox.isholey"));
    	holeyCheckbox.setBorder(border);
    	holeyCheckbox.setSelected(false);
    	holeyCheckbox.setEnabled(false);
    	holeyCheckbox.addActionListener(new HoleyBagHandler(bagView));
    	holeyCheckbox.setToolTipText(bagView.getPropertyMessage("bag.isholey.help"));
    	holeyCheckbox.setEnabled(false);
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.anchor = GridBagConstraints.WEST;
		gbc_label_5.insets = new Insets(0, 0, 5, 5);
		gbc_label_5.gridx = 1;
		gbc_label_5.gridy = 3;
		pane.add(holeyValue, gbc_label_5);
		
		JLabel serializeLabel = new JLabel(bagView.getPropertyMessage("bag.label.ispackage"));
    	serializeLabel.setToolTipText(bagView.getPropertyMessage("bag.serializetype.help"));
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.anchor = GridBagConstraints.WEST;
		gbc_label_7.ipady = 7;
		gbc_label_7.insets = new Insets(0, 0, 0, 5);
		gbc_label_7.gridx = 0;
		gbc_label_7.gridy = 4;
		pane.add(serializeLabel, gbc_label_7);
		
		serializeValue = new JLabel(DefaultBag.NO_LABEL);
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.anchor = GridBagConstraints.WEST;
		gbc_label_8.insets = new Insets(0, 0, 0, 5);
		gbc_label_8.gridx = 1;
		gbc_label_8.gridy = 4;
		pane.add(serializeValue, gbc_label_8);
		
	
		
		
		
		JPanel panel_2 = new JPanel();
		mainPanel.add(panel_2, BorderLayout.EAST);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel);
		
		newProjectButton = new JButton(bagView.getPropertyMessage("bag.button.project.new"));
    	newProjectButton.addActionListener(new NewProjectHandler());
    	newProjectButton.setOpaque(true);
    	newProjectButton.setToolTipText(bagView.getPropertyMessage("bag.button.project.new.help"));
    	newProjectButton.setEnabled(false);
		GridBagConstraints gbc_btnEditProfile = new GridBagConstraints();
		gbc_btnEditProfile.fill = GridBagConstraints.BOTH;
		gbc_btnEditProfile.insets = new Insets(0, 0, 5, 0);
		gbc_btnEditProfile.gridx = 0;
		gbc_btnEditProfile.gridy = 0;
		panel_2.add(newProjectButton, gbc_btnEditProfile);
		
		btnEditProfile = new JButton("Edit Profile");
		GridBagConstraints gbc_btnCreateNewProfile = new GridBagConstraints();
		gbc_btnCreateNewProfile.fill = GridBagConstraints.BOTH;
		gbc_btnCreateNewProfile.insets = new Insets(0, 0, 5, 0);
		gbc_btnCreateNewProfile.gridx = 0;
		gbc_btnCreateNewProfile.gridy = 1;
		panel_2.add(btnEditProfile, gbc_btnCreateNewProfile);
		btnEditProfile.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				 List<ProfileField> profileFields = bagView.bagProject.userProjectProfiles.get(bagView.getBag().getProfile().getName());
			        if(profileFields == null || profileFields.size()==0)
			        {
			        	bagView.showWarningErrorDialog("Edit Profile", "Profile: " + 
			        			         bagView.getBag().getProfile().getName() + " does not have fields");
			        	return;
			        }
				NewFieldFrame newFieldFrame =new NewFieldFrame(bagView, "Edit Profile Fields");
				newFieldFrame.setVisible(true);
			}
		});
		
		
		saveProfiles = new JButton("Save Profiles");
		GridBagConstraints gbc_btnSaveProfles = new GridBagConstraints();
		gbc_btnSaveProfles.insets = new Insets(0, 0, 5, 0);
		gbc_btnSaveProfles.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSaveProfles.gridx = 0;
		gbc_btnSaveProfles.gridy = 2;
		panel_2.add(saveProfiles, gbc_btnSaveProfles);
		saveProfiles.addActionListener(new SaveFieldHandler(bagView));
		
		removeProfileBtn= new JButton("Remove Profile");
		GridBagConstraints gbc_removeProfileBtn = new GridBagConstraints();
		gbc_removeProfileBtn.insets = new Insets(0, 0, 5, 0);
		gbc_removeProfileBtn.fill = GridBagConstraints.HORIZONTAL;
		gbc_removeProfileBtn.gridx = 0;
		gbc_removeProfileBtn.gridy = 3;
		panel_2.add(removeProfileBtn, gbc_removeProfileBtn);
		removeProfileBtn.addActionListener(new RemoveProjectHandler());

		
		JPanel profileFieldButtonsPane_1 = new JPanel();
		mainPanel.add(profileFieldButtonsPane_1, BorderLayout.SOUTH);
		profileFieldButtonsPane_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 0));
		
		showAllDefaultFeildsButton = new JButton("Add All Default BagIt Fields");
		showAllDefaultFeildsButton.addActionListener(bagInfoInputPane.bagInfoForm.new  AddFieldDefaultsHandler());
		profileFieldButtonsPane_1.add(showAllDefaultFeildsButton);
		
		addCustomFieldButton = new JButton("Add Fields");
		profileFieldButtonsPane_1.add(addCustomFieldButton);
		addCustomFieldButton.addActionListener(bagInfoInputPane.bagInfoForm.new  AddFieldHandler());
		
		
	
    	return contentPane;
    }

    private JPanel createButtonPanel(boolean enabled) {
    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

    	saveButton = new JButton(bagView.getPropertyMessage("bag.button.field.save"));
    	saveButton.addActionListener(new SaveFieldHandler(bagView));
    	saveButton.setOpaque(true);
    	saveButton.setToolTipText(bagView.getPropertyMessage("bag.button.field.save.help"));
    	saveButton.setEnabled(enabled);
    	buttonPanel.add(saveButton);
    	
    	loadDefaultsButton = new JButton(bagView.getPropertyMessage("bag.button.field.load"));
    	loadDefaultsButton.addActionListener(new LoadFieldHandler(bagView));
    	loadDefaultsButton.setOpaque(true);
    	loadDefaultsButton.setToolTipText(bagView.getPropertyMessage("bag.button.field.load.help"));
    	loadDefaultsButton.setEnabled(true);
    	buttonPanel.add(loadDefaultsButton);

    	clearDefaultsButton = new JButton(bagView.getPropertyMessage("bag.button.field.clear"));
    	clearDefaultsButton.addActionListener(new ClearFieldHandler(bagView));
    	clearDefaultsButton.setOpaque(true);
    	clearDefaultsButton.setToolTipText(bagView.getPropertyMessage("bag.button.field.clear.help"));
    	clearDefaultsButton.setEnabled(enabled);
    	buttonPanel.add(clearDefaultsButton);

    	return buttonPanel;
    }

    private class NewProjectHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		newProjectFrame = new NewProjectFrame(bagView, bagView.getPropertyMessage("bag.frame.newproject"));
    		newProjectFrame.setVisible(true);
       	}
    }
    
    private class RemoveProjectHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		String name = (String) bagView.infoInputPane.profileList.getSelectedItem();
    		bagView.bagProject.removeProject(name);
       	}
    }

    public void setBagVersion(String value) {
    	bagVersionValue.setText(value);
    	bagVersionValue.invalidate();
    }

    public String getBagVersion() {
    	return bagVersionValue.getText();
    }

   

    public void setBagName(String name) {
    	if (name == null || name.length() < 1) return;
    	bagNameField.setText(name);
        bagNameField.setCaretPosition(name.length()-1);
        if (name.trim().equalsIgnoreCase(bagView.getPropertyMessage("bag.label.noname"))) {
        	bagNameField.setEnabled(false);
        }
    	bagNameField.invalidate();
    	this.invalidate();
    }
    
    public String getBagName() {
    	return bagNameField.getText();
    }
    
    public void updateInfoForms() {
    	bagInfoInputPane.populateForms(bag, false);
    	bagInfoInputPane.enableForms(bag, false);
    	bagInfoInputPane.invalidate();
    }

    public void updateInfoFormsPane(boolean enabled) {
    	bagInfoInputPane = new BagInfoInputPane(bagView, bagView.bagProject.username, bagView.bagProject.projectContact, enabled);
    	bagInfoInputPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	bagInfoScrollPane.setViewportView(bagInfoInputPane);
    	bagInfoScrollPane.setPreferredSize(bagInfoInputPane.getPreferredSize());
    	this.setPreferredSize(bagInfoScrollPane.getPreferredSize());
    	this.invalidate();
    }
    
    public void showTabPane(int i) {
    	bagInfoInputPane.setSelectedIndex(i);
    	bagInfoInputPane.invalidate();
    }

	public void updateButtonEnableSettings(boolean b) {
		newProjectButton.setEnabled(b);
		addCustomFieldButton.setEnabled(b);
		showAllDefaultFeildsButton.setEnabled(b);
		saveProfiles.setEnabled(b);
		btnEditProfile.setEnabled(b);
		removeProfileBtn.setEnabled(b);			
	}
}
