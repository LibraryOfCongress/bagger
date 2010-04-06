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
import gov.loc.repository.bagger.bag.impl.DefaultBag;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.dialog.TitlePane;
import org.springframework.richclient.util.GuiStandardUtils;

public class NewProjectFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag = null;
	private Dimension preferredDimension = new Dimension(400, 200);
	JPanel addPanel;
	JButton okButton;
	JButton cancelButton;
    JTextField projectName;
    JPanel projectPanel;

	public NewProjectFrame(BagView bagView, String title) {
        super(title);
		this.bagView = bagView;
		bag = bagView.getBag();
		getContentPane().removeAll();
		addPanel = createComponents();
        addPanel.setPreferredSize(preferredDimension);
        getContentPane().add(addPanel, BorderLayout.CENTER);
        setLocation(300, 200);
        pack();
    }

    private JPanel createComponents() {
    	TitlePane titlePane = new TitlePane();
        initStandardCommands();
        JPanel pageControl = new JPanel(new BorderLayout());
		JPanel titlePaneContainer = new JPanel(new BorderLayout());
		titlePane.setTitle(bagView.getPropertyMessage("NewProfileFrame.title"));
		titlePane.setMessage( new DefaultMessage(bagView.getPropertyMessage("NewProfileFrame.description")));
		titlePaneContainer.add(titlePane.getControl());
		titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
		pageControl.add(titlePaneContainer, BorderLayout.NORTH);
		JPanel contentPane = new JPanel();
    	
    	//contents
    	JLabel projectLabel = new JLabel(getMessage("project.name"));
    	projectName = new JTextField(10);
    	projectName.setToolTipText(getMessage("project.name.help"));
    	projectName.setEnabled(true);
    	
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        int row = 0;
        JLabel spacerLabel = new JLabel();
        
        buildConstraints(glbc, 0, row, 1, 1, 5, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        layout.setConstraints(projectLabel, glbc);
        buildConstraints(glbc, 1, row, 1, 1, 40, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        layout.setConstraints(projectName, glbc);
        buildConstraints(glbc, 2, row, 1, 1, 40, 50, GridBagConstraints.NONE, GridBagConstraints.EAST);
        layout.setConstraints(spacerLabel, glbc);
        contentPane.setLayout(layout);
        
        contentPane.add(projectLabel);
        contentPane.add(projectName);
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
				
				new OkAddFieldHandler().actionPerformed(null);

			}
		};


		cancelCommand = new ActionCommand(getCancelCommandId()) {
			public void doExecuteCommand() {
				new CancelAddFieldHandler().actionPerformed(null);
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

    public void actionPerformed(ActionEvent e) {
    	invalidate();
    	repaint();
    }

    private class OkAddFieldHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
    		String name = projectName.getText().trim();
    		Profile profile = new Profile();
    		profile.setName(name);
    		if (bagView.bagProject.ProfileExists(name)) {
    			bagView.showWarningErrorDialog("New Project Dialog", "Project already exists!");
    			return;
    		} else {
    			setVisible(false);
    			bagView.bagProject.addProfile(profile);
    			bagView.getBag().setProfile(profile);
        		bagView.bagProject.updateProfile(name);
            	bagView.infoInputPane.updateBagHandler.updateBag(bagView.getBag());
    		}
        }
    }

    private class CancelAddFieldHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			setVisible(false);
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

    private String getMessage(String property) {
    	return bagView.getPropertyMessage(property);
    }
}