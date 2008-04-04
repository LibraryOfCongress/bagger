package gov.loc.repository.transfer.ui.reports;

import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.transfer.ui.models.Report;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public abstract class AbstractReport extends Report {
    
	protected static final Log log = LogFactory.getLog(AbstractReport.class);
	
    public AbstractReport(){}
    
    public void prepareReport() throws Exception{
        log.info("Preparing report.");
        //Gathering report data should isolate all data access code.  It 
        //is up to the implementation to descide how filter strings are
        //used to modify dao queries.
        gatherData();
        //Processing report data should isolate additional work done to the 
        //gathered data, eg generating additional statistical measures
        processData();
    };
    
    abstract void gatherData() throws Exception;
    abstract void processData();
    
    //Can't autowire because this dao isnt instantiated by app framework
    //See AbstractRestController and ReportsController.handleGet
	protected PackageModelDAO packageDao;
	public void setPackageDao(PackageModelDAO packageDao){
	    this.packageDao = packageDao;
	}
	public PackageModelDAO getPackageDao(){
	    return this.packageDao;
    }
}
