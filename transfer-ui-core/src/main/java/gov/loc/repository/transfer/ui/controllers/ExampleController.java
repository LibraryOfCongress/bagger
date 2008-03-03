package gov.loc.repository.transfer.ui.controllers;

import org.jvnet.hyperjaxb3.ejb.tests.po.PurchaseOrderType;
import gov.loc.repository.transfer.ui.dao.PurchaseOrderTypeDAO;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Transactional
public class ExampleController implements IExampleController{
    
	protected static final Log logger = LogFactory.getLog(PurchaseOrderTypeDAO.class);
	
    private PurchaseOrderTypeDAO purchaseOrderDAO;
    
    public void setPurchaseOrderDAO(PurchaseOrderTypeDAO purchaseOrderDAO) {
      this.purchaseOrderDAO = purchaseOrderDAO;
    }
   
	public List query(String queryString, Object... params) {
	   this.logger.debug("Querying for purchaseOrder :" + queryString );
	   if(/*logger.isEnabledDebug*/true){
	       for(int i =0;i<params.length;i++){
	           this.logger.debug(" with parameter: " + params[i].toString());
	       }
	   }
       return purchaseOrderDAO.query(queryString, params);
   }
   
   public Object save(PurchaseOrderType  purchaseOrder){
	   this.logger.debug("Saving purchaseOrder ");
       return purchaseOrderDAO.save(purchaseOrder);
   }

	public PurchaseOrderType fromFile(String filename) throws FileNotFoundException{
	    this.logger.debug("Loading xml from file: " + filename);
	    PurchaseOrderType po = null;
	    try{
	        po = purchaseOrderDAO.unmarshal(
    	        new StreamSource( new File(filename) )
    	    );
        }catch(Exception e){
            this.logger.error("Failed to load XML", e);
        }
	    return po;
	}
}