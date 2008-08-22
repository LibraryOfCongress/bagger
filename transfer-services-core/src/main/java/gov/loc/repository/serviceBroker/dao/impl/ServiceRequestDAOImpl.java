package gov.loc.repository.serviceBroker.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import gov.loc.repository.serviceBroker.ServiceContainerRegistration;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;
import gov.loc.repository.serviceBroker.impl.ServiceRequestImpl;

@Repository("serviceBroker")
public class ServiceRequestDAOImpl implements ServiceRequestDAO {

	private SessionFactory sessionFactory;
	
	@Autowired
	public ServiceRequestDAOImpl(@Qualifier(value="serviceBrokerSessionFactory")SessionFactory factory) {
		this.sessionFactory = factory;
	}
	
	@Override
	public void save(ServiceRequest req) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(req);		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceRequest> findServiceRequests(
			boolean includeRequestAcknowledged, boolean includeResponded,
			boolean includeResponseAcknowledged) {
		String query = "from ServiceRequest req where 1=1";
		if (! includeRequestAcknowledged)
		{
			query += " and req.responder is null";
		}
		if (! includeResponded)
		{
			query += " and req.isSuccess is null";
		}
		if (! includeResponseAcknowledged)
		{
			query += " and req.responseAcknowledgedDate is null";
		}
		return this.sessionFactory.getCurrentSession().createQuery(query).list();
	}
	
	@Override
	public ServiceRequest findNextServiceRequest(String[] queues,
			String[] jobTypes, String responder) {
		if (queues == null || queues.length == 0 || jobTypes == null || jobTypes.length == 0)
		{
			throw new IllegalArgumentException("Queues and jobtypes must be provided");
		}
		String queryString = "from ServiceRequest req " +
			"where req.responder is null " +
			"and req.jobType in (:jobTypes) " +
			"and req.queue in (:queues) " +
			"and req.isSuspended = false " +
			"order by req.requestDate asc";
		Query query = this.sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameterList("jobTypes", this.arrayToList(jobTypes));
		query.setParameterList("queues", this.arrayToList(queues));
		query.setMaxResults(1);
		return (ServiceRequest)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceRequest> findAcknowledgedServiceRequestsWithoutResponses(
			String responder) {
		String queryString = "from ServiceRequest req " +
		"where req.responder = :responder " +
		"and req.isSuccess is null";
		
		Query query = this.sessionFactory.getCurrentSession().createQuery(queryString);
		query.setString("responder", responder);
		return query.list();
	}
	
	@Override
	public ServiceRequest findNextServiceRequestWithResponse(String requester) {
		String queryString = "from ServiceRequest req " +
		"where req.isSuccess is not null " +
		"and req.responseAcknowledgedDate is null " +
		"and req.requester = :requester " +
		"and req.isSuspended = false " +
		"order by req.responseDate asc";
		
		Query query = this.sessionFactory.getCurrentSession().createQuery(queryString);
		query.setString("requester", requester);
		query.setMaxResults(1);
		return (ServiceRequest)query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceRequest> findServiceRequests(String requester, String correlationKey) {
		String queryString = "from ServiceRequest req " +
		"where req.correlationKey = :correlationKey " +
		"and req.requester = :requester";
		
		Query query = this.sessionFactory.getCurrentSession().createQuery(queryString);
		query.setString("correlationKey", correlationKey);
		query.setString("requester", requester);
		return query.list();
	}
	
	private List<String> arrayToList(String[] array)
	{
		List<String> list = new ArrayList<String>(array.length);
		for(String item : array)
		{
			list.add(item);
		}
		return list;
	}
	
	@Override
	public ServiceRequest findServiceRequest(Long key) {
		return (ServiceRequest)this.sessionFactory.getCurrentSession().get(ServiceRequestImpl.class, key);
	}
	
	@Override
	public void delete(ServiceContainerRegistration registration) {
		this.sessionFactory.getCurrentSession().delete(registration);
		
	}
	
	@Override
	public void save(ServiceContainerRegistration registration) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(registration);		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceContainerRegistration> findServiceContainerRegistrations(Long latency) {
		String queryString = "from ServiceContainerRegistration sc";
		if (latency != null)
		{
			queryString += " where sc.timestamp >= :date";
		}
		Query query = this.sessionFactory.getCurrentSession().createQuery(queryString);
		if (latency != null)
		{
			Date date = new Date(Calendar.getInstance().getTime().getTime() - latency);
			query.setTimestamp("date", date);
			
		}
		
		return query.list();
	}
}
