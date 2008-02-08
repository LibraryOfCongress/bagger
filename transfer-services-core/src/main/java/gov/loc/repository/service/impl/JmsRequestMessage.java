package gov.loc.repository.service.impl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.MapMessage;
import javax.jms.Message;

import gov.loc.repository.service.RequestMessage;

public class JmsRequestMessage implements RequestMessage {

	private Message message = null;
	
	public String getJobType() throws Exception {
		if (message != null)
		{
			return message.getStringProperty("jobType");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getVariableMap() throws Exception {
		Map<String,Object> variableMap = new HashMap<String, Object>();
		if (this.message == null)
		{
			return variableMap;
		}
		if (!(this.message instanceof MapMessage))
		{
			throw new Exception("Message is not a Map Message: " + this.message.toString());
		}
		MapMessage mapMessage = (MapMessage)this.message;
		Enumeration<String> enumeration = mapMessage.getMapNames();
		while(enumeration.hasMoreElements())
		{
			String name = enumeration.nextElement();
			variableMap.put(name, mapMessage.getObject(name));
		}
		
		return variableMap;
	}

	public void setMessage(Message message)
	{
		this.message = message;
	}

	public Message getMessage()
	{
		return this.message;
	}
	
}
