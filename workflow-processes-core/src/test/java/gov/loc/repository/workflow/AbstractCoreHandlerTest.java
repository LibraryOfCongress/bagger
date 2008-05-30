package gov.loc.repository.workflow;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

@ContextConfiguration(locations={"classpath:conf/packagemodeler-core-context.xml","classpath:conf/packagemodeler-core-test-context.xml"})
public abstract class AbstractCoreHandlerTest extends AbstractHandlerTest {

	public HibernateTemplate template;
	
	public SessionFactory sessionFactory;
		
	@Resource(name="packageModelerSessionFactory")
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
		this.template = new HibernateTemplate(sessionFactory);
	}
	
	@Resource(name="packageModelerTransactionManager")
	public PlatformTransactionManager txManager;

	@Resource(name="modelerFactory")
	public ModelerFactory modelerFactory;
	
	@Resource(name="packageModelDao")
	public PackageModelDAO dao;
	
	@Override
	public final void createCommonFixtures() throws Exception {
		this.fixtureHelper.createSystem(this.getConfiguration().getString("agent.workflow.id"));
	}
				
}
