package gov.loc.repository.transfer.components.fileexamination.impl;

import javax.annotation.Resource;

import org.junit.Test;
import static org.junit.Assert.*;
import gov.loc.repository.transfer.components.AbstractCorePackageModelerAwareComponentTest;
import gov.loc.repository.transfer.components.fileexamination.DirectorySizeCalculator;

public class CommonsIODirectorySizeCalculatorTest extends AbstractCorePackageModelerAwareComponentTest {

	@Resource(name="commonsIODirectorySizeCalculatorComponent")
	public DirectorySizeCalculator calculator;
	
	@Test
	public void testCalculate() throws Exception
	{
		calculator.calculate(this.getFile("batch").toString());
		assertEquals(Long.valueOf(305L), calculator.getDirectorySize());
	}
	
}
