package gov.loc.repository.packagemodeler.packge;

import gov.loc.repository.fixity.FixityAlgorithm;

import java.text.MessageFormat;

import javax.persistence.*;

@Embeddable
public class Fixity {
	
	public Fixity() {
	}

	public Fixity(String fixityValue, FixityAlgorithm algorithm) {
		this.value = fixityValue;
		this.algorithm = algorithm;
	}

	public Fixity(String fixityValue, String algorithm) {
		this.value = fixityValue;
		this.algorithm = FixityAlgorithm.fromString(algorithm);
	}
		
	@Column(name="fixity_value", nullable = false)
	private String value;
	
	@Enumerated(EnumType.STRING)
	@Column(name="algorithm", nullable = false)
	private FixityAlgorithm algorithm;

	public FixityAlgorithm getFixityAlgorithm() {
		return this.algorithm;
	}

	public String getValue() {
		return this.value;
	}

	public void setFixityAlgorithm(FixityAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {		
		return MessageFormat.format("Fixity with value {0} and type {1}", this.value, this.algorithm.toString());
	}
}
