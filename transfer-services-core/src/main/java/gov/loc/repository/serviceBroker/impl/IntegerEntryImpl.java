package gov.loc.repository.serviceBroker.impl;

import gov.loc.repository.serviceBroker.ServiceRequest.IntegerEntry;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class IntegerEntryImpl implements IntegerEntry, Serializable
{
	private static final long serialVersionUID = 1L;
	
	@Column(name = "key", length = 50, nullable = false)
	private String key;
	
	@Column(name = "value", nullable = true)
	private Long value;
	
	public IntegerEntryImpl(String key, Long value) {
		this.key = key;
		this.value = value;
	}
	
	public IntegerEntryImpl() {
	}
	
	@Override
	public String getKey() {
		return this.key;
	}
	
	@Override
	public Long getValue() {
		return this.value;
	}
	
	@Override
	public Object getValueObject() {
		return this.value;
	}
}
