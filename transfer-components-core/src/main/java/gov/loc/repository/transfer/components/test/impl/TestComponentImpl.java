package gov.loc.repository.transfer.components.test.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.transfer.components.test.TestComponent;

public class TestComponentImpl implements TestComponent {

	private static final Log log = LogFactory.getLog(TestComponentImpl.class);	
	private static final long WAIT = 5000;
	
	public void test(String message) throws Exception {
		log.info("Test Component called with message: " + message);
		log.debug("Starting wait");
		Thread.sleep(WAIT);
		log.debug("Done waiting");
	}

}
