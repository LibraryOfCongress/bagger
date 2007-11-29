package gov.loc.repository.utilities.persistence;

import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.Ided;
import gov.loc.repository.Keyed;
import gov.loc.repository.constants.Roles;
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
	private Session session = null;
	
	private Map<String, Ided> idedMap = new HashMap<String, Ided>();
	
	public TestFixtureHelper()
	{
		sessionFactory = HibernateUtil.getSessionFactory();
	}
	
	protected Session getSession()
	{
		if (session != null)
		{
			return session;
		}
		return sessionFactory.getCurrentSession();
	}
	
	public void setSession(Session session)
	{
		this.session = session;
	}

	protected <T extends Agent> T createAgent(Class<T> clazz, String id, String[] roles) throws Exception
	{
		Agent agent = this.create(clazz, id);
		if (roles != null)
		{
			for(String role : roles)
			{
				agent.addRole(this.get(Role.class, role));
			}
		}
		return clazz.cast(agent);
	}
	
	protected <T extends Ided> T create(Class<T> clazz, String id) throws Exception
	{
		String implClassName = getImplClassName(clazz);
		Ided obj = Ided.class.cast(Class.forName(implClassName).newInstance());
		obj.setId(id);
		this.getSession().save(obj);
		this.idedMap.put(this.getKey(clazz, id), obj);
		return clazz.cast(obj);
	}
	
	private static String getImplClassName(Class clazz)
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
	
	public System createStorageSystem(String storageSystemId) throws Exception
	{
		return this.createAgent(System.class, storageSystemId, new String[] {Roles.STORAGE_SYSTEM}); 
	}
		
	public System createSystem(String systemId) throws Exception
	{
		return this.create(System.class, systemId);
	}

	public Software createSoftware(String softwareId) throws Exception
	{
		return this.create(Software.class, softwareId);
	}
	
	protected String getKey(Class clazz, String id)
	{
		return clazz.getName() + "_" + id;
	}
	
	protected <T extends Ided> T get(Class<T> clazz, String id) throws Exception
	{
		String key = this.getKey(clazz, id);
		if (! this.idedMap.containsKey(key))
		{
			this.idedMap.put(key, this.create(clazz, id));
		}
		Ided obj = this.idedMap.get(key);
		if (! this.getSession().contains(obj))
		{
			this.getSession().load(obj, obj.getKey());
		}
		return clazz.cast(obj);
		
	}
		
	public Organization createOrganization(String organizationId, String name, String[] roles) throws Exception
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
			
	public void reload(Keyed obj)
	{
		if (this.getSession().contains(obj))
		{
			this.getSession().refresh(obj);
		}
		this.getSession().load(obj, obj.getKey());
	}
}
