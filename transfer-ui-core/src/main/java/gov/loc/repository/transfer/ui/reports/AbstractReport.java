package gov.loc.repository.transfer.ui.reports;

import gov.loc.repository.transfer.ui.models.Report;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Component
public abstract class AbstractReport extends Report {
    
	protected static final Log log = LogFactory.getLog(AbstractReport.class);	
	
	//the hash should hold report data in the hash map similar
	//to the way a ModelAndView holds objects for the view to render
	protected Map<String, Object> data = new HashMap<String, Object>();
	
    public AbstractReport(){}
    
    protected void prepareReport(){
        log.info("Preparing report.");
        //Gathering report data should isolate all data access code.  It 
        //is up to the implementation to descide how filter strings are
        //used to modify dao queries.
        gatherData();
        //Processing report data should isolate additional work done to the 
        //gathered data, eg generating additional statistical measures
        processData();
    };
    
    abstract void gatherData();
    abstract void processData();
}
