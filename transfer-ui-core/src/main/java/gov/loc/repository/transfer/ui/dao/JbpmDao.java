package gov.loc.repository.transfer.ui.dao;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;

import gov.loc.repository.transfer.ui.models.Base;
import org.jbpm.identity.hibernate.IdentitySession;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher 
 * @version $Rev$
 */
public abstract class JbpmDao<T, PK extends Serializable> implements IDao<T, PK>{
    protected JbpmContext jbpmContext;
    protected IdentitySession identitySession;

	protected static final Log log = LogFactory.getLog(JbpmDao.class);	
	
	public void setJbpmContext(JbpmContext jbpmContext) {
		this.jbpmContext = jbpmContext;
		this.identitySession = null;//resets the hibernate session
	}
	
	public JbpmContext getJbpmContext() {
		return this.jbpmContext;
	}
	
	public void setIdentitySession(IdentitySession identitySession) {
		this.identitySession = identitySession;
	}
	
	public IdentitySession getIdentitySession() {
	    if(this.identitySession == null){
	        this.identitySession = 
	            new IdentitySession(
	                this.getJbpmContext().getSession()
	            );
	    } return this.identitySession;
	}
	
	
}