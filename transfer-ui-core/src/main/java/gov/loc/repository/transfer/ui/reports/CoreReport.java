package gov.loc.repository.transfer.ui.reports;

import gov.loc.repository.transfer.ui.models.Report;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
abstract class CoreReport extends AbstractReport {
    
    protected String namespace = "core";
    public String getNamespace(){
        return this.namespace;
    }
    public void setNamespace(String namespace){
        this.namespace = namespace;
    }
}
