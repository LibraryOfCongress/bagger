package gov.loc.repository.workflow.jbpm.graph.def;

import gov.loc.repository.workflow.actionhandlers.BaseActionHandler;

import org.dom4j.Element;
import org.jbpm.jpdl.xml.JpdlXmlReader;

public class Action extends org.jbpm.graph.def.Action {

	private static final long serialVersionUID = 1L;
	private static final String CLASS_PREFIX = "gov.loc.repository.workflow.actionhandlers.";
	
	
	@Override
	public void read(Element actionElement, JpdlXmlReader jpdlReader) {
		String className = actionElement.attributeValue("class");
		if (className != null && ! className.contains(".") && className.endsWith("ActionHandler"))
		{
			className = CLASS_PREFIX + className;
			actionElement.addAttribute("class", className);
		}
		try
		{
			Class<?> clazz = Class.forName(className);
			if (hasSuperclass(clazz, BaseActionHandler.class))
			{
				actionElement.addAttribute("config-type", "constructor");
			}
		}
		catch(Exception ex)
		{			
		}
		super.read(actionElement, jpdlReader);
	}
	
	@SuppressWarnings("unchecked")
	private boolean hasSuperclass(Class clazz, Class superClazz)
	{
		Class checkSuperClazz = clazz.getSuperclass();
		while(checkSuperClazz != null)
		{
			if (superClazz.equals(checkSuperClazz))
			{
				return true;
			}
			checkSuperClazz = checkSuperClazz.getSuperclass();
		}
		return false;
	}
}
