package gov.loc.repository.fixity.impl;

import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.fixity.FixityGenerator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("javaSecurityFixityGenerator")
public class JavaSecurityFixityGenerator implements FixityGenerator {

	private static final Log log = LogFactory.getLog(JavaSecurityFixityGenerator.class);	
	
    private static final int BUFFERSIZE = 65536;
    
    @Override
    public boolean canGenerate(FixityAlgorithm algorithm) {
    	try
    	{
    		MessageDigest.getInstance(algorithm.getJavaSecurityName());
    	}
    	catch(NoSuchAlgorithmException ex)
    	{
    		return false;
    	}
    	return true;
    }
    
    @Override
	public String generateFixity(File file, FixityAlgorithm algorithm) {
		
		log.debug(MessageFormat.format("Generating {0} fixity for {1}", algorithm.getJavaSecurityName(), file.toString()));
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance(algorithm.getJavaSecurityName());
		}
		catch(NoSuchAlgorithmException ex)
		{
			throw new IllegalArgumentException(ex);
		}
		try
		{
			FileInputStream fis = new FileInputStream(file);
			byte[] dataBytes = new byte[BUFFERSIZE];
			int nread = fis.read(dataBytes);
			while (nread > 0)
			{
				md.update(dataBytes, 0, nread);
			    nread = fis.read(dataBytes);
			}
			fis.close();
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
		return new String(Hex.encodeHex(md.digest()));	
		
	}	

    @Override
    public boolean fixityMatches(File file, FixityAlgorithm algorithm,
    		String fixity) {
    	String checkFixity = this.generateFixity(file, algorithm);
    	if (checkFixity.equalsIgnoreCase(fixity))
    	{
    		return true;
    	}
    	return false;
    }
    
}
