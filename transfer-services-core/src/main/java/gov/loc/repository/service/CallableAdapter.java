package gov.loc.repository.service;


import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;
import gov.loc.repository.transfer.components.annotations.Result;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class CallableAdapter implements Callable<TaskResult> {

	private static final Log log = LogFactory.getLog(CallableAdapter.class);
	
	private Object bean;
	private String jobType;
	private Map<String,Object> variableMap;
	private Method jobTypeMethod = null;
	private Method resultMethod = null;
	private Object[] jobTypeParameters;
	
	public CallableAdapter(Object bean, String jobType, Map<String,Object> variableMap) {
		this.bean = bean;
		this.jobType = jobType;
		this.variableMap = variableMap;
	}	
	
	private void findJobTypeMethod()
	{
		for(Method method : bean.getClass().getDeclaredMethods())
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
		for(Method method : bean.getClass().getDeclaredMethods())
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
	
	public TaskResult call() throws Exception {
		TaskResult result = new TaskResult();
		//For each method
		try
		{
			this.findJobTypeMethod();
			if (this.jobTypeMethod == null)
			{
				result.error = "Unable to match jobType and provided variables to a component";
				return result;
			}
			this.findResultMethod();
			//Let's wrap this in a session
			Session session = HibernateUtil.getSessionFactory(DatabaseRole.DATA_WRITER).getCurrentSession();
			try
			{					
				session.beginTransaction();
				//Invoke and return taskResult
				this.jobTypeMethod.invoke(this.bean, this.jobTypeParameters);
				session.getTransaction().commit();
			}
			catch(Exception ex)
			{
				if (session != null && session.isOpen())
				{
					session.getTransaction().rollback();
				}
				throw ex;
			}
			finally
			{
				if (session != null && session.isOpen())
				{
					session.close();
				}
			}
			if (this.resultMethod == null)
			{
				result.isSuccess = true;
			}
			else
			{
				result.isSuccess = (Boolean)this.resultMethod.invoke(this.bean, (Object[])null); 
			}
			return result;
						
		}
		catch(Exception ex)
		{
			result.error = ex.getMessage();
			log.error(ex.getMessage(), ex);
		}
		//Return taskresult with an error
		log.debug(result.error);
		return result;
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
