package gov.loc.repository.service.container;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.serviceBroker.ServiceBrokerFactory;
import gov.loc.repository.serviceBroker.ServiceContainerRegistration;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;


public class ServiceContainerHeartbeat {
	private static final Log log = LogFactory.getLog(ServiceContainerHeartbeat.class);
	
	private Long interval;
	private Timer timer = new Timer(true);
	private ServiceContainerHeartbeatTimerTask task;
			
	public ServiceContainerHeartbeat(ServiceRequestDAO dao, String host, Integer port, Long interval) {
		this.task = new ServiceContainerHeartbeatTimerTask(dao, host, port);
		this.interval = interval;
	}
	
	public void start()
	{
		this.timer.schedule(this.task, 0, this.interval);
	}
	
	public void stop()
	{
		this.task.cancel();
	}
	
	public class ServiceContainerHeartbeatTimerTask extends TimerTask{

		private ServiceRequestDAO dao;
		private ServiceBrokerFactory factory = new ServiceBrokerFactory();
		private String host;
		private Integer port;
		
		
		public ServiceContainerHeartbeatTimerTask(ServiceRequestDAO dao, String host, Integer port)
		{
			this.dao = dao;
			this.host = host;
			this.port = port;			
		}
		
		@Override
		public void run() {
			log.debug("Running heartbeat");
			ServiceContainerRegistration registration = factory.createServiceContainerRegistration(host, port);
			registration.beat();
			this.dao.save(registration);
		}
		
		@Override
		public boolean cancel() {
			log.debug("Cancelling heartbeat");
			this.dao.delete(factory.createServiceContainerRegistration(host, port));
			return super.cancel();
		}
		
	}
}
