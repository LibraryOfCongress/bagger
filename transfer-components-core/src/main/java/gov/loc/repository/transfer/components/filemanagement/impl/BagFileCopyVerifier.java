package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.loc.repository.bagit.bag.BagGeneratorVerifier;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.FileCopyVerifier;


@Component("bagFileCopyVerifier")
public class BagFileCopyVerifier implements FileCopyVerifier {

	private static final Log log = LogFactory.getLog(BagFileCopyVerifier.class);
	
	private BagGeneratorVerifier verifier;
	
	@Autowired
	public BagFileCopyVerifier(@Qualifier("javaSecurityBagGeneratorVerifier")BagGeneratorVerifier verifier) {
		this.verifier = verifier;
	}
	
	@Override
	public boolean verify(CopyDescription copyDescription) {
        SimpleResult result = this.verifier.isValid(new File(copyDescription.destCopyToPath));
        if (result.isSuccess()) {
            log.info(MessageFormat.format("Package transported to {0} verified as a valid bag.", copyDescription.destCopyToPath));
            return true;
        }
        log.error(MessageFormat.format("Package at {0} not valid: {1}", copyDescription.destCopyToPath, result.getMessage()));
        return false;        
	}

}
