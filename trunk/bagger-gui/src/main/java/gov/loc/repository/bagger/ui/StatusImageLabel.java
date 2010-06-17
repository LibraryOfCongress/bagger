package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.model.Status;
import gov.loc.repository.bagger.model.StatusModel;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class StatusImageLabel extends JLabel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	
	public static final String PASS_STATUS_ICON = "status.success.icon";
	public static final String FAILURE_STATUS_ICON = "status.fail.icon";
	public static final String UNKNOWN_STATUS_ICON = "status.unknown.icon";
	
	public StatusImageLabel(StatusModel model) {
		super("");
		changeIcon(model.getStatus());
		model.addPropertyChangeListener(this);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		Status newValue = (Status)evt.getNewValue();
		changeIcon(newValue);
	}

	private void changeIcon(Status status) {
		ImageIcon icon = null;
		if (status == Status.PASS) {
			icon = new ImageIcon(ApplicationContextUtil.getImage(PASS_STATUS_ICON));
			setToolTipText(ApplicationContextUtil.getMessage("consolepane.status.pass.help"));
		} else if (status == Status.FAILURE) {
			icon = new ImageIcon(ApplicationContextUtil.getImage(FAILURE_STATUS_ICON));
			setToolTipText(ApplicationContextUtil.getMessage("consolepane.status.fail.help"));
		} else {
			icon = new ImageIcon(ApplicationContextUtil.getImage(UNKNOWN_STATUS_ICON));
			setToolTipText(ApplicationContextUtil.getMessage("consolepane.status.unknown.help"));
		}
		this.setIcon(icon);
		
	}
}
