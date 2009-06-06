
package gov.loc.repository.bagger.ui;

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

    public BagTableFormBuilder(BindingFactory bindingFactory) {
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

    public JComponent[] add(String fieldName, String label, String attributes) {
    	JComponent textField = new JTextField();
        //Binding binding = createBinding(fieldName, textField);
        return addBinding(fieldName, label, textField, textField, attributes, getLabelAttributes());
    }

    public JComponent[] addTextArea(String fieldName, String label, String attributes) {
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
        return addBinding(fieldName, label, textArea, wrappedComponent, attributes, labelAttributes);
    }

    public JComponent[] addLabel(String labelName) {
    	JLabel label = new JLabel(labelName); 
        TableLayoutBuilder layoutBuilder = getLayoutBuilder();
        layoutBuilder.cell(label, "");
        layoutBuilder.labelGapCol();
        return new JComponent[] { label };
    }

    public JComponent[] addBinding(String fieldName, String labelName, JComponent component, JComponent wrappedComponent, String attributes, String labelAttributes) {
//        JComponent component = binding.getControl();
//        JLabel label = createLabelFor(binding.getProperty(), component);
    	JCheckBox checkbox = new JCheckBox();
    	checkbox.setFocusable(false);
    	JLabel label = new JLabel(labelName); //createLabelFor(fieldName, component);
        if (wrappedComponent == null) {
            wrappedComponent = component;
        }
        TableLayoutBuilder layoutBuilder = getLayoutBuilder();
        if (!layoutBuilder.hasGapToLeft()) {
            layoutBuilder.gapCol();
        }
        layoutBuilder.cell(label, labelAttributes);
        layoutBuilder.labelGapCol();
        layoutBuilder.cell(wrappedComponent, attributes);
        layoutBuilder.labelGapCol();
        layoutBuilder.cell(checkbox, attributes);
        return new JComponent[] { label, component, checkbox };
//        return new JComponent[] { label, component, wrappedComponent };
    }
}