
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

import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.handlers.DefaultProjectHandler;
import gov.loc.repository.bagger.ui.handlers.HoleyBagHandler;
import gov.loc.repository.bagger.ui.handlers.ProjectListHandler;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;

public class InfoFormsPane extends JScrollPane {
	private static final long serialVersionUID = -5988111446773491301L;
	private static final Log log = LogFactory.getLog(InfoFormsPane.class);
    private BagView bagView;
    private DefaultBag bag;
    private JScrollPane bagInfoScrollPane;
    public UpdateBagHandler updateBagHandler;
	public JPanel buttonPanel;
    public JPanel infoPanel;
    public SerializeBagHandler serializeBagHandler;
	private NewProjectFrame newProjectFrame;

    public InfoFormsPane(BagView bagView) {
    	super();
		this.bagView = bagView;
		this.bag = bagView.getBag();
		bag.getInfo().setBag(bag);
    	createScrollPane(true);
    }

    private void createScrollPane(boolean enabled) {
    	buttonPanel = createButtonPanel(enabled);

    	bagView.bagInfoInputPane = new BagInfoInputPane(bagView, bagView.username, bagView.projectContact, false);
    	bagView.bagInfoInputPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	bagView.bagInfoInputPane.setEnabled(false);
    	bagView.bagSettingsPanel = createSettingsPanel();
    	bagInfoScrollPane = new JScrollPane();
    	bagInfoScrollPane.setViewportView(bagView.bagInfoInputPane);
    	bagInfoScrollPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));

        // Create a panel for the form error messages and the update button
        bagView.updatePropButton = new JButton(bagView.getPropertyMessage("button.saveupdates"));
        bagView.updatePropButton.setMnemonic(KeyEvent.VK_S);
        updateBagHandler = new UpdateBagHandler(bagView);
        bagView.updatePropButton.addActionListener(updateBagHandler);
        bagView.updatePropButton.setToolTipText(bagView.getPropertyMessage("button.saveupdates.help"));
        bagView.updatePropButton.setEnabled(false);
        
        GridBagLayout infoLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;
        bagView.buildConstraints(gbc, 0, row, 3, 1, 50, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagView.bagSettingsPanel, gbc);
        row++;
        bagView.buildConstraints(gbc, 0, row, 3, 1, 20, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(buttonPanel, gbc);
        row++;
        bagView.buildConstraints(gbc, 0, row, 3, 1, 20, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagInfoScrollPane, gbc);
        
        infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        infoPanel.setBorder(emptyBorder);
        infoPanel.add(bagView.bagSettingsPanel, BorderLayout.NORTH);
        infoPanel.add(buttonPanel, BorderLayout.CENTER);
        infoPanel.add(bagInfoScrollPane, BorderLayout.SOUTH);
    	this.setViewportView(infoPanel);
    }

    private JPanel createSettingsPanel() {
    	JPanel panel = new JPanel();
    	Border border = new EmptyBorder(2, 1, 2, 1);

    	JLabel bagNameLabel = new JLabel(bagView.getPropertyMessage("bag.label.name"));
    	Dimension labelDim = bagNameLabel.getPreferredSize();
    	bagView.bagNameField = new JTextField(" " + bag.getName() + " ");
    	bagView.bagNameField.setEditable(false);
    	bagView.bagNameField.setEnabled(false);
        bagView.bagNameField.setCaretPosition(bag.getName().length()-1);
    	Dimension fieldDim = bagView.bagInfoInputPane.getPreferredSize();
    	Dimension maxFieldDim = new Dimension(fieldDim.width/2, labelDim.height+10);
    	bagView.bagNameField.setMaximumSize(maxFieldDim);
    	bagView.bagNameField.setPreferredSize(maxFieldDim);

    	JLabel bagVersionLabel = new JLabel(bagView.getPropertyMessage("bag.label.version"));
    	bagVersionLabel.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
    	ArrayList<String> versionModel = new ArrayList<String>();
    	Version[] vals = Version.values();
    	for (int i=0; i < vals.length; i++) {
    		versionModel.add(vals[i].versionString);
    	}
    	bagView.bagVersionValue = new JLabel(Version.V0_96.versionString);
    	bagView.bagVersionList = new JComboBox(versionModel.toArray());
    	bagView.bagVersionList.setName(bagView.getPropertyMessage("bag.label.versionlist"));
    	bagView.bagVersionList.setSelectedItem(Version.V0_96.versionString);
    	bagView.bagVersionValue.setText(Version.V0_96.versionString);
    	bagView.bagVersionList.addActionListener(new VersionListHandler(bagView));
    	bagView.bagVersionList.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
    	bagView.bagVersionList.setEnabled(false);

    	// Project control
    	JLabel projectLabel = new JLabel(bagView.getPropertyMessage("bag.label.project"));
    	projectLabel.setToolTipText(bagView.getPropertyMessage("bag.projectlist.help"));
    	ArrayList<String> listModel = new ArrayList<String>();
    	Object[] array = bagView.userProjects.toArray();
    	for (int i=0; i < bagView.userProjects.size(); i++) listModel.add(((Project)array[i]).getName());
    	bagView.projectList = new JComboBox(listModel.toArray());
    	bagView.projectList.setName(bagView.getPropertyMessage("bag.label.projectlist"));
    	bagView.projectList.setSelectedItem(bagView.getPropertyMessage("bag.project.noproject"));
    	bagView.projectList.addActionListener(new ProjectListHandler(bagView));
    	bagView.projectList.setToolTipText(bagView.getPropertyMessage("bag.projectlist.help"));
    	bagView.projectList.setEnabled(false);
    	String selected = (String) bagView.projectList.getSelectedItem();
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
    	bagView.newProjectButton = new JButton(bagView.getPropertyMessage("bag.button.project.new"));
    	bagView.newProjectButton.addActionListener(new NewProjectHandler());
    	bagView.newProjectButton.setOpaque(true);
    	bagView.newProjectButton.setToolTipText(bagView.getPropertyMessage("bag.button.project.new.help"));
    	bagView.newProjectButton.setEnabled(false);

    	// Default project bag control
    	JLabel defaultLabel = new JLabel(bagView.getPropertyMessage("bag.label.projectDefault"));
    	defaultLabel.setToolTipText(bagView.getPropertyMessage("bag.isdefault.help"));
    	bagView.defaultProject = new JCheckBox(bagView.getPropertyMessage("bag.checkbox.isdefault"));
    	bagView.defaultProject.setBorder(border);
    	Project project = bag.getProject();
    	if (project != null && project.getIsDefault())
    		bagView.defaultProject.setSelected(true);
    	else
    		bagView.defaultProject.setSelected(false);
    	bagView.defaultProject.addActionListener(new DefaultProjectHandler(bagView));
    	bagView.defaultProject.setToolTipText(bagView.getPropertyMessage("bag.isdefault.help"));
    	
    	// Holey bag control
    	JLabel holeyLabel = new JLabel(bagView.getPropertyMessage("bag.label.isholey"));
    	holeyLabel.setToolTipText(bagView.getPropertyMessage("bag.isholey.help"));
    	bagView.holeyValue = new JLabel("false");
    	bagView.holeyCheckbox = new JCheckBox(bagView.getPropertyMessage("bag.checkbox.isholey"));
    	bagView.holeyCheckbox.setBorder(border);
    	bagView.holeyCheckbox.setSelected(false);
    	bagView.holeyCheckbox.setEnabled(false);
    	bagView.holeyCheckbox.addActionListener(new HoleyBagHandler(bagView));
    	bagView.holeyCheckbox.setToolTipText(bagView.getPropertyMessage("bag.isholey.help"));
    	bagView.holeyCheckbox.setEnabled(false);

    	// Bag is to be serialized control
    	bagView.serializeLabel = new JLabel(bagView.getPropertyMessage("bag.label.ispackage"));
    	bagView.serializeLabel.setToolTipText(bagView.getPropertyMessage("bag.serializetype.help"));
    	bagView.serializeValue = new JLabel(DefaultBag.NO_LABEL);
    	bagView.noneButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.none"));
    	bagView.noneButton.setSelected(true);
    	bagView.noneButton.setEnabled(false);
    	serializeBagHandler = new SerializeBagHandler(bagView);
    	bagView.noneButton.addActionListener(serializeBagHandler);
    	bagView.noneButton.setToolTipText(bagView.getPropertyMessage("bag.serializetype.none.help"));
    	bagView.noFilter = new FileFilter() {
    		public boolean accept(File f) {
    			return f.isFile() || f.isDirectory();
    		}
    		public String getDescription() {
    			return "";
    		}
    	};

    	bagView.zipButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.zip"));
    	bagView.zipButton.setSelected(false);
    	bagView.zipButton.setEnabled(false);
    	bagView.zipButton.addActionListener(serializeBagHandler);
    	bagView.zipButton.setToolTipText(bagView.getPropertyMessage("bag.serializetype.zip.help"));
    	bagView.zipFilter = new FileFilter() {
    		public boolean accept(File f) {
    			return f.getName().toLowerCase().endsWith("."+DefaultBag.ZIP_LABEL)	|| f.isDirectory();
    		}
    		public String getDescription() {
    			return "*."+DefaultBag.ZIP_LABEL;
    		}
    	};

    	bagView.tarButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.tar"));
    	bagView.tarButton.setSelected(false);
    	bagView.tarButton.setEnabled(false);
    	bagView.tarButton.addActionListener(serializeBagHandler);
    	bagView.tarButton.setToolTipText(bagView.getPropertyMessage("bag.serializetype.tar.help"));
    	bagView.tarFilter = new FileFilter() {
    		public boolean accept(File f) {
    			return f.getName().toLowerCase().endsWith("."+DefaultBag.TAR_LABEL)	|| f.isDirectory();
    		}
    		public String getDescription() {
    			return "*."+DefaultBag.TAR_LABEL;
    		}
    	};

    	bagView.tarGzButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.targz"));
    	bagView.tarGzButton.setEnabled(false);
    	bagView.tarGzButton.addActionListener(serializeBagHandler);
    	bagView.tarGzButton.setToolTipText(bagView.getPropertyMessage("bag.serializetype.targz.help"));
    	
    	bagView.tarBz2Button = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.tarbz2"));
    	bagView.tarBz2Button.setEnabled(false);
    	bagView.tarBz2Button.addActionListener(serializeBagHandler);
    	bagView.tarBz2Button.setToolTipText(bagView.getPropertyMessage("bag.serializetype.tarbz2.help"));
    	
    	ButtonGroup serializeGroup = new ButtonGroup();
    	serializeGroup.add(bagView.noneButton);
    	serializeGroup.add(bagView.zipButton);
    	serializeGroup.add(bagView.tarButton);
    	serializeGroup.add(bagView.tarGzButton);
    	serializeGroup.add(bagView.tarBz2Button);
    	bagView.serializeGroupPanel = new JPanel(new FlowLayout());
    	bagView.serializeGroupPanel.add(bagView.serializeLabel);
    	bagView.serializeGroupPanel.add(bagView.noneButton);
    	bagView.serializeGroupPanel.add(bagView.zipButton);
    	bagView.serializeGroupPanel.add(bagView.tarButton);
    	bagView.serializeGroupPanel.add(bagView.tarGzButton);
    	bagView.serializeGroupPanel.add(bagView.tarBz2Button);
    	bagView.serializeGroupPanel.setBorder(border);
    	bagView.serializeGroupPanel.setEnabled(false);
    	bagView.serializeGroupPanel.setToolTipText(bagView.getPropertyMessage("bag.serializetype.help"));
    	
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
    	gridLayout.setConstraints(bagView.bagNameField, gbc);
    	panel.add(bagView.bagNameField);
    	row++;
    	/* */
		bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(projectLabel, gbc);
    	panel.add(projectLabel);
    	bagView.buildConstraints(gbc, 1, row, 1, 1, 40, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagView.projectList, gbc);
    	panel.add(bagView.projectList);
    	bagView.buildConstraints(gbc, 2, row, 1, 1, 40, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagView.newProjectButton, gbc);
    	panel.add(bagView.newProjectButton);
    	/* */
    	row++;
    	bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagVersionLabel, gbc);
    	panel.add(bagVersionLabel);
    	bagView.buildConstraints(gbc, 1, row, 1, 1, wx2, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagView.bagVersionValue, gbc);
    	panel.add(bagView.bagVersionValue);
    	row++;
    	bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(holeyLabel, gbc);
    	panel.add(holeyLabel);
    	bagView.buildConstraints(gbc, 1, row, 1, 1, wx2, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagView.holeyValue, gbc);
    	panel.add(bagView.holeyValue);
    	row++;
    	bagView.buildConstraints(gbc, 0, row, 1, 1, wx1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagView.serializeLabel, gbc);
    	panel.add(bagView.serializeLabel);
    	bagView.buildConstraints(gbc, 1, row, 1, 1, wx2, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(bagView.serializeValue, gbc);
    	panel.add(bagView.serializeValue);
    	
    	return panel;
    }

    private JPanel createButtonPanel(boolean enabled) {
    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

    	JButton saveButton = new JButton(bagView.getPropertyMessage("bag.button.field.save"));
    	saveButton.addActionListener(new SaveFieldHandler());
    	saveButton.setOpaque(true);
    	saveButton.setToolTipText(bagView.getPropertyMessage("bag.button.field.save.help"));
    	saveButton.setEnabled(enabled);
    	buttonPanel.add(saveButton);
    	
    	JButton loadDefaultsButton = new JButton(bagView.getPropertyMessage("bag.button.field.load"));
    	loadDefaultsButton.addActionListener(new LoadFieldHandler());
    	loadDefaultsButton.setOpaque(true);
    	loadDefaultsButton.setToolTipText(bagView.getPropertyMessage("bag.button.field.load.help"));
    	loadDefaultsButton.setEnabled(enabled);
    	buttonPanel.add(loadDefaultsButton);

    	JButton clearDefaultsButton = new JButton(bagView.getPropertyMessage("bag.button.field.clear"));
    	clearDefaultsButton.addActionListener(new ClearFieldHandler());
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

    private class SaveFieldHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
        	bagView.infoInputPane.updateBagHandler.updateBag(bagView.getBag());
    		bagView.saveProfiles();
    		bagView.bagInfoInputPane.setSelectedIndex(1);
       	}
    }

    private class LoadFieldHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		bagView.loadProfiles();
    		bagView.bagInfoInputPane.setSelectedIndex(1);
       	}
    }

    private class ClearFieldHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		bagView.clearProfiles();
    		bagView.bagInfoInputPane.setSelectedIndex(1);
       	}
    }

    public void updateInfoFormsPane(boolean enabled) {
    	bagView.bagInfoInputPane = new BagInfoInputPane(bagView, bagView.username, bagView.projectContact, enabled);
    	bagView.bagInfoInputPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	bagInfoScrollPane.setViewportView(bagView.bagInfoInputPane);
    	bagInfoScrollPane.setPreferredSize(bagView.bagInfoInputPane.getPreferredSize());
    	this.setPreferredSize(bagInfoScrollPane.getPreferredSize());
    	this.invalidate();
    }
}
