package gov.loc.repository.transfer.components.test.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import gov.loc.repository.transfer.components.test.TestComponent;

@Component("testComponent")
public class TestComponentImpl implements TestComponent {

	private static final Log log = LogFactory.getLog(TestComponentImpl.class);	
	
	public void test(String message, boolean istrue, long key) throws Exception {
		log.info("Test Component called with message: " + message);
		log.debug("Starting wait");
		Thread.sleep(key);
		log.debug("Done waiting");
	}

}
