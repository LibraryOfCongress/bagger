package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;

public class OrganizationProfileForm extends AbstractForm implements FocusListener {
  public static final String PROFILE_FORM_PAGE = "profilePage";

  private JComponent form;
  private JComponent contactName;
  private JComponent field;
  private BagView bagView;

  public OrganizationProfileForm(FormModel formModel, BagView bagView) {
    super(formModel, PROFILE_FORM_PAGE);
    this.bagView = bagView;
  }

  @Override
  protected JComponent createFormControl() {
    form = new JPanel();
    form.setLayout(new BorderLayout());
    JComponent formFields = createFormFields();
    form.add(formFields, BorderLayout.CENTER);

    return form;
  }

  protected JComponent createFormFields() {
    JComponent fieldForm;
    BagTableFormBuilder formBuilder = new BagTableFormBuilder(getBindingFactory());
    formBuilder.row();
    formBuilder.addSeparator("Send from Organization");
    formBuilder.row();
    this.field = formBuilder.add("sourceOrganization")[1];
    Organization organization = bagView.getBag().getProfile().getOrganization();
    if (organization != null && organization.getName().isReadOnly()) {
      field.setEnabled(false);
    }
    this.field.addFocusListener(this);
    formBuilder.row();
    JComponent orgAddress = formBuilder.add("organizationAddress")[1];
    if (organization != null && organization.getAddress().isReadOnly()) {
      field.setEnabled(false);
    }

    orgAddress.addFocusListener(this);

    Contact fromContact = bagView.getBag().getProfile().getSendFromContact();

    formBuilder.row();
    formBuilder.addSeparator("Send from Contact");
    formBuilder.row();

    this.contactName = formBuilder.add("srcContactName")[1];

    if (fromContact != null && fromContact.getContactName().isReadOnly()) {
      field.setEnabled(false);
    }

    this.contactName.addFocusListener(this);
    formBuilder.row();
    this.field = formBuilder.add("srcContactPhone")[1];
    if (fromContact != null && fromContact.getTelephone().isReadOnly()) {
      field.setEnabled(false);
    }
    this.field.addFocusListener(this);
    formBuilder.row();
    this.field = formBuilder.add("srcContactEmail")[1];

    if (fromContact != null && fromContact.getEmail().isReadOnly()) {
      field.setEnabled(false);
    }

    this.field.addFocusListener(this);
    formBuilder.row();
    formBuilder.addSeparator("Send to Contact");
    formBuilder.row();
    this.field = formBuilder.add("toContactName")[1];
    Contact contact = bagView.getBag().getProfile().getSendToContact();
    if (contact != null && contact.getContactName().isReadOnly()) {
      field.setEnabled(false);
    }
    this.field.addFocusListener(this);
    formBuilder.row();
    this.field = formBuilder.add("toContactPhone")[1];

    if (contact != null && contact.getTelephone().isReadOnly()) {
      field.setEnabled(false);
    }
    this.field.addFocusListener(this);
    formBuilder.row();
    this.field = formBuilder.add("toContactEmail")[1];
    if (contact != null && contact.getEmail().isReadOnly()) {
      field.setEnabled(false);
    }
    this.field.addFocusListener(this);
    formBuilder.row();
    this.contactName.requestFocus();
    fieldForm = formBuilder.getForm();
    return fieldForm;
  }

  public boolean requestFocusInWindow() {
    return contactName.requestFocusInWindow();
  }

  @Override
  public void focusGained(FocusEvent evt) {
  }

  @Override
  public void focusLost(FocusEvent evt) {
    if (bagView != null && !this.hasErrors() && this.isDirty()) {
      bagView.infoInputPane.updateBagHandler.updateBag(bagView.getBag());
      bagView.infoInputPane.bagInfoInputPane.setSelectedIndex(1);
    }
  }
}