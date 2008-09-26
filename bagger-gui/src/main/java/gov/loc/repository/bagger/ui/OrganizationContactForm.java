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
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationContactForm extends AbstractForm {
    public static final String CONTACT_FORM_PAGE = "contactPage";

    private JComponent contact;

    public OrganizationContactForm(FormModel formModel) {
        super(formModel, CONTACT_FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        this.contact = formBuilder.add("contactName")[1];
        formBuilder.row();
        formBuilder.add("telephone");
        formBuilder.row();
        formBuilder.add("email");
        return formBuilder.getForm();
    }

    public boolean requestFocusInWindow() {
        return contact.requestFocusInWindow();
    }
}
