package gov.loc.repository.utilities;

import java.io.File;

public interface FixityHelper {
	public void setAlgorithm(String algorithm);
	
	public String getAlgorithm(); 
	
	public String generateFixity(File file) throws Exception;
}
