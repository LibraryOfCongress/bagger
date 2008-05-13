package gov.loc.repository.serviceBroker.impl;

import gov.loc.repository.serviceBroker.ServiceRequest.StringEntry;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class StringEntryImpl implements StringEntry, Serializable	
{
	private static final long serialVersionUID = 1L;

	@Column(name = "key", length = 50, nullable = false)
	private String key;
	
	@Column(name = "value", nullable = true)
	private String value;
	
	public StringEntryImpl(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public StringEntryImpl() {
	
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
	
	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public Object getValueObject() {
		return this.value;
	}
}
