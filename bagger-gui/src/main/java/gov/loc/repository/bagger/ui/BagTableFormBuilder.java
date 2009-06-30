
package gov.loc.repository.bagger.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.BindingFactory;
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

    public JComponent[] addTextArea(String fieldName, boolean isRequired, String label, JComponent checkbox, String attributes) {
//        JComponent textArea = createTextArea(fieldName);
    	JComponent textArea = new NoTabTextArea(5, 40);
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

    public JComponent[] addBinding(String fieldName, boolean isRequired, String labelName, JComponent component, JComponent wrappedComponent, JComponent checkbox, String attributes, String labelAttributes) {
    	checkbox.setFocusable(false);
    	JLabel label = new JLabel(labelName); //createLabelFor(fieldName, component);
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
        	reqComp = new JLabel();
        }
/* */
        reqComp.setFocusable(false);
    	layoutBuilder.cell(reqComp, "colSpec=left:pref:noGrow");
        //layoutBuilder.labelGapCol();
        layoutBuilder.cell(component, "colSpec=fill:pref:grow");
        layoutBuilder.labelGapCol();
        layoutBuilder.cell(checkbox, "colSpec=left:pref:noGrow");
        layoutBuilder.labelGapCol();
        return new JComponent[] { label, reqComp, component, checkbox };
    }
}