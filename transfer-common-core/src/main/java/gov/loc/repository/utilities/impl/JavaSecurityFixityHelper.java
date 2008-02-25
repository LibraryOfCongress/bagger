package gov.loc.repository.utilities.impl;

import gov.loc.repository.utilities.FixityHelper;

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("fixityhelper")
@Scope("prototype")
public class JavaSecurityFixityHelper implements FixityHelper {

	private static final Log log = LogFactory.getLog(JavaSecurityFixityHelper.class);	
	
    private static final int BUFFERSIZE = 65536;
    private String algorithm;
    
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}    
    
	public String getAlgorithm() {
		return this.algorithm;
	}	
	
	public String generateFixity(File file) throws Exception {
		
		log.debug(MessageFormat.format("Generating {0} fixity for {1}", this.algorithm, file.toString()));
		MessageDigest md = MessageDigest.getInstance(this.algorithm);
		FileInputStream fis = new FileInputStream(file);
		byte[] dataBytes = new byte[BUFFERSIZE];
		int nread = fis.read(dataBytes);
		while (nread > 0)
		{
			md.update(dataBytes, 0, nread);
		    nread = fis.read(dataBytes);
		}
		return new String(Hex.encodeHex(md.digest()));	
		
	}	
	
}
