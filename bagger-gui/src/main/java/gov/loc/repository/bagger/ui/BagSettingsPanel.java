
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;

import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.handlers.DefaultProjectHandler;
import gov.loc.repository.bagger.ui.handlers.HoleyBagHandler;
import gov.loc.repository.bagger.ui.handlers.ProjectListHandler;
import gov.loc.repository.bagger.ui.handlers.SerializeBagHandler;
import gov.loc.repository.bagger.ui.handlers.VersionListHandler;
import gov.loc.repository.bagit.BagFactory.Version;

public class BagSettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(BagSettingsPanel.class);
    private BagView bagView;
    private DefaultBag bag;

    public BagSettingsPanel(BagView bagView) {
		Application app = Application.instance();
		ApplicationPage page = app.getActiveWindow().getPage();
		PageComponent component = page.getActiveComponent();
		if (component != null) this.bagView = (BagView) component;
		else this.bagView = bagView;
		this.bag = bagView.getBag();
		createPanel();
    }
    
    private void createPanel() {
        Border border = new EmptyBorder(5, 5, 5, 5);

        JLabel bagNameLabel = new JLabel(bagView.getPropertyMessage("bag.label.name"));
        Dimension labelDim = bagNameLabel.getPreferredSize();
        bagView.bagNameField = new JTextField(" " + bag.getName() + " ");
        bagView.bagNameField.setEditable(false);
        bagView.bagNameField.setEnabled(false);
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
        bagView.bagVersionList = new JComboBox(versionModel.toArray());
        bagView.bagVersionList.setName(bagView.getPropertyMessage("bag.label.versionlist"));
        bagView.bagVersionList.setSelectedItem(Version.V0_96.versionString);
        bagView.bagVersion = Version.V0_96.versionString;
        bagView.bagVersionList.addActionListener(new VersionListHandler(bagView));
        bagView.bagVersionList.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));

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
    	String selected = (String) bagView.projectList.getSelectedItem();
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.edeposit"))) {
    		bag.setIsEdeposit(true);
    	} else {
    		bag.setIsEdeposit(false);
    	}
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.ndnp"))) {
    		bag.setIsNdnp(true);
    	} else {
    		bag.setIsNdnp(false);
    	}
        JScrollPane projectPane = new JScrollPane(bagView.projectList);

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
        bagView.holeyCheckbox = new JCheckBox(bagView.getPropertyMessage("bag.checkbox.isholey"));
        bagView.holeyCheckbox.setBorder(border);
        bagView.holeyCheckbox.setSelected(false);
        bagView.holeyCheckbox.addActionListener(new HoleyBagHandler(bagView));
        bagView.holeyCheckbox.setToolTipText(bagView.getPropertyMessage("bag.isholey.help"));

        // Bag is to be serialized control
        bagView.serializeLabel = new JLabel(bagView.getPropertyMessage("bag.label.ispackage"));
        bagView.serializeLabel.setToolTipText(bagView.getPropertyMessage("bag.serializetype.help"));
        bagView.noneButton = new JRadioButton(bagView.getPropertyMessage("bag.serializetype.none"));
        bagView.noneButton.setSelected(true);
        bagView.noneButton.setEnabled(false);
        SerializeBagHandler serializeBagHandler = new SerializeBagHandler(bagView);
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
	              return f.getName().toLowerCase().endsWith("."+DefaultBag.ZIP_LABEL)
	                  || f.isDirectory();
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
                return f.getName().toLowerCase().endsWith("."+DefaultBag.TAR_LABEL)
                    || f.isDirectory();
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
        
        int row = 0;
        bagView.buildConstraints(gbc, 0, row, 1, 1, 40, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(bagNameLabel, gbc);
        bagView.buildConstraints(gbc, 1, row, 1, 1, 60, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(bagView.bagNameField, gbc);
        row++;
        bagView.buildConstraints(gbc, 0, row, 1, 1, 40, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(bagVersionLabel, gbc);
        bagView.buildConstraints(gbc, 1, row, 1, 1, 60, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(bagView.bagVersionList, gbc);
        row++;
        bagView.buildConstraints(gbc, 0, row, 1, 1, 30, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(projectLabel, gbc);
        bagView.buildConstraints(gbc, 1, row, 1, 1, 40, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(projectPane, gbc);
        bagView.buildConstraints(gbc, 2, row, 1, 1, 30, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(bagView.defaultProject, gbc);
        row++;
        bagView.buildConstraints(gbc, 0, row, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(holeyLabel, gbc);
        bagView.buildConstraints(gbc, 1, row, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(bagView.holeyCheckbox, gbc);
        row++;
        bagView.buildConstraints(gbc, 0, row, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(bagView.serializeLabel, gbc);
        bagView.buildConstraints(gbc, 1, row, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST);
        gridLayout.setConstraints(bagView.serializeGroupPanel, gbc);

        this.setLayout(gridLayout);
        this.add(bagNameLabel);
        this.add(bagView.bagNameField);
        this.add(bagVersionLabel);
        this.add(bagView.bagVersionList);
        bagView.bagVersionList.setEnabled(false);
        this.add(projectLabel);
        this.add(projectPane);
        bagView.projectList.setEnabled(false);
        this.add(bagView.defaultProject);
        bagView.defaultProject.setEnabled(false);
        this.add(holeyLabel);
        this.add(bagView.holeyCheckbox);
        bagView.holeyCheckbox.setEnabled(false);
        this.add(bagView.serializeLabel);
        this.add(bagView.serializeGroupPanel);
//        this.add(checksumLabel);
//        this.add(checksumGroupPanel);    	
    }
}
