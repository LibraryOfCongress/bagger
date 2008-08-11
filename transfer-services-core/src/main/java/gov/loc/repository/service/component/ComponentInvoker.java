package gov.loc.repository.service.component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;
import gov.loc.repository.service.annotations.Result;
import gov.loc.repository.service.annotations.ResultParam;
import gov.loc.repository.service.component.ComponentRequest.ObjectEntry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ComponentInvoker {
	
	private static final Log log = LogFactory.getLog(ComponentInvoker.class);
	
	public void invoke(Object component, ComponentRequest req)
	{
		try
		{
			Method jobTypeMethod = this.findJobTypeMethod(component, req.getJobType(), req.getRequestEntries());
			Object[] jobTypeParameters = this.findJobTypeParameters(jobTypeMethod, req.getJobType(), req.getRequestEntries());
			if (jobTypeMethod == null)
			{
				throw new Exception("Unable to find jobTypeMethod for jobType " + req.getJobType());
			}
			
			jobTypeMethod.invoke(component, jobTypeParameters);
			Method[] responseParamMethods = this.findResultParamMethods(component, req.getJobType());
			for(Method responseParamMethod : responseParamMethods)
			{
				Class<?> returnType = responseParamMethod.getReturnType();
				String responseParamName = this.getResponseParamName(responseParamMethod, req.getJobType());
				Object responseValue = responseParamMethod.invoke(component, (Object[])null);
				if (String.class.equals(returnType))
				{
					req.addResponseString(responseParamName, (String)responseValue);
				}
				else if (Long.class.equals(returnType) || Long.TYPE.equals(returnType))
				{
					req.addResponseInteger(responseParamName, (Long)responseValue);
				}
				else if (Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType))
				{
					req.addResponseBoolean(responseParamName, (Boolean)responseValue);
				}
				else
				{
					throw new RuntimeException("Cannot handle response of type " + returnType);
				}
			}
			Method resultMethod = this.findResultMethod(component, req.getJobType());
			if (resultMethod != null)
			{
				Boolean result = (Boolean)resultMethod.invoke(component, (Object[])null);
				log.debug("Result is " + result);
				req.respondSuccess(result); 
			}		
			else
			{
				req.respondSuccess(true);
			}
			
		}
		catch(Exception ex)
		{
			log.error(ex);
			req.respondFailure(ex);
		}
	}
		
	
	private Method findJobTypeMethod(Object component, String jobType, Collection<ObjectEntry> objectEntries) throws Exception
	{
		for(Method method : component.getClass().getDeclaredMethods())
		{
			log.debug("Trying method " + method.getName());
			//Get super method (which is where the annotations would be located)
			Method superMethod = this.getSuperMethodForJobType(method, jobType);
			if (superMethod == null)
			{
				log.debug("Is not annotated for jobType " + jobType);
				continue;
			}
			log.debug("Is annotated for jobType " + jobType);
			//For each parameter
			Annotation[][] parameterAnnotations = superMethod.getParameterAnnotations();
			int satisfiedParameterCount = 0;
			//For each parameter				
			for(int i=0; i < parameterAnnotations.length; i++)
			{
				Annotation[] annotationArray = parameterAnnotations[i];
				if (this.hasSatisfyingValue(objectEntries, annotationArray))
				{
					satisfiedParameterCount++;
				}
			}
			log.debug(MessageFormat.format("{0} of {1} parameters are satisfied", satisfiedParameterCount, method.getParameterTypes().length));
			if (satisfiedParameterCount == method.getParameterTypes().length)
			{
				log.debug("All parameters are satisfied");
				return method;
			}
		}
		return null;
	}

	private Object[] findJobTypeParameters(Method method, String jobType, Collection<ObjectEntry> objectEntries) throws Exception
	{
		Object[] parameters = new Object[method.getParameterTypes().length];
		Method superMethod = this.getSuperMethodForJobType(method, jobType);
		
		//For each parameter
		Annotation[][] parameterAnnotations = superMethod.getParameterAnnotations();
		
		//For each parameter				
		for(int i=0; i < parameterAnnotations.length; i++)
		{
			Annotation[] annotationArray = parameterAnnotations[i];
			parameters[i] = this.getSatisfyingValue(objectEntries, annotationArray);
		}		
		return parameters;
		
	}
	
	
	private Method findResultMethod(Object component, String jobType)
	{
		for(Method method : component.getClass().getDeclaredMethods())
		{
			log.debug("Trying method " + method.getName());
			//Get super method (which is where the annotations would be located)
			Method superMethod = this.getSuperMethodForResult(method, jobType);
			if (superMethod != null)
			{
				log.debug("This method is a Result");
				return method;
			}
		}
		return null;
	}
	
	private Method getSuperMethodForJobType(Method method, String jobType)
	{
		for(Class<?> clazz : method.getDeclaringClass().getInterfaces())
		{
			log.debug(MessageFormat.format("Checking {0} for method {1} that is annotated with JobType", clazz.getName(), method.getName()));
			Method superMethod;
			try
			{
				superMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
				JobType jobTypeAnnot = (JobType)superMethod.getAnnotation(JobType.class);
				if (jobTypeAnnot != null)
				{
					log.debug("Method contains JobType annotation with value " + jobTypeAnnot.name());
				}
				else
				{
					log.debug("Method does not contain JobType annotation");
				}
				if (jobTypeAnnot != null && jobType.equals(jobTypeAnnot.name()))
				{
					return superMethod;
				}
				
			}
			catch(NoSuchMethodException ignore)
			{
				log.debug("Does not contain method");
			}
		}
		return null;
	}

	private Method getSuperMethodForResult(Method method, String jobType)
	{
		for(Class<?> clazz : method.getDeclaringClass().getInterfaces())
		{
			Method superMethod;
			try
			{
				superMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
				Result resultAnnot = (Result)superMethod.getAnnotation(Result.class);
				if (resultAnnot != null && jobType.equals(resultAnnot.jobType()))
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
	
	
	private boolean hasSatisfyingValue(Collection<ObjectEntry> objectEntries, Annotation[] annotationArray)
	{
		for(Annotation annot : annotationArray)
		{
			if (annot instanceof RequestParam)
			{
				RequestParam mapParameterAnnot = (RequestParam)annot;
				if (this.entriesContain(objectEntries, mapParameterAnnot.name()))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		return false;
	}
	
	private Collection<ObjectEntry> getObjectEntries(Collection<ObjectEntry> objectEntries, String key)
	{
		Collection<ObjectEntry> matchingEntries = new ArrayList<ObjectEntry>();
		for(ObjectEntry entry : objectEntries)
		{
			if (entry.getKey().equals(key))
			{
				matchingEntries.add(entry);
			}			
		}
		
		return matchingEntries;
	}
	
	private boolean entriesContain(Collection<ObjectEntry> objectEntries, String key)
	{
		for(ObjectEntry entry : objectEntries)
		{
			if (entry.getKey().equals(key))
			{
				return true;
			}			
		}
		return false;
	}
	
	private Object getSatisfyingValue(Collection<ObjectEntry> objectEntries, Annotation[] annotationArray) throws Exception
	{
		for(Annotation annot : annotationArray)
		{
			if (annot instanceof RequestParam)
			{
				RequestParam mapParameterAnnot = (RequestParam)annot;
				if (this.entriesContain(objectEntries, mapParameterAnnot.name()))
				{
					Collection<ObjectEntry> entries = this.getObjectEntries(objectEntries, mapParameterAnnot.name());
					if (entries.size() != 1)
					{
						throw new Exception("Multiple entries for key " + mapParameterAnnot.name()); 
					}
					Object value = entries.iterator().next().getValueObject();
					log.debug(MessageFormat.format("VariableMap contains value {0} for MapParameter {1}", value, mapParameterAnnot.name()));
					return value;
				}
				else
				{
					log.debug(MessageFormat.format("VariableMap does not contain a value for MapParameter {0}", mapParameterAnnot.name()));
				}
			}
		}
		return null;
	}
	
	private Method[] findResultParamMethods(Object component, String jobType)
	{
		List<Method> methodList = new ArrayList<Method>();
		for(Method method : component.getClass().getDeclaredMethods())
		{
			log.debug("Trying method " + method.getName());
			//Get super method (which is where the annotations would be located)
			Method superMethod = this.getSuperMethodForResultParam(method, jobType);
			if (superMethod != null)
			{
				log.debug("This method is a ResultParam");
				methodList.add(method);
			}
		}
		return methodList.toArray(new Method[]{});

	}

	private Method getSuperMethodForResultParam(Method method, String jobType)
	{
		for(Class<?> clazz : method.getDeclaringClass().getInterfaces())
		{
			Method superMethod;
			try
			{
				superMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
				ResultParam resultAnnot = (ResultParam)superMethod.getAnnotation(ResultParam.class);
				if (resultAnnot != null && jobType.equals(resultAnnot.jobType()))
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
	
	private String getResponseParamName(Method method, String jobType)
	{
		for(Class<?> clazz : method.getDeclaringClass().getInterfaces())
		{
			Method superMethod;
			try
			{
				superMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
				ResultParam resultAnnot = (ResultParam)superMethod.getAnnotation(ResultParam.class);
				if (resultAnnot != null && jobType.equals(resultAnnot.jobType()))
				{
					return resultAnnot.name();
				}
				
			}
			catch(NoSuchMethodException ignore)
			{
			}
		}
		return null;
		
	}
}
