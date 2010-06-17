package gov.loc.repository.bagger.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class StatusModel {

	private Status status = Status.UNKNOWN;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		Status old = this.status;
		this.status = status;
		this.pcs.firePropertyChange("status", old, status);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

}
