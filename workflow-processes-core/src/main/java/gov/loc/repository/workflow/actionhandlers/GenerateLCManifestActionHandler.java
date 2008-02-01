package gov.loc.repository.workflow.actionhandlers;

import java.text.MessageFormat;
import java.util.Iterator;

import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.transfer.components.annotations.MapParameter;
import gov.loc.repository.transfer.components.fileexamination.LCManifestGenerator;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

public class GenerateLCManifestActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;

	@ConfigurationField
	public String algorithmName;
	
	@ContextVariable(name="destMountPath")
	public String mountPath;

	@ContextVariable(name="destStorageSystemId")
	public String storageSystemId;

	@ContextVariable(name="repositoryId")
	public String repositoryId;

	@ContextVariable(name="packageId")
	public String packageId;
	
	private Long fileLocationKey;
	
	@Override
	protected void initialize() throws Exception {
		Package packge = this.dao.findRequiredPackage(Package.class, this.repositoryId, this.packageId);
		FileLocation fileLocation = packge.getFileLocation(this.storageSystemId, this.mountPath);
		if (fileLocation == null)
		{
			throw new Exception(MessageFormat.format("Staging file location with system identifier value {0} and mount path {1} for {2} is not found", this.storageSystemId, this.mountPath, packge.toString()));
		}
		this.fileLocationKey = fileLocation.getKey();
	}
	
	@Override
	protected void execute() throws Exception {
		LCManifestGenerator generator = this.createObject(LCManifestGenerator.class);
		generator.generate(this.fileLocationKey, this.mountPath, this.algorithmName, this.getWorkflowAgentId());
	}

}