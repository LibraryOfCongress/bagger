package gov.loc.repository.bagger;

public class ProjectProfile {
	private int id = -1;
	private int projectId = -1;
	private String fieldName = "";
	private String fieldValue = "";
	private String fieldType = "";
	private String elements = "";
	private boolean isRequired;
	private boolean isValueRequired;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setProjectId(int id) {
		this.projectId = id;
	}

	public int getProjectId() {
		return this.projectId;
	}

	public void setFieldName(String s) {
		this.fieldName = s;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setIsRequired(boolean b) {
		this.isRequired = b;
	}

	public boolean getIsRequired() {
		return this.isRequired;
	}

	public void setFieldValue(String s) {
		this.fieldValue = s;
	}

	public String getFieldValue() {
		return this.fieldValue;
	}

	public void setElements(String s) {
		this.elements = s;
	}
	
	public String getElements() {
		return this.elements;
	}

	public void setFieldType(String s) {
		this.fieldType = s;
	}
	
	public String getFieldType() {
		return this.fieldType;
	}

	public void setIsValueRequired(boolean b) {
		this.isValueRequired = b;
	}

	public boolean getIsValueRequired() {
		return this.isValueRequired;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Id: ");
		sb.append(this.getId());
		sb.append('\n');
		sb.append("Project Id: ");
		sb.append(this.projectId);
		sb.append('\n');
		sb.append(this.fieldName + '=' + this.fieldValue);
		sb.append('\n');

		return sb.toString();
	}
}
