package gov.loc.repository.service.impl;

import javax.jms.Queue;

import gov.loc.repository.service.Memento;

public class JmsRequestMessageMemento implements Memento {

	private static final long serialVersionUID = 6680921300179071619L;

	private String jobType;
	private String jmsReplyTo;
	private String jmsCorrelationId;
	
	public JmsRequestMessageMemento(JmsRequestMessage requestMessage) throws Exception {
		this.jobType = requestMessage.getJobType();
		this.jmsReplyTo = ((Queue)requestMessage.getMessage().getJMSReplyTo()).getQueueName();
		this.jmsCorrelationId = requestMessage.getMessage().getJMSCorrelationID();
	}
	
	public String getJobType() {
		return this.jobType;
	}

	public String getJMSReplyTo()
	{
		return this.jmsReplyTo;
	}

	public String getJMSCorrelationId()
	{
		return this.jmsCorrelationId;
	}
}
