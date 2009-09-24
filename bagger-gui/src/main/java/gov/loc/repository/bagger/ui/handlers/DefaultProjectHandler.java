
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.Project;
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
		Set<String> projectKeys = bagView.bagProject.userProjects.keySet();
		for (Iterator<String> iter = projectKeys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Project project = bagView.bagProject.userProjects.get(key);
			project.setIsDefault(false);
			bagView.bagProject.userProjects.put(key, project);
		}
		Project bagProject = bag.getProject();
   		if (isSelected) bagProject.setIsDefault(true);
   		else bagProject.setIsDefault(false);
   		bag.setProject(bagProject);
   		bagView.setBag(bag);
	}
}
