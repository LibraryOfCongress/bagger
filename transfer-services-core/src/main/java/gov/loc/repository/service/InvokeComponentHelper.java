package gov.loc.repository.service;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;
import gov.loc.repository.transfer.components.annotations.Result;


public class InvokeComponentHelper {
	
	private static final Log log = LogFactory.getLog(InvokeComponentHelper.class);
	private Object component;
	
	private Method jobTypeMethod = null;
	private Method resultMethod = null;
	private Object[] jobTypeParameters;
	private String jobType;
	private Map<String,Object> variableMap;
	
	public InvokeComponentHelper(Object component, String jobType, Map<String,Object> variableMap) {
		this.component = component;
		this.jobType = jobType;
		this.variableMap = variableMap;
	}

	public boolean invoke() throws Exception
	{
		this.findJobTypeMethod();
		this.findResultMethod();
		this.jobTypeMethod.invoke(this.component, this.jobTypeParameters);
		if (this.resultMethod != null)
		{
			return (Boolean)this.resultMethod.invoke(this.component, (Object[])null); 
		}		
		return true;
	}
	
	public Method getJobTypeMethod()
	{
		return this.jobTypeMethod;
	}
	
	public Method getResultMethod()
	{
		return this.resultMethod;
	}
	
	public Object[] getJobTypeParameters()
	{
		return this.jobTypeParameters;
	}
	
	private void findJobTypeMethod()
	{
		for(Method method : component.getClass().getDeclaredMethods())
		{
			log.debug("Trying method " + method.getName());
			//Get super method (which is where the annotations would be located)
			Method superMethod = this.getSuperMethodForJobType(method);
			if (superMethod == null)
			{
				log.debug("Is not annotated for jobType " + this.jobType);
				continue;
			}
			Object[] parameters = new Object[method.getParameterTypes().length];
			//For each parameter
			Annotation[][] parameterAnnotations = superMethod.getParameterAnnotations();
			//For each parameter				
			for(int i=0; i < parameterAnnotations.length; i++)
			{
				Annotation[] annotationArray = parameterAnnotations[i];
				parameters[i] = this.getSatisfyingValue(annotationArray);
			}
			//If all parameters are filled
			if (areParametersSatisfied(parameters))
			{
				this.jobTypeMethod = method;
				this.jobTypeParameters = parameters;
				return;
			}
		}
	}
	
	private void findResultMethod()
	{
		for(Method method : component.getClass().getDeclaredMethods())
		{
			log.debug("Trying method " + method.getName());
			//Get super method (which is where the annotations would be located)
			Method superMethod = this.getSuperMethodForResult(method);
			if (superMethod != null)
			{
				this.resultMethod = method;
			}
			log.debug("Is not annotated for jobType " + this.jobType);			
		}
	}
	
	private Method getSuperMethodForJobType(Method method)
	{
		for(Class<?> clazz : method.getDeclaringClass().getInterfaces())
		{
			Method superMethod;
			try
			{
				superMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
				JobType jobTypeAnnot = (JobType)superMethod.getAnnotation(JobType.class);
				if (jobTypeAnnot != null && this.jobType.equals(jobTypeAnnot.name()))
				{
					return superMethod;
				}
				
			}
			catch(NoSuchMethodException ignore)
			{
			}
		}
		return null;
	}

	private Method getSuperMethodForResult(Method method)
	{
		for(Class<?> clazz : method.getDeclaringClass().getInterfaces())
		{
			Method superMethod;
			try
			{
				superMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
				Result resultAnnot = (Result)superMethod.getAnnotation(Result.class);
				if (resultAnnot != null && this.jobType.equals(resultAnnot.jobType()))
				{
					return superMethod;
				}
				
			}
			catch(NoSuchMethodException ignore)
			{
			}
		}
		return null;
	}
	
	
	private boolean areParametersSatisfied(Object[] parameters)
	{
		for(Object obj : parameters)
		{
			if (obj == null)
			{
				log.debug("All parameters are not satisfied");
				return false;
			}
		}
		log.debug("All parameters are satisfied");
		return true;
	}
	
	private Object getSatisfyingValue(Annotation[] annotationArray)
	{
		for(Annotation annot : annotationArray)
		{
			if (annot instanceof MapParameter)
			{
				MapParameter mapParameterAnnot = (MapParameter)annot;
				if (variableMap.containsKey(mapParameterAnnot.name()))
				{
					log.debug(MessageFormat.format("VariableMap contains value {0} for MapParameter {1}", variableMap.get(mapParameterAnnot.name()), mapParameterAnnot.name()));
					return variableMap.get(mapParameterAnnot.name());
				}
				else
				{
					log.debug(MessageFormat.format("VariableMap does not contain a value for MapParameter {0}", mapParameterAnnot.name()));
				}
			}
		}
		return null;
	}
	
}
