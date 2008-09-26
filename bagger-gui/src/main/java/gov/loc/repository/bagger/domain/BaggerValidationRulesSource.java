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

import org.springframework.core.closure.Constraint;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

public class BaggerValidationRulesSource extends DefaultRulesSource {

    public BaggerValidationRulesSource() {
        super();
//        addRules(createOwnerRules());
  //      addRules(createPetRules());
    }
/*
    private Rules createOwnerRules() {
        return new Rules(Owner.class) {
            protected void initRules() {
                add("firstName", getNameValueConstraint());
                add("lastName", getNameValueConstraint());
                add(not(eqProperty("firstName", "lastName")));
                add("address", required());
            }
        };
    }

    private Rules createPetRules() {
        return new Rules(Pet.class) {
            protected void initRules() {
                add("type", required());
                add("name", getNameValueConstraint());
                add("birthDate", required());
                add("birthDate", lt(new Date()));
            }
        };
    }
*/
    private Constraint getNameValueConstraint() {
        return all(new Constraint[] {required(), maxLength(25), regexp("[a-zA-Z]*", "alphabetic")});
    }
}
