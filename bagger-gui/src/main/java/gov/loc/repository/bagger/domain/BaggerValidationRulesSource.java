
package gov.loc.repository.bagger.domain;

import java.util.Date;

import gov.loc.repository.bagger.bag.BagInfo;
import gov.loc.repository.bagger.bag.BagOrganization;
import gov.loc.repository.bagger.bag.Fetch;
import gov.loc.repository.bagger.Contact;

import org.springframework.core.closure.Constraint;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;
import org.springframework.rules.constraint.property.*;

public class BaggerValidationRulesSource extends DefaultRulesSource {
	boolean isCopyright = false;
	boolean isHoley = false;
	
    public BaggerValidationRulesSource() {
        super();
    }
    
    public void init(boolean isCopyright, boolean isHoley) {
    	this.isCopyright = isCopyright;
    	this.isHoley = isHoley;
        addRules(createContactRules());
        addRules(createBagOrganizationRules());
        addRules(createBagInfoRules());
        addRules(createFetchRules());
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
        return new Rules(BagOrganization.class) {
            protected void initRules() {
                add("orgName", getNameValueConstraint());
                add("orgAddress", required());
            }
        };
    }

    private Rules createBagInfoRules() {
        return new Rules(BagInfo.class) {
            protected void initRules() {
                add("bagName", required());
                add("externalDescription", required());
                add("baggingDate", getDateConstraint());
                add("externalIdentifier", required());
                add("bagSize", required());
                if (isCopyright) {
                	add("publisher", required());
                }
//                add("payloadOxum", required() );
//              add("payloadOssum", required() );
            }
        };
    }

    private Rules createFetchRules() {
        return new Rules(Fetch.class) {
            protected void initRules() {
            	if (isHoley) {
                    add("baseURL", required());            		
            	}
            }
        };
    }
    
    private Constraint getNameValueConstraint() {
    	Constraint res;
    	res = all(new Constraint[] {required(), maxLength(50), regexp("[a-zA-Z ]*", "alphabetic")});
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

    private Constraint getIsCopyright() {
    	Constraint res;
    	// TODO: RequiredIfTrue does not work.  This class needs to be rewritten without Spring RC
    	RequiredIfTrue isPublisherReq = new RequiredIfTrue("publisher", eq("isCopyright", true));
        res = isPublisherReq.getConstraint();
        return res;
    }
/* */
    private Rules createRules() {
        return new Rules(BagOrganization.class) {
            protected void initRules() {
                add("orgName", required());
                add("name", getNameValueConstraint());
                add("birthDate", required());
                add("birthDate", lt(new Date()));
//              add(not(eqProperty("firstName", "lastName")));
            }
        };
    }
/* */
}

