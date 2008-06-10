package gov.loc.repository.bagit.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;

public class Md5DeepManifestGeneratorVerifierTest extends AbstractManifestGeneratorVerifierTest {

	private static Log log = LogFactory.getLog(Md5DeepManifestGeneratorVerifierTest.class);
			
	@Before
	public void setup() throws Exception {
		//Look for md5deep
		Map<String, String> commandMap = new HashMap<String, String>();
	    for (String command : new String[] {"c:/md5deep/md5deep.exe", "c:/Program Files/md5deep\\md5deep.exe", "/usr/bin/md5deep"})
		{
			File file = new File(command);
			if (file.exists())
			{
				commandMap.put("md5", command);
				canRunTest = true;
				break;
			}
		}
		
		if (! canRunTest)
		{
			log.warn("Can't run test because can't find md5deep");
		}
		
		generator = new Md5DeepManifestGeneratorVerifierImpl(commandMap);
		
	}
		
}
