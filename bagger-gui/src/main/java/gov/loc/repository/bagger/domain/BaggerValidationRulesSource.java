
package gov.loc.repository.bagger.domain;

import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.BaggerFetch;
import gov.loc.repository.bagger.Contact;

import org.springframework.core.closure.Constraint;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

public class BaggerValidationRulesSource extends DefaultRulesSource {
	boolean isCopyright = false;
	boolean isNdnp = false;
	boolean isLcProject = false;
	boolean isHoley = false;
	
    public BaggerValidationRulesSource() {
        super();
    }
    
    public void init(boolean isCopyright, boolean isNdnp, boolean isLcProject, boolean isHoley) {
    	clear();
    	this.isCopyright = isCopyright;
    	this.isNdnp = isNdnp;
    	this.isLcProject = isLcProject;
    	this.isHoley = isHoley;
/*
    	if (isCopyright || isNdnp) addRules(createContactRules());
        if (isCopyright || isNdnp) addRules(createBagOrganizationRules());
        if (isCopyright || isNdnp) addRules(createBagInfoRules());
        if (isHoley) addRules(createFetchRules());
*/
    }
    
    public void clear() {
    	java.util.List<Rules> empty = new java.util.ArrayList<Rules>();
    	setRules(empty);
    }

    private Rules createContactRules() {
        return new Rules(Contact.class) {
            protected void initRules() {
                add("contactName", getNameValueConstraint());
                add("telephone", required());
                add("email", getEmailConstraint());
            }
        };
    }

    private Rules createBagOrganizationRules() {
        return new Rules(BaggerOrganization.class) {
            protected void initRules() {
                add("orgName", getNameValueConstraint());
                add("orgAddress", required());
            }
        };
    }

    private Rules createBagInfoRules() {
        return new Rules(DefaultBagInfo.class) {
            protected void initRules() {
                add("bagName", required());
                add("externalDescription", required());
                add("baggingDate", getDateConstraint());
                add("externalIdentifier", required());
                add("bagSize", required());
                if (isCopyright) {
                	add("publisher", required());
                }
                if (isNdnp) {
                	add("awardeePhase", required());
                }
                if (isLcProject) {
                	add("lcProject", required());
                }
            }
        };
    }

    private Rules createFetchRules() {
        return new Rules(BaggerFetch.class) {
            protected void initRules() {
            	if (isHoley) {
                    add("baseURL", required());            		
            	}
            }
        };
    }
    
    private Constraint getNameValueConstraint() {
    	Constraint res;
    	res = all(new Constraint[] {required(), maxLength(50), regexp("[a-zA-Z\\.\\- ]*", "alphabetic")});
    	return res;
    }
    
    private Constraint getEmailConstraint() {
    	Constraint res;
    	res = all(new Constraint[] {required(), maxLength(50), regexp("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", "emailRule")});
    	return res;
    }
    
    private Constraint getDateConstraint() {
    	Constraint res;
    	res = all(new Constraint[] {required(), maxLength(10), regexp("(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])", "dateRule")});
    	return res;
    }
/*
    private Constraint getIsCopyright() {
    	Constraint res;
    	// TODO: RequiredIfTrue does not work.  This class needs to be rewritten without Spring RC
    	RequiredIfTrue isPublisherReq = new RequiredIfTrue("publisher", eq("isCopyright", true));
        res = isPublisherReq.getConstraint();
        return res;
    }
 */
}

