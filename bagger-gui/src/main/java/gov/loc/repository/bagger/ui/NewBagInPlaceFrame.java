/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.util.LayoutUtil;
import gov.loc.repository.bagit.BagFactory.Version;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.dialog.TitlePane;
import org.springframework.richclient.util.GuiStandardUtils;

public class NewBagInPlaceFrame extends JFrame implements ActionListener {
	private static final Log log = LogFactory.getLog(NewBagFrame.class);
	private static final long serialVersionUID = 1L;
	private BagView bagView;
	private DefaultBag bag = null;
	private Dimension preferredDimension = new Dimension(400, 230);
	private JPanel createPanel;
	private JTextField bagNameField;
	private File bagFile;
	private String bagFileName = "";
	private JButton saveAsButton;
	private JComboBox bagVersionList;
	private JComboBox profileList;
	private JCheckBox addKeepFilesToEmptyFoldersCheckBox;

	public NewBagInPlaceFrame(BagView bagView, String title) {
        super(title);
		Application app = Application.instance();
		ApplicationPage page = app.getActiveWindow().getPage();
		PageComponent component = page.getActiveComponent();
		if (component != null) this.bagView = BagView.instance;
		else this.bagView = bagView;
		if (bagView != null) {
			bag = bagView.getBag();
	        getContentPane().removeAll();
	        createPanel = createComponents();
		} else {
			createPanel = new JPanel();
		}
        getContentPane().add(createPanel, BorderLayout.CENTER);
        setPreferredSize(preferredDimension);
        setLocation(200, 100);
        pack();
    }

    private JPanel createComponents() {

    	TitlePane titlePane = new TitlePane();
    	initStandardCommands();
    	JPanel pageControl = new JPanel(new BorderLayout());
    	JPanel titlePaneContainer = new JPanel(new BorderLayout());
    	titlePane.setTitle(bagView.getPropertyMessage("NewBagInPlace.title"));
    	titlePane.setMessage( new DefaultMessage(bagView.getPropertyMessage("NewBagInPlace.description")));
    	titlePaneContainer.add(titlePane.getControl());
    	titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
    	pageControl.add(titlePaneContainer, BorderLayout.NORTH);
    	
    	JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        int row = 0;
        layoutSelectDataContent(contentPanel, row++);
        layoutBagVersionContent(contentPanel, row++);
        layoutProfileSelectionContent(contentPanel, row++);
        layoutAddKeepFilesToEmptyCheckBox(contentPanel, row++);
        layoutSpacer(contentPanel, row++);
        
        GuiStandardUtils.attachDialogBorder(contentPanel);
		pageControl.add(contentPanel);
		JComponent buttonBar = createButtonBar();
		pageControl.add(buttonBar,BorderLayout.SOUTH);
	
		this.pack();
		return pageControl;
        
    }
    
	private void layoutSelectDataContent(JPanel contentPanel, int row) {
		GridBagConstraints glbc = new GridBagConstraints();
    	JLabel location = new JLabel("Select Data:");
    	saveAsButton = new JButton(bagView.getPropertyMessage("bag.button.browse"));
    	saveAsButton.addActionListener(new BrowseFileHandler());
    	saveAsButton.setEnabled(true);
    	saveAsButton.setToolTipText(bagView.getPropertyMessage("bag.button.browse.help"));
    	
    	String fileName = "";
    	if (bag != null) fileName = bag.getName();
    	bagNameField = new JTextField(fileName);
        bagNameField.setCaretPosition(fileName.length());
        bagNameField.setEditable(false);
        bagNameField.setEnabled(false);

        glbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 1, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        contentPanel.add(location, glbc);
        
        glbc = LayoutUtil.buildGridBagConstraints(2, row, 1, 1, 1, 50, GridBagConstraints.NONE, GridBagConstraints.EAST); 
        glbc.ipadx=5;
        glbc.ipadx=0;
        contentPanel.add(saveAsButton, glbc);
        
        glbc = LayoutUtil.buildGridBagConstraints(1, row, 1, 1, 80, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        glbc.ipadx=5;
        glbc.ipadx=0;
        contentPanel.add(bagNameField, glbc);
    }
	
	private void layoutBagVersionContent(JPanel contentPanel, int row) {
		GridBagConstraints glbc = new GridBagConstraints();
		
		JLabel bagVersionLabel = new JLabel(bagView.getPropertyMessage("bag.label.version"));
        bagVersionLabel.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
        ArrayList<String> versionModel = new ArrayList<String>();
        Version[] vals = Version.values();
        for (int i=0; i < vals.length; i++) {
        	versionModel.add(vals[i].versionString);
        }

        bagVersionList = new JComboBox(versionModel.toArray());
        bagVersionList.setName(bagView.getPropertyMessage("bag.label.versionlist"));
        bagVersionList.setSelectedItem(Version.V0_96.versionString);
        bagVersionList.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
        
		
        glbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 1, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        contentPanel.add(bagVersionLabel, glbc);
        glbc = LayoutUtil.buildGridBagConstraints(1, row, 1, 1, 80, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST); 
        contentPanel.add(bagVersionList, glbc);
	}
	
	private void layoutProfileSelectionContent(JPanel contentPane, int row) {
		// content
		// profile selection
		JLabel bagProfileLabel = new JLabel(bagView.getPropertyMessage("Select Profile:"));
		bagProfileLabel.setToolTipText(bagView.getPropertyMessage("bag.projectlist.help"));
       
        profileList = new JComboBox(bagView.getProfileStore().getProfileNames());
        profileList.setName(bagView.getPropertyMessage("bag.label.projectlist"));
        profileList.setSelectedItem(bagView.getPropertyMessage("bag.project.noproject"));
        profileList.setToolTipText(bagView.getPropertyMessage("bag.projectlist.help"));
		
        GridBagConstraints glbc = new GridBagConstraints();

        JLabel spacerLabel = new JLabel();
        glbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 5, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        contentPane.add(bagProfileLabel, glbc);
        glbc = LayoutUtil.buildGridBagConstraints(1, row, 1, 1, 40, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        contentPane.add(profileList, glbc);
        glbc = LayoutUtil.buildGridBagConstraints(2, row, 1, 1, 40, 50, GridBagConstraints.NONE, GridBagConstraints.EAST);
        contentPane.add(spacerLabel, glbc);
	}

    /*
     *  The actionPerformed method in this class
     *  is called each time the ".keep Files in Empty Folder(s):" Check Box
     *  is Selected
     */   
	private class AddKeepFilesToEmptyFoldersHandler extends AbstractAction {	       	
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {			

			JCheckBox cb = (JCheckBox)e.getSource();

			// Determine status
			boolean isSelected = cb.isSelected();
			if (isSelected) {
				bagView.getBag().isAddKeepFilesToEmptyFolders(true);
				bagView.infoInputPane.serializeValue.setText("true");
			} else {
				bagView.getBag().isAddKeepFilesToEmptyFolders(false);
			}
		}
	}
	
	/*
     *  Setting and displaying the ".keep Files in Empty Folder(s):" Check Box 
     *  on the Create Bag In Place Pane
     */
	private void layoutAddKeepFilesToEmptyCheckBox(JPanel contentPane, int row) {
		// Delete Empty Folder(s)
		JLabel addKeepFilesToEmptyFoldersCheckBoxLabel = new JLabel(bagView.getPropertyMessage("bag.label.addkeepfilestoemptyfolders"));
		addKeepFilesToEmptyFoldersCheckBoxLabel.setToolTipText(bagView.getPropertyMessage("bag.addkeepfilestoemptyfolders.help"));
       addKeepFilesToEmptyFoldersCheckBox = new JCheckBox(bagView.getPropertyMessage(""));
       addKeepFilesToEmptyFoldersCheckBox.setSelected(bag.isAddKeepFilesToEmptyFolders());
       addKeepFilesToEmptyFoldersCheckBox.addActionListener(new AddKeepFilesToEmptyFoldersHandler());
       
       GridBagConstraints glbc = new GridBagConstraints();

       JLabel spacerLabel = new JLabel();
       glbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 5, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
       contentPane.add(addKeepFilesToEmptyFoldersCheckBoxLabel, glbc);
       glbc = LayoutUtil.buildGridBagConstraints(1, row, 1, 1, 40, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
       contentPane.add(addKeepFilesToEmptyFoldersCheckBox, glbc);
       glbc = LayoutUtil.buildGridBagConstraints(2, row, 1, 1, 40, 50, GridBagConstraints.NONE, GridBagConstraints.EAST);
       contentPane.add(spacerLabel, glbc);
	}
	
	private void layoutSpacer(JPanel contentPanel, int row) {
		GridBagConstraints glbc = new GridBagConstraints();
		glbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 1, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        JLabel spacerLabel = new JLabel("");
        contentPanel.add(spacerLabel, glbc);
	}

	protected JComponent createButtonBar() {
		CommandGroup dialogCommandGroup = CommandGroup.createCommandGroup(null, getCommandGroupMembers());
		JComponent buttonBar = dialogCommandGroup.createButtonBar();
		GuiStandardUtils.attachDialogBorder(buttonBar);
		return buttonBar;
	}
	
	protected Object[] getCommandGroupMembers() {
		return new AbstractCommand[] { finishCommand, cancelCommand };
	}
	
    /**
	 * Initialize the standard commands needed on a Dialog: Ok/Cancel.
	 */
	private void initStandardCommands() {
		finishCommand = new ActionCommand(getFinishCommandId()) {
			public void doExecuteCommand() {
				
				new OkNewBagHandler().actionPerformed(null);

			}
		};


		cancelCommand = new ActionCommand(getCancelCommandId()) {
			public void doExecuteCommand() {
				new CancelNewBagHandler().actionPerformed(null);
			}
		};
	}
	
	protected String getFinishCommandId() {
		return DEFAULT_FINISH_COMMAND_ID;
	}
	
	protected String getCancelCommandId() {
		return DEFAULT_CANCEL_COMMAND_ID;
	}
	
	protected static final String DEFAULT_FINISH_COMMAND_ID = "okCommand";

	protected static final String DEFAULT_CANCEL_COMMAND_ID = "cancelCommand";
	
	private ActionCommand finishCommand;

	private ActionCommand cancelCommand;

    public void setBag(DefaultBag bag) {
    	this.bag = bag;
    	createPanel.invalidate();
    }

    public void actionPerformed(ActionEvent e) {
    	invalidate();
    	repaint();
    }

    private class BrowseFileHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			bag = bagView.getBag();
			File selectFile = new File(File.separator+".");
	        JFrame frame = new JFrame();
	        JFileChooser fs = new JFileChooser(selectFile);
	    	fs.setDialogType(JFileChooser.OPEN_DIALOG);
	    	fs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    	fs.addChoosableFileFilter(bagView.infoInputPane.noFilter);
			fs.setFileFilter(bagView.infoInputPane.noFilter);
	        fs.setDialogTitle("Existing Data Location");
		    if (bagView.getBagRootPath() != null) fs.setCurrentDirectory(bagView.getBagRootPath().getParentFile());
	    	fs.setCurrentDirectory(bag.getRootDir());
	    	if (bag.getName() != null && !bag.getName().equalsIgnoreCase(bagView.getPropertyMessage("bag.label.noname"))) {
	    		String selectedName = bag.getName();
	    		if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
	    			selectedName += "."+DefaultBag.ZIP_LABEL;
	    			fs.setFileFilter(bagView.infoInputPane.zipFilter);
	    		}
	    		else if (bag.getSerialMode() == DefaultBag.TAR_MODE ||
	    				bag.getSerialMode() == DefaultBag.TAR_GZ_MODE ||
	    				bag.getSerialMode() == DefaultBag.TAR_BZ2_MODE) {
	    			selectedName += "."+DefaultBag.TAR_LABEL;
	    			fs.setFileFilter(bagView.infoInputPane.tarFilter);
	    		}
	    		else {
	    			fs.setFileFilter(bagView.infoInputPane.noFilter);
	    		}
	    		fs.setSelectedFile(new File(selectedName));
	    	} else {
    			fs.setFileFilter(bagView.infoInputPane.noFilter);
	    	}
	    	int	option = fs.showOpenDialog(frame);

	        if (option == JFileChooser.APPROVE_OPTION) {
	            File file = fs.getSelectedFile();
	            bagFile = file;
	            bagFileName = bagFile.getAbsolutePath();
	            // TODO: bag name is bag_<filename>
	            //bagView.bagNameField.setText(bagFile.getName());
	            bagNameField.setText(bagFileName);
	            bagNameField.setCaretPosition(bagFileName.length());
	            bagNameField.invalidate();
	        }
        }
    }

    /*
     *  The actionPerformed method in this class
     *  is called each time the "OK" button is clicked.
     *  The Create Bag In Place is created based on the 
     *  ".keep Files in Empty Folder(s):" Check Box being selected
     */
    private class OkNewBagHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			log.info("BagVersionFrame.OkNewBagHandler");
			setVisible(false);			
			if (bagView.getBag().isAddKeepFilesToEmptyFolders()) {
				bagView.createBagInPlaceHandler.createPreBagAddKeepFilesToEmptyFolders(bagFile,
					    (String)bagVersionList.getSelectedItem(),
					    (String)profileList.getSelectedItem());					
			} else {							
				bagView.createBagInPlaceHandler.createPreBag(bagFile, 
					(String)bagVersionList.getSelectedItem(),
					(String)profileList.getSelectedItem());
			}
        }
    }

    private class CancelNewBagHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			setVisible(false);
        }
    }

}