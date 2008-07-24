package gov.loc.repository.transfer.components.test;

import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;

public interface TestComponent extends Component {

	@JobType(name="test")
	public void test(
			@RequestParam(name="message") String message,
			@RequestParam(name="istrue") boolean isTrue,
			@RequestParam(name="key") long key
	) throws Exception;
}
