
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationGeneralForm extends AbstractForm {
    public static final String GENERAL_FORM_PAGE = "generalPage";

    private JComponent orgField;

    public OrganizationGeneralForm(FormModel formModel) {
        super(formModel, GENERAL_FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());

        formBuilder.row();
        this.orgField = formBuilder.add("orgName")[1];
        formBuilder.row();
        formBuilder.add("orgAddress");
        return formBuilder.getForm();
    }

    public boolean requestFocusInWindow() {
        return orgField.requestFocusInWindow();
    }

}