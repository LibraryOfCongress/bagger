package gov.loc.repository.transfer.components.filemanagement;

import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.MapParameter;
import gov.loc.repository.service.Component;

public interface ZFSFileSystemCreator extends Component {

    static final String COMPONENT_NAME = "zfsfilesystemcreator";
    
    @JobType(name="zfsfilesystemcreate")
    public void create(
            @MapParameter(name="mountpath") String mountPath,
            @MapParameter(name="size") Long size         
            )
            throws Exception;
    
        
}
