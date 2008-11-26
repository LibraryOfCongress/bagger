
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;
import java.awt.Dimension;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationInfoForm extends AbstractForm {
    public static final String INFO_FORM_PAGE = "infoPage";

    private JComponent infoField;
    private Dimension dimension = new Dimension(400, 400);

    public OrganizationInfoForm(FormModel formModel) {
        super(formModel, INFO_FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        this.infoField = formBuilder.add("bagName")[1];
        formBuilder.row();
        formBuilder.add("externalDescription");
        formBuilder.row();
        formBuilder.add("baggingDate");
        formBuilder.row();
        formBuilder.add("externalIdentifier");
        formBuilder.row();
        formBuilder.add("bagSize");
        formBuilder.row();
        formBuilder.add("payloadOxum");
        formBuilder.row();
        formBuilder.add("bagGroupIdentifier");
        formBuilder.row();
        formBuilder.add("bagCount");
        formBuilder.row();
        formBuilder.add("internalSenderIdentifier");
        formBuilder.row();
        formBuilder.add("internalSenderDescription");
        formBuilder.row();
        formBuilder.add("publisher");
        formBuilder.row();
        formBuilder.add("awardeePhase");
        infoField = formBuilder.getForm();
        infoField.setPreferredSize(dimension);
        return infoField;
    }

    public boolean requestFocusInWindow() {
        return infoField.requestFocusInWindow();
    }

}