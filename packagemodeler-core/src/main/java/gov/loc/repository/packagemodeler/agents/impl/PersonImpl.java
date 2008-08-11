package gov.loc.repository.packagemodeler.agents.impl;

import java.text.MessageFormat;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.agents.Person;

@Entity(name="Person")
@DiscriminatorValue("person")
public class PersonImpl extends AgentImpl implements Person {

	@Column(name = "first_name", nullable = true)
	private String firstName;		

	@Column(name = "surname", nullable = true)
	private String surname;		
		
	public String getFirstName() {
		return this.firstName;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		String name = "";
		if (this.firstName != null)
		{
			name += firstName;
		}
		if (this.firstName != null && this.surname != null)
		{
			name += " ";
		}
		if (this.surname != null)
		{
			name += this.surname;
		}
		return name;
	}

	@Override
	public String toString() {
		String name = this.getName();
		if (name == null || name.length() == 0)
		{
			return MessageFormat.format("yet to be named Person with id {0}", this.getId());
		}
		return MessageFormat.format("Person named {0} with id {1}", name, this.getId());
	}
	
}
