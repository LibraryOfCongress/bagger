package gov.loc.repository.serviceBroker.impl;

import java.util.Date;

import gov.loc.repository.serviceBroker.AbstractServiceBrokerTest;
import gov.loc.repository.serviceBroker.ServiceContainerRegistration;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceContainerRegistrationImplTest extends AbstractServiceBrokerTest {
	
	@Test
	public void testServiceContainerRegistration() throws Exception
	{		
		ServiceContainerRegistration reg = serviveBrokerFactory.createServiceContainerRegistration(HOST_1, 1194);
		assertNull(reg.getTimestamp());
		assertTrue(reg.getBeatCount() == 0L);

		template.save(reg);
		Date timeStamp1 = reg.getTimestamp();
		assertNotNull(timeStamp1);
		
		Thread.sleep(1000);
		reg.beat();		
		template.saveOrUpdate(reg);
		
		assertFalse(reg.getTimestamp().equals(timeStamp1));
		assertTrue(reg.getBeatCount() == 1L);
		
	}	
}
