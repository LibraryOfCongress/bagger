
package gov.loc.repository.bagger.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationGeneralForm extends AbstractForm implements PropertyChangeListener, FocusListener {
    public static final String GENERAL_FORM_PAGE = "generalPage";

    private JComponent orgField;
    private BagView bagView;

    public OrganizationGeneralForm(FormModel formModel, BagView bagView) {
        super(formModel, GENERAL_FORM_PAGE);
        this.bagView = bagView;
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());

        formBuilder.row();
        this.orgField = formBuilder.add("orgName")[1];
        this.orgField.addFocusListener(this);
        formBuilder.row();
        JComponent orgAddress = formBuilder.add("orgAddress")[1];
        orgAddress.addFocusListener(this);
        this.orgField.requestFocus();
        return formBuilder.getForm();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (bagView != null && !this.hasErrors()) {
        	bagView.updatePropButton.setEnabled(true);
        }
    }

    public boolean requestFocusInWindow() {
        return orgField.requestFocusInWindow();
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