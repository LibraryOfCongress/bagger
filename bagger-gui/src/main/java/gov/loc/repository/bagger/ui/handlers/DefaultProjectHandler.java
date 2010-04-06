
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;

public class DefaultProjectHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public DefaultProjectHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		this.bag = bagView.getBag();
		JCheckBox cb = (JCheckBox)e.getSource();

		// Determine status
		boolean isSelected = cb.isSelected();
		Set<String> profileKeys = bagView.bagProject.userProfiles.keySet();
		for (Iterator<String> iter = profileKeys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Profile profile = bagView.bagProject.userProfiles.get(key);
			profile.setIsDefault(false);
			bagView.bagProject.userProfiles.put(key, profile);
		}
		
		Profile bagProfile = bag.getProfile();
   		if (isSelected) bagProfile.setIsDefault(true);
   		else bagProfile.setIsDefault(false);
   		bag.setProfile(bagProfile);
   		bagView.setBag(bag);
	}
}
