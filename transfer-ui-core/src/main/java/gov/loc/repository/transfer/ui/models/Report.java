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
    protected List<String> filters;
    protected List<String> sorters;
    protected String query;
    protected Map<String, String> parameters;
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public List<String> getFilters(){
        return this.filters;
    }
    public void setFilters(List<String> filters){
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
}
