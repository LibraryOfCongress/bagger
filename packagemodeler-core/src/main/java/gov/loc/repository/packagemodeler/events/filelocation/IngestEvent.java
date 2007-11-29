package gov.loc.repository.packagemodeler.events.filelocation;

public interface IngestEvent extends FileLocationEvent {
	/*
	 * Returns the repository system that the package is ingested into.
	 */
	public gov.loc.repository.packagemodeler.agents.System getRepositorySystem();
	
	public void setRepositorySystem(gov.loc.repository.packagemodeler.agents.System system);
}
