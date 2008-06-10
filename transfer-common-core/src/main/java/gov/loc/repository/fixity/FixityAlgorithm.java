package gov.loc.repository.fixity;

public enum FixityAlgorithm {
	MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256");
	
	private final String javaSecurityName;
	
	FixityAlgorithm(String javaSecurityName) {
		this.javaSecurityName = javaSecurityName;
	}
	
	public String getJavaSecurityName()
	{
		return javaSecurityName;
	}
	
	public static FixityAlgorithm fromString(String algorithm) throws IllegalArgumentException
	{
		return FixityAlgorithm.valueOf(algorithm.toUpperCase());
		
	}

}
