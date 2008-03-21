package gov.loc.repository.transfer.ui.dao;


import gov.loc.repository.transfer.ui.models.Group;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class GroupDao extends JbpmDao<Group, Long>{  
    
	protected static final Log log = LogFactory.getLog(GroupDao.class);	
	
    public GroupDao() { }
    
    public Group find(Long id){
        this.log.debug("Looking for user with id "+ id);
        Group group = null;
        try{
            org.jbpm.identity.Group jbpmGroup = this.getIdentitySession().loadGroup(id);
            group = this.toGroup(jbpmGroup);
        }catch(Exception e){
            this.log.error(e.getMessage());
        }
        return group;
    }
    
    public List<Group> findAll(){
        return null;
    }
    
    public void save(Group group){
        this.log.debug("Saving group");
        this.getIdentitySession().saveGroup(
            this.toJbpmGroup(group)
        );
        return;
    }
    
    public Group merge(Group group){
        this.log.debug("Saving group");
        this.getIdentitySession().saveGroup(
            this.toJbpmGroup(group)
        );
        return null;//FIX -finish implementation
    }
    
    public void remove(Group group){
        this.log.debug("Removing group "+ group.getId());
        return;//FIX - finish implementing
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.Group.class;
    };
    
    //NAMED QUERIES
    public Group findByUserNameAndGroupType(Map params){//param map is {name:"name", groupeType:"organisation"}
        this.log.debug("Search for groups by username and group type "+ params.toString());
        List groups = this.getIdentitySession().getGroupNamesByUserAndGroupType(
            params.get("name").toString(),
            params.get("groupType").toString()
        );
        return null;//FIX - finish implementation
    }
    
    //HELPERS
    private Group toGroup(org.jbpm.identity.Group jbpmGroup){
        Group group = new Group();
        group.setId(jbpmGroup.getId());
        group.setName(jbpmGroup.getName());
        return group;
    }
    
    private org.jbpm.identity.Group toJbpmGroup(Group group){
        org.jbpm.identity.Group jbpmGroup = 
            new org.jbpm.identity.Group(
                group.getName()
            );
        return jbpmGroup;
    }
}
