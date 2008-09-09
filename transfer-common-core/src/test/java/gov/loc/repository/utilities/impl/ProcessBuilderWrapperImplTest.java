package gov.loc.repository.utilities.impl;

import static org.junit.Assert.*;
import java.io.File;

import gov.loc.repository.utilities.OperatingSystemHelper;
import gov.loc.repository.utilities.ProcessBuilderWrapper;
import gov.loc.repository.utilities.ProcessBuilderWrapper.ProcessBuilderResult;

import org.junit.Test;

public class ProcessBuilderWrapperImplTest {

	@Test
	public void testExecute() {
		ProcessBuilderWrapper pb = new ProcessBuilderWrapperImpl();
		String command = "echo foo";
		if (OperatingSystemHelper.isLinux() || OperatingSystemHelper.isWindows())
		{
			//Making sure pipe works
			command += " | xargs echo";
		}
		ProcessBuilderResult result = pb.execute(new File("."), command);
		assertEquals("foo", result.getOutput());
		assertEquals(0, result.getExitValue());
	}

}
