package gov.loc.repository.packagemodeler.drivers;


import java.util.Collection;

import gov.loc.repository.drivers.AbstractCommandLineDriver.MapDriver;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Organization;
import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.agents.Software;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.utilities.EnhancedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("mapFixtureDriver")
public class MapFixtureDriver implements MapDriver{

    //Arg types
    public static final String TYPE_ID = "id";
    public static final String TYPE_NAME = "name";
    public static final String TYPE_ID_LIST = "list of ids";
        
    //Actions
    public static final String ACTION_CREATE_REPOSITORY = "createrepository";
    public static final String ACTION_LIST_REPOSITORIES = "listrepositories";
    public static final String ACTION_CREATE_ROLE = "createrole";
    public static final String ACTION_LIST_ROLES = "listroles";
    public static final String ACTION_CREATE_PERSON = "createperson";
    public static final String ACTION_LIST_PERSONS = "listpersons";
    public static final String ACTION_CREATE_ORGANIZATION = "createorganization";
    public static final String ACTION_LIST_ORGANIZATIONS = "listorganizations";
    public static final String ACTION_CREATE_SYSTEM = "createsystem";
    public static final String ACTION_LIST_SYSTEMS = "listsystems";
    public static final String ACTION_CREATE_SOFTWARE = "createsoftware";
    public static final String ACTION_LIST_SOFTWARE = "listsoftware";
    public static final String ACTION_TEST = "test";
    
    //Options
    public static final String OPT_FIRSTNAME = "firstname";
    public static final String OPT_FIRSTNAME_DESCRIPTION = "The first name of the person.";
    public static final String OPT_FIRSTNAME_TYPE = TYPE_NAME;
    
    public static final String OPT_HOST = "host";
    public static final String OPT_HOST_DESCRIPTION = "The hostname of the system.";
    public static final String OPT_HOST_TYPE = TYPE_NAME;
    
    
    public static final String OPT_ID = "id";
    public static final String OPT_ID_DESCRIPTION = "The identifier of the fixture.";
    public static final String OPT_ID_TYPE = TYPE_ID;

    public static final String OPT_NAME = "name";
    public static final String OPT_NAME_DESCRIPTION = "The name of the agent.";
    public static final String OPT_NAME_TYPE = TYPE_NAME;    
    
    public static final String OPT_ROLES = "roles";
    public static final String OPT_ROLES_DESCRIPTION = "List of roles for the agent.  Default is none.";
    public static final String OPT_ROLES_TYPE = TYPE_ID_LIST;
        
    public static final String OPT_SURNAME = "surname";
    public static final String OPT_SURNAME_DESCRIPTION = "The surname of the person.";
    public static final String OPT_SURNAME_TYPE = TYPE_NAME;
        
    private PackageModelDAO dao;
    private ModelerFactory factory;

    private EnhancedHashMap<String,String> options;
    
    @Autowired
    public MapFixtureDriver(@Qualifier("modelerFactory")ModelerFactory factory, @Qualifier("packageModelDao")PackageModelDAO dao) {
		this.dao = dao;
		this.factory = factory;
	}
        
    public void execute(String action, EnhancedHashMap<String,String> options) throws Exception
    {
        this.options = options;
        
        if (ACTION_TEST.equalsIgnoreCase(action))
        {
            dao.findRepository("foo");
            java.lang.System.out.println("Database connection is good.");
        }
        else if (ACTION_CREATE_REPOSITORY.equalsIgnoreCase(action))
        {
            Repository repository = factory.createRepository(options.getRequired(OPT_ID));
            dao.save(repository);
        }
        else if (ACTION_LIST_REPOSITORIES.equalsIgnoreCase(action))
        {
        	this.printCollection(dao.findRepositories());
        }
        else if (ACTION_CREATE_ROLE.equalsIgnoreCase(action))
        {
            Role role = factory.createRole(options.getRequired(OPT_ID));
            dao.save(role);
        }
        else if (ACTION_LIST_ROLES.equalsIgnoreCase(action))
        {
        	this.printCollection(dao.findRoles());
        }
        else if (ACTION_CREATE_PERSON.equalsIgnoreCase(action))
        {
            Person agent = factory.createAgent(Person.class, options.getRequired(OPT_ID));
            dao.save(agent);
            agent.setFirstName(options.getRequired(OPT_FIRSTNAME));
            agent.setSurname(options.getRequired(OPT_SURNAME));
            addRoles(agent);
        }
        else if (ACTION_LIST_PERSONS.equalsIgnoreCase(action))
        {
        	this.printCollection(dao.findAgents(Person.class));
        }
        else if (ACTION_CREATE_ORGANIZATION.equalsIgnoreCase(action))
        {
            Organization agent = factory.createAgent(Organization.class, options.getRequired(OPT_ID));
            dao.save(agent);
            agent.setName(options.getRequired(OPT_NAME));
            addRoles(agent);
        }
        else if (ACTION_LIST_ORGANIZATIONS.equalsIgnoreCase(action))
        {
        	this.printCollection(dao.findAgents(Organization.class));
        }
        else if (ACTION_CREATE_SOFTWARE.equalsIgnoreCase(action))
        {
            Software agent = factory.createAgent(Software.class, options.getRequired(OPT_ID));
            dao.save(agent);
            addRoles(agent);
        }
        else if (ACTION_LIST_SOFTWARE.equalsIgnoreCase(action))
        {
        	this.printCollection(dao.findAgents(Software.class));
        }        
        else if (ACTION_CREATE_SYSTEM.equalsIgnoreCase(action))
        {
            System agent = factory.createAgent(System.class, options.getRequired(OPT_ID));
            agent.setHost(options.get(OPT_HOST, null));
            dao.save(agent);
            addRoles(agent);
        }
        else if (ACTION_LIST_SYSTEMS.equalsIgnoreCase(action))
        {
        	this.printCollection(dao.findAgents(System.class));
        }        
        else
        {
            throw new Exception(action + " is an unrecognized action");
        }        
    }
    
    private void addRoles(Agent agent) throws Exception
    {
        if (! options.containsKey(OPT_ROLES))
        {
            return;
        }
        String[] roleIdArray = options.getRequired(OPT_ROLES).split(",");
        for(String roleId : roleIdArray)
        {
            agent.addRole(dao.findRequiredRole(roleId));
        }
    }
    
    private void printCollection(Collection<?> collection)
    {
    	for(Object obj : collection)
    	{
    		java.lang.System.out.println(obj.toString());
    	}
    }
}
