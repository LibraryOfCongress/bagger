package gov.loc.repository.serviceBroker.impl;

import gov.loc.repository.serviceBroker.ServiceRequest.BooleanEntry;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BooleanEntryImpl implements BooleanEntry, Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Column(name = "key", length = 50, nullable = false)
	private String key;
	
	@Column(name = "value", nullable = true)
	private Boolean value;
	
	public BooleanEntryImpl(String key, Boolean value) {
		this.key = key;
		this.value = value;
	}

	public BooleanEntryImpl() {
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
	
	@Override
	public Boolean getValue() {
		return this.value;
	}

	@Override
	public Object getValueObject() {
		return this.value;
	}
	
}
