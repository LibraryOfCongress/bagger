package gov.loc.repository.transfer.components;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.TestFixtureHelper;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

import javax.annotation.Resource;


import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/components-core-context.xml", "classpath:conf/packagemodeler-core-test-context.xml"})
public abstract class AbstractPackageModelerAwareComponentTest extends AbstractComponentTest {

	public HibernateTemplate template;
	
	public SessionFactory sessionFactory;
	
	@Autowired
	public TestFixtureHelper fixtureHelper;
	
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
	
}
