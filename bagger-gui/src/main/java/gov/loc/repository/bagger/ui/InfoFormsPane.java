
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.handlers.NextButtonHandler;
import gov.loc.repository.bagger.ui.handlers.UpdateBagHandler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

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

    public InfoFormsPane(BagView bagView) {
    	super();
		Application app = Application.instance();
		ApplicationPage page = app.getActiveWindow().getPage();
		PageComponent component = page.getActiveComponent();
		if (component != null) this.bagView = (BagView) component;
		else this.bagView = bagView;
		this.bag = bagView.getBag();
		bag.getInfo().setBag(bag);
		//bag.getInfo().createStandardFieldList(false);
    	createScrollPane();
    }

    private void createScrollPane() {
    	// TODO: Add buttons for editing BagInfo form fields
    	bagView.bagInfoInputPane = new BagInfoInputPane(bagView, bagView.username, bagView.projectContact, false);
    	bagView.bagInfoInputPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	bagView.bagInfoInputPane.setEnabled(false);
    	JPanel bagSettingsPanel = new BagSettingsPanel(bagView);
    	bagInfoScrollPane = new JScrollPane();
    	bagInfoScrollPane.setViewportView(bagView.bagInfoInputPane);
    	//bagInfoScrollPane.setMinimumSize(bagView.bagInfoInputPane.getMinimumSize());
    	bagInfoScrollPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
/*    	
    	// Create a panel for the form error messages and the update button
        JButton nextButton = new JButton(bagView.getPropertyMessage("button.next"));
        nextButton.setMnemonic(KeyEvent.VK_ENTER);
        nextButton.addActionListener(new NextButtonHandler(bagView));
        nextButton.setToolTipText(bagView.getPropertyMessage("button.next.help"));
*/
        // Create a panel for the form error messages and the update button
        bagView.updatePropButton = new JButton(bagView.getPropertyMessage("button.saveupdates"));
        bagView.updatePropButton.setMnemonic(KeyEvent.VK_S);
        updateBagHandler = new UpdateBagHandler(bagView);
        bagView.updatePropButton.addActionListener(updateBagHandler);
        bagView.updatePropButton.setToolTipText(bagView.getPropertyMessage("button.saveupdates.help"));
        bagView.updatePropButton.setEnabled(false);
        
        JPanel infoLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bagView.infoFormMessagePane = new BagTextPane("");
        if (bagView.bagInfoInputPane.hasFormErrors(bag)) {
        	bagView.infoFormMessagePane.setMessage(bagView.getPropertyMessage("error.form"));
        }
        Dimension labelDimension = bagView.bagInfoInputPane.getPreferredSize();
        int offsetx = bagView.updatePropButton.getWidth();
        if (offsetx == 0) offsetx = 80;
        int offsety = 25;
        java.awt.Font font = bagView.infoFormMessagePane.getFont();
        if (font != null) {
            java.awt.FontMetrics fm = bagView.infoFormMessagePane.getFontMetrics(font);
            if (fm != null) offsety = 2*fm.getHeight();
        }
        labelDimension.setSize(bagView.bagInfoInputPane.getPreferredSize().width-offsetx, offsety);
        infoLabelPanel.setPreferredSize(labelDimension);
        bagView.infoFormMessagePane.setPreferredSize(labelDimension);
        bagView.infoFormMessagePane.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        infoLabelPanel.add(bagView.infoFormMessagePane, "North");
        if (bagView.bagInfoInputPane.hasFormErrors(bag)) {
            bagView.infoFormMessagePane.setBackground(bagView.errorColor);
        } else {
            bagView.infoFormMessagePane.setBackground(bagView.infoColor);
        }

        JLabel button = new JLabel("");
    	
        // Combine the information panel with the forms pane
        GridBagLayout infoLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        bagView.buildConstraints(gbc, 0, 0, 3, 1, 50, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagSettingsPanel, gbc);

        bagView.buildConstraints(gbc, 0, 1, 3, 1, 10, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(infoLabelPanel, gbc);

        bagView.buildConstraints(gbc, 0, 2, 1, 1, 80, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST);
        infoLayout.setConstraints(button, gbc);
/*
        bagView.buildConstraints(gbc, 1, 2, 1, 1, 10, 0, GridBagConstraints.NONE, GridBagConstraints.EAST);
        infoLayout.setConstraints(nextButton, gbc);
*/
        bagView.buildConstraints(gbc, 2, 2, 1, 1, 10, 0, GridBagConstraints.NONE, GridBagConstraints.EAST);
        infoLayout.setConstraints(bagView.updatePropButton, gbc);
        
        bagView.buildConstraints(gbc, 0, 3, 3, 1, 20, 0, GridBagConstraints.BOTH, GridBagConstraints.WEST);
        infoLayout.setConstraints(bagInfoScrollPane, gbc);
        
        JPanel infoPanel = new JPanel(infoLayout);
        infoPanel.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
        Border emptyBorder = new EmptyBorder(5, 5, 5, 5);
        int width = 0;
        int height = 0;
        infoPanel.setBorder(emptyBorder);
        infoPanel.add(infoLabelPanel);
        height += (int) infoLabelPanel.getPreferredSize().getHeight();
        infoPanel.add(bagInfoScrollPane);
        width += (int) bagInfoScrollPane.getPreferredSize().getWidth();
        height += (int) bagInfoScrollPane.getPreferredSize().getHeight();
        infoPanel.add(button);
//        infoPanel.add(nextButton);
        height += (int) bagView.updatePropButton.getPreferredSize().getHeight();
        infoPanel.add(bagView.updatePropButton);
        infoPanel.add(bagSettingsPanel);
        height += (int) bagSettingsPanel.getPreferredSize().getHeight();
        Dimension preferredSize = new Dimension(width, height);
        
    	this.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	this.setPreferredSize(preferredSize);
    	this.setViewportView(infoPanel);
    }

    public void updateInfoFormsPane(boolean enabled) {
    	bagView.bagInfoInputPane = new BagInfoInputPane(bagView, bagView.username, bagView.projectContact, enabled);
    	bagView.bagInfoInputPane.setToolTipText(bagView.getPropertyMessage("bagView.bagInfoInputPane.help"));
    	//bagView.bagInfoInputPane.setEnabled(false);
    	bagInfoScrollPane.setViewportView(bagView.bagInfoInputPane);
    	bagInfoScrollPane.setPreferredSize(bagView.bagInfoInputPane.getPreferredSize());
    	this.setPreferredSize(bagInfoScrollPane.getPreferredSize());
    	this.invalidate();
    }
}
