package gov.loc.repository.workflow.actionhandlers;

import java.io.File;
import java.text.MessageFormat;

import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;
import gov.loc.repository.workflow.actionhandlers.annotations.Transitions;
import gov.loc.repository.transfer.components.packaging.Unpackager;
import gov.loc.repository.transfer.components.packaging.impl.ZipPackager;

@Transitions(transitions={"continue"})
public class UnzipActionHandler extends BaseActionHandler 
{
	private static final long serialVersionUID = 1L; 
	
	@ContextVariable(name="sourceFile")
	public String sourceFileName;
	private File sourceFile;
	
	@ConfigurationField
	public String baseDestinationDirectory;
	
	@Override
	protected void initialize() throws Exception
	{
		sourceFile = new File(sourceFileName);		
	}

	@Override
	public void execute() throws Exception
	{						
			String destinationDirectoryName = this.baseDestinationDirectory + File.separator + Double.toHexString(Math.random());
			File destinationDirectory = new File(destinationDirectoryName);
			executionContext.getContextInstance().setVariable("destinationDirectory", destinationDirectoryName);
			this.reportingLog.info(MessageFormat.format("Unzipping {0} to {1}", sourceFile.toString(), destinationDirectoryName));			 
			//Use this.creatObject() to instantiate a component.
			//This will be used to allow a mock component to be injected in a unit test.
			Unpackager unpackager = this.createObject(Unpackager.class);
			unpackager.unpackage(sourceFile, destinationDirectory);

			this.reportingLog.info("Unzip succeeded");
		
			executionContext.leaveNode("continue");				
	}

	//For each object that is instantiated in an action handler, a method createXXX() should be provided.
	//This method will be used by createObject() when a mock component isn't being inject in a unit test.
	public Unpackager createUnpackager() throws Exception
	{
		return new ZipPackager();
	}
	
}
