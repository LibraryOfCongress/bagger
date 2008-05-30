package gov.loc.repository.packagemodeler;

import gov.loc.repository.Ided;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Organization;
import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.Software;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.packge.Repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class TestFixtureHelper {
	private SessionFactory sessionFactory;
	
	public TestFixtureHelper(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}
	
	public TestFixtureHelper() {
	}
	
	protected Session getSession()
	{
		return sessionFactory.getCurrentSession();
	}
	
	private <T extends Agent> T createAgent(Class<T> clazz, String id, Role[] roles) throws Exception
	{
		Agent agent = this.create(clazz, id);
		if (roles != null)
		{
			for(Role role : roles)
			{
				agent.addRole(role);
			}
		}
		return clazz.cast(agent);
	}
	
	private <T extends Ided> T create(Class<T> clazz, String id) throws Exception
	{
		String implClassName = getImplClassName(clazz);
		Ided obj = Ided.class.cast(Class.forName(implClassName).newInstance());
		obj.setId(id);
		this.getSession().save(obj);
		return clazz.cast(obj);
	}
	
	private static String getImplClassName(Class<?> clazz)
	{
		return clazz.getName().substring(0, (clazz.getName().length()-clazz.getSimpleName().length())) + "impl." + clazz.getSimpleName() + "Impl";		
	}	
	
	public Repository createRepository(String repositoryId) throws Exception
	{
		return this.create(Repository.class, repositoryId);
	}
	
	public Role createRole(String roleId) throws Exception
	{
		return this.create(Role.class, roleId);
	}
			
	public System createSystem(String systemId) throws Exception
	{
		return this.create(System.class, systemId);
	}

	public System createSystem(String systemId, Role[] roles) throws Exception
	{
		return this.createAgent(System.class, systemId, roles);
	}	
	
	public Software createSoftware(String softwareId) throws Exception
	{
		return this.create(Software.class, softwareId);
	}
			
	public Organization createOrganization(String organizationId, String name, Role[] roles) throws Exception
	{
		Organization organization = this.createAgent(Organization.class, organizationId, roles);
		organization.setName(name);
		return organization;
	}	
	
	public Person createPerson(String personId, String firstName, String surname) throws Exception
	{
		Person person = this.createAgent(Person.class, personId, null);
		person.setFirstName(firstName);
		person.setSurname(surname);
		return person;
	}			
}
