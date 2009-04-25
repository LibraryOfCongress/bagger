
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;

public class OrganizationInfoForm extends AbstractForm implements PropertyChangeListener {
    public static final String INFO_FORM_PAGE = "infoPage";

    private JComponent infoForm;
    private Dimension dimension = new Dimension(400, 370);
    private BagView bagView;
    private boolean enabled;

    public OrganizationInfoForm(FormModel formModel, BagView bagView, boolean enabled) {
        super(formModel, INFO_FORM_PAGE);
        this.bagView = bagView;
        this.enabled = enabled;
    }

    protected JComponent createFormControl() {
    	int rowCount = 0;
        BagTableFormBuilder formBuilder = new BagTableFormBuilder(getBindingFactory());

        formBuilder.row();
        JComponent nameField = formBuilder.add("bagName")[1];
        JTextField nameTextField = (JTextField) nameField;
        nameTextField.setEnabled(false);
        int fieldHeight = nameTextField.getFontMetrics(nameTextField.getFont()).getHeight();
        formBuilder.row();
        rowCount++;
        JComponent extDesc = formBuilder.addTextArea("externalDescription")[1];
        extDesc.setEnabled(enabled);
/* */ 
		((NoTabTextArea) extDesc).setBorder(new EmptyBorder(1,1,1,1));
		((NoTabTextArea) extDesc).setColumns(1);
		((NoTabTextArea) extDesc).setRows(3);
		((NoTabTextArea) extDesc).setLineWrap(true);
/* */ 
        formBuilder.row();
        rowCount++;
        rowCount++;
        JComponent baggingDate = formBuilder.add("baggingDate")[1];
        baggingDate.setEnabled(enabled);
        formBuilder.row();
        rowCount++;
        JComponent externalIdentifier = formBuilder.add("externalIdentifier")[1];
        externalIdentifier.setEnabled(enabled);
        formBuilder.row();
        rowCount++;
        JComponent bagSize = formBuilder.add("bagSize")[1];
        bagSize.setEnabled(enabled);
        formBuilder.row();
        rowCount++;
        JComponent oxumField = formBuilder.add("payloadOxum")[1];
        JTextField oxumTextField = (JTextField) oxumField;
        oxumTextField.setEnabled(false);
        formBuilder.row();
        rowCount++;
        JComponent bagGroupIdentifier = formBuilder.add("bagGroupIdentifier")[1];
        bagGroupIdentifier.setEnabled(enabled);
        formBuilder.row();
        rowCount++;
        JComponent bagCount = formBuilder.add("bagCount")[1];
        bagCount.setEnabled(enabled);
        formBuilder.row();
        rowCount++;
        JComponent internalSenderIdentifier = formBuilder.add("internalSenderIdentifier")[1];
        internalSenderIdentifier.setEnabled(enabled);
        formBuilder.row();
        rowCount++;
        JComponent senderDesc = formBuilder.addTextArea("internalSenderDescription")[1];
        senderDesc.setEnabled(enabled);
/* */
		((NoTabTextArea) senderDesc).setColumns(1);
		((NoTabTextArea) senderDesc).setRows(3);
		((NoTabTextArea) senderDesc).setLineWrap(true);
/* */
        formBuilder.row();
        rowCount++;
        rowCount++;
        if (this.bagView.getBag().getIsEdeposit()) {
            JComponent publisher = formBuilder.add("publisher")[1];
            publisher.setEnabled(enabled);
        	formBuilder.row();
            rowCount++;
        }
        if (this.bagView.getBag().getIsNdnp()) {
            JComponent awardeePhase = formBuilder.add("awardeePhase")[1];
            awardeePhase.setEnabled(enabled);
            formBuilder.row();
            rowCount++;
        }
        infoForm = formBuilder.getForm();
        int height = 2 * fieldHeight * rowCount;
        dimension = new Dimension(400, height);
        infoForm.setPreferredSize(dimension);
        return infoForm;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("FF-pce: prop=" + evt.getPropertyName() + ", evt=" + evt);
        if (bagView != null && !this.hasErrors()) bagView.updatePropButton.setEnabled(true);
    }

    public boolean requestFocusInWindow() {
        return infoForm.requestFocusInWindow();
    }

}