package gov.loc.repository.serviceBroker;

import gov.loc.repository.serviceBroker.ServiceBrokerFactory;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/servicerequestbroker-context.xml", "classpath:conf/services-test-context.xml"})
public abstract class AbstractServiceBrokerTest {
	protected static final String REQUESTER_1 = "requester1";
	protected static final String REQUESTER_2 = "requester2";
	protected static final String RESPONDER_1 = "responder1";
	protected static final String RESPONDER_2 = "responder2";
	protected static final String QUEUE_1 = "jobQueue";
	protected static final String QUEUE_2 = "specificJobQueue";
	protected static final String JOBTYPE_1 = "test_job1";
	protected static final String JOBTYPE_2 = "test_job2";
	protected static final String REGISTRATION_1 = "reg1";
	protected static final String REGISTRATION_2 =  "reg2";

	protected ServiceBrokerFactory serviveBrokerFactory = new ServiceBrokerFactory();

	protected HibernateTemplate template;
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.template = new HibernateTemplate(sessionFactory);	
	}
	
	@Before
	public void truncateDb()
	{
		this.template.execute(
				new HibernateCallback()
				{
					public Object doInHibernate(org.hibernate.Session session) throws org.hibernate.HibernateException ,java.sql.SQLException
					{
						session.createSQLQuery("delete from request_string_entries").executeUpdate();
						session.createSQLQuery("delete from request_boolean_entries").executeUpdate();
						session.createSQLQuery("delete from request_integer_entries").executeUpdate();
						session.createSQLQuery("delete from response_string_entries").executeUpdate();
						session.createSQLQuery("delete from response_boolean_entries").executeUpdate();
						session.createSQLQuery("delete from response_integer_entries").executeUpdate();						
						session.createSQLQuery("delete from service_request").executeUpdate();
						session.createSQLQuery("delete from service_container_registry").executeUpdate();
						return null;
					};
				}
		);
	}
	 
}
