package gov.loc.repository.fixity;

public enum FixityAlgorithm {
	MD5, SHA1, SHA256;
		
	public static FixityAlgorithm fromString(String algorithm) throws IllegalArgumentException
	{
		return FixityAlgorithm.valueOf(algorithm.toUpperCase());
		
	}

}
