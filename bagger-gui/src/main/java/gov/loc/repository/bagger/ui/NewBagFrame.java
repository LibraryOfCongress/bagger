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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
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
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.TitlePane;
import org.springframework.richclient.util.GuiStandardUtils;

public class NewBagFrame extends JFrame implements ActionListener {
	private static final Log log = LogFactory.getLog(NewBagFrame.class);
	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag = null;
	private Dimension preferredDimension = new Dimension(400, 200);
	JPanel createPanel;
//	JButton okButton;
//	JButton cancelButton;
	JComboBox bagVersionList;
	String bagVersion;

	public NewBagFrame(BagView bagView, String title) {
		
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

        int row = 0;
        
        JLabel spacerLabel = new JLabel();
        
        
        buildConstraints(glbc, 0, row, 1, 1, 5, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        layout.setConstraints(bagVersionLabel, glbc);
        buildConstraints(glbc, 1, row, 1, 1, 40, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        layout.setConstraints(bagVersionList, glbc);
        buildConstraints(glbc, 2, row, 1, 1, 40, 50, GridBagConstraints.NONE, GridBagConstraints.EAST);
        layout.setConstraints(spacerLabel, glbc);
        contentPane.setLayout(layout);
        
        contentPane.add(bagVersionLabel);
        contentPane.add(bagVersionList);
        contentPane.add(spacerLabel);
        
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
		        bagView.infoInputPane.bagVersionValue.setText(bagVersion);
				bagView.startNewBagHandler.createNewBag();

			}
		};


		cancelCommand = new ActionCommand(getCancelCommandId()) {

			public void doExecuteCommand() {
				NewBagFrame.this.setVisible(false);
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

//    private class OkNewBagHandler extends AbstractAction {
//		private static final long serialVersionUID = 1L;
//
//		public void actionPerformed(ActionEvent e) {
//			log.info("BagVersionFrame.OkNewBagHandler");
//			setVisible(false);
//	        bagView.infoInputPane.bagVersionValue.setText(bagVersion);
//			bagView.startNewBagHandler.createNewBag();
//        }
//    }
//
//    private class CancelNewBagHandler extends AbstractAction {
//		private static final long serialVersionUID = 1L;
//
//		public void actionPerformed(ActionEvent e) {
//			setVisible(false);
//        }
//    }

    private class VersionListHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
        	JComboBox jlist = (JComboBox)e.getSource();
        	String version = (String) jlist.getSelectedItem();
        	bagVersion = version;
        	bagView.infoInputPane.bagVersionValue.setText(version);
    	}
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