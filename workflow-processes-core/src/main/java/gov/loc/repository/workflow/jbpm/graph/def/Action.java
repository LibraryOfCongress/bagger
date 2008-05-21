package gov.loc.repository.workflow.jbpm.graph.def;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.jpdl.xml.JpdlXmlReader;

public class Action extends org.jbpm.graph.def.Action {

	private static final long serialVersionUID = 1L;
	private static final String CLASS_PREFIX = "gov.loc.repository.workflow.actionhandlers.";
	
	private static final Log log = LogFactory.getLog(Action.class);
	
	@Override
	public void read(Element actionElement, JpdlXmlReader jpdlReader) {
		String origClassName = actionElement.attributeValue("class");
		if (origClassName != null)
		{
			String className = origClassName;
			if (! origClassName.contains(".") && origClassName.endsWith("ActionHandler"))
			{
				className = CLASS_PREFIX + origClassName;
				log.debug(MessageFormat.format("Changing {0} to {1}", origClassName, className));
				actionElement.addAttribute("class", className);
			}
			if (className.startsWith(CLASS_PREFIX) && actionElement.attribute("config-type") == null)
			{
				log.debug("Setting config-type to constructor for " + className);
				actionElement.addAttribute("config-type", "constructor");				
			}
		}
		super.read(actionElement, jpdlReader);
	}	
}
