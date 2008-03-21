package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.Token;

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
public class TokenDao extends JbpmDao<Token, Long>{
    
	protected static final Log log = LogFactory.getLog(TokenDao.class);	
	
    public TokenDao() { }
    
    public Token find(Long id){
        this.log.debug("Looking for token with id "+ id);
        Token token = null;
        try{
            org.jbpm.graph.exe.Token jbpmToken =
                this.getJbpmContext().getToken(id);
            token = this.toToken(jbpmToken);
        }catch(Exception e){
            this.log.error(e.getMessage());
        } return token;
    }
    
    public List<Token> findAll(){
        this.log.warn("This is a potentially heavy operation, please consider using a named query.");
        return null;
    }
    
    public void save(Token token){
        this.jbpmContext.save(toJbpmToken(token));
        return;
    }
    
    public Token merge(Token token){
        this.jbpmContext.save(toJbpmToken(token));
        return null;
    }
    
    public void remove(Token token){
        this.log.warn("This Dao cannot remove objects");
        return;
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.Token.class;
    };
    
    //NAMED QUERIES
    public List<Token> findAllByProcessId(Map params){//param map is {name:"name", tokeneType:"organisation"}
        List<Token> tokens = new ArrayList<Token>();
        this.log.debug("Search for tokens by username and token type "+ params.toString());
        List jpbmTokens = 
            this.getJbpmContext().getProcessInstance(
                Long.parseLong(params.get("processId").toString())
            ).findAllTokens();
        for(Object jbpmToken: jpbmTokens){
            tokens.add( toToken((org.jbpm.graph.exe.Token)jbpmToken) );
        }
        return tokens;
    }
    
    //HELPERS
    private Token toToken(org.jbpm.graph.exe.Token jbpmToken){
        Token token = new Token();
        token.setId(jbpmToken.getId());
        return token;
    }
    
    private org.jbpm.graph.exe.Token toJbpmToken(Token token){
        org.jbpm.graph.exe.Token jbpmToken = new org.jbpm.graph.exe.Token();
        //jbpmToken.setId(token.getId());//TODO FIX
        return jbpmToken;
    }
    
}