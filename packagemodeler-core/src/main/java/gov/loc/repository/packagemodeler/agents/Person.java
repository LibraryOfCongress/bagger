package gov.loc.repository.packagemodeler.agents;

public interface Person extends Agent {

	public abstract void setFirstName(String firstName);
	
	public abstract String getFirstName();
	
	public abstract void setSurname(String surname);
	
	public abstract String getSurname();
	
}
