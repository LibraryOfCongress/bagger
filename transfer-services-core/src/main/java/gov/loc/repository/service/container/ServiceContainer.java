package gov.loc.repository.service.container;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import gov.loc.repository.service.component.ComponentFactory;
import gov.loc.repository.service.component.ComponentInvoker;
import gov.loc.repository.serviceBroker.RespondingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.utilities.ProcessBuilderWrapper;
import gov.loc.repository.utilities.ProcessBuilderWrapper.ProcessBuilderResult;
import gov.loc.repository.utilities.impl.ProcessBuilderWrapperImpl;

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
	private ServiceContainerHeartbeat heartbeat;
	private List<String> delegateJobTypeList = new ArrayList<String>();
	
	public ServiceContainer(ThreadPoolTaskExecutor executor, RespondingServiceBroker broker, ComponentFactory factory, ServiceContainerHeartbeat registry, String[] queues, String[] jobTypes, String[] delegateJobTypes) {
		this.factory = factory;
		this.executor = executor;
		this.executor.setWaitForTasksToCompleteOnShutdown(true);
		this.broker = broker;
		this.heartbeat = registry;
		
		List<String> mergedJobTypeList = new ArrayList<String>();
		for(String jobType : jobTypes)
		{
			mergedJobTypeList.add(jobType);
		}
		for(String jobType : delegateJobTypes)
		{
			if (! mergedJobTypeList.contains(jobType))
			{
				mergedJobTypeList.add(jobType);
			}
			delegateJobTypeList.add(jobType);
		}
		this.broker.setJobTypes(mergedJobTypeList.toArray(new String[] {}));
		this.broker.setQueues(queues);
				
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
	
	@ManagedAttribute
	public Long getMaxMemory()
	{
		return Runtime.getRuntime().maxMemory()/1024;		
	}

	@ManagedAttribute
	public Long getTotalMemory()
	{
		return Runtime.getRuntime().totalMemory()/1024;		
	}
	
	@ManagedAttribute
	public Long getFreeMemory()
	{
		return Runtime.getRuntime().freeMemory()/1024;		
	}
	
	
	@PostConstruct
	public void init() {		
		for(String jobType : this.broker.getJobTypes())
		{
			log.debug("Checking if component factory handles jobType: " + jobType);
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
		this.run();
	}
	
	public void run()
	{
		if (this.state == State.STARTING)
		{
			this.state = State.STARTED;
			log.debug("Starting");
			this.heartbeat.start();
		}
		while(this.state != State.SHUTDOWN)
		{
			if (this.state == State.STARTED)
			{
				try
				{				
					ServiceRequest req = this.getNextServiceRequest();
					while(req != null)
					{					
						if (! this.delegateJobTypeList.contains(req.getJobType()))
						{
							log.debug("Executing request with ServiceRunnable");
							this.executor.execute(new ServiceRunnable(req, this.broker, this.factory));	
						}
						else
						{
							log.debug("Executing request with DelegationRunnable");
							this.executor.execute(new DelegationRunnable(req, this.broker));
						}
											
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
					this.state = State.SHUTDOWN;
				}
			}
		}
		this.heartbeat.stop();		
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
			Object component = null;
			try {
				component = componentFactory.getComponent(req.getJobType());
			} catch (Exception ex) {
				req.respondFailure(ex);
			}
			if (component != null)
			{
				ComponentInvoker helper = new ComponentInvoker();
				//Invoke and return taskResult
				log.info("Received request: " + req);
				System.out.println("Starting " + req);
				helper.invoke(component, req);
			}
			
			log.info("Responding to request: " + req);
			System.out.println("Responding " + req);
			broker.sendResponse(req);
								
		}
				
	}
	
	public class DelegationRunnable implements Runnable
	{
		private ServiceRequest req;
		private RespondingServiceBroker broker;
		
		public DelegationRunnable(ServiceRequest req, RespondingServiceBroker broker) {
			this.req = req;
			this.broker = broker;
		}
		
		public ServiceRequest getServiceRequest()
		{
			return this.req;
		}
		
		@Override
		public void run() {
			ProcessBuilderWrapper pb = new ProcessBuilderWrapperImpl();
			Map<String, String> env = new HashMap<String, String>();
			env.put("JAVA_OPTS", "-Dlog_suffix=-" + System.currentTimeMillis());
			ProcessBuilderResult result = pb.execute(new File("."), "./delegationdriver " + this.req.getKey(), env);
			if (result.getExitValue() != 0)
			{
				log.error(MessageFormat.format("Call to delegationdriver for ServiceRequest {0} returned {1}.  Output is {2}", this.req.getKey(), result.getExitValue(), result.getOutput()));
				try
				{
					req = broker.findRequiredServiceRequest(req.getKey());
					if (req.getResponseDate() == null)
					{
						req.respondFailure("Call to delegationdriver returned " + result.getExitValue(), result.getOutput());
						log.info("Responding to request: " + req);
						System.out.println("Responding " + req);
						broker.sendResponse(req);
					}
					
				}
				catch(Exception ex)
				{
					log.error(ex);
				}
				
			}											
		}
	}
	
	
}
