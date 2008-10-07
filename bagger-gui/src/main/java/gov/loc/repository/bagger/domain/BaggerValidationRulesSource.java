/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.loc.repository.bagger.domain;

import java.util.Date;

import gov.loc.repository.bagger.bag.BagInfo;
import gov.loc.repository.bagger.bag.BagOrganization;
import gov.loc.repository.bagger.Contact;

import org.springframework.core.closure.Constraint;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

public class BaggerValidationRulesSource extends DefaultRulesSource {

    public BaggerValidationRulesSource() {
        super();
        addRules(createContactRules());
        addRules(createBagOrganizationRules());
        addRules(createBagInfoRules());
    }

    private Rules createContactRules() {
        return new Rules(Contact.class) {
            protected void initRules() {
                add("contactName", getNameValueConstraint());
                add("telephone", required());
                add("email", getEmailConstraint());
//              add(not(eqProperty("firstName", "lastName")));
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
                add("baggingDate", required());
            }
        };
    }
/* */
    private Rules createRules() {
        return new Rules(BagOrganization.class) {
            protected void initRules() {
                add("orgName", required());
                add("name", getNameValueConstraint());
                add("birthDate", required());
                add("birthDate", lt(new Date()));
            }
        };
    }
/* */
    private Constraint getNameValueConstraint() {
        return all(new Constraint[] {required(), maxLength(50), regexp("[a-zA-Z]*", "alphabetic")});
    }
    
    private Constraint getEmailConstraint() {
    	return all(new Constraint[] {required(), maxLength(50), regexp("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}")});
    }
}
