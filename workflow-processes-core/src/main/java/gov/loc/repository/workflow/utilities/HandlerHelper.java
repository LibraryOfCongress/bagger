package gov.loc.repository.workflow.utilities;

import org.jbpm.graph.exe.ExecutionContext;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;
import gov.loc.repository.workflow.actionhandlers.annotations.Transitions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

public class HandlerHelper
{
	private static final Log log = LogFactory.getLog(HandlerHelper.class);
	
	ExecutionContext executionContext;
	Configuration configuration;
	Object handler;
	
	public HandlerHelper(ExecutionContext executionContext, Configuration configuration, Object handler) {
		this.executionContext = executionContext;
		this.configuration = configuration;
		this.handler = handler;
	}
	
	public void checkTransition(String name) throws Exception
	{
		if (! this.executionContext.getNode().hasLeavingTransition(name))
		{
			throw new Exception(MessageFormat.format("Transition {0} is missing from Node {1}", name, this.executionContext.getNode().getName()));
		}
	}
	
	public void checkRequiredField(Object obj, String name) throws IllegalArgumentException
	{
		if (obj == null)
		{
			throw new IllegalArgumentException(MessageFormat.format("Required field {0} is missing", name));
		}
	}
	
	public Object getVariable(String name)
	{
		return executionContext.getContextInstance().getVariable(name);
	}
	
	public Object getRequiredVariable(String name) throws IllegalArgumentException
	{
		Object obj = this.getVariable(name);
		if (obj == null)
		{
			throw new IllegalArgumentException(MessageFormat.format("Required variable {0} is missing", name));
		}
		return obj;
	}
	
	public String getRequiredConfigString(String name) throws IllegalArgumentException
	{
		String value = this.configuration.getString(name);
		if (value == null)
		{
			throw new IllegalArgumentException(MessageFormat.format("Required variable {0} is missing", name));
		}
		return value;
	}
	
	public void initializeContextVariables() throws Exception
	{
		log.debug("Initializing context variables");
		Class clazz = this.handler.getClass();
		while(clazz != null)
		{
			log.debug("Class is " + clazz.getName());
			for(Field field : clazz.getDeclaredFields())
			{
				log.debug("Field is " + field.getName());
				for(Annotation annotation : field.getAnnotations())
				{
					if (annotation.annotationType().equals(ContextVariable.class))
					{
						ContextVariable contextVariableAnnotation = (ContextVariable)annotation;
						if (contextVariableAnnotation.name() != null && contextVariableAnnotation.name().length() != 0)
						{
							log.debug(MessageFormat.format("ContextVariable annotation with name {0} and isRequired {1}", contextVariableAnnotation.name(), contextVariableAnnotation.isRequired()));
							Object value = this.executionContext.getContextInstance().getVariable(contextVariableAnnotation.name());						
							if (contextVariableAnnotation.isRequired() && value == null)
							{
								throw new Exception("Required context variable " + contextVariableAnnotation.name() + " is missing or null");
							}
							if (value != null && (! field.getType().isInstance(value)))
							{
								throw new Exception(MessageFormat.format("Context variable is not type compatible.  Field is {0}.  Context variable is {1}.", field.getType(), value.getClass().getName()));
							}
							//This leaves a default value if there is one
							if (value != null)
							{
								field.set(this.handler,value);
							}
						}
					}
				}
			}
			clazz = clazz.getSuperclass();			
		}
		
	}
	
	public void checkRequiredTransitions() throws Exception
	{
		for(Annotation annotation : this.handler.getClass().getAnnotations())
		{
			if (annotation.annotationType().equals(Transitions.class))
			{
				Transitions requiredTransitions = (Transitions)annotation;
				for(String transition : requiredTransitions.transitions())
				{
					this.checkTransition(transition);
				}
			}
		}
		
	}
	
	
	public void initializeIndirectContextVariables() throws Exception
	{
		log.debug("Initializing indirect context variables");
		Class clazz = this.handler.getClass();
		while(clazz != null)
		{
			log.debug("Class is " + clazz.getName());
			for(Field field : clazz.getDeclaredFields())
			{
				log.debug("Field is " + field.getName());
				for(Annotation annotation : field.getAnnotations())
				{
					if (annotation.annotationType().equals(ContextVariable.class))
					{
						ContextVariable contextVariableAnnotation = (ContextVariable)annotation;
						if (contextVariableAnnotation.configurationFieldName() != null && contextVariableAnnotation.configurationFieldName().length() != 0)
						{
							log.debug(MessageFormat.format("ContextVariable annotation with configFieldName {0} and isRequired {1}", contextVariableAnnotation.configurationFieldName(), contextVariableAnnotation.isRequired()));
							Field configField = this.handler.getClass().getField(contextVariableAnnotation.configurationFieldName());
							String contextVariableName = (String)configField.get(this.handler);	
							if (contextVariableName == null && configField.getAnnotation(ConfigurationField.class).isRequired())
							{
								throw new Exception("Configuration Field " + contextVariableAnnotation.configurationFieldName() + " is null");
							}
							log.debug("Context variable name is " + contextVariableName);
							if (contextVariableName != null)
							{
								String value = (String)this.executionContext.getContextInstance().getVariable(contextVariableName);						
								if (contextVariableAnnotation.isRequired() && value == null)
								{
									throw new Exception("Required context variable " + contextVariableName + " is missing or null");
								}
								field.set(this.handler,value);
							}
						}
					}
				}
			}
			clazz = clazz.getSuperclass();			
		}
		
	}
	
	public void checkConfigurationFields() throws Exception
	{
		Field fields[] = this.handler.getClass().getFields();
		for(Field field : fields)
		{
			for(Annotation annotation : field.getAnnotations())
			{
				if (annotation.annotationType().equals(ConfigurationField.class))
				{
					ConfigurationField configurationField = (ConfigurationField)annotation;
					if (configurationField.isRequired() && field.get(this.handler) == null)
					{
						throw new Exception("Required configuration field " + field.getName() + " is missing");
					}
						
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void replacePropertiesInFields() throws Exception
	{
		Field fields[] = this.handler.getClass().getFields();
		for(Field field : fields)
		{
			log.debug("Checking " + field.getName() + " for replaceable properties.  It is a " + field.getType().getName());
			if (Modifier.isFinal(field.getModifiers()))
			{
				//Nothing
				log.debug("Is final, so doing nothing");
			}
			else if (Collection.class.isInstance(field.get(this.handler)))
			{
				Collection collection = (Collection)field.get(this.handler);
				Object[] collectionArray = collection.toArray();
				for(Object obj : collectionArray)
				{
					collection.remove(obj);
					collection.add(this.replacePropertyInObject(obj));
				}
				field.set(this.handler, collection);
			}
			else if (Map.class.isInstance(field.get(this.handler)))
			{
				Map map = (Map)field.get(this.handler);
				for(Object name : map.keySet())
				{
					map.put(name, this.replacePropertyInObject(map.get(name)));
				}
				field.set(this.handler, map);
			}
			else
			{
				field.set(this.handler, this.replacePropertyInObject(field.get(this.handler)));
			}
		}
	}
	
	private Object replacePropertyInObject(Object obj)
	{
		if (obj != null && String.class.isInstance(obj))
		{
			String value = (String)obj;				
			if (value.startsWith("${") && value.endsWith("}"))
			{
				String name = value.substring(2, value.length()-1);
				String configValue = this.getRequiredConfigString(name);
				log.debug(MessageFormat.format("Replacing {0} with {1}", value, configValue));
				return configValue;
			}
		}
		return obj;
	}	
	
	
}
