package gov.loc.repository.bagger;

import java.util.List;

public interface Bagger {
	
	public List<Profile> loadProfiles();
	public void loadProfile(String profileName);
	public void saveProfile(Profile profile);
	public void removeProfile(Profile profile);

}
