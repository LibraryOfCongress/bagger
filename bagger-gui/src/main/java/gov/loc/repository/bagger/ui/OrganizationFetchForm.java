
package gov.loc.repository.bagger.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationFetchForm extends AbstractForm implements PropertyChangeListener, FocusListener {
    public static final String FETCH_FORM_PAGE = "fetchFormPage";

    private JComponent baseURL;
    private BagView bagView;

    public OrganizationFetchForm(FormModel formModel, BagView bagView) {
        super(formModel, FETCH_FORM_PAGE);
        this.bagView = bagView;
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());

        formBuilder.row();
        this.baseURL = formBuilder.add("baseURL")[1];
        formBuilder.row();
        JComponent userName = formBuilder.add("userName")[1];
        userName.addFocusListener(this);
        formBuilder.row();
        JComponent userPassword = formBuilder.add("userPassword")[1];
        userPassword.addFocusListener(this);
        this.baseURL.requestFocus();
        return formBuilder.getForm();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (bagView != null && !this.hasErrors()) {
        	bagView.updatePropButton.setEnabled(true);
        }
    }

    public boolean requestFocusInWindow() {
        return baseURL.requestFocusInWindow();
    }

    public void focusGained(FocusEvent evt) {
    }
    
    public void focusLost(FocusEvent evt) {
    	if (bagView != null && !this.hasErrors() && this.isDirty()) {
        	//bagView.infoInputPane.updateBagHandler.updateBag(bagView.getBag());
    	}
    }
}