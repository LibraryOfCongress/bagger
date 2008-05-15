package gov.loc.repository.serviceBroker.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import gov.loc.repository.serviceBroker.ServiceContainerRegistration;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;
import gov.loc.repository.serviceBroker.impl.ServiceRequestImpl;

@Repository("serviceBroker")
public class ServiceRequestDAOImpl implements ServiceRequestDAO {

	private SessionFactory sessionFactory;
	
	@Autowired
	public ServiceRequestDAOImpl(SessionFactory factory) {
		this.sessionFactory = factory;
	}
	
	@Override
	@Transactional
	public void save(ServiceRequest req) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(req);		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true)
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
	@Transactional
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
	@Transactional
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
	@Transactional
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
	@Transactional(readOnly=true)
	public List<ServiceRequest> findServiceRequests(String correlationKey) {
		String queryString = "from ServiceRequest req " +
		"where req.correlationKey = :correlationKey";
		
		Query query = this.sessionFactory.getCurrentSession().createQuery(queryString);
		query.setString("correlationKey", correlationKey);
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
	@Transactional(readOnly=true)
	public ServiceRequest findServiceRequest(Long key) {
		return (ServiceRequest)this.sessionFactory.getCurrentSession().get(ServiceRequestImpl.class, key);
	}
	
	@Override
	@Transactional
	public void delete(ServiceContainerRegistration registration) {
		this.sessionFactory.getCurrentSession().delete(registration);
		
	}
	
	@Override
	@Transactional
	public void save(ServiceContainerRegistration registration) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(registration);		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly=true)
	public List<ServiceContainerRegistration> findServiceContainerRegistrations() {
		String queryString = "from ServiceContainerRegistration";
		Query query = this.sessionFactory.getCurrentSession().createQuery(queryString);
		return query.list();
	}
}
