
package gov.loc.repository.bagger.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationGeneralForm extends AbstractForm implements PropertyChangeListener {
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
        formBuilder.row();
        formBuilder.add("orgAddress");
        return formBuilder.getForm();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("FF-pce: prop=" + evt.getPropertyName() + ", evt=" + evt);
        if (bagView != null && !this.hasErrors()) bagView.updatePropButton.setEnabled(true);
    }

    public boolean requestFocusInWindow() {
        return orgField.requestFocusInWindow();
    }

}