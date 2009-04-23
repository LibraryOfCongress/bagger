
package gov.loc.repository.bagger.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationFetchForm extends AbstractForm implements PropertyChangeListener {
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
        formBuilder.add("userName");
        formBuilder.row();
        formBuilder.add("userPassword");
        return formBuilder.getForm();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("FF-pce: prop=" + evt.getPropertyName() + ", evt=" + evt);
        if (bagView != null && !this.hasErrors()) bagView.updatePropButton.setEnabled(true);
    }

    public boolean requestFocusInWindow() {
        return baseURL.requestFocusInWindow();
    }

}