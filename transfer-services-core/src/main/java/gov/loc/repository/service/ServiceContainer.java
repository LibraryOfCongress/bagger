package gov.loc.repository.service;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;

import gov.loc.repository.exceptions.ConfigurationException;
import gov.loc.repository.serviceBroker.RespondingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component("serviceContainer")
public class ServiceContainer implements Runnable {
	
	public enum State {STARTING, STARTED, STOPPING, STOPPED};
	
	private static final Log log = LogFactory.getLog(ServiceContainer.class);

	private ComponentFactory factory;
	private String[] queues;
	private String[] jobTypes;
	private ThreadPoolTaskExecutor executor;
	private Long wait = 5000L;
	private State state = State.STOPPED;
	private RespondingServiceBroker broker;
	private String responder;
	
	public ServiceContainer(ThreadPoolTaskExecutor executor, RespondingServiceBroker broker, ComponentFactory factory, String responder, String[] queues, String[] jobTypes) {
		this.factory = factory;
		this.executor = executor;
		this.executor.setWaitForTasksToCompleteOnShutdown(true);
		this.broker = broker;
		this.responder = responder;
		this.queues = queues;
		this.jobTypes = jobTypes;
	}
	
	public void setWait(Long wait)
	{
		this.wait = wait;
	}
	
	public String[] getJobTypes()
	{
		return this.jobTypes;
	}
	
	
	public String[] getQueues()
	{
		return this.queues;
	}
		
	public String getResponder()
	{
		return this.responder;
	}
	
	@PostConstruct
	public void init() {
		if (this.responder == null || this.responder.length() == 0)
		{
			throw new ConfigurationException("Responder not provided");
		}
		this.broker.setResponder(responder);
		
		//Make sure that factory can create all of the requested jobTypes		
		if (jobTypes == null || jobTypes.length == 0)
		{
			throw new ConfigurationException("JobTypes not provided");
		}
		
		for(String jobType : this.jobTypes)
		{
			log.debug("Handles jobType: " + jobType);
			if (! this.factory.handlesJobType(jobType))
			{
				throw new UnsupportedOperationException("Factory cannot create component to handle jobType " + jobType);
			}
		}
		this.broker.setJobTypes(this.jobTypes);
		
		if (queues == null || queues.length == 0)
		{
			throw new ConfigurationException("Queues not provided");
		}
		for(String queue : this.queues)
		{
			log.debug("Handles queue: " + queue);
		}
		this.broker.setQueues(queues);		
		
		//Report any uncompleted tasks for this responder as errors
		this.broker.reportErrorsForAcknowledgedServiceRequestsWithoutResponses();
	}

	public void run()
	{
		this.state = State.STARTED;
		log.debug("Starting");
		while(this.state == State.STARTED)
		{
			try
			{
				ServiceRequest req = this.getNextServiceRequest();
				while(req != null)
				{
					this.executor.execute(new ServiceRunnable(req, this.broker, this.factory));
					req = this.getNextServiceRequest();
				}
				try
				{
					log.debug("Sleeping");
					Thread.sleep(this.wait);
					log.debug("Done sleeping");
				}
				catch(InterruptedException ex)
				{
					log.warn("Thread sleeping interrupted", ex);
				}
			}
			catch(Exception ex)
			{
				log.error(ex);
				this.stop();
			}
		}
		this.executor.shutdown();
		log.debug("Stopped");
		this.state = State.STOPPED;
		
	}
	
	public void start()
	{
		if (this.state == State.STOPPED)
		{
			this.state = State.STARTING;
			(new Thread(this)).start();
		}
		else
		{
			log.warn("Could not start because state is " + this.state);
		}
	}

	private ServiceRequest getNextServiceRequest()
	{
		if (! this.threadsAreAvailable())
		{
			log.debug("No threads are available");
			return null;
		}
		return this.broker.findAndAcknowledgeNextServiceRequest();
	}
	
	private boolean threadsAreAvailable()
	{
		return this.executor.getActiveCount() < this.executor.getMaxPoolSize();
	}
	
	public State getState()
	{
		return this.state;
	}
	
	public void stop()
	{
		log.debug("Stopping");		
		this.state = State.STOPPING;
	}
	
	public class ServiceRunnable implements Runnable
	{
		private ServiceRequest req;
		private RespondingServiceBroker broker;
		private ComponentFactory componentFactory;
		
		public ServiceRunnable(ServiceRequest req, RespondingServiceBroker broker, ComponentFactory factory) {
			this.req = req;
			this.broker = broker;
			this.componentFactory = factory;
		}
		
		@Override
		public void run() {
			Exception error = null;
			boolean result = true;
			try
			{
				Object component = componentFactory.getComponent(req.getJobType());
				InvokeComponentHelper helper = new InvokeComponentHelper(component, req.getJobType(), req.getVariableMap());
				org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory(DatabaseRole.DATA_WRITER).getCurrentSession();
				try
				{					
					hibernateSession.beginTransaction();
					//Invoke and return taskResult
					log.debug("Invoking for Service Request " + req);
					result = helper.invoke();
					hibernateSession.getTransaction().commit();
				}
				catch(Exception ex)
				{
					if (hibernateSession != null && hibernateSession.isOpen())
					{
						hibernateSession.getTransaction().rollback();
					}
					throw ex;
				}
				finally
				{
					if (hibernateSession != null && hibernateSession.isOpen())
					{
						hibernateSession.close();
					}
				}
				
			}
			catch(Exception ex)
			{
				log.error("Error handling message", error);
				req.respondFailure(ex);
				broker.sendResponse(req);
				return;
			}
			
			log.debug( MessageFormat.format("Responding {0} for Service Request {1}", result, req));
			req.respondSuccess(result);
			broker.sendResponse(req);
								
		}
	}
	
	
}
