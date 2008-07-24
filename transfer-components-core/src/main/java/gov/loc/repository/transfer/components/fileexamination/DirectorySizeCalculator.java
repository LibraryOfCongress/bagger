package gov.loc.repository.transfer.components.fileexamination;

import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;
import gov.loc.repository.service.annotations.ResultParam;

public interface DirectorySizeCalculator extends Component {
	
	public static final String COMPONENT_NAME = "directorysizecalculator";
		
	@JobType(name="calculatedirectorysize")	
	public void calculate(
			@RequestParam(name="mountpath") String mountPath)
			throws Exception;
	
	@ResultParam(name="directorysize", jobType="calculatedirectorysize")
	public Long getDirectorySize();
		
}
