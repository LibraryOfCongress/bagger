
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.handlers.UpdateBagHandler;
import gov.loc.repository.bagger.ui.util.LayoutUtil;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

public class InfoFormsPane extends JScrollPane {
	private static final long serialVersionUID = -5988111446773491301L;
    private BagView bagView;
    private DefaultBag bag;
	private JPanel bagSettingsPanel;
    private JPanel infoPanel;
    protected JPanel serializeGroupPanel;

    public BagInfoInputPane bagInfoInputPane;
	public UpdateBagHandler updateBagHandler;

	protected JLabel bagNameValue;
    public JButton removeProjectButton;
    public JLabel bagVersionValue;
    public JLabel bagProfileValue;
    public JLabel holeyValue;
    public JLabel serializeLabel;
    public JLabel serializeValue;
	protected JComboBox bagVersionList;
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
		createUiComponent(false);
		updateBagHandler = new UpdateBagHandler(bagView);
    }
    
    public void setProfile(String profileName) {
    	bagProfileValue.setText(profileName);
    }

    private void createUiComponent(boolean enabled) {
    	bagSettingsPanel = createSettingsPanel();
        
        infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        infoPanel.setBorder(emptyBorder);
        
        GridBagConstraints gbc = LayoutUtil.buildGridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoPanel.add(bagSettingsPanel, gbc);
        
    	bagInfoInputPane = new BagInfoInputPane(bagView, false);
    	bagInfoInputPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	bagInfoInputPane.setEnabled(false);
    	
        gbc = LayoutUtil.buildGridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoPanel.add(bagInfoInputPane, gbc);
    	this.setViewportView(infoPanel);
    }


    private JPanel createSettingsPanel() {
    	JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		
		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel, BorderLayout.NORTH);
		mainPanel.setLayout(new BorderLayout(0, 0));
		
		// Bag settings
		mainPanel.add(createBagSettingsPanel(),BorderLayout.CENTER);
		
    	return contentPane;
    }
    
    

	private JPanel createBagSettingsPanel() {
		JPanel pane = new JPanel();
		
		pane.setLayout(new GridBagLayout());
		
		// bag name
		int row = 0;
		JLabel lblBagName = new JLabel(bagView.getPropertyMessage("bag.label.name"));
		GridBagConstraints gbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(lblBagName, gbc);
		
		bagNameValue = new JLabel(bagView.getPropertyMessage("bag.label.noname"));
		gbc = LayoutUtil.buildGridBagConstraints(1, row, 3, 1, 3, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(bagNameValue, gbc);
		
		// bag profile
		row++;
		JLabel bagProfileLabel = new JLabel("Profile:");
		gbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(bagProfileLabel, gbc);
		
		bagProfileValue = new JLabel("");
		gbc = LayoutUtil.buildGridBagConstraints(1, row, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(bagProfileValue, gbc);
		
		// bag version
		JLabel bagVersionLabel = new JLabel(bagView.getPropertyMessage("bag.label.version"));
    	bagVersionLabel.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
    	gbc = LayoutUtil.buildGridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(bagVersionLabel, gbc);
		
		bagVersionValue = new JLabel("");
    	gbc = LayoutUtil.buildGridBagConstraints(3, row, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
    	gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(bagVersionValue, gbc);
		
		// is Holey bag?
		row++;
		JLabel holeyLabel = new JLabel(bagView.getPropertyMessage("bag.label.isholey"));
    	holeyLabel.setToolTipText(bagView.getPropertyMessage("bag.isholey.help"));
    	gbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(holeyLabel, gbc);
		
		holeyValue = new JLabel("");
		gbc = LayoutUtil.buildGridBagConstraints(1, row, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(holeyValue, gbc);
		
		// is packed?
		JLabel serializeLabel = new JLabel(bagView.getPropertyMessage("bag.label.ispackage"));
    	serializeLabel.setToolTipText(bagView.getPropertyMessage("bag.serializetype.help"));
    	gbc = LayoutUtil.buildGridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.NONE, GridBagConstraints.WEST);
		gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(serializeLabel, gbc);
		
		serializeValue = new JLabel("");
		gbc = LayoutUtil.buildGridBagConstraints(3, row, 1, 1, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
    	gbc.insets = new Insets(0, 0, 5, 5);
		pane.add(serializeValue, gbc);
		return pane;
    }
	



    public void setBagVersion(String value) {
    	bagVersionValue.setText(value);
    }

    public String getBagVersion() {
    	return bagVersionValue.getText();
    }
    
    public void setHoley(String value) {
    	holeyValue.setText(value);
    }

   

    public void setBagName(String name) {
    	if (name == null || name.length() < 1) return;
    	bagNameValue.setText(name);
    }
    
    public String getBagName() {
    	return bagNameValue.getText();
    }
    
    public void updateInfoForms() {
    	bagInfoInputPane.populateForms(bag, false);
    	bagInfoInputPane.enableForms(false);
    	bagInfoInputPane.invalidate();
    }

    public void updateInfoFormsPane(boolean enabled) {
    	// need to remove something?
    	infoPanel.remove(bagInfoInputPane);
    	infoPanel.validate();
    	
    	bagInfoInputPane = new BagInfoInputPane(bagView, enabled);
    	bagInfoInputPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	
    	
    	GridBagConstraints gbc = LayoutUtil.buildGridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoPanel.add(bagInfoInputPane, gbc);
    	
        this.validate();
    }
    
    public void showTabPane(int i) {
    	bagInfoInputPane.setSelectedIndex(i);
    	bagInfoInputPane.invalidate();
    }

}
