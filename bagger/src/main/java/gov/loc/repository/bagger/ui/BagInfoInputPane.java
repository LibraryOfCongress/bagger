package gov.loc.repository.bagger.ui;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.form.FormModelHelper;

import gov.loc.repository.bagger.bag.BaggerProfile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;

public class BagInfoInputPane extends JTabbedPane {
  private static final long serialVersionUID = 1L;
  protected static final Logger log = LoggerFactory.getLogger(BagInfoInputPane.class);

  private BagView bagView;
  private DefaultBag defaultBag;
  private BaggerProfile bagProfile;
  private BagInfoForm bagInfoForm = null;
  private OrganizationProfileForm profileForm = null;
  private HierarchicalFormModel infoFormModel = null;
  private HierarchicalFormModel profileFormModel = null;

  public BagInfoInputPane(BagView bagView, boolean b) {
    this.bagView = bagView;
    this.defaultBag = bagView.getBag();
    populateForms(defaultBag, b);

    InputMap im = this.getInputMap();
    im.put(KeyStroke.getKeyStroke("F2"), "tabNext");
    ActionMap am = this.getActionMap();
    am.put("tabNext", new AbstractAction("tabNext") {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent evt) {
        try {
          int selected = getSelectedIndex();
          int count = getComponentCount();
          if (selected >= 0 && selected < count - 1) {
            setSelectedIndex(selected + 1);
          }
          else {
            setSelectedIndex(0);
          }
          invalidate();
          repaint();
        }
        catch (Exception e) {
        }
      }
    });
    this.setActionMap(am);
  }

  public void enableForms(boolean b) {
    profileForm.setEnabled(b);
    profileForm.getControl().invalidate();
    bagInfoForm.setEnabled(b);
    bagInfoForm.getControl().invalidate();
    this.setEnabled(b);
    this.invalidate();
  }

  public void populateForms(DefaultBag bag, boolean enabled) {

    defaultBag = bag;
    DefaultBagInfo bagInfo = bag.getInfo();

    if (bagProfile == null) {
      bagProfile = new BaggerProfile();
    }

    bagProfile.setOrganization(bagInfo.getBagOrganization());
    bagProfile.setToContact(bagInfo.getToContact());

    profileFormModel = FormModelHelper.createCompoundFormModel(bagProfile);
    profileForm = new OrganizationProfileForm(FormModelHelper.createChildPageFormModel(profileFormModel, null), bagView);

    infoFormModel = FormModelHelper.createCompoundFormModel(bagInfo);
    bagInfoForm = new BagInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null), bagView, bagInfo.getFieldMap(), enabled);

    createTabbedUiComponentsWithForms();
  }

  // Create a tabbed pane for the information forms and checkbox panel
  private void createTabbedUiComponentsWithForms() {
    removeAll();
    // invalidate();
    validate();
    setName("Profile");
    bagInfoForm.getControl().setToolTipText(bagView.getPropertyMessage("infoinputpane.tab.details.help"));
    addTab(bagView.getPropertyMessage("infoInputPane.tab.details"), bagInfoForm.getControl());
    profileForm.getControl().setToolTipText("Profile Form");
  }

  public String verifyForms(DefaultBag bag) {
    String messages = "";

    if (!profileForm.hasErrors()) {
      profileForm.commit();
    }
    else {
      throw new RuntimeException("Bag-Info has errors");
    }

    if (!bagInfoForm.hasErrors()) {
      bagInfoForm.commit();
    }
    else {
      throw new RuntimeException("Bag-Info has errors");
    }
    updateBagInfo(bag);

    return messages;
  }

  public String updateForms(DefaultBag bag) {
    String messages = "";

    messages = verifyForms(bag);
    createTabbedUiComponentsWithForms();
    update(bag);

    return messages;
  }

  public boolean hasFormErrors(DefaultBag bag) {
    return false;
  }

  public void update(DefaultBag bag) {
    java.awt.Component[] components = bagInfoForm.getControl().getComponents();
    for (int i = 0; i < components.length; i++) {
      java.awt.Component c = components[i];
      c.invalidate();
      c.repaint();
    }
    bagInfoForm.getControl().invalidate();
    profileForm.getControl().invalidate();
    invalidate();
    repaint();
  }

  public void updateProject(BagView bagView) {
    bagView.infoInputPane.updateInfoFormsPane(true);
  }

  private void updateBagInfo(DefaultBag bag) {
    HashMap<String, String> map = bagInfoForm.getBagInfoMap();
    bag.updateBagInfo(map);
  }

  @Override
  public void requestFocus() {
    bagInfoForm.getControl().requestFocus();
  }
}