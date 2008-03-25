package gov.loc.repository.transfer.ui.reports;

import gov.loc.repository.transfer.ui.models.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.springframework.stereotype.Component;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Component
public class CoreReport00 extends Report {
    
    public CoreReport00(){
        setId(new Long(0));
        setName("Core Report 00");
        setDescription("This report is intended to show a general progress report of many transfers at once.");
        List<String> filters = new ArrayList<String>(); 
        filters.add("size");
        filters.add("type");
        setFilters(filters);
        List<String> sorters = new ArrayList<String>(); 
        sorters.add("size");
        sorters.add("type");
        setSorters(sorters);
        setQuery("from gov.loc.repository.packagemodeler.packge.Repository");
        //setParameters(new HashMap());
    }
}
