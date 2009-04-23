
package gov.loc.repository.bagger.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationContactForm extends AbstractForm implements PropertyChangeListener {
    public static final String CONTACT_FORM_PAGE = "contactPage";

    private JComponent contact;
    private BagView bagView;

    public OrganizationContactForm(FormModel formModel, BagView bagView) {
        super(formModel, CONTACT_FORM_PAGE);
        this.bagView = bagView;
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());

        formBuilder.row();
        this.contact = formBuilder.add("contactName")[1];
        formBuilder.row();
        formBuilder.add("telephone");
        formBuilder.row();
        formBuilder.add("email");
        return formBuilder.getForm();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("FF-pce: prop=" + evt.getPropertyName() + ", evt=" + evt);
        if (bagView != null && !this.hasErrors()) bagView.updatePropButton.setEnabled(true);
   }

    public boolean requestFocusInWindow() {
        return contact.requestFocusInWindow();
    }
}
