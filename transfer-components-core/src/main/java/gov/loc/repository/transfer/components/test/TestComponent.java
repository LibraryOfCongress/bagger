package gov.loc.repository.transfer.components.test;

import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;

public interface TestComponent extends Component {

	@JobType(name="test")
	public void test(@MapParameter(name="message") String message) throws Exception;
}
