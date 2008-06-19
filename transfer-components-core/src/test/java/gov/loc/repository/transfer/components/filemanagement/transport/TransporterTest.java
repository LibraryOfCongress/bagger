package gov.loc.repository.transfer.components.filemanagement.transport;

import gov.loc.repository.transfer.components.filemanagement.transport.Transporter;

public class TransporterTest {
    public static void main(String[] args) {
        Transporter transporter = new Transporter("/home/mjg/.ssh/id_rsa");
        transporter.transport("/home/mjg/tmp/test1", "mgia@ga.rdc.lctl.gov:tmp/test2");
        transporter.transport("mgia@ga.rdc.lctl.gov:tmp/test2", "/home/mjg/tmp/test3");  
    }
}