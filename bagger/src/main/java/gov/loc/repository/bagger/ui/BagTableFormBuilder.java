package gov.loc.repository.bagger.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class BagTableFormBuilder extends TableFormBuilder {
  public BagTableFormBuilder(BindingFactory bindingFactory) {
    super(bindingFactory);
  }

  private ComponentFactory componentFactory;

  @Override
  protected ComponentFactory getComponentFactory() {
    if (componentFactory == null) {
      componentFactory = (ComponentFactory) ApplicationServicesLocator.services().getService(ComponentFactory.class);
    }
    return componentFactory;
  }

  public JComponent[] add(boolean isRequired, String label, JComponent checkbox) {
    JComponent textField = new JTextField();
    return addBinding(isRequired, label, textField, checkbox);
  }

  public JComponent[] addList(boolean isRequired, String label, Collection<String> elements, String defaultValue,
                              JComponent checkbox) {
    ArrayList<String> listModel = new ArrayList<>();
    for (Iterator<String> iter = elements.iterator(); iter.hasNext();) {
      String item = iter.next();
      listModel.add(item);
    }

    // Set default value selected from value list
    JComboBox<String> dropDownTextField = new JComboBox<>(listModel.toArray(new String[listModel.size()]));
    dropDownTextField.setSelectedItem(defaultValue);
    Object obj = dropDownTextField.getSelectedItem();
    dropDownTextField.setSelectedItem(obj);
    JComponent list = dropDownTextField;

    return addBinding(isRequired, label, list, checkbox);
  }

  public JComponent[] addTextArea(boolean isRequired, String label, JComponent checkbox) {
    JComponent textArea = new NoTabTextArea(3, 40);
    // Binding binding = createBinding(fieldName, textArea);
    // TODO: using the JScrollPane component causes the validation 'x' to
    // disappear
    // JComponent wrappedComponent = new JScrollPane(textArea)
    return addBinding(isRequired, label, textArea, checkbox);
  }

  public JComponent[] addLabel(String labelName) {
    JLabel label = new JLabel(labelName);
    TableLayoutBuilder layoutBuilder = getLayoutBuilder();
    layoutBuilder.cell(label, "");
    layoutBuilder.labelGapCol();
    return new JComponent[] { label };
  }

  public JComponent[] addBinding(boolean isRequired, String labelName, JComponent component, JComponent removeButton) {
    removeButton.setFocusable(false);
    JLabel label = new JLabel(labelName); // createLabelFor(fieldName,
                                          // component);
    label.setToolTipText("Double-Click to Edit");
    TableLayoutBuilder layoutBuilder = getLayoutBuilder();
    if (!layoutBuilder.hasGapToLeft()) {
      layoutBuilder.gapCol();
    }
    layoutBuilder.cell(label, "colSpec=left:pref:noGrow");
    JComponent reqComp;
    /* */
    if (isRequired) {
      JButton b = new JButton("R");
      b.setForeground(Color.red);
      b.setOpaque(false);
      b.setBorderPainted(false);
      reqComp = b;
    }
    else {
      JButton b = new JButton("");
      b.setOpaque(false);
      b.setBorderPainted(false);
      reqComp = b;
    }
    /* */
    reqComp.setFocusable(false);
    layoutBuilder.cell(reqComp, "colSpec=left:pref:noGrow");
    layoutBuilder.cell(component, "colSpec=fill:pref:grow");
    layoutBuilder.labelGapCol();
    layoutBuilder.cell(removeButton, "colSpec=left:pref:noGrow");
    layoutBuilder.labelGapCol();
    return new JComponent[] { label, reqComp, component, removeButton };
  }
}