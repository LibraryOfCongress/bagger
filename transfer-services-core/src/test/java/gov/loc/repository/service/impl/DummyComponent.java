package gov.loc.repository.service.impl;

import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;
import gov.loc.repository.transfer.components.annotations.Result;

public interface DummyComponent extends Component {

	@JobType(name="foo")
	public void executeFoo(@MapParameter(name="fooParam") String fooParam, @MapParameter(name="barParam")String barParam, @MapParameter(name="booleanParam")boolean booleanParam);
	
	@Result(jobType="foo")
	public boolean getExecuteFooResult();
	
	@JobType(name="foo2")
	public void executeFoo2(@MapParameter(name="fooParam") String fooParam, @MapParameter(name="barParam")String barParam);
	
}
