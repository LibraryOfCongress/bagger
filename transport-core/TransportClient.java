import gov.loc.repository.transfer.components.filemanagement.transport.*;

public class TransportClient {
    public static void main(String[] args) {
        Transporter transporter = new Transporter("/home/mjg/.ssh/id_rsa");
        transporter.transport("/home/mjg/tmp/test1", "mgia@rg.rdc.lctl.gov:tmp/test2");
        transporter.transport("mgia@rg.rdc.lctl.gov:tmp/test2", "/home/mjg/tmp/test3");  
    }
}
