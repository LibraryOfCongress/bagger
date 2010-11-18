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

import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.ui.util.LayoutUtil;
import gov.loc.repository.bagit.BagFactory.Version;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

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

public class NewBagFrame extends JFrame implements ActionListener {
	private static final Log log = LogFactory.getLog(NewBagFrame.class);
	private static final long serialVersionUID = 1L;
	private BagView bagView;
	private JComboBox bagVersionList;
	private JComboBox profileList;

	public NewBagFrame(BagView bagView, String title) {
        super(title);
        JPanel createPanel;
		Application app = Application.instance();
		ApplicationPage page = app.getActiveWindow().getPage();
		PageComponent component = page.getActiveComponent();
		
		if (component != null) this.bagView = BagView.instance;
		else this.bagView = bagView;
		if (bagView != null) {
	        getContentPane().removeAll();
	        createPanel = createComponents();
		} else {
			createPanel = new JPanel();
		}
        getContentPane().add(createPanel, BorderLayout.CENTER);
        
        setPreferredSize(new Dimension(400, 200));
        setLocation(300, 200);
        pack();
    }

    private JPanel createComponents() {
    	TitlePane titlePane = new TitlePane();
        initStandardCommands();
        JPanel pageControl = new JPanel(new BorderLayout());
		JPanel titlePaneContainer = new JPanel(new BorderLayout());
		titlePane.setTitle(bagView.getPropertyMessage("NewBagFrame.title"));
		titlePane.setMessage( new DefaultMessage(bagView.getPropertyMessage("NewBagFrame.description")));
		titlePaneContainer.add(titlePane.getControl());
		titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
		pageControl.add(titlePaneContainer, BorderLayout.NORTH);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new GridBagLayout());
		
		int row = 0;
		layoutBagVersionSelection(contentPane, row++);
		layoutProfileSelection(contentPane, row++);
        
		if (getPreferredSize() != null) {
			contentPane.setPreferredSize(getPreferredSize());
		}
		
		GuiStandardUtils.attachDialogBorder(contentPane);
		pageControl.add(contentPane);
		JComponent buttonBar = createButtonBar();
		pageControl.add(buttonBar,BorderLayout.SOUTH);
	
		this.pack();
		return pageControl;
    }

	

	private void layoutBagVersionSelection(JPanel contentPane, int row) {
		//contents
		// Bag version dropdown list
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
		
        GridBagConstraints glbc = null;

        JLabel spacerLabel = new JLabel();
        glbc = LayoutUtil.buildGridBagConstraints(0, row, 1, 1, 5, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        contentPane.add(bagVersionLabel, glbc);
        glbc = LayoutUtil.buildGridBagConstraints(1, row, 1, 1, 40, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        contentPane.add(bagVersionList, glbc);
        glbc = LayoutUtil.buildGridBagConstraints(2, row, 1, 1, 40, 50, GridBagConstraints.NONE, GridBagConstraints.EAST);
        contentPane.add(spacerLabel, glbc);
	}
	
	private void layoutProfileSelection(JPanel contentPane, int row) {
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
				
				log.info("BagVersionFrame.OkNewBagHandler");
				NewBagFrame.this.setVisible(false);
				bagView.startNewBagHandler.createNewBag((String)bagVersionList.getSelectedItem(),
						(String)profileList.getSelectedItem());
			}
		};


		cancelCommand = new ActionCommand(getCancelCommandId()) {
			public void doExecuteCommand() {
				NewBagFrame.this.setVisible(false);
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

    public void actionPerformed(ActionEvent e) {
    	invalidate();
    	repaint();
    }
   

}