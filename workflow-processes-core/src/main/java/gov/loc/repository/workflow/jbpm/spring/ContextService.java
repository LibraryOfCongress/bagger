package gov.loc.repository.workflow.jbpm.spring;

import org.jbpm.svc.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContextService implements Service {

	private static final long serialVersionUID = 1L;

	private ApplicationContext context = new ClassPathXmlApplicationContext("classpath:conf/servicerequestbroker-context.xml");
	
	public ApplicationContext getContext()
	{
		return this.context;
	}
	
	@Override
	public void close() {
		context = null;
	}

}
