package gov.loc.repository.utilities.ndnp;

import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Attribute;
import org.dom4j.io.SAXReader;


public class BatchHelper {
	public static String normalizePackageId(String originalPackageId) 
	{
		// Remove date string from packageId, e.g.,
		// convert batch_encyclopedia_20070808_barnard to batch_encyclopedia_barnard 	
		return originalPackageId.replaceAll("_\\d{8}_", "_");
	}

	public static File discoverBatchFile(File batchDir)
	{
		File bestGuessFile = null;
		int bestGuess = -1;
		File fileArray[] = batchDir.listFiles();
		for(File file : fileArray)
		{
			if (file.isFile() && file.getName().toUpperCase().startsWith("BATCH") && file.getName().toUpperCase().endsWith(".XML"))
			{				
				int guess = 0;
				if (file.getName().indexOf('_') > -1)
				{
					guess = Integer.parseInt(file.getName().substring(6, file.getName().length()-4));
				}
				if (guess > bestGuess)
				{
					bestGuess = guess;
					bestGuessFile = file;
				}
			}
		}
		return bestGuessFile;
	}
	
	public static BatchCharacterization characterize(InputStream batch) throws Exception
	{
		return characterize(loadBatch(batch));
	}	
	
	public static BatchCharacterization characterize(String batchDir) throws Exception
	{
		return characterize(loadBatch(new File(batchDir)));
	}
	
	private static BatchCharacterization characterize(Document batchDocument) throws Exception
	{
		BatchCharacterization characterization = new BatchCharacterization();
		//Get packageId
		characterization.setPackageId(getPackageId(batchDocument));
		//Get lccns
		characterization.setLccnList(getLccnList(batchDocument));
		//Get reel numbers
		characterization.setReelNumberList(getReelNumberList(batchDocument));
		
		return characterization;
		
	}
	
	private static Document loadBatch(InputStream batch) throws Exception
	{
		SAXReader reader = new SAXReader();
		return reader.read(batch);
	}
	
	private static Document loadBatch(File batchDir) throws Exception
	{
		File batchFile = BatchHelper.discoverBatchFile(batchDir);
		if (batchFile == null)
		{
			String msg = MessageFormat.format("Batch file not found in {0}", batchDir.toString());
			throw new Exception(msg);
		}
		SAXReader reader = new SAXReader();
		return reader.read(batchFile);
	}
	
	private static String getPackageId(Document batchDocument)
	{
		return batchDocument.getRootElement().attributeValue("name");
	}
	
	private static List<String> getLccnList(Document batchDocument)
	{
		List<String> lccnList = new ArrayList<String>();
		List lccnNodeList = batchDocument.selectNodes("/ndnp:batch/ndnp:issue/@lccn");
		for(Object obj : lccnNodeList)
		{
			String lccn = ((Attribute)obj).getText();
			if (! lccnList.contains(lccn))
			{
				lccnList.add(lccn);
			}
		}
		return lccnList;					
	}
	
	private static List<String> getReelNumberList(Document batchDocument)
	{
		List<String> reelNumberList = new ArrayList<String>();
		List reelNumberNodeList = batchDocument.selectNodes("/ndnp:batch/ndnp:reel/@reelNumber");
		for(Object obj : reelNumberNodeList)
		{
			String reelNumber = ((Attribute)obj).getText();
			if (! reelNumberList.contains(reelNumber))
			{
				reelNumberList.add(reelNumber);
			}
		}
		return reelNumberList;					
	}
}
