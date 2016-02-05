package gov.loc.repository.bagger.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;

import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.util.LayoutUtil;

public class BagInfoForm extends AbstractForm implements FocusListener {
  protected static final Logger log = LoggerFactory.getLogger(BagInfoForm.class);

  public static final String INFO_FORM_PAGE = "infoPage";

  private JComponent focusField;
  private BagView bagView;
  private HashMap<String, BagInfoField> fieldMap;
  private JComponent form;
  private AddFieldPanel addFieldPannel;

  public BagInfoForm(FormModel formModel, BagView bagView, HashMap<String, BagInfoField> map) {
    super(formModel, INFO_FORM_PAGE);
    this.bagView = bagView;
    this.fieldMap = map;
  }

  public void setBagView(BagView bagView) {
    this.bagView = bagView;
  }

  @Override
  protected JComponent createFormControl() {
    // add field panel
    JPanel contentPanel = new JPanel(new GridBagLayout());
    int row = 0;
    int col = 0;
    GridBagConstraints gbc = LayoutUtil.buildGridBagConstraints(col, row++, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
    addFieldPannel = new AddFieldPanel();
    contentPanel.add(addFieldPannel, gbc);

    gbc = LayoutUtil.buildGridBagConstraints(col, row++, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
    contentPanel.add(new JSeparator(), gbc);

    // bag-info input form
    form = createFormFields();
    gbc = LayoutUtil.buildGridBagConstraints(col, row++, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.WEST);
    contentPanel.add(form, gbc);
    return contentPanel;
  }

  protected JComponent createFormFields() {
    BagTableFormBuilder formBuilder = new BagTableFormBuilder(getBindingFactory());
    formBuilder.row();
    if (fieldMap != null && !fieldMap.isEmpty()) {
      createFormFieldsFromMap(formBuilder);//TODO
    }
    JComponent fieldForm = formBuilder.getForm();
    fieldForm.invalidate();
    return fieldForm;
  }
  
  protected void createFormFieldsFromMap(BagTableFormBuilder formBuilder){
    int rowCount = 0;
    int index = 2;
    
    Set<String> keys = fieldMap.keySet();
    if (keys != null) {
      for(BagInfoField field : fieldMap.values()){
        formBuilder.row();
        rowCount++;
        ImageIcon imageIcon = bagView.getPropertyImage("bag.delete.image");
        JButton removeButton = new JButton(imageIcon);
        Dimension dimension = removeButton.getPreferredSize();
        dimension.width = imageIcon.getIconWidth();
        removeButton.setMaximumSize(dimension);
        removeButton.setOpaque(false);
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.addActionListener(new RemoveFieldHandler());
        logger.debug("OrganizationInfoForm add: " + field);
        if (field.getValue() != null && field.getValue().length() > 30) {
          field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
        }
        if (field.isRequired()) {
          removeButton = new JButton();
          removeButton.setOpaque(false);
          removeButton.setBorderPainted(false);
          removeButton.setContentAreaFilled(false);
        }
        switch (field.getComponentType()) {
        case BagInfoField.TEXTAREA_COMPONENT:
          JComponent[] tlist = formBuilder.addTextArea(field.isRequired(), field.getLabel(), removeButton);
          JComponent textarea = tlist[index];
          textarea.setEnabled(field.isEnabled());
          textarea.addFocusListener(this);
          ((NoTabTextArea) textarea).setText(field.getValue());
          ((NoTabTextArea) textarea).setBorder(new EmptyBorder(1, 1, 1, 1));
          ((NoTabTextArea) textarea).setLineWrap(true);
          if (rowCount == 1)
            focusField = textarea;
          break;
        case BagInfoField.TEXTFIELD_COMPONENT:
          JComponent[] flist = formBuilder.add(field.isRequired(), field.getLabel(), removeButton);
          JComponent comp = flist[index];
          comp.setEnabled(field.isEnabled());
          comp.addFocusListener(this);
          ((JTextField) comp).setText(field.getValue());
          if (rowCount == 1)
            focusField = comp;
          break;
        case BagInfoField.LIST_COMPONENT:
          List<String> elements = field.getElements();
          JComponent[] llist = formBuilder.addList(field.isRequired(), field.getLabel(), elements, field.getValue(), removeButton);
          JComponent lcomp = llist[index];
          lcomp.setEnabled(field.isEnabled());
          lcomp.addFocusListener(this);
          if (field.getValue() != null) {
            ((JComboBox<?>) lcomp).setSelectedItem(field.getValue().trim());
          }
          if (rowCount == 1)
            focusField = lcomp;
          break;
        default:
        }
      }
      if (focusField != null) {
        focusField.requestFocus();
      }
    }
  
  }

  @Override
  public void focusGained(FocusEvent evt) {
  }

  @Override
  public void focusLost(FocusEvent evt) {
    DefaultBag defaultBag = bagView.getBag();
    bagView.infoInputPane.updateBagHandler.updateBag(defaultBag);
    bagView.infoInputPane.bagInfoInputPane.setSelectedIndex(0);
  }

  private class RemoveFieldHandler extends AbstractAction {
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        Component selected = (Component) e.getSource();
        String key = "";
        Component[] components = getFieldComponents();
        for (int i = 0; i < components.length; i++) {
          Component c;
          // See BagTableFormBuilder.addBinding for component info
          // Field label
          c = components[i];
          if (c instanceof JLabel) {
            JLabel label = (JLabel) c;
            key = label.getText();
          }
          i++;
          // Required button
          c = components[i];
          i++;
          // Input text field
          c = components[i];
          i++;
          // Remove button
          c = components[i];
          if (c instanceof JButton) {
            if (c == selected) {
              BagInfoField field = getField(key);
              if (field != null) {
                // remove field
                bagView.getBag().removeBagInfoField(key);
              }
            }
          }
        }
        bagView.infoInputPane.updateInfoFormsPane();
      }
      catch (Exception ex) {
      }
    }
  }

  private BagInfoField getField(String key) {
    BagInfoField field = null;
    Set<String> keys = fieldMap.keySet();
    if (keys != null) {
      for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
        String keySet = iter.next();
        if (keySet.equalsIgnoreCase(key)) {
          field = fieldMap.get(key);
          return field;
        }
      }
    }
    return field;
  }

  public HashMap<String, String> getBagInfoMap() {
    HashMap<String, String> map = new HashMap<String, String>();
    String key = "";
    String value = "";
    java.awt.Component[] components = getFieldComponents();
    for (int i = 0; i < components.length; i++) {
      java.awt.Component c;
      c = components[i];
      if (c instanceof JLabel) {
        JLabel label = (JLabel) c;
        key = label.getText();
      }
      i++;
      // Is required component
      c = components[i];
      i++;
      c = components[i];
      if (c instanceof JTextField) {
        JTextField tf = (JTextField) c;
        value = tf.getText();
      }
      else if (c instanceof JTextArea) {
        JTextArea ta = (JTextArea) c;
        value = ta.getText();
      }
      else if (c instanceof JComboBox) {
        JComboBox<?> tb = (JComboBox<?>) c;
        value = (String) tb.getSelectedItem();
      }
      map.put(key, value);
      i++;
      c = components[i];
    }
    return map;
  }

  private Component[] getFieldComponents() {
    return form.getComponents();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    addFieldPannel.setEnabled(enabled);
  }

}
