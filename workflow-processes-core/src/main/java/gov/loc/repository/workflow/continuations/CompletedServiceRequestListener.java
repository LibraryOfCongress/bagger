package gov.loc.repository.workflow.continuations;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.loc.repository.service.component.ComponentRequest.ObjectEntry;
import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;

@Component("completedServiceRequestListener")
public class CompletedServiceRequestListener implements Runnable
{    	
	
	public enum State {STARTING, STARTED, STOPPING, STOPPED};
	
	private static final Log log = LogFactory.getLog(CompletedServiceRequestListener.class);
	
	private RequestingServiceBroker broker;
	private State state = State.STOPPED;
	private Long wait = 5000L;
	private SimpleContinuationController continuationController;
	
	@Autowired
	public CompletedServiceRequestListener(RequestingServiceBroker broker, SimpleContinuationController continuationController) {
		this.broker = broker;
		this.continuationController = continuationController;
	}
	
	@Override
	public void run() {
		this.state = State.STARTED;
		log.debug("Starting");
		while(this.state == State.STARTED)
		{
			ServiceRequest req = this.broker.findAndAcknowledgeNextServiceRequestWithResponse();
			if (req != null)
			{
				log.debug("Received " + req);
				//Create a SimpleContinuationController				
				Long tokenId = Long.parseLong(req.getCorrelationKey());
				try
				{
					Map<String,Object> responseParameterMap = new HashMap<String,Object>();					
					for(ObjectEntry entry : req.getResponseEntries())
					{
						responseParameterMap.put(entry.getKey(), entry.getValueObject());
					}
					
					if (req.getErrorMessage() == null)
					{
						continuationController.invoke(tokenId, responseParameterMap, req.isSuccess());
					}
					else
					{
						continuationController.invoke(tokenId, responseParameterMap, req.getErrorMessage(), req.getErrorDetail());
					}
				}
				catch(Exception ex)
				{
					log.error(ex);
				}
				
			}
			else
			{
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
		}
		
		this.state = State.STOPPED;
		log.debug("Stopped");				
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
	
	public void stop()
	{
		log.debug("Stopping");
		this.state = State.STOPPING;
	}
	
	public State getState()
	{
		return this.state;
	}

	public void setWait(Long wait) {
		this.wait = wait;
	}

	public Long getWait() {
		return wait;
	}
}
