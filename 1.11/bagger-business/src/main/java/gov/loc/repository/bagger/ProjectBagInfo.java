package gov.loc.repository.bagger;

public class ProjectBagInfo {
	private int id = -1;
	private int projectId;
	private String defaults = "";

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

	public void setDefaults(String s) {
		this.defaults = s;
	}

	public String getDefaults() {
		return this.defaults;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Id: ");
		sb.append(this.getId());
		sb.append('\n');
		sb.append("Project Id: ");
		sb.append(this.projectId);
		sb.append('\n');
		sb.append("Defaults: ");
		sb.append(this.defaults);
		sb.append('\n');

		return sb.toString();
	}
}
