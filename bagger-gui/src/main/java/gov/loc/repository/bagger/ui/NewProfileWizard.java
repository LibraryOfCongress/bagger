
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.bag.BaggerProfile;

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
    private OrganizationProfileForm profileForm;    
    private BagView bagView = null;

    public NewProfileWizard() {
        super("newOrganizationWizard");
    }
    
    public void setBagView(BagView bagView) {
    	this.bagView = bagView;
    }

    public void addPages() {
        HierarchicalFormModel profileModel;
        profileModel = FormModelHelper.createCompoundFormModel(new BaggerProfile());
        profileForm = new OrganizationProfileForm(FormModelHelper.createChildPageFormModel(profileModel, null), bagView);
        addPage(new FormBackedWizardPage(profileForm));
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
        if (!profileForm.hasErrors()) {
            profileForm.commit();
        }
        BaggerProfile baggerProfile = (BaggerProfile)profileForm.getFormObject();
        profile.setSendFromContact(baggerProfile.getSourceContact());
        profile.setSendToContact(baggerProfile.getToContact());
        return profile;
    }

}