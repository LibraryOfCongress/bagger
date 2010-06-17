package gov.loc.repository.bagger.model;

public class BagStatus {
	
	private static BagStatus instance = new BagStatus();

	private StatusModel validationStatus = new StatusModel();
	private StatusModel completenessStatus = new StatusModel();
	private StatusModel profileComplianceStatus = new StatusModel();
	
	public StatusModel getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(StatusModel validationStatus) {
		this.validationStatus = validationStatus;
	}

	public StatusModel getCompletenessStatus() {
		return completenessStatus;
	}

	public void setCompletenessStatus(StatusModel completenessStatus) {
		this.completenessStatus = completenessStatus;
	}

	public StatusModel getProfileComplianceStatus() {
		return profileComplianceStatus;
	}

	public void setProfileComplianceStatus(StatusModel profileComplianceStatus) {
		this.profileComplianceStatus = profileComplianceStatus;
	}

	public static BagStatus getInstance() {
		return instance;
	}
	
}
