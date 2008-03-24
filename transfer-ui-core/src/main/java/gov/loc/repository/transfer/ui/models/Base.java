package gov.loc.repository.transfer.ui.models;

import java.io.Serializable;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class Base<K extends Serializable> {
    
	protected K id;
	protected String name;
	
	public K getId() {
		return this.id;
	}
	
	public void setId(K id) {
		this.id = id;
	}
	
	public String getName(){
	    return this.name;
	}
	
	public void setName(String name){
	    this.name = name;
	}
	
}
