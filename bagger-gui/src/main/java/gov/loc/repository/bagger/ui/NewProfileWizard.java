
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.bag.BaggerOrganization;

import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.command.ActionCommandExecutor;
import org.springframework.richclient.form.CompoundForm;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.richclient.wizard.AbstractWizard;
import org.springframework.richclient.wizard.FormBackedWizardPage;
import org.springframework.richclient.wizard.WizardDialog;

public class NewProfileWizard extends AbstractWizard implements ActionCommandExecutor {
    private WizardDialog wizardDialog;

    private CompoundForm wizardForm;
    private OrganizationContactForm userContactForm;    
    private OrganizationGeneralForm organizationGeneralForm;
    private OrganizationContactForm organizationContactForm;    

    public NewProfileWizard() {
        super("newOrganizationWizard");
    }

    public void addPages() {
        HierarchicalFormModel contactFormModel;
        contactFormModel = FormModelHelper.createCompoundFormModel(new Contact());
        userContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(contactFormModel, null));
        addPage(new FormBackedWizardPage(userContactForm));

    	HierarchicalFormModel organizationFormModel;
        organizationFormModel = FormModelHelper.createCompoundFormModel(new BaggerOrganization());
        organizationGeneralForm = new OrganizationGeneralForm(FormModelHelper.createChildPageFormModel(organizationFormModel, null));
        addPage(new FormBackedWizardPage(organizationGeneralForm));

        organizationContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(contactFormModel, null));
        addPage(new FormBackedWizardPage(organizationContactForm));
    }

    public void execute() {
        if (wizardDialog == null) {
            wizardForm = new CompoundForm();
            wizardForm.setFormObject(new Profile());
            wizardDialog = new WizardDialog(this);
        }
        wizardForm.setFormObject(new Profile());
        wizardDialog.showDialog();
    }

    protected boolean onFinish() {
        Profile newProfile = getNewProfile();
        getApplicationContext()
                .publishEvent(new LifecycleApplicationEvent(LifecycleApplicationEvent.CREATED, newProfile));
        return true;
    }

    private Profile getNewProfile() {
    	Profile profile = new Profile();
        if (!organizationGeneralForm.hasErrors()) {
            organizationGeneralForm.commit();
        }
        BaggerOrganization newOrganization = (BaggerOrganization)organizationGeneralForm.getFormObject();
        Organization org = new Organization();
        org.setName(newOrganization.getOrgName());
        org.setAddress(newOrganization.getOrgAddress());

        if (!userContactForm.hasErrors()) {
        	userContactForm.commit();
        }
        Contact newUser = (Contact)userContactForm.getFormObject();
        newUser.setOrganization(org);
        profile.setPerson(newUser);

        if (!organizationContactForm.hasErrors()) {
        	organizationContactForm.commit();
        }
        Contact newContact = (Contact)organizationContactForm.getFormObject();
        profile.setContact(newContact);
        return profile;
    }

}