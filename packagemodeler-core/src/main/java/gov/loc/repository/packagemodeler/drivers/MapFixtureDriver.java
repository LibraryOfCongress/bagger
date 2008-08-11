package gov.loc.repository.packagemodeler.drivers;


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
public class MapFixtureDriver {

    //Arg types
    public static final String TYPE_ID = "id";
    public static final String TYPE_NAME = "name";
    public static final String TYPE_ID_LIST = "list of ids";
        
    //Actions
    public static final String ACTION_REPOSITORY = "createrepository";
    public static final String ACTION_ROLE = "createrole";
    public static final String ACTION_PERSON = "createperson";
    public static final String ACTION_ORGANIZATION = "createorganization";
    public static final String ACTION_SYSTEM = "createsystem";
    public static final String ACTION_SOFTWARE = "createsoftware";
    public static final String ACTION_TEST = "test";
    
    //Options
    public static final String OPT_FIRSTNAME = "firstname";
    public static final String OPT_FIRSTNAME_DESCRIPTION = "The first name of the person.";
    public static final String OPT_FIRSTNAME_TYPE = TYPE_NAME;
    
    public static final String OPT_HELP = "help";
    public static final String OPT_HELP_DESCRIPTION = "Print this message";

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
    
    public MapFixtureDriver() {
	}
    
    public void execute(String action, EnhancedHashMap<String,String> options) throws Exception
    {
        this.options = options;
        
        if (ACTION_TEST.equalsIgnoreCase(action))
        {
            dao.findRepository("foo");
            java.lang.System.out.println("Database connection is good.");
        }
        else if (ACTION_REPOSITORY.equalsIgnoreCase(action))
        {
            Repository repository = factory.createRepository(options.getRequired(OPT_ID));
            dao.save(repository);
        }
        else if (ACTION_ROLE.equalsIgnoreCase(action))
        {
            Role role = factory.createRole(options.getRequired(OPT_ID));
            dao.save(role);
        }
        else if (ACTION_PERSON.equalsIgnoreCase(action))
        {
            Person agent = factory.createAgent(Person.class, options.getRequired(OPT_ID));
            dao.save(agent);
            agent.setFirstName(options.getRequired(OPT_FIRSTNAME));
            agent.setSurname(options.getRequired(OPT_SURNAME));
            addRoles(agent);
        }
        else if (ACTION_ORGANIZATION.equalsIgnoreCase(action))
        {
            Organization agent = factory.createAgent(Organization.class, options.getRequired(OPT_ID));
            dao.save(agent);
            agent.setName(options.getRequired(OPT_NAME));
            addRoles(agent);
        }
        else if (ACTION_SOFTWARE.equalsIgnoreCase(action))
        {
            Software agent = factory.createAgent(Software.class, options.getRequired(OPT_ID));
            dao.save(agent);
            addRoles(agent);
        }            
        else if (ACTION_SYSTEM.equalsIgnoreCase(action))
        {
            System agent = factory.createAgent(System.class, options.getRequired(OPT_ID));
            agent.setHost(options.get(OPT_HOST, null));
            dao.save(agent);
            addRoles(agent);
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
}
