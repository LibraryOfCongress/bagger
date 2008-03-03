package gov.loc.repository.transfer.ui.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jvnet.hyperjaxb3.ejb.tests.po.PurchaseOrderType;
import org.jvnet.hyperjaxb3.ejb.util.EntityUtils;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */

public class PurchaseOrderTypeDAO{
	protected static final Log logger = LogFactory.getLog(PurchaseOrderTypeDAO.class);

    private EntityManager entityManager;
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    private Jaxb2Marshaller jaxb2Marshaller;
    public void setJaxb2Marshaller(Jaxb2Marshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }
    
    public Object save(PurchaseOrderType po){
        Object id = null;
        try{
            entityManager.persist(po);
            id = EntityUtils.getId(po); 
        }catch(Exception e){
            this.logger.error("Unable to save object", e);
        }return id;
    }
    
    public PurchaseOrderType find(Object hjid){
        return (PurchaseOrderType)entityManager.find(PurchaseOrderType.class, hjid);
    }
    
    public List query(String queryString, final Object... params) {
        Query query = null;
        List resultList = null;
        try{
            query = entityManager.createQuery(queryString);
            for(int i=0;i<params.length;i++){
                query.setParameter(i+1, params[i]);
            }
            resultList = query.getResultList();
        }catch(Exception e){
            this.logger.error("Error executing query: ", e);
        }
        return resultList;
    }
    
    public PurchaseOrderType unmarshal(Source source){
        JAXBElement unmarshalledElement = (JAXBElement) jaxb2Marshaller.unmarshal(source);
        return (PurchaseOrderType)unmarshalledElement.getValue();
    }
    
    public void marshal(PurchaseOrderType po, Result result){
        jaxb2Marshaller.marshal(po, result);
    }
}
