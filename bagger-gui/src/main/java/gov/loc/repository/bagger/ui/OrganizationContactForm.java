
package gov.loc.repository.bagger.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationContactForm extends AbstractForm implements PropertyChangeListener, FocusListener {
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
        this.contact.addFocusListener(this);
        formBuilder.row();
        JComponent telephoneField = formBuilder.add("telephone")[1];
        telephoneField.addFocusListener(this);
        formBuilder.row();
        JComponent emailField = formBuilder.add("email")[1];
        emailField.addFocusListener(this);
        this.contact.requestFocus();
        return formBuilder.getForm();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (bagView != null && !this.hasErrors()) {
        	bagView.updatePropButton.setEnabled(true);
        }
   }

    public boolean requestFocusInWindow() {
        return contact.requestFocusInWindow();
    }

    public void focusGained(FocusEvent evt) {
    }
    
    public void focusLost(FocusEvent evt) {
    	if (bagView != null && !this.hasErrors() && this.isDirty()) {
    		// TODO: Activate for Bagger 1.6
    		//bagView.updateBagInfo();
    	}
    }
}
