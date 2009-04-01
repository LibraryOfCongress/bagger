
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationFetchForm extends AbstractForm {
    public static final String FETCH_FORM_PAGE = "fetchFormPage";

    private JComponent baseURL;

    public OrganizationFetchForm(FormModel formModel) {
        super(formModel, FETCH_FORM_PAGE);
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

    public boolean requestFocusInWindow() {
        return baseURL.requestFocusInWindow();
    }

}