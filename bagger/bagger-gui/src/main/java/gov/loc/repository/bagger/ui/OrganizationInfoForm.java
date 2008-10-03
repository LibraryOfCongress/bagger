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
import java.awt.Dimension;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationInfoForm extends AbstractForm {
    public static final String INFO_FORM_PAGE = "infoPage";

    private JComponent infoField;
    private Dimension dimension = new Dimension(500, 300);

    public OrganizationInfoForm(FormModel formModel) {
        super(formModel, INFO_FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        this.infoField = formBuilder.add("bagName")[1];
        formBuilder.row();
        formBuilder.add("externalDescription");
        formBuilder.row();
        formBuilder.add("baggingDate");
        formBuilder.row();
        formBuilder.add("externalIdentifier");
        formBuilder.row();
        formBuilder.add("bagSize");
        formBuilder.row();
        formBuilder.add("payloadOssum");
        formBuilder.row();
        formBuilder.add("bagGroupIdentifier");
        formBuilder.row();
        formBuilder.add("bagCount");
        formBuilder.row();
        formBuilder.add("internalSenderIdentifier");
        formBuilder.row();
        formBuilder.add("internalSenderDescription");
        formBuilder.row();
        formBuilder.add("publisher");
        infoField = formBuilder.getForm();
        infoField.setPreferredSize(dimension);
        return infoField;
    }

    public boolean requestFocusInWindow() {
        return infoField.requestFocusInWindow();
    }

}