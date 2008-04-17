package gov.loc.repository.service;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

@Component("messageListener")
public class Listener implements SessionAwareMessageListener {
	
	private ComponentFactory componentFactory;
	private static final Log log = LogFactory.getLog(Listener.class);
	
	@Autowired
	public void setComponentFactory(ComponentFactory factory)
	{
		this.componentFactory = factory;
	}
	
	public void onMessage(Message message, Session session) {
		Exception error = null;
		boolean result = true;
		String jobType = null;
		try
		{
			log.debug("Received " + message.toString());
			if (! (message instanceof MapMessage))
			{
				throw new Exception("Message is not a MapMessage");
			}
			MapMessage mapMessage = (MapMessage)message;
			jobType = message.getStringProperty("jobType");
			if (jobType == null)
			{
				throw new Exception("Message does not contain jobType property");
			}
			System.out.println("Received message with jobType " + jobType);
			Object component = componentFactory.getComponent(jobType);
			InvokeComponentHelper helper = new InvokeComponentHelper(component, jobType, this.mapMessageToVariableMap(mapMessage));
			org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory(DatabaseRole.DATA_WRITER).getCurrentSession();
			try
			{					
				hibernateSession.beginTransaction();
				//Invoke and return taskResult
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
			error = ex;
		}
		
		//Send reply Message
		try
		{
			MessageProducer producer = session.createProducer(message.getJMSReplyTo());
			MapMessage replyMessage = session.createMapMessage();
			if (jobType != null)
			{
				replyMessage.setStringProperty("jobType", jobType);
			}
			replyMessage.setJMSCorrelationID(message.getJMSCorrelationID());
			if (error != null)
			{
				log.error("Error handling message", error);
				replyMessage.setString("error", error.getMessage());
			}
			else
			{
				replyMessage.setBoolean("isSuccess", result);
			}
			log.debug("Sending reply " + replyMessage.toString());
			producer.send(replyMessage);
			producer.close();
		}
		catch(Exception ex)
		{
			log.error(ex);
			throw new RuntimeException(ex);
		}
		
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> mapMessageToVariableMap(MapMessage message) throws Exception {
		Map<String,Object> variableMap = new HashMap<String, Object>();
		Enumeration<String> enumeration = message.getMapNames();
		while(enumeration.hasMoreElements())
		{
			String name = enumeration.nextElement();
			variableMap.put(name, message.getObject(name));
		}
		
		return variableMap;
	}	
}
