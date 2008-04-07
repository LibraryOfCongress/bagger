package gov.loc.repository.workflow.utilities;

import org.jbpm.graph.exe.ExecutionContext;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.workflow.actionhandlers.annotations.Required;
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
	
	public Object getContextVariable(String name)
	{
		return executionContext.getContextInstance().getVariable(name);
	}
	
	public String getConfigString(String name)
	{
		return this.configuration.getString(name);
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
				return;
			}
		}
	}

	public String getRequiredConfigString(String name) throws IllegalArgumentException
	{
		String value = this.getConfigString(name);
		if (value == null)
		{
			throw new IllegalArgumentException(MessageFormat.format("Required variable {0} is missing", name));
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public void replacePlaceholdersInFields() throws Exception
	{
		Field fields[] = this.handler.getClass().getFields();
		for(Field field : fields)
		{
			log.debug("Checking " + field.getName() + " for placeholders.  It is a " + field.getType().getName());
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
					collection.add(this.replacePlaceholderInObject(obj));
				}
				field.set(this.handler, collection);
			}
			else if (Map.class.isInstance(field.get(this.handler)))
			{
				Map map = (Map)field.get(this.handler);
				for(Object name : map.keySet())
				{
					map.put(name, this.replacePlaceholderInObject(map.get(name)));
				}
				field.set(this.handler, map);
			}
			else
			{
				field.set(this.handler, this.replacePlaceholderInObject(field.get(this.handler)));
			}
		}
	}
	
	private Object replacePlaceholderInObject(Object obj)
	{
		if (obj != null && String.class.isInstance(obj))
		{
			String value = (String)obj;				
			if (value.startsWith("${") && value.endsWith("}"))
			{
				String name = value.substring(2, value.length()-1);
				String newValue = (String)this.getContextVariable(name);
				log.debug(MessageFormat.format("Replacing {0} with {1}", value, newValue));
				return newValue;
			}
			else if (value.startsWith("$#{") && value.endsWith("}"))
			{
				String name = value.substring(3, value.length()-1);
				String newValue = this.getConfigString(name);
				log.debug(MessageFormat.format("Replacing {0} with {1}", value, newValue));
				return newValue;
			} 
		}
		return obj;
	}
	
	public void checkRequiredFields() throws Exception
	{
		log.debug("Checking required fields");
		Class<?> clazz = this.handler.getClass();
		while(clazz != null)
		{
			log.debug("Class is " + clazz.getName());
			for(Field field : clazz.getDeclaredFields())
			{
				log.debug("Field is " + field.getName());
				for(Annotation annotation : field.getAnnotations())
				{
					if (annotation.annotationType().equals(Required.class) && field.get(this.handler) == null)
					{
						throw new IllegalArgumentException(MessageFormat.format("Required field {0} is missing", field.getName()));
					}
				}
			}
			clazz = clazz.getSuperclass();			
		}
		
	}
	
	
}
