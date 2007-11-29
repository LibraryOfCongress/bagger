package gov.loc.repository.packagemodeler.packge.impl;

import gov.loc.repository.constants.Roles;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.agents.impl.SystemImpl;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.AssertTrue;

@Entity(name="StorageSystemFileLocation")
@Table(name = "storagesystem_filelocation", schema="core", uniqueConstraints={@UniqueConstraint(columnNames={"storagesystem_key","base_path"})})
public class StorageSystemFileLocationImpl extends FileLocationImpl implements
		StorageSystemFileLocation {

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity=SystemImpl.class)
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name="storagesystem_key", nullable = false)
	private System storageSystem;
	
	@Column(name = "base_path", nullable=false)	
	private String basePath;

	public void setStorageSystem(System storageSystem) {
		this.storageSystem = storageSystem;
	}

	public System getStorageSystem() {
		return this.storageSystem;
	}

	@SuppressWarnings("unused")
	@AssertTrue
	private boolean validateStorageSystem()
	{
		if (this.storageSystem == null)
		{
			return true;
		}
		return this.storageSystem.isInRole(Roles.STORAGE_SYSTEM);
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}
	
}
