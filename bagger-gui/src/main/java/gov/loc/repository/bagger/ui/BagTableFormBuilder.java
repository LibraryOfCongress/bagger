
package gov.loc.repository.bagger.ui;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

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

    public JComponent[] addTextArea(String fieldName) {
        return addTextArea(fieldName, "");
    }

    public JComponent[] addTextArea(String fieldName, String attributes) {
//        JComponent textArea = createTextArea(fieldName);
    	JComponent textArea = new NoTabTextArea(5, 40);
        String labelAttributes = getLabelAttributes();
        if (labelAttributes == null) {
            labelAttributes = VALIGN_TOP;
        } else if (!labelAttributes.contains(TableLayoutBuilder.VALIGN)) {
            labelAttributes += " " + VALIGN_TOP;
        }
        Binding binding = createBinding(fieldName, textArea);
        JComponent wrappedComponent = textArea;
        // TODO: using the JScrollPane component causes the validation 'x' to disappear
//        JComponent wrappedComponent = new JScrollPane(textArea)
        return addBinding(binding, wrappedComponent, attributes, labelAttributes);
    }
}