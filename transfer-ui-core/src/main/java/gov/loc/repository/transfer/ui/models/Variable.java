package gov.loc.repository.transfer.ui.models;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class Variable extends Base<String>{
    protected Object value;
    protected Boolean readable;
    protected Boolean writable;
    protected Boolean required;
    
    public Object getValue(){
        return this.value;
    }
    public void setValue(Object value){
        this.value = value;
    }
    public Boolean getReadable(){
        return this.readable;
    }
    public void setReadable(Boolean readable){
        this.readable = readable;
    }
    public boolean isReadable(){
        return Boolean.valueOf(this.readable);
    }
    public Boolean getWritable(){
        return this.writable;
    }
    public void setWritable(Boolean writable){
        this.writable = writable;
    }
    public boolean isWritable(){
        return Boolean.valueOf(this.writable);
    }
    public Boolean getRequired(){
        return this.required;
    }
    public void setRequired(Boolean required){
        this.required = required;
    }
    public boolean isRequired(){
        return Boolean.valueOf(this.required);
    }
}
