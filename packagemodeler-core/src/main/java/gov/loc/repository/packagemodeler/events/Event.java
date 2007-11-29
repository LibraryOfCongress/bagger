package gov.loc.repository.packagemodeler.events;

import gov.loc.repository.Keyed;
import gov.loc.repository.Timestamped;
import gov.loc.repository.packagemodeler.agents.Agent;

import java.util.Date;

import org.dom4j.Document;

public interface Event extends Keyed, Timestamped, Comparable<Event> {

	/*
	 * The Agent that reported the Event to the Package Modeler.
	 */
	public abstract Agent getReportingAgent();
	
	public abstract void setReportingAgent(Agent reportingAgent);
		
	public abstract Date getEventStart();
	
	public abstract void setEventStart(Date start);

	public abstract boolean isUnknownEventStart();
	
	public abstract void setUnknownEventStart(boolean isUnknown);
	
	public abstract Date getEventEnd();
	
	public abstract void setEventEnd(Date end);

	/*
	 * The Agent that pPerformed the Event.
	 * If null and isUnknownPerformingAgent is true, indicates  that there was a PerformingAgent, but the PerformingAgent is not known.
	 * If null and isUnknownPerformingAgent is false, indicates that there was no PerformingAgent.
	 */
	public abstract Agent getPerformingAgent();
	
	public abstract void setPerformingAgent(Agent performingAgent);

	/*
	 * If true, indicates that there was a PerformingAgent, but the PerformingAgent is not known. 
	 */
	public abstract boolean isUnknownPerformingAgent();
	
	public abstract void setUnknownPerformingAgent(boolean isUnknown);
		
	/*
	 * The Agent that requested or initiated the Event (but did not perform the Event).
	 * If null and isUnknownRequestingAgent is true, indicates  that there was a RequestingAgent, but the RequestingAgent is not known.
	 * If null and isUnknownRequestingAgent is false, indicates that there was no RequestingAgent.
	 */
	public abstract Agent getRequestingAgent();
	
	public abstract void setRequestingAgent(Agent requestingAgent);

	/*
	 * If true, indicates that there was a RequestingAgent, but the RequestingAgent is not known. 
	 */
	public abstract boolean isUnknownRequestingAgent();
	
	public abstract void setUnknownRequestingAgent(boolean isUnknown);
		
	public void setSuccess(boolean isSuccess);

	public boolean isSuccess();
	
	public abstract String getName();
	
	public Document toPremis() throws Exception;	
	
}