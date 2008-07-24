package gov.loc.repository.component;

import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;
import gov.loc.repository.service.annotations.Result;
import gov.loc.repository.service.annotations.ResultParam;

public interface TestComponent extends Component {

	@JobType(name="test")
	public void test(
			@RequestParam(name="message") String message,
			@RequestParam(name="istrue") boolean isTrue,
			@RequestParam(name="key") long key
	) throws Exception;
	
	@Result(jobType="test")
	public boolean getResult();
	
	@ResultParam(jobType="test", name="respMessage")
	public String getRespMessage();
	
	@ResultParam(jobType="test", name="respIsTrue")
	public boolean getRespIsTrue();
	
	@ResultParam(jobType="test", name="respKey")
	public Long getRespKey();
	
}
