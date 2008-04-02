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
	protected String namespace = "fixme";
	
	//the hash should hold report data in the hash map similar
	//to the way a ModelAndView holds objects for the view to render
	protected Map<String, Object> data = new HashMap<String, Object>();
	
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
    
    //collections of reports should follow this pattern
    //so that report templates can be added by say, ndnp,
    //and we can minimized the likelyhood of report template 
    //name conflict, eg see CoreReport.
    public String getNamespace(){
        return this.namespace;
    }
    public void setNamespace(String namespace){
        this.namespace = namespace;
    }
    
    public Map<String, Object> getData(){
        return this.data;
    }
    public void setData(Map<String, Object> data){
        this.data = data;
    }
    
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
