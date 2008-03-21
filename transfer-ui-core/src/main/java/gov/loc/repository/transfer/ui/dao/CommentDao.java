package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.Comment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Repository
public class CommentDao extends JbpmDao<Comment, String>{
    
    protected static final Log log = LogFactory.getLog(CommentDao.class);	
	
    public CommentDao() { }
    
    public Comment find(String id){
        log.warn("Not Implemented");
        Comment comment = null;
        try{
            org.jbpm.graph.exe.Comment jbpmComment = null;
                //this.getJbpmContext().getGraphSession().findLatestComment(id);
            //comment = toComment(jbpmComment);
        }catch(Exception e){
            log.error("Error",e);
        }
        return comment;
    }
    
    public List<Comment> findAll(){
        return null;
    }
    
    public void save(Comment comment){
        log.warn("Not Implemented");
        return;//FIX -finish implementation
    }
    
    public Comment merge(Comment comment){
        log.warn("Not Implemented");
        return null;//FIX -finish implementation
    }
    
    public void remove(Comment comment){
        log.warn("Not Implemented");
        return;//FIX - finish implementing
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.Comment.class;
    };
    
    //NAMED QUERIES
    public List<Comment> findAllByProcessId(Map params){//param map is {processId:Long}
        List<Comment> comments = new ArrayList<Comment>();
        log.debug("Search for comments by process id "+ params.toString());
        Long processId = Long.parseLong(params.get("processId").toString());
        List<org.jbpm.graph.exe.Comment> jbpmComments = getJbpmContext().getProcessInstance(
            Long.parseLong(params.get("processId").toString())
        ).getRootToken().getComments();
        log.debug("Found " + (jbpmComments!=null?jbpmComments.size():0) + " comments");
        for(org.jbpm.graph.exe.Comment jbpmComment:jbpmComments){
            try{
                Comment comment = toComment(jbpmComment);
                comment.setProcessIdRef(processId);
                comments.add(comment);
            }catch(Throwable t){
                log.error("Error loading comments", t);
            }
        }return comments;  
    }
    
    //HELPERS
    private Comment toComment(org.jbpm.graph.exe.Comment jbpmComment){
        Comment comment = new Comment();
        comment.setUserName(jbpmComment.getActorId());
        comment.setDateCreated(jbpmComment.getTime());
        comment.setMessage(jbpmComment.getMessage());
        return comment;
    }
    
    private org.jbpm.graph.exe.Comment toJbpmComment(Comment comment){
        org.jbpm.graph.exe.Comment jbpmComment = 
            new org.jbpm.graph.exe.Comment();
        return jbpmComment;
    }
}
