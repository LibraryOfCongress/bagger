
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationProfileForm extends AbstractForm implements PropertyChangeListener, FocusListener {
    public static final String PROFILE_FORM_PAGE = "profilePage";

    private JComponent form;
    private JComponent contactName;
    private JComponent field;
    private BagView bagView;
    private Dimension dimension = new Dimension(400, 370);

    public OrganizationProfileForm(FormModel formModel, BagView bagView) {
        super(formModel, PROFILE_FORM_PAGE);
        this.bagView = bagView;
    }

    protected JComponent createFormControl() {
        BagTableFormBuilder formBuilder = new BagTableFormBuilder(getBindingFactory());
        JTextField nameTextField = new JTextField();
        int fieldHeight = nameTextField.getFontMetrics(nameTextField.getFont()).getHeight();
        int rowCount = 0;

        formBuilder.row();
        rowCount++;
        JComponent orgLabel = formBuilder.addLabel("Send from Organization")[0];
        formBuilder.row();
        rowCount++;
        this.field = formBuilder.add("sourceOrganization")[1];
        this.field.addFocusListener(this);
        formBuilder.row();
        rowCount++;
        JComponent orgAddress = formBuilder.add("organizationAddress")[1];
        orgAddress.addFocusListener(this);

        formBuilder.row();
        rowCount++;
        JComponent contactLabel = formBuilder.addLabel("Send from Contact")[0];
        formBuilder.row();
        rowCount++;
        this.contactName = formBuilder.add("srcContactName")[1];
        this.contactName.addFocusListener(this);
        formBuilder.row();
        rowCount++;
        this.field = formBuilder.add("srcContactPhone")[1];
        this.field.addFocusListener(this);
        formBuilder.row();
        rowCount++;
        this.field = formBuilder.add("srcContactEmail")[1];
        this.field.addFocusListener(this);

        formBuilder.row();
        rowCount++;
        contactLabel = formBuilder.addLabel("Send to Contact")[0];
        formBuilder.row();
        rowCount++;
        this.field = formBuilder.add("toContactName")[1];
        this.field.addFocusListener(this);
        formBuilder.row();
        rowCount++;
        this.field = formBuilder.add("toContactPhone")[1];
        this.field.addFocusListener(this);
        formBuilder.row();
        rowCount++;
        this.field = formBuilder.add("toContactEmail")[1];
        this.field.addFocusListener(this);
        formBuilder.row();
        rowCount++;

        this.contactName.requestFocus();
        form = formBuilder.getForm();
        int height = 2 * fieldHeight * rowCount;
        dimension = new Dimension(400, height);
        form.setPreferredSize(dimension);

        return form;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (bagView != null && !this.hasErrors()) {
        	bagView.updatePropButton.setEnabled(true);
        }
    }

    public boolean requestFocusInWindow() {
        return contactName.requestFocusInWindow();
    }

    public void focusGained(FocusEvent evt) {
    }

    public void focusLost(FocusEvent evt) {
    	if (bagView != null && !this.hasErrors() && this.isDirty()) {
        	bagView.infoInputPane.updateBagHandler.updateBag(bagView.getBag());
        	//bagView.showWarningErrorDialog("Profile form was updated to prevent data loss.");
    	}
    }
}