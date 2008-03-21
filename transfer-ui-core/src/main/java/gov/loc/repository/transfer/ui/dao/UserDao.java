package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.User;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Repository
public class UserDao extends JbpmDao<User, Long>{  
    
	protected static final Log log = LogFactory.getLog(UserDao.class);	
	
    public UserDao() { }
    
    public User find(Long id){
        log.debug("Looking for user with id "+ id);
        User user = null;
        try{
            org.jbpm.identity.User jbpmUser = getIdentitySession().loadUser(id);
            user = toUser(jbpmUser);
        }catch(Exception e){
            log.error(e.getMessage());
        } return user;
    }
    
    public List<User> findAll(){
        List<User> users = new ArrayList<User>();
        List<org.jbpm.identity.User> jbpmUsers = getIdentitySession().getUsers();
        for(org.jbpm.identity.User jbpmUser:jbpmUsers){
            users.add( toUser(jbpmUser) );
        }
        return users;
    }
    
    public void save(User user){
        log.debug("Saving user");
        getIdentitySession().saveUser(
            toJbpmUser(user)
        );
        return;
    }
    
    public User merge(User user){
        log.debug("Saving user");
        getIdentitySession().saveUser( toJbpmUser(user) );
        return null;//FIX -finish implementation
    }
    
    public void remove(User user){
        log.debug("Removing user "+ user.getId());
        return;//FIX - finish implementing
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.User.class;
    };
    
    //NAMED QUERIES
    //@Transactional(readOnly=true)
    public User findByUserName(Map params){//param map is {name:"name", }
        User user = null;
        this.log.debug("Search for users by username and user type "+ params.toString());
        org.jbpm.identity.User jbpmUser = 
            getIdentitySession().getUserByName(
                params.get("name").toString()
            );
        user = toUser(jbpmUser);
        return user;
    }
    
    //HELPERS
    //@Transactional(readOnly=true)
    private User toUser(org.jbpm.identity.User jbpmUser){
        User user = null;
        if(jbpmUser != null){ 
            user = new User();
            user.setId(jbpmUser.getId());
            user.setName(jbpmUser.getName());
            user.setGroupNames(
                getIdentitySession().getGroupNamesByUserAndGroupType(
                    user.getName(), 
                    "organisation"
                )
            );
        }
        return user;
    }
    
    private org.jbpm.identity.User toJbpmUser(User user){
        org.jbpm.identity.User jbpmUser = 
            new org.jbpm.identity.User(
                user.getName()
            );
        return jbpmUser;
    }
}
