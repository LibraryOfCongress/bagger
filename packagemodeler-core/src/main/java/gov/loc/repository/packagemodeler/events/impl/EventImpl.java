package gov.loc.repository.packagemodeler.events.impl;

import javax.persistence.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.impl.AgentImpl;
import gov.loc.repository.packagemodeler.events.Event;

import java.io.StringReader;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.validator.AssertTrue;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

@MappedSuperclass
public abstract class EventImpl implements Event {
	
	private static final Log log = LogFactory.getLog(EventImpl.class);	
		
	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
	
	@Column(name="create_timestamp", nullable = false)
	private Date createTimestamp;

	@Column(name="update_timestamp", nullable = false)
	private Date updateTimestamp;
		
	@ManyToOne(targetEntity=AgentImpl.class)
	@JoinColumn(name="reporting_agent_key", nullable = false)
	private Agent reportingAgent;

	@Column(name="event_start", nullable=false)
	private Date eventStart; 

	@Column(name="is_unknown_event_start", nullable=false)
	private boolean isUnknownEventStart = false;
	
	@Column(name="event_end")
	private Date eventEnd; 

	@ManyToOne(targetEntity=AgentImpl.class)
	@JoinColumn(name="performing_agent_key", nullable=true)
	private Agent performingAgent;

	@Column(name="is_unknown_performing_agent", nullable=false)
	private boolean isUnknownPerformingAgent = false;
		
	@ManyToOne(targetEntity=AgentImpl.class)
	@JoinColumn(name="requesting_agent_key", nullable=true)
	private Agent requestingAgent;
	
	@Column(name="is_unknown_requesting_agent", nullable=false)
	private boolean isUnknownRequestingAgent = false;
		
	@Column(name="is_success", nullable=false)
	private boolean isSuccess = true;
		
	public Long getKey() {
		return key;
	}

	public Date getCreateTimestamp() {
		return this.createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return this.updateTimestamp;
	}
		
	public Agent getReportingAgent() {
		return this.reportingAgent;
	}
	
	public void setReportingAgent(Agent reportingAgent) {
		this.reportingAgent = reportingAgent;
		
	}

	public void setEventStart(Date eventStart) {
		this.eventStart = eventStart;
	}

	public Date getEventStart() {
		return eventStart;
	}

	public void setEventEnd(Date eventEnd) {
		this.eventEnd = eventEnd;
	}

	public Date getEventEnd() {
		return eventEnd;
	}

	public Agent getPerformingAgent() {
		return this.performingAgent;
	}
	
	public void setPerformingAgent(Agent performingAgent) {
		this.performingAgent = performingAgent;		
	}

	public Agent getRequestingAgent() {
		return this.requestingAgent;
	}
	
	public void setRequestingAgent(Agent requestingAgent) {
		this.requestingAgent = requestingAgent;		
	}
		
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public boolean isSuccess() {
		return isSuccess;
	}
	
	public boolean isUnknownRequestingAgent() {
		return this.isUnknownRequestingAgent;
	}

	public void setUnknownRequestingAgent(boolean isUnknown) {
		this.isUnknownRequestingAgent = isUnknown;
	
	}
	
	@SuppressWarnings("unused")
	@AssertTrue(message="Error validating RequestingAgent")
	private boolean validateRequestingAgent()
	{
		if (this.isUnknownRequestingAgent && this.requestingAgent != null)
		{
			log.error("Error validating RequestingAgent.");
			log.debug("IsUnknownRequestingAgent is " + this.isUnknownRequestingAgent);
			if (this.requestingAgent != null)
			{
				log.debug("RequestingAgent is not null");
			}
			else
			{
				log.debug("RequestingAgent is null");
			}
			return false;
		}
		return true;
	}

	public boolean isUnknownPerformingAgent() {
		return this.isUnknownPerformingAgent;
	}

	public void setUnknownPerformingAgent(boolean isUnknown) {
		this.isUnknownPerformingAgent = isUnknown;
	
	}
	
	@SuppressWarnings("unused")
	@AssertTrue(message="Error validating PerformingAgent")
	private boolean validatePerformingAgent()
	{
		if (this.isUnknownPerformingAgent && this.performingAgent != null)
		{
			log.error("Error validating PerformingAgent.");
			log.debug("IsUnknownPerformingAgent is " + this.isUnknownPerformingAgent);
			if (this.performingAgent != null)
			{
				log.debug("PerformingAgent is not null");
			}
			else
			{
				log.debug("PerformingAgent is null");
			}
			return false;
		}
		return true;
	}
	
	
	public boolean isUnknownEventStart()
	{
		return this.isUnknownEventStart;
	}

	public void setUnknownEventStart(boolean isUnknown) {
		this.isUnknownEventStart = isUnknown;	
	}

	@SuppressWarnings("unused")
	@AssertTrue(message="Error validating EventStart")
	private boolean validateEventStart()
	{
		if ((this.isUnknownEventStart || this.eventStart != null) && (!(this.isUnknownEventStart && this.eventStart != null)))
		{
			return true;
		}
		return false;
	}
	
	
	public String getName() {
		String name = this.getClass().getSimpleName();
		if (name.endsWith("Impl"))
		{
			name = name.substring(0, name.length() - 4);
		}
		StringBuffer buf = new StringBuffer();
		for(char c : name.toCharArray())
		{
			if (buf.length() != 0 && Character.isUpperCase(c))
			{
				buf.append(" ");
			}
			buf.append(c);
		}
		return buf.toString();
	}
	
	/*
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Event))
		{
			return false;
		}
		Event event = (Event)obj;
		if (! this.getName().equals(event.getName()))
		{
			return false;
		}
		else if ((this.getPackage() != null && ! this.getPackage().equals(event.getPackage())) || (this.getPackage() == null && this.getPackage() != null))
		{
			return false;
		}
		else if ((this.getPerformingAgent() != null && ! this.getPerformingAgent().equals(event.getPerformingAgent())) || (this.getPerformingAgent() == null && this.getPerformingAgent() != null))
		{
			return false;
		}
		else if ((this.getReportingAgent() != null && ! this.getReportingAgent().equals(event.getReportingAgent())) || (this.getReportingAgent() == null && this.getReportingAgent() != null))
		{
			return false;
		}
		else if ((this.getEventStart() != null && ! this.getEventStart().equals(event.getEventStart())) || (this.getEventStart() == null && this.getEventStart() != null))
		{
			return false;
		}
		else if ((this.getEventEnd() != null && ! this.getEventEnd().equals(event.getEventEnd())) || (this.getEventStart() == null && this.getEventStart() != null))
		{
			return false;
		}			
		return true;
	}
	*/
	
	public Document toPremis() throws Exception
	{
		Document doc = DocumentHelper.createDocument();
		Element event = DocumentHelper.createElement("premis:event");
		event.add(DocumentHelper.createNamespace("premis", "http://www.loc.gov/standards/premis/v1"));
		doc.add(event);
		Element eventIdentifier = event.addElement("premis:eventIdentifier");
		
		Element eventIdentiferType = eventIdentifier.addElement("premis:eventIdentifierType");
		eventIdentiferType.setText("info:loc-repo/entity/event");
		
		Element eventIdentifierValue = eventIdentifier.addElement("premis:eventIdentifierValue");
		eventIdentifierValue.setText("info:loc-repo/entity/event/" + this.getKey());
		
		Element eventType = event.addElement("premis:eventType");
		eventType.setText("info:loc-repo/event/" + this.getName());
		
		Element eventDateTime = event.addElement("premis:eventDateTime");
		eventDateTime.setText(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(this.eventStart));
		
		Element eventOutcomeInformation = event.addElement("premis:eventOutcomeInformation");
		Element eventOutcome = eventOutcomeInformation.addElement("premis:eventOutcome");
		if (this.isSuccess)
		{
			eventOutcome.setText("success");
		}
		else
		{
			eventOutcome.setText("failure");
		}

		Element reportingAgentIdentifier = event.addElement("premis:linkingAgentIdentifier");
		Element reportingAgentIdentifierType = reportingAgentIdentifier.addElement("premis:linkingAgentIdentifierType");
		reportingAgentIdentifierType.setText("info:loc-repo/entity/service");
		
		Element reportingAgentIdentifierValue = reportingAgentIdentifier.addElement("premis:linkingAgentIdentifierValue");
		reportingAgentIdentifierValue.setText("info:loc-repo/entity/service/" + this.reportingAgent.getId());
		
		Element reportingAgentRole = reportingAgentIdentifier.addElement("premis:linkingAgentRole");
		reportingAgentRole.setText("reporter");

		if (this.performingAgent != null)
		{
			Element performingAgentIdentifier = event.addElement("premis:linkingAgentIdentifier");
			Element performingAgentIdentifierType = performingAgentIdentifier.addElement("premis:linkingAgentIdentifierType");
			performingAgentIdentifierType.setText("info:loc-repo/entity/agent");
			
			Element performingAgentIdentifierValue = performingAgentIdentifier.addElement("premis:linkingAgentIdentifierValue");
			performingAgentIdentifierValue.setText("info:loc-repo/entity/agent/" + this.getPerformingAgent());
			
			Element performingAgentRole = performingAgentIdentifier.addElement("premis:linkingAgentRole");
			performingAgentRole.setText("performer");
		}
		
		Element linkingObjectIdentifier = event.addElement("premis:linkingObjectIdentifier");
		Element linkingObjectIdentifierType = linkingObjectIdentifier.addElement("premis:linkingObjectIdentifierType");
		linkingObjectIdentifierType.setText("info:loc-repo/entity/package");
		
		Element linkingObjectIdentifierValue = linkingObjectIdentifier.addElement("premis:linkingObjectIdentifierValue");
		linkingObjectIdentifierValue.setText(this.getPremisLinkingObjectIdentifierValueText());				
		
		try
		{
			this.validate(doc);
		}
		catch(Exception ex)
		{
			log.error("Invalid xml is: " + doc.asXML(), ex);
			throw ex;
		}
		return doc;
	}
	
	protected abstract String getPremisLinkingObjectIdentifierValueText();
	
	private void validate(Document doc) throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware (true);
        SAXParser parser = factory.newSAXParser();
        XMLReader reader = parser.getXMLReader();
        
        SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = sf.newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream("schemas/Event-v1-1.xsd"), this.getClass().getClassLoader().getResource("schemas/Event-v1-1.xsd").toString()));
        reader.setContentHandler(schema.newValidatorHandler());
        
        reader.parse(new InputSource(new StringReader(doc.asXML())));
	}
	
	public int compareTo(Event other) {
		if (this.isUnknownEventStart && other.isUnknownEventStart())
		{
			return 0;
		}
		if (this.isUnknownEventStart && ! other.isUnknownEventStart())
		{
			return 1;
		}
		if (! this.isUnknownEventStart && other.isUnknownEventStart())
		{
			return -1;
		}
		return this.eventStart.compareTo(other.getEventStart());
	}
}
