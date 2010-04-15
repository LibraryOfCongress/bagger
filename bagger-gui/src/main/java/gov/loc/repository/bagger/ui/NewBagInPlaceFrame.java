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
	BagView bagView;
	DefaultBag bag = null;
	private Dimension preferredDimension = new Dimension(400, 230);
	JPanel createPanel;
	JTextField bagNameField;
	File bagFile;
	String bagFileName = "";
	JButton saveAsButton;
	JButton okButton;
	JButton cancelButton;
	JComboBox bagVersionList;
	String bagVersion;

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

    	//contents
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
        bagVersion = Version.V0_96.versionString;
        bagVersionList.addActionListener(new VersionListHandler());
        bagVersionList.setToolTipText(bagView.getPropertyMessage("bag.versionlist.help"));
        
    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();
        JPanel panel = new JPanel(layout);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        int row = 0;
        
        buildConstraints(glbc, 0, row, 1, 1, 1, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        layout.setConstraints(location, glbc);
        panel.add(location);
        
        buildConstraints(glbc, 2, row, 1, 1, 1, 50, GridBagConstraints.NONE, GridBagConstraints.EAST); 
        glbc.ipadx=5;
        layout.setConstraints(saveAsButton, glbc);
        glbc.ipadx=0;
        panel.add(saveAsButton);
        
        buildConstraints(glbc, 1, row, 1, 1, 80, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        glbc.ipadx=5;
        layout.setConstraints(bagNameField, glbc);
        glbc.ipadx=0;
        panel.add(bagNameField);
        
        row++;
        buildConstraints(glbc, 0, row, 1, 1, 1, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        layout.setConstraints(bagVersionLabel, glbc);
        panel.add(bagVersionLabel);
        buildConstraints(glbc, 1, row, 1, 1, 80, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST); 
        layout.setConstraints(bagVersionList, glbc);
        panel.add(bagVersionList);
        
        row++;
        
        buildConstraints(glbc, 0, row, 1, 1, 1, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        JLabel spacerLabel = new JLabel("");
        layout.setConstraints(spacerLabel, glbc);
        panel.add(spacerLabel);
        
        
        GuiStandardUtils.attachDialogBorder(panel);
		pageControl.add(panel);
		JComponent buttonBar = createButtonBar();
		pageControl.add(buttonBar,BorderLayout.SOUTH);
	
		this.pack();
		return pageControl;
        
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
	
	/**
	 * Select the appropriate close logic.
	 */
	private void executeCloseAction() {
		
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

    private class OkNewBagHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			log.info("BagVersionFrame.OkNewBagHandler");
			setVisible(false);
	        bagView.infoInputPane.bagVersionValue.setText(bagVersion);
			bagView.createBagInPlaceHandler.createPreBag(bagFile);
        }
    }

    private class CancelNewBagHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			setVisible(false);
        }
    }

    private class VersionListHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
        	JComboBox jlist = (JComboBox)e.getSource();
        	String version = (String) jlist.getSelectedItem();
        	bagVersion = version;
        	bagView.infoInputPane.bagVersionValue.setText(version);
    	}
    }

    private String getMessage(String property) {
    	return bagView.getPropertyMessage(property);
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

}