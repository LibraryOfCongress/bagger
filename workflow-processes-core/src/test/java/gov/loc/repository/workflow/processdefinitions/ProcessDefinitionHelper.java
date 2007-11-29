package gov.loc.repository.workflow.processdefinitions;

import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;

public class ProcessDefinitionHelper {

	private Map<String,String> actionHandlerMap = new HashMap<String,String>();
	private Map<String,Map<String,String>> factoryMethodMapMap = new HashMap<String,Map<String,String>>();
	private static JbpmConfiguration jbpmConfiguration;
	private Document processDefinitionDocument;
	
	/**
	 * Register an ActionHandler in the ProcessDefinition to be overriden for testing purposes.
	 * @param xpath An XPath indicating the ActionHandler that is to be overriden.  For example, "//action[@class='gov.loc.repository.workflow.actionhandlers.FooActionHandler']"
	 * @param className The class name of the replacement ActionHandler.  For example, "//action[@class='gov.loc.repository.workflow.actionhandlers.BarActionHandler']"
	 */
	public void registerActionHandler(String xpath, String className)
	{
		this.actionHandlerMap.put(xpath, className);
	}
	
	/**
	 * Register factory methods to be used by an ActionHandler in the ProcessDefinition to be overriden for testing purposes.
	 * @param xpath  An XPath indicating the ActionHandler that is to be overriden.  For example, "//action[@class='gov.loc.repository.workflow.actionhandlers.FooActionHandler']"
	 * @param simpleName Simple name of the class to be instantiated, e.g., "String"
	 * @param factoryMethodName Static factory method, e.g., "gov.loc.repository.workflow.actionhandlers.BarActionHandler.createMockString"
	 */
	public void registerFactoryMethod(String xpath, String simpleName, String factoryMethodName)
	{
		if (! this.factoryMethodMapMap.containsKey(xpath))
		{
			this.factoryMethodMapMap.put(xpath, new HashMap<String, String>());
		}
		this.factoryMethodMapMap.get(xpath).put(simpleName, factoryMethodName);
	}

	/**
	 * Deploys the process definition.
	 * @param processDefinitionDoc The process definition.
	 * @return The name of the process definition as a String. 
	 * @throws Exception
	 */
	public String deploy() throws Exception
	{
		JbpmContext jbpmContext = getJbpmConfiguration().createJbpmContext();
		try
		{
			this.overrideActionHandlers();
			this.overrideFactoryMaps();
			ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(this.processDefinitionDocument.asXML());			
		    jbpmContext.deployProcessDefinition(processDefinition);
		    return processDefinition.getName();
		}
		finally
		{
			jbpmContext.close();
		}
	}

	private void overrideFactoryMaps() throws Exception
	{
		for(String xpath : this.factoryMethodMapMap.keySet())
		{
			List nodeList = this.processDefinitionDocument.selectNodes(xpath);
			if (nodeList.isEmpty())
			{
				throw new Exception(xpath + " does not match any actionHandlers");
			}
			for(Object obj : nodeList)
			{
				if (! (obj instanceof Element))
				{
					throw new Exception(xpath + " does not resolve to an Element");
				}
				Element actionElement = (Element)obj;
				if (! "action".equals(actionElement.getName()))
				{
					throw new Exception(xpath + " does not resolve to an action Element"); 
				}
				Element factoryMethodMapElement = actionElement.addElement("factoryMethodMap");
				for(String className : this.factoryMethodMapMap.get(xpath).keySet())
				{
					String factoryMethodName = this.factoryMethodMapMap.get(xpath).get(className);
					Element entryElement = factoryMethodMapElement.addElement("entry");
					Element keyElement = entryElement.addElement("key");
					keyElement.addText(className);
					Element valueElement = entryElement.addElement("value");
					valueElement.addText(factoryMethodName);
				}
			}
		}
		
	}
	
	private void overrideActionHandlers() throws Exception
	{
		for(String xpath : actionHandlerMap.keySet())
		{
			List nodeList = this.processDefinitionDocument.selectNodes(xpath);
			if (nodeList.isEmpty())
			{
				throw new Exception(xpath + " does not match any actionHandlers");
			}
			for(Object obj : nodeList)
			{
				if (! (obj instanceof Element))
				{
					throw new Exception(xpath + " does not resolve to an Element");
				}
				Element actionElement = (Element)obj;
				if (! "action".equals(actionElement.getName()))
				{
					throw new Exception(xpath + " does not resolve to an action Element"); 
				}
				actionElement.addAttribute("class", actionHandlerMap.get(xpath));
			}
		}
	}

	
	public void setProcessDefinition(String processDefinition) throws Exception
	{
		SAXReader reader = new SAXReader();	
		this.processDefinitionDocument = reader.read(new StringReader(processDefinition));
	}
	
	
	public void setProcessDefinitionResource(String processDefinitionResource) throws Exception
	{
		SAXReader reader = new SAXReader();	
		URL url = this.getClass().getClassLoader().getResource(processDefinitionResource);
		this.processDefinitionDocument = reader.read(url);
	}

	public void setProcessDefinition(Document processDefinitionDocument) {
		this.processDefinitionDocument = processDefinitionDocument;
	}

	public static void setJbpmConfiguration(JbpmConfiguration jbpmConfiguration) {
		ProcessDefinitionHelper.jbpmConfiguration = jbpmConfiguration;
	}

	public static JbpmConfiguration getJbpmConfiguration() {
		return ProcessDefinitionHelper.jbpmConfiguration;
	}
	
	public void clear()
	{
		this.actionHandlerMap.clear();
		this.factoryMethodMapMap.clear();
		this.processDefinitionDocument = null;
	}
}
