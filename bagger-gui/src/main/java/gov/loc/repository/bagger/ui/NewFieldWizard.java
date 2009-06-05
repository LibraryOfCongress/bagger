
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

public class NewFieldWizard extends AbstractWizard implements ActionCommandExecutor {
    private WizardDialog wizardDialog;

    private CompoundForm wizardForm;
    private OrganizationContactForm userContactForm;
    private BagView bagView = null;

    public NewFieldWizard() {
        super("newFieldWizard");
    }

    public void setBagView(BagView bagView) {
    	this.bagView = bagView;
    }

    public void addPages() {
        HierarchicalFormModel contactFormModel;
        contactFormModel = FormModelHelper.createCompoundFormModel(new Contact());
        userContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(contactFormModel, null), bagView);
        addPage(new FormBackedWizardPage(userContactForm));
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
        getApplicationContext().publishEvent(new LifecycleApplicationEvent(LifecycleApplicationEvent.CREATED, newProfile));
        return true;
    }

    private Profile getNewProfile() {
    	Profile profile = new Profile();

        Organization org = new Organization();
        if (!userContactForm.hasErrors()) {
        	userContactForm.commit();
        }
        Contact newUser = (Contact)userContactForm.getFormObject();
        newUser.setOrganization(org);
        profile.setPerson(newUser);

        return profile;
    }

}