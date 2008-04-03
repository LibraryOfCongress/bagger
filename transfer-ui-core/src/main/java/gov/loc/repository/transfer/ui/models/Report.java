package gov.loc.repository.transfer.ui.models;

import java.util.List;
import java.util.Map;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class Report extends Base<Long> {
    protected String description;
    protected Map<String, List<String>> filters;
    protected List<String> sorters;
    protected String query;
    protected Map<String, String> parameters;
	protected String namespace;
	//the hash should hold report data in the hash map similar
	//to the way a ModelAndView holds objects for the view to render
	protected Map<String, Object> data;
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public Map<String, List<String>> getFilters(){
        return this.filters;
    }
    public void setFilters(Map<String, List<String>> filters){
        this.filters = filters;
    }
    public List<String> getSorters(){
        return this.sorters;
    }
    public void setSorters(List<String> sorters){
        this.sorters = sorters;
    }
    public String getQuery(){
        return this.query;
    }
    public void setQuery(String query){
        this.query = query;
    }
    public Map<String,String> getParameters(){
        return this.parameters;
    }
    public void setParameters(Map<String,String> parameters){
        this.parameters = parameters;
    }
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
}
