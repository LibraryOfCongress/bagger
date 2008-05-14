package gov.loc.repository.service;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;

import gov.loc.repository.serviceBroker.RespondingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceContainerRegistry;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component("serviceContainer")
@ManagedResource(objectName="bean:name=serviceContainer")
public class ServiceContainer implements Runnable {
	
	public enum State {STARTING, STARTED, STOPPING, STOPPED, SHUTTINGDOWN, SHUTDOWN};
	
	private static final Log log = LogFactory.getLog(ServiceContainer.class);

	private ComponentFactory factory;
	private ThreadPoolTaskExecutor executor;
	private Long wait = 10000L;
	private State state = State.STOPPED;
	private RespondingServiceBroker broker;
	private String serviceUrl;
	private ServiceContainerRegistry registry;
	
	public ServiceContainer(ThreadPoolTaskExecutor executor, RespondingServiceBroker broker, ComponentFactory factory, String serviceUrl, ServiceContainerRegistry registry) {
		this.factory = factory;
		this.executor = executor;
		this.executor.setWaitForTasksToCompleteOnShutdown(true);
		this.broker = broker;
		this.serviceUrl = serviceUrl;
		this.registry = registry;
	}
	
	public void setWait(Long wait)
	{
		this.wait = wait;
	}
	
	@ManagedAttribute
	public String[] getJobTypes()
	{
		return this.broker.getJobTypes();
	}
	
	@ManagedAttribute
	public String[] getQueues()
	{
		return this.broker.getQueues();
	}
	
	@ManagedAttribute
	public String getResponder()
	{
		return this.broker.getResponder();
	}

	@ManagedAttribute
	public Integer getActiveServiceRequestCount()
	{
		return this.executor.getActiveCount();
	}
	
	@PostConstruct
	public void init() {		
		for(String jobType : this.broker.getJobTypes())
		{
			log.debug("Handles jobType: " + jobType);
			if (! this.factory.handlesJobType(jobType))
			{
				throw new UnsupportedOperationException("Factory cannot create component to handle jobType " + jobType);
			}
		}
		
		for(String queue : this.broker.getQueues())
		{
			log.debug("Handles queue: " + queue);
		}
		
		//Report any uncompleted tasks for this responder as errors
		this.broker.reportErrorsForAcknowledgedServiceRequestsWithoutResponses();
	}

	public void shutdown()
	{
		log.debug("Shutting down");		
		this.state = State.SHUTTINGDOWN;
	}
	
	public void run()
	{
		this.state = State.STARTED;
		log.debug("Starting");
		while(this.state != State.SHUTDOWN)
		{
			if (this.state == State.STARTED || this.state == State.STOPPED)
			{
				//Register
				this.registry.register(serviceUrl);				
			}
			if (this.state == State.STARTED)
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
			else if (this.state == State.STOPPING || this.state == State.SHUTTINGDOWN)
			{
				this.executor.shutdown();
				
				
				if (this.state == State.STOPPING)
				{
					log.debug("Stopped");
					this.state = State.STOPPED;
				}
				else
				{
					this.registry.unregister(serviceUrl);
					this.state = State.SHUTTINGDOWN;
				}
			}
		}
		log.debug("Shutdown");
	}
	
	@ManagedOperation
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
	
	@ManagedAttribute
	public String getStateString()
	{
		return this.state.toString();
	}
	
	@ManagedOperation
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
		
		public ServiceRequest getServiceRequest()
		{
			return this.req;
		}
		
		@Override
		public void run() {			
			boolean result = true;
			try
			{
				Object component = componentFactory.getComponent(req.getJobType());
				InvokeComponentHelper helper = new InvokeComponentHelper(component, req.getJobType(), req.getEntries());
				org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory(DatabaseRole.DATA_WRITER).getCurrentSession();
				try
				{					
					hibernateSession.beginTransaction();
					//Invoke and return taskResult
					log.debug("Invoking for Service Request " + req);
					System.out.println("Starting " + req);
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
				log.error("Error handling message", ex);
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
