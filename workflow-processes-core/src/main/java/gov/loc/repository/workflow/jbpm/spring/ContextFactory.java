package gov.loc.repository.workflow.jbpm.spring;

import org.jbpm.svc.Service;
import org.jbpm.svc.ServiceFactory;

public class ContextFactory implements ServiceFactory {

	private static final long serialVersionUID = 1L;

	@Override
	public void close() {
		//Do nothing
	}

	@Override
	public Service openService() {
		return new ContextService();
	}

}
