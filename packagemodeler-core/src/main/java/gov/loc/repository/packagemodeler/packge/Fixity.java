package gov.loc.repository.packagemodeler.packge;

import javax.persistence.*;

@Embeddable
public class Fixity {

	public enum Algorithm {
		MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256");
		
		private final String javaSecurityName;
		
		Algorithm(String javaSecurityName) {
			this.javaSecurityName = javaSecurityName;
		}
		
		public String getJavaSecurityName()
		{
			return javaSecurityName;
		}
		
		public static Algorithm fromString(String algorithm) throws IllegalArgumentException
		{
			return Algorithm.valueOf(algorithm.toUpperCase());
			
		}
	}
	
	public Fixity() {
	}

	public Fixity(String fixityValue, Algorithm algorithm) {
		this.value = fixityValue;
		this.algorithm = algorithm;
	}

	public Fixity(String fixityValue, String algorithm) {
		this.value = fixityValue;
		this.algorithm = Algorithm.fromString(algorithm);
	}
	
	
	@Column(name="fixity_value", nullable = false)
	private String value;
	
	@Enumerated(EnumType.STRING)
	@Column(name="algorithm", nullable = false)
	private Algorithm algorithm;

	public Algorithm getAlgorithm() {
		return this.algorithm;
	}

	public String getValue() {
		return this.value;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
