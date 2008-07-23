package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;

import gov.loc.repository.transfer.components.filemanagement.ZFSFileSystemCreator;
import gov.loc.repository.utilities.ProcessBuilderWrapper;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ZFSFileSystemCreatorImplTest {

	static final String POOL = "pool";
	static final String MOUNTPATH = "/staging/packages";
	static final String FILESYS = "foo";
	
	Mockery context = new JUnit4Mockery();
	ProcessBuilderWrapper mockPb = context.mock(ProcessBuilderWrapper.class);
	
	@Test
	public void testCreate() throws Exception {
		context.checking(new Expectations() {{
			one(mockPb).execute(with(aNonNull(File.class)), with(equal("zfs get -o value -H -p avail " + POOL + MOUNTPATH)));
			will(returnValue(new ProcessBuilderWrapper.ProcessBuilderResult(0, "600")));
			one(mockPb).execute(with(aNonNull(File.class)), with(equal("zfs create " + POOL + MOUNTPATH + "/" + FILESYS )));
			will(returnValue(new ProcessBuilderWrapper.ProcessBuilderResult(0, "")));			
		}});
		
		ZFSFileSystemCreator fsCreator = new ZFSFileSystemCreatorImpl(POOL, mockPb);
		fsCreator.create(MOUNTPATH + '/' + FILESYS, 500L);
	}

	@Test(expected=RuntimeException.class)
	public void testCreateWithoutEnoughSpace() throws Exception {
		context.checking(new Expectations() {{
			one(mockPb).execute(with(aNonNull(File.class)), with(equal("zfs get -o value -H -p avail " + POOL + MOUNTPATH)));
			will(returnValue(new ProcessBuilderWrapper.ProcessBuilderResult(0, "400")));
		}});
		
		ZFSFileSystemCreator fsCreator = new ZFSFileSystemCreatorImpl(POOL, mockPb);
		fsCreator.create(MOUNTPATH + '/' + FILESYS, 500L);
	}
	
	
}
