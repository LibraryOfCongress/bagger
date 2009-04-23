
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;
import javax.swing.JTextField;
//import javax.swing.JTextArea;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

public class OrganizationInfoForm extends AbstractForm implements PropertyChangeListener {
    public static final String INFO_FORM_PAGE = "infoPage";

    private JComponent infoForm;
    private Dimension dimension = new Dimension(400, 370);
    private BagView bagView;

    public OrganizationInfoForm(FormModel formModel, BagView bagView) {
        super(formModel, INFO_FORM_PAGE);
        this.bagView = bagView;
    }

    protected JComponent createFormControl() {
    	int rowCount = 0;
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());

        formBuilder.row();
        JComponent nameField = formBuilder.add("bagName")[1];
        JTextField nameTextField = (JTextField) nameField;
        nameTextField.setEnabled(false);
        int fieldHeight = nameTextField.getFontMetrics(nameTextField.getFont()).getHeight();
        formBuilder.row();
        rowCount++;
/* */ 
        JComponent extDesc = formBuilder.addTextArea("externalDescription")[1];
		((javax.swing.JTextArea) extDesc).setColumns(1);
		((javax.swing.JTextArea) extDesc).setRows(3);
		((javax.swing.JTextArea) extDesc).setLineWrap(true);
/* */ 
/*
        JComponent extDesc = formBuilder.add("externalDescription")[1];
        JTextField extDescTextField = (JTextField) extDesc;
        extDescTextField.setPreferredSize(new Dimension(400, 3*fieldHeight));
*/
        formBuilder.row();
        rowCount++;
        rowCount++;
        formBuilder.add("baggingDate");
        formBuilder.row();
        rowCount++;
        formBuilder.add("externalIdentifier");
        formBuilder.row();
        rowCount++;
        formBuilder.add("bagSize");
        formBuilder.row();
        rowCount++;
        JComponent oxumField = formBuilder.add("payloadOxum")[1];
        JTextField oxumTextField = (JTextField) oxumField;
        oxumTextField.setEnabled(false);
        formBuilder.row();
        rowCount++;
        formBuilder.add("bagGroupIdentifier");
        formBuilder.row();
        rowCount++;
        formBuilder.add("bagCount");
        formBuilder.row();
        rowCount++;
        formBuilder.add("internalSenderIdentifier");
        formBuilder.row();
        rowCount++;
//        formBuilder.add("internalSenderDescription");
/* */
        JComponent senderDesc = formBuilder.addTextArea("internalSenderDescription")[1];
		((javax.swing.JTextArea) senderDesc).setColumns(1);
		((javax.swing.JTextArea) senderDesc).setRows(3);
		((javax.swing.JTextArea) senderDesc).setLineWrap(true);
/* */
        formBuilder.row();
        rowCount++;
        rowCount++;
        if (this.bagView.getBag().getIsEdeposit()) {
        	formBuilder.add("publisher");
        	formBuilder.row();
            rowCount++;
        }
        if (this.bagView.getBag().getIsNdnp()) {
            formBuilder.add("awardeePhase");        	
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