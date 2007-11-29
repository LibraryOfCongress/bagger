package gov.loc.repository.utilities.ndnp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import gov.loc.repository.utilities.ResourceHelper;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;


public class BatchHelperTest {
	@Test
	public void testDiscoverBatchFile() throws Exception {
		File batchDir = new File(this.getFile((String)null), "batchDir");
		try
		{
			if (batchDir.exists())
			{
				FileUtils.deleteDirectory(batchDir);
			}
			assertFalse(batchDir.exists());
			assertTrue(batchDir.mkdir());
			assertNull(BatchHelper.discoverBatchFile(batchDir));
			File batchFile = new File(batchDir, "batch.xml"); 
			this.createFile(batchFile);
			assertEquals(batchFile, BatchHelper.discoverBatchFile(batchDir));
			batchFile = new File(batchDir, "batch_1.xml");
			this.createFile(batchFile);
			assertEquals(batchFile, BatchHelper.discoverBatchFile(batchDir));
			batchFile = new File(batchDir, "batch_2.xml");
			this.createFile(batchFile);
			assertEquals(batchFile, BatchHelper.discoverBatchFile(batchDir));
		}
		finally
		{
			//FileUtils.deleteDirectory(batchDir);
		}
	}
		
	@Test
	public void testCharacterize() throws Exception {
		File batchDir = this.getFile("batch");
		BatchCharacterization characterization = BatchHelper.characterize(batchDir.toString());
		
		assertEquals("fooBatch", characterization.getPackageId());
		
		List<String> lccnList = characterization.getLccnList();
		assertEquals(2, lccnList.size());
		assertTrue(lccnList.contains("c"));
		assertTrue(lccnList.contains("d"));
		
		List<String> reelNumberList = characterization.getReelNumberList();
		assertEquals(2, reelNumberList.size());
		assertTrue(reelNumberList.contains("e"));
		assertTrue(reelNumberList.contains("f"));

	}
	
	
	private void createFile(File file) throws Exception
	{
		FileWriter writer = new FileWriter(file);
		writer.write("test");
		writer.close();
	}
	
	private File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this, filename);

	}		
	

}
