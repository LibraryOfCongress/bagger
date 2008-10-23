
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
