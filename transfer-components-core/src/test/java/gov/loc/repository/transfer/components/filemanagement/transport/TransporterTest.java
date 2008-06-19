package gov.loc.repository.transfer.components.filemanagement.transport;

import gov.loc.repository.transfer.components.filemanagement.transport.Transporter;

public class TransporterTest {
    public static void main(String[] args) {
        // assumes local transfer account's pubkey has been added
        //   to authorized_keys for remote transfer account and
        //   local ndnp account
        Transporter t1 = new Transporter("/home/transfer/.ssh/id_rsa");
        t1.transport("transfer@ga.rdc.lctl.gov:tmp/test", "test");  
        t1.transport("test", "ndnp@localhost:/ndnp_sips/test");
    }
}