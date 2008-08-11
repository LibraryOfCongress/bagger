package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.transfer.components.fileexamination.DirectorySizeCalculator;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;

public class CalculateFileLocationSizeActionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;

	@Required
	public String mountPath;
	
	private DirectorySizeCalculator calculator;
	
	public CalculateFileLocationSizeActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected void initialize() throws Exception
	{
		this.calculator = this.createObject(DirectorySizeCalculator.class);
	}
		
	@Override
	protected void execute() throws Exception {
		
		calculator.calculate(this.mountPath);
	}

}
