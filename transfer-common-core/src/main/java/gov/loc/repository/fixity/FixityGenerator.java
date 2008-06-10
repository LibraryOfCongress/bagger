package gov.loc.repository.fixity;


import java.io.File;

public interface FixityGenerator {
	public String generateFixity(File file, FixityAlgorithm algorithm);
	
	public boolean canGenerate(FixityAlgorithm algorithm);
	
	public boolean fixityMatches(File file, FixityAlgorithm algorithm, String fixity);
}
