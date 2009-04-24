
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.factory.ComponentFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
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
/*
	public JComponent[] add(String fieldName, boolean b) {
//		JComponent component = getComponentFactory().createTextArea();
    	JComponent component = getComponentFactory().createTextField();
        return addBinding(createBinding(fieldName, component), "", getLabelAttributes());
    }
*/    
    public JComponent[] addTextArea(String fieldName) {
        return addTextArea(fieldName, "");
    }

    public JComponent[] addTextArea(String fieldName, String attributes) {
        JComponent textArea = createTextArea(fieldName);
        String labelAttributes = getLabelAttributes();
        if (labelAttributes == null) {
            labelAttributes = VALIGN_TOP;
        } else if (!labelAttributes.contains(TableLayoutBuilder.VALIGN)) {
            labelAttributes += " " + VALIGN_TOP;
        }
        return addBinding(createBinding(fieldName, textArea), attributes, labelAttributes);
//        return addBinding(createBinding(fieldName, textArea), new JScrollPane(textArea), attributes, labelAttributes);
    }
}