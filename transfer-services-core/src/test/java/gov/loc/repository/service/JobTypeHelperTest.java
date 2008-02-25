package gov.loc.repository.service;


import gov.loc.repository.service.JobTypeHelper;

import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class JobTypeHelperTest {

	
	@Test
	public void testGetJobTypeToBeanIdMap() throws Exception
	{
		
		ApplicationContext context = new ClassPathXmlApplicationContext("services-context-core.xml");
		Map<String,String> jobTypeMap = JobTypeHelper.getJobTypeToBeanIdMap(context);
		assertEquals(1, jobTypeMap.size());
		assertEquals("testcomponent", jobTypeMap.get("test"));
	}
}
