
package gov.loc.repository.bagger.domain;

import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

public class BaggerValidationRulesSource extends DefaultRulesSource {
	boolean isLcProject = false;
	boolean isHoley = false;
	
    public BaggerValidationRulesSource() {
        super();
    }
    
    public void init(boolean isLcProject, boolean isHoley) {
    	clear();
    	this.isLcProject = isLcProject;
    	this.isHoley = isHoley;
    }
    
    public void clear() {
    	java.util.List<Rules> empty = new java.util.ArrayList<Rules>();
    	setRules(empty);
    }

}

