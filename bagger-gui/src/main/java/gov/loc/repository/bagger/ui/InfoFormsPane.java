
package gov.loc.repository.bagger.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.handlers.ClearFieldHandler;
import gov.loc.repository.bagger.ui.handlers.DefaultProjectHandler;
import gov.loc.repository.bagger.ui.handlers.HoleyBagHandler;
import gov.loc.repository.bagger.ui.handlers.LoadFieldHandler;
import gov.loc.repository.bagger.ui.handlers.ProjectListHandler;
import gov.loc.repository.bagger.ui.handlers.SaveFieldHandler;
import gov.loc.repository.bagger.ui.handlers.SerializeBagHandler;
import gov.loc.repository.bagger.ui.handlers.UpdateBagHandler;
import gov.loc.repository.bagger.ui.handlers.VersionListHandler;
import gov.loc.repository.bagit.BagFactory.Version;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
	protected JComboBox projectList;
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
        bagView.buildConstraints(gbc, 0, row, 3, 1, 1, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(buttonPanel, gbc);
        row++;
        bagView.buildConstraints(gbc, 0, row, 3, 1, 1, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagInfoScrollPane, gbc);
        
        infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        infoPanel.setBorder(emptyBorder);
        infoPanel.add(bagSettingsPanel, BorderLayout.NORTH);
        infoPanel.add(buttonPanel, BorderLayout.CENTER);
        infoPanel.add(bagInfoScrollPane, BorderLayout.SOUTH);
    	this.setViewportView(infoPanel);
    }

    private JPanel createSettingsPanel() {
    	JPanel panel = new JPanel();
    	Border border = new EmptyBorder(2, 1, 2, 1);

    	JLabel bagNameLabel = new JLabel(bagView.getPropertyMessage("bag.label.name"));
    	Dimension labelDim = bagNameLabel.getPreferredSize();
    	bagNameField = new JTextField(bagView.getPropertyMessage("bag.label.noname"));
    	bagNameField.setEditable(false);
    	bagNameField.setEnabled(false);
        bagNameField.setCaretPosition(bag.getName().length()-1);
    	Dimension fieldDim = bagInfoInputPane.getPreferredSize();
    	Dimension maxFieldDim = new Dimension(fieldDim.width/2, labelDim.height+10);
    	bagNameField.setMaximumSize(maxFieldDim);
    	bagNameField.setPreferredSize(maxFieldDim);

    	JLabel bagVersionLabel = new JLabel(bagView.getPropertyMessage("bag.label.version"));
    	bagVersionLabel.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
    	ArrayList<String> versionModel = new ArrayList<String>();
    	Version[] vals = Version.values();
    	for (int i=0; i < vals.length; i++) {
    		versionModel.add(vals[i].versionString);
    	}
    	bagVersionValue = new JLabel(Version.V0_96.versionString);
    	bagVersionList = new JComboBox(versionModel.toArray());
    	bagVersionList.setName(bagView.getPropertyMessage("bag.label.versionlist"));
    	bagVersionList.setSelectedItem(Version.V0_96.versionString);
    	bagVersionValue.setText(Version.V0_96.versionString);
    	bagVersionList.addActionListener(new VersionListHandler(bagView));
    	bagVersionList.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
    	bagVersionList.setEnabled(false);

    	// Project control
    	JLabel projectLabel = new JLabel(bagView.getPropertyMessage("bag.label.project"));
    	projectLabel.setToolTipText(bagView.getPropertyMessage("bag.projectlist.help"));
    	ArrayList<String> listModel = new ArrayList<String>();
		Set<String> projectKeys = bagView.bagProject.userProjects.keySet();
		for (Iterator<String> iter = projectKeys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Project p = bagView.bagProject.userProjects.get(key);
			if (p != null) listModel.add(p.getName());
		}
    	projectList = new JComboBox(listModel.toArray());
    	projectList.setName(bagView.getPropertyMessage("bag.label.projectlist"));
    	projectList.setSelectedItem(bagView.getPropertyMessage("bag.project.noproject"));
    	projectList.addActionListener(new ProjectListHandler(bagView));
    	projectList.setToolTipText(bagView.getPropertyMessage("bag.projectlist.help"));
    	projectList.setEnabled(false);
    	String selected = (String) projectList.getSelectedItem();
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.edeposit"))) {
    		bag.isEdeposit(true);
    	} else {
    		bag.isEdeposit(false);
    	}
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.ndnp"))) {
    		bag.isNdnp(true);
    	} else {
    		bag.isNdnp(false);
    	}
    	newProjectButton = new JButton(bagView.getPropertyMessage("bag.button.project.new"));
    	newProjectButton.addActionListener(new NewProjectHandler());
    	newProjectButton.setOpaque(true);
    	newProjectButton.setToolTipText(bagView.getPropertyMessage("bag.button.project.new.help"));
    	newProjectButton.setEnabled(false);
    	
    	removeProjectButton = new JButton(bagView.getPropertyMessage("bag.button.project.remove"));
    	removeProjectButton.addActionListener(new RemoveProjectHandler());
    	removeProjectButton.setOpaque(true);
    	removeProjectButton.setToolTipText(bagView.getPropertyMessage("bag.button.project.remove.help"));
    	removeProjectButton.setEnabled(false);

    	// Default project bag control
    	JLabel defaultLabel = new JLabel(bagView.getPropertyMessage("bag.label.projectDefault"));
    	defaultLabel.setToolTipText(bagView.getPropertyMessage("bag.isdefault.help"));
    	defaultProject = new JCheckBox(bagView.getPropertyMessage("bag.checkbox.isdefault"));
    	defaultProject.setBorder(border);
    	Project project = bag.getProject();
    	if (project != null && project.getIsDefault())
    		defaultProject.setSelected(true);
    	else
    		defaultProject.setSelected(false);
    	defaultProject.addActionListener(new DefaultProjectHandler(bagView));
    	defaultProject.setToolTipText(bagView.getPropertyMessage("bag.isdefault.help"));
    	
    	// Holey bag control
    	JLabel holeyLabel = new JLabel(bagView.getPropertyMessage("bag.label.isholey"));
    	holeyLabel.setToolTipText(bagView.getPropertyMessage("bag.isholey.help"));
    	holeyValue = new JLabel("false");
    	holeyCheckbox = new JCheckBox(bagView.getPropertyMessage("bag.checkbox.isholey"));
    	holeyCheckbox.setBorder(border);
    	holeyCheckbox.setSelected(false);
    	holeyCheckbox.setEnabled(false);
    	holeyCheckbox.addActionListener(new HoleyBagHandler(bagView));
    	holeyCheckbox.setToolTipText(bagView.getPropertyMessage("bag.isholey.help"));
    	holeyCheckbox.setEnabled(false);

    	// Bag is to be serialized control
    	serializeLabel = new JLabel(bagView.getPropertyMessage("bag.label.ispackage"));
    	serializeLabel.setToolTipText(bagView.getPropertyMessage("bag.serializetype.help"));
    	serializeValue = new JLabel(DefaultBag.NO_LABEL);
    	noneButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.none"));
    	noneButton.setSelected(true);
    	noneButton.setEnabled(false);
    	serializeBagHandler = new SerializeBagHandler(bagView);
    	noneButton.addActionListener(serializeBagHandler);
    	noneButton.setToolTipText(bagView.getPropertyMessage("bag.serializetype.none.help"));
    	noFilter = new FileFilter() {
    		public boolean accept(File f) {
    			return f.isFile() || f.isDirectory();
    		}
    		public String getDescription() {
    			return "";
    		}
    	};

    	zipButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.zip"));
    	zipButton.setSelected(false);
    	zipButton.setEnabled(false);
    	zipButton.addActionListener(serializeBagHandler);
    	zipButton.setToolTipText(bagView.getPropertyMessage("bag.serializetype.zip.help"));
    	zipFilter = new FileFilter() {
    		public boolean accept(File f) {
    			return f.getName().toLowerCase().endsWith("."+DefaultBag.ZIP_LABEL)	|| f.isDirectory();
    		}
    		public String getDescription() {
    			return "*."+DefaultBag.ZIP_LABEL;
    		}
    	};

    	tarButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.tar"));
    	tarButton.setSelected(false);
    	tarButton.setEnabled(false);
    	tarButton.addActionListener(serializeBagHandler);
    	tarButton.setToolTipText(bagView.getPropertyMessage("bag.serializetype.tar.help"));
    	tarFilter = new FileFilter() {
    		public boolean accept(File f) {
    			return f.getName().toLowerCase().endsWith("."+DefaultBag.TAR_LABEL)	|| f.isDirectory();
    		}
    		public String getDescription() {
    			return "*."+DefaultBag.TAR_LABEL;
    		}
    	};

    	tarGzButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.targz"));
    	tarGzButton.setEnabled(false);
    	tarGzButton.addActionListener(serializeBagHandler);
    	tarGzButton.setToolTipText(bagView.getPropertyMessage("bag.serializetype.targz.help"));
    	
    	tarBz2Button = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.tarbz2"));
    	tarBz2Button.setEnabled(false);
    	tarBz2Button.addActionListener(serializeBagHandler);
    	tarBz2Button.setToolTipText(bagView.getPropertyMessage("bag.serializetype.tarbz2.help"));
    	
    	ButtonGroup serializeGroup = new ButtonGroup();
    	serializeGroup.add(noneButton);
    	serializeGroup.add(zipButton);
    	serializeGroup.add(tarButton);
    	serializeGroup.add(tarGzButton);
    	serializeGroup.add(tarBz2Button);
    	serializeGroupPanel = new JPanel(new FlowLayout());
    	serializeGroupPanel.add(serializeLabel);
    	serializeGroupPanel.add(noneButton);
    	serializeGroupPanel.add(zipButton);
    	serializeGroupPanel.add(tarButton);
    	serializeGroupPanel.add(tarGzButton);
    	serializeGroupPanel.add(tarBz2Button);
    	serializeGroupPanel.setBorder(border);
    	serializeGroupPanel.setEnabled(false);
    	serializeGroupPanel.setToolTipText(bagView.getPropertyMessage("bag.serializetype.help"));
    	
    	GridBagLayout gridLayout = new GridBagLayout();
    	GridBagConstraints gbc = new GridBagConstraints();
    	
    	panel.setLayout(gridLayout);
    	int row = 0;
    	int wx1 = 1;
    	int wx2 = 90;
    	bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagNameLabel, gbc);
    	panel.add(bagNameLabel);
    	bagView.buildConstraints(gbc, 1, row, 2, 1, wx2, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagNameField, gbc);
    	panel.add(bagNameField);
    	row++;
    	/* */
		bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(projectLabel, gbc);
    	panel.add(projectLabel);
    	bagView.buildConstraints(gbc, 1, row, 1, 1, 30, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(projectList, gbc);
    	panel.add(projectList);
    	bagView.buildConstraints(gbc, 2, row, 1, 1, 30, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(newProjectButton, gbc);
    	panel.add(newProjectButton);
    	bagView.buildConstraints(gbc, 3, row, 1, 1, 30, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(removeProjectButton, gbc);
    	panel.add(removeProjectButton);
    	/* */
    	row++;
    	bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagVersionLabel, gbc);
    	panel.add(bagVersionLabel);
    	bagView.buildConstraints(gbc, 1, row, 1, 1, wx2, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagVersionValue, gbc);
    	panel.add(bagVersionValue);
    	row++;
    	bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(holeyLabel, gbc);
    	panel.add(holeyLabel);
    	bagView.buildConstraints(gbc, 1, row, 1, 1, wx2, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(holeyValue, gbc);
    	panel.add(holeyValue);
    	row++;
    	bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(serializeLabel, gbc);
    	panel.add(serializeLabel);
    	bagView.buildConstraints(gbc, 1, row, 1, 1, wx2, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(serializeValue, gbc);
    	panel.add(serializeValue);
    	
    	return panel;
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
    		String name = (String) bagView.infoInputPane.projectList.getSelectedItem();
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

    public void setBagVersionList(String version) {
    	bagVersionList.setSelectedItem(version);
    	bagVersionList.invalidate();
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
}
