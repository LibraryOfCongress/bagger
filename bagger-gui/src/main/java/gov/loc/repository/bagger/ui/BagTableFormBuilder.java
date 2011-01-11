
package gov.loc.repository.bagger.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
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
    private static final String VALIGN_TOP = TableLayoutBuilder.VALIGN + "=top";
    ImageIcon requiredIcon;
    
    public BagTableFormBuilder(BindingFactory bindingFactory, ImageIcon requiredIcon) {
    	super(bindingFactory);
    }

	private ComponentFactory componentFactory;

	protected ComponentFactory getComponentFactory() {
		if (componentFactory == null) {
			componentFactory = (ComponentFactory) ApplicationServicesLocator.services().getService(
					ComponentFactory.class);
		}
		return componentFactory;
	}

    public JComponent[] add(String fieldName, boolean isRequired, String label, JComponent checkbox, String attributes) {
    	JComponent textField = new JTextField();
        return addBinding(fieldName, isRequired, label, textField, textField, checkbox, attributes, getLabelAttributes());
    }

    public JComponent[] addList(String fieldName, boolean isRequired, String label, Collection<String> elements, String defaultValue, JComponent checkbox, String attributes) {
    	ArrayList<String> listModel = new ArrayList<String>();
		for (Iterator<String> iter = elements.iterator(); iter.hasNext();) {
			String item = (String) iter.next();
			listModel.add(item);
		}
		
		// Set default value selected from value list
		JComboBox dropDownTextField = new JComboBox(listModel.toArray());
		dropDownTextField.setSelectedItem(defaultValue);
		Object obj = dropDownTextField.getSelectedItem();
		dropDownTextField.setSelectedItem(obj);
		JComponent list = dropDownTextField;
		
        JComponent wrappedComponent = list;
        return addBinding(fieldName, isRequired, label, list, wrappedComponent, checkbox, attributes, getLabelAttributes());
    }

    public JComponent[] addTextArea(String fieldName, boolean isRequired, String label, JComponent checkbox, String attributes) {
    	JComponent textArea = new NoTabTextArea(3, 40);
        String labelAttributes = getLabelAttributes();
        if (labelAttributes == null) {
            labelAttributes = VALIGN_TOP;
        } else if (!labelAttributes.contains(TableLayoutBuilder.VALIGN)) {
            labelAttributes += " " + VALIGN_TOP;
        }
        //Binding binding = createBinding(fieldName, textArea);
        JComponent wrappedComponent = textArea;
        // TODO: using the JScrollPane component causes the validation 'x' to disappear
//        JComponent wrappedComponent = new JScrollPane(textArea)
        return addBinding(fieldName, isRequired, label, textArea, wrappedComponent, checkbox, attributes, labelAttributes);
    }

    public JComponent[] addLabel(String labelName) {
    	JLabel label = new JLabel(labelName); 
        TableLayoutBuilder layoutBuilder = getLayoutBuilder();
        layoutBuilder.cell(label, "");
        layoutBuilder.labelGapCol();
        return new JComponent[] { label };
    }

    public JComponent[] addBinding(String fieldName, boolean isRequired, String labelName, JComponent component, JComponent wrappedComponent, JComponent removeButton, String attributes, String labelAttributes) {
    	removeButton.setFocusable(false);
    	JLabel label = new JLabel(labelName); //createLabelFor(fieldName, component);
    	label.setToolTipText("Double-Click to Edit");
        if (wrappedComponent == null) {
            wrappedComponent = component;
        }
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
        } else {
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