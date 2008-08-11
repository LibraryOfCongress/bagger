package gov.loc.repository.workflow.jbpm.instantiation;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.graph.exe.ExecutionContext;

public class FieldInstantiator extends org.jbpm.instantiation.FieldInstantiator{

	private static final Log log = LogFactory.getLog(FieldInstantiator.class);
	
	@SuppressWarnings("unchecked")
	public void configure(Object obj, String configuration, ExecutionContext executionContext)
	{
	    if ((configuration!=null) && (! "".equals(configuration)))
	    {
	         // parse the bean configuration
	         Element configurationElement = parseConfiguration(configuration);
	         
	         // loop over the configured properties
	         Iterator<Element> iter = configurationElement.elements().iterator();
	         while( iter.hasNext() ) {
	           Element propertyElement = iter.next();
	           String propertyName = propertyElement.getName();
	           String value = propertyElement.getText();
	           if (value.startsWith("${") && value.endsWith("}"))
	           {
	        	   String contextVariableName = value.substring(2, value.length()-1);
	        	   log.debug("Context variable name is " + contextVariableName);
	        	   setPropertyValue(obj.getClass(), obj, propertyName, executionContext.getVariable(contextVariableName));
	           }
	           else
	           {
	        	   setPropertyValue(obj.getClass(), obj, propertyName, propertyElement);
	           }
	         }
	       }
	}

	@SuppressWarnings("unchecked")
	protected void setPropertyValue(Class clazz, Object obj, String propertyName, Object value)
	{
		try{
			log.debug(MessageFormat.format("Setting field {0} to {1}", propertyName, value));
			Field f = findField(clazz, propertyName);
			f.setAccessible(true);
		    f.set(obj, value);			
		}
		catch (Exception e) {
			log.error( "couldn't set field '"+propertyName+"' to value '"+value.toString()+"'", e );
	    }
	}
	
	@SuppressWarnings("unchecked")
	private Field findField(Class clazz, String propertyName) throws NoSuchFieldException {
	    Field f = null;
	    if (clazz!=null) {
	      try {
	        f = clazz.getDeclaredField(propertyName);
	      } catch (NoSuchFieldException e) {
	        f = findField(clazz.getSuperclass(), propertyName);
	      }
	    }
	    return f;
	  }

}
