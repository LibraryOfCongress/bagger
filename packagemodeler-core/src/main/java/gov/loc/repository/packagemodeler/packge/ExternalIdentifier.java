package gov.loc.repository.packagemodeler.packge;

import javax.persistence.*;

@Embeddable
public class ExternalIdentifier {

	public enum IdentifierType {
		SERIAL_NUMBER
	}
	
	public ExternalIdentifier() {
		
	}
	
	public ExternalIdentifier(String identifierValue, String identifierType) {
		this.identifierValue = identifierValue;
		this.identifierType = Enum.valueOf(IdentifierType.class, identifierType);
	}
		
	public ExternalIdentifier(String identifierValue, IdentifierType identifierType) {
		this.identifierValue = identifierValue;
		this.identifierType = identifierType;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="identifier_type", nullable = true)	
	private IdentifierType identifierType;

	@Column(name="identifier_value", nullable = true)	
	private String identifierValue;

	public void setIdentifierType(IdentifierType identifierType) {
		this.identifierType = identifierType;
	}

	public IdentifierType getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}

	public String getIdentifierValue() {
		return identifierValue;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
		{
			return true;
		}
		if (! (obj instanceof ExternalIdentifier))
		{
			return false;
		}
		final ExternalIdentifier externalIdentifierObj = (ExternalIdentifier)obj;
		if (this.identifierValue.equals(externalIdentifierObj.getIdentifierValue()) && this.identifierType.equals(externalIdentifierObj.getIdentifierType()))
		{
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.identifierValue.hashCode() + this.identifierType.hashCode();
	}
}
