package gov.loc.repository.workflow;

import org.jbpm.graph.def.ActionHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

public abstract class AbstractPackageModelerAwareHandler extends AbstractHandler implements ActionHandler
{
	public PackageModelDAO dao;
	public ModelerFactory factory;
	public Agent workflowAgent;
	
	public AbstractPackageModelerAwareHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}

	@Override
	public final void commonInitialize() throws Exception {
		txManager = (PlatformTransactionManager)this.springContext.getBean("packageModelerTransactionManager");
		
		factory = this.createObject(ModelerFactory.class);
		dao = this.createObject(PackageModelDAO.class);
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		this.workflowAgent = dao.findRequiredAgent(Agent.class, this.getWorkflowAgentId());
		txManager.commit(status);
	}
				
}
