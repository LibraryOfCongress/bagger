package gov.loc.repository.utilities.persistence;

import gov.loc.repository.Timestamped;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

public class TimestampedInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(TimestampedInterceptor.class);
	
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types)
	{

		if ( entity instanceof Timestamped )
		{
			for ( int i=0; i < propertyNames.length; i++ )
			{
				if ( "updateTimestamp".equals( propertyNames[i] ) )
				{
					//log.debug("Setting update timestamp for " + entity.getClass().toString());
					currentState[i] = new Date();
					return true;
				}
				
			}
			log.warn(MessageFormat.format("{0} implements Timestamped but does not have updateTimestamp property", entity.getClass().toString()));
		}
		return false;
	}
	
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		boolean isModified = false;
		if ( entity instanceof Timestamped )
		{
			
			for ( int i=0; i < propertyNames.length; i++ )
			{
				
				if ( "updateTimestamp".equals( propertyNames[i] ) )
				{
					//log.debug("Setting update timestamp for " + entity.getClass().toString());
					state[i] = new Date();
					isModified = true;
				}
				else if ( "createTimestamp".equals( propertyNames[i] ) )
				{
					//log.debug("Setting create timestamp for " + entity.getClass().toString());
					state[i] = new Date();
					isModified = true;
				}
			}
			if (! isModified)
			{
				log.warn(MessageFormat.format("{0} implements Timestamped but does not have updateTimestamp and/or createTimestamp property", entity.getClass().toString()));
			}
		}

		return isModified;		
	}
}
