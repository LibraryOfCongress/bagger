package gov.loc.repository.workflow.continuations;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.service.component.ComponentRequest.ObjectEntry;
import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;

public class CompletedServiceRequestListener
{    	
	
	private static final Log log = LogFactory.getLog(CompletedServiceRequestListener.class);
	
	private CompletedServiceRequestListenerTimerTask task;
	private Timer timer = new Timer(true);
	private Long interval;
	private boolean isRunning = false;
	
	public CompletedServiceRequestListener(RequestingServiceBroker broker, SimpleContinuationController continuationController, Long interval) {		
		this.task = new CompletedServiceRequestListenerTimerTask(broker, continuationController);
		this.interval = interval;
	}
	
	public void start()
	{
		log.debug("Starting");
		this.timer.schedule(this.task, 0, this.interval);
		this.isRunning = true;
	}
	
	public void stop()
	{
		log.debug("Stopping");
		this.task.cancel();
		this.isRunning = false;
	}
	
	public boolean isRunning()
	{
		return this.isRunning;
	}

	public Long getInterval() {
		return this.interval;
	}
	
	public class CompletedServiceRequestListenerTimerTask extends TimerTask
	{
		private RequestingServiceBroker broker;
		private SimpleContinuationController continuationController;
		
		public CompletedServiceRequestListenerTimerTask(RequestingServiceBroker broker, SimpleContinuationController continuationController)
		{
			this.broker = broker;
			this.continuationController = continuationController;
		}
		
		@Override
		public void run() {
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
					throw new RuntimeException(ex);
				}
				
			}
			
			
		}
	}
}
