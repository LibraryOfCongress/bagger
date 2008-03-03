package gov.loc.repository.transfer.ui.controllers;

import java.util.List;
import java.io.FileNotFoundException;
import org.jvnet.hyperjaxb3.ejb.tests.po.PurchaseOrderType;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public interface IExampleController {
    
	public abstract List query(String queryString, Object... params);
	
	public abstract Object save(PurchaseOrderType purchaseOrder);
	
	public abstract PurchaseOrderType fromFile(String filename) throws FileNotFoundException;
}
