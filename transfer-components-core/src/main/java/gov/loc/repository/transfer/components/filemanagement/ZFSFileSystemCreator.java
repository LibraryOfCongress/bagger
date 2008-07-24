package gov.loc.repository.transfer.components.filemanagement;

import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;
import gov.loc.repository.service.Component;

public interface ZFSFileSystemCreator extends Component {

    static final String COMPONENT_NAME = "zfsfilesystemcreator";
    
    @JobType(name="zfsfilesystemcreate")
    public void create(
            @RequestParam(name="mountpath") String mountPath,
            @RequestParam(name="size") Long size         
            )
            throws Exception;
    
        
}
