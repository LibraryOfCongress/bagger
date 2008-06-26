package gov.loc.repository.transfer.components.filemanagement.transport;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;
import gov.loc.repository.transfer.components.filemanagement.transport.Transporter;

public class TransporterTest {
	@Test
	public void testTransporter() throws Exception {
        // test out the transporter class
/*
 * Not sure how best to mock this out.
 * 
 
        File stagingFile = new File("/tmp/staging/tmp");
        File archiveFile = new File("/tmp/archive/tmp");

        Transporter transporter = new Transporter("/home/mjg/.ssh/id_rsa");
        
        assertFalse(stagingFile.exists());
        assertFalse(archiveFile.exists());
        
        transporter.pull("mgia", "ga.rdc.lctl.gov", "/home/mgia/tmp/", "/tmp/staging/");

        assertTrue(stagingFile.exists());
        assertFalse(archiveFile.exists());

        transporter.archive("foobar", "/tmp/archive/");

        assertTrue(stagingFile.exists());
        assertTrue(archiveFile.exists());
 */
    }
}
