package gov.loc.repository.component.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.component.TestComponent;

@org.springframework.stereotype.Component("testComponent")
public class TestComponentImpl implements TestComponent
{

	private static final Log log = LogFactory.getLog(TestComponentImpl.class);	
	private static final long WAIT = 1000;
	
	public void test(String message, boolean istrue, long key) throws Exception {
		log.info("Test Component called with message: " + message);
		log.debug("Starting wait");
		Thread.sleep(WAIT);
		log.debug("Done waiting");
	}

	@Override
	public boolean getRespIsTrue() {
		return false;
	}
	
	@Override
	public Long getRespKey() {
		return null;
	}
	
	@Override
	public String getRespMessage() {
		return null;
	}
	
	@Override
	public boolean getResult() {
		return true;
	}
}
