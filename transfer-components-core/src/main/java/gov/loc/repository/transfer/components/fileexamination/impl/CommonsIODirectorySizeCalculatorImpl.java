package gov.loc.repository.transfer.components.fileexamination.impl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.loc.repository.transfer.components.AbstractComponent;
import gov.loc.repository.transfer.components.fileexamination.DirectorySizeCalculator;

@Component("commonsIODirectorySizeCalculatorComponent")
@Scope("prototype")
public class CommonsIODirectorySizeCalculatorImpl extends AbstractComponent implements
		DirectorySizeCalculator {

	private Long size;
		
	@Override
	protected String getComponentName() {
		return COMPONENT_NAME;
	}

	@Override
	public void calculate(String mountPath) throws Exception {
		this.size = FileUtils.sizeOfDirectory(new File(mountPath));
		
	}
	
	@Override
	public Long getDirectorySize() {
		return this.size;
	}

}
