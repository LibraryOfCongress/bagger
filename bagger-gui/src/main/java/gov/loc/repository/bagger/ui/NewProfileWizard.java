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

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.bag.BagOrganization;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.form.CompoundForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.wizard.AbstractWizard;
import org.springframework.richclient.wizard.FormBackedWizardPage;
import org.springframework.richclient.wizard.WizardDialog;
import org.springframework.util.Assert;

public class NewProfileWizard extends AbstractWizard implements ActionCommandExecutor {
    private WizardDialog wizardDialog;

    private CompoundForm wizardForm;
    
    private Contact contact;

    public NewProfileWizard() {
        super("newOrganizationWizard");
    }

    public void addPages() {
        addPage(new FormBackedWizardPage(new OrganizationGeneralForm(FormModelHelper.createChildPageFormModel(wizardForm.getFormModel()))));
        addPage(new FormBackedWizardPage(new OrganizationContactForm(FormModelHelper.createChildPageFormModel(wizardForm.getFormModel()))));
    }

    public void execute() {
        if (wizardDialog == null) {
            wizardForm = new CompoundForm();
            wizardForm.setFormObject(new BagOrganization());
            wizardDialog = new WizardDialog(this);
        }
        wizardForm.setFormObject(new BagOrganization());
        wizardDialog.showDialog();
    }

    protected boolean onFinish() {
        BagOrganization newOrganization = getNewOrganization();
        getApplicationContext()
                .publishEvent(new LifecycleApplicationEvent(LifecycleApplicationEvent.CREATED, newOrganization));
        return true;
    }

    private BagOrganization getNewOrganization() {
        wizardForm.commit();
        return (BagOrganization)wizardForm.getFormObject();
    }

}