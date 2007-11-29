package gov.loc.repository.workflow.listeners.impl;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.workflow.actionhandlers.BaseActionHandler;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;
import gov.loc.repository.workflow.continuations.impl.SimpleContinuationControllerImpl;
import gov.loc.repository.workflow.listeners.JmsCompletedJobListener;

public class ActiveMQJmsCompletedJobListener implements MessageListener, JmsCompletedJobListener {
	private static final Log log = LogFactory.getLog(ActiveMQJmsCompletedJobListener.class);
	protected static Configuration configuration;
	static
	{
		try
		{
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			URL url = BaseActionHandler.class.getClassLoader().getResource("workflow.core.cfg.xml");
			if (url != null)
			{
				log.debug("Loading configuration: " + url.toString());
			}
			else
			{
				log.error("workflow.cfg.xml is missing.");
			}
			builder.setURL(url);
			configuration = builder.getConfiguration(true);
		}
		catch(ConfigurationException ex)
		{
			log.error("Error loading configuration", ex);
			throw new RuntimeException();
		}
	}
		
	private Map<String, Map<String,String>> requiredParameters;
	private Map<String, Map<String,String>> optionalParameters;
	private String defaultSuccessTransition = "continue";
	private String defaultFailureTransition = null;
	private Map<String, String> successTransitions;
	private Map<String, String> failureTransitions;
	
	private Connection connection;
	private MessageConsumer consumer;
	private boolean isStarted = false;
	
	public void start() throws Exception
	{
		if (this.isStarted)
		{
			return;
		}
		
		this.stop();
		
		//Setup a listener
		//Create the connection
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(configuration.getString("jms.connection"));
		connectionFactory.setCloseTimeout(2000);
		connection = connectionFactory.createConnection();
		
		//Create the session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(configuration.getString("jms.replytoqueue"));
		
		//Create the consumer
		consumer = session.createConsumer(destination);
		consumer.setMessageListener(this);
		
		connection.start();		
		this.isStarted = true;
	}
	
	public void stop()
	{
		this.isStarted = false;
		try
		{
			consumer.close();
		}
		catch(Throwable ignore)
		{			
		}
		
		try
		{
			connection.close();
		}
		catch(Throwable ignore)
		{			
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		stop();
	}	
	
	public void setRequiredParameters(Map<String, Map<String,String>> requiredParameters) {
		this.requiredParameters = requiredParameters;
	}
	public Map<String, Map<String,String>> getRequiredParameters() {
		return requiredParameters;
	}
	public void setOptionalParameters(Map<String, Map<String,String>> optionalParameters) {
		this.optionalParameters = optionalParameters;
	}
	public Map<String, Map<String,String>> getOptionalParameters() {
		return optionalParameters;
	}
	public void setDefaultSuccessTransition(String defaultSuccessTransition) {
		this.defaultSuccessTransition = defaultSuccessTransition;
	}
	public String getDefaultSuccessTransition() {
		return defaultSuccessTransition;
	}
	public void setDefaultFailureTransition(String defaultFailureTransition) {
		this.defaultFailureTransition = defaultFailureTransition;
	}
	public String getDefaultFailureTransition() {
		return defaultFailureTransition;
	}
	public void setSuccessTransitions(Map<String, String> successTransitions) {
		this.successTransitions = successTransitions;
	}
	public Map<String, String> getSuccessTransitions() {
		return successTransitions;
	}
	public void setFailureTransitions(Map<String, String> failureTransitions) {
		this.failureTransitions = failureTransitions;
	}
	public Map<String, String> getFailureTransitions() {
		return failureTransitions;
	}

	private Long handleTokenId(Message message) throws Exception
	{
		String tokenIdString = message.getJMSCorrelationID();
		log.debug("TokenId string is " + tokenIdString);
		if (tokenIdString == null)
		{
			throw new Exception("TokenId is missing from message");			
		}
		Long tokenId = Long.parseLong(tokenIdString);
		log.debug("TokenId is " + tokenId);
		return tokenId;
	}

	private String handleJobType(Message message) throws Exception
	{
		if (! message.propertyExists("jobType"))
		{
			throw new Exception("jobType is missing from message header");
		}
		return message.getStringProperty("jobType");
		
	}

	private boolean handleIsSuccess(MapMessage message) throws Exception
	{
		if (! message.itemExists("isSuccess"))
		{
			throw new Exception("isSuccess is missing from message");
		}
		return message.getBoolean("isSuccess");
		
	}
	
	
	private void configureContinuationController(SimpleContinuationController continuationController, String jobType)
	{
		log.debug("Configuring continuation controller for jobType " + jobType);
		//Configure the continuation controller based on the jobId		
		if (this.requiredParameters != null && this.requiredParameters.containsKey(jobType))
		{
			log.debug("Adding required variables for jobType " + jobType);
			continuationController.setRequiredParameters(this.requiredParameters.get(jobType));
		}
		if (this.optionalParameters != null && this.optionalParameters.containsKey(jobType))
		{
			log.debug("Adding optional variables for jobType " + jobType);
			continuationController.setOptionalParameters(this.optionalParameters.get(jobType));
		}
		if (this.successTransitions != null && this.successTransitions.containsKey(jobType))
		{
			continuationController.setSuccessTransition(this.successTransitions.get(jobType));
		}
		else
		{
			continuationController.setSuccessTransition(this.defaultSuccessTransition);
		}
		if (this.failureTransitions != null && this.failureTransitions.containsKey(jobType))
		{
			continuationController.setFailureTransition(this.failureTransitions.get(jobType));
		}
		else
		{
			continuationController.setFailureTransition(this.defaultFailureTransition);
		}
		
	}
	
	private Map<String,String> handleVariableMap(MapMessage mapMessage) throws Exception
	{
		Map<String,String> variableMap = new HashMap<String, String>();
		Enumeration enumeration = mapMessage.getMapNames();
		while(enumeration.hasMoreElements())
		{
			String name = (String)enumeration.nextElement();
			if (! "isSuccess".equals(name))
			{
				String value = mapMessage.getString(name);
				log.debug(MessageFormat.format("Adding entry with name {0} and value {1} to variable map", name, value));
				variableMap.put(name, value);
			}
		}
		return variableMap;		
	}
	
	public void onMessage(Message message) {
		log.debug("The message is: " + message.toString());
		//Get the tokenId
		Long tokenId;
		try
		{
			tokenId = this.handleTokenId(message);
		}
		catch(Exception ex)
		{		
			log.error("Error getting tokenId from message: " + message.toString(), ex);
			return;
		}

		//Create a SimpleContinuationController
		SimpleContinuationController continuationController = new SimpleContinuationControllerImpl();
				
		try
		{
			//Get the jobType
			String jobType = this.handleJobType(message);
			if (!(message instanceof MapMessage))
			{
				throw new Exception("Message is not a MapMessage");
			}
			MapMessage mapMessage = (MapMessage)message;
			//Check to see if message is reporting an error
			if (mapMessage.itemExists("error"))
			{
				continuationController.invoke(tokenId, mapMessage.getString("error"), mapMessage.getString("errorDetail"));
				return;
			}
			boolean isSuccess = this.handleIsSuccess(mapMessage);
			Map<String,String> variableMap = this.handleVariableMap(mapMessage);
			this.configureContinuationController(continuationController, jobType);
			
			//Now invoke
			continuationController.invoke(tokenId, isSuccess, variableMap);
			
		}
		catch(Exception ex)
		{
			try
			{
				continuationController.invoke(tokenId, ex.getMessage(), null);
			}
			catch(Exception ex2)
			{
				log.error("Error reporting an error processing message: " + message.toString(), ex2);
				return;
			}
		}
	}

	public boolean isStarted() {
		return this.isStarted;
	}
}
