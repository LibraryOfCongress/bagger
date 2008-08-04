package gov.loc.repository.transfer.components.filemanagement;

import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;
import gov.loc.repository.service.Component;

public interface FileSystemCreator extends Component {

    static final String COMPONENT_NAME = "filesystemcreator";
    
    @JobType(name="filesystemcreate")
    public void create(
            @RequestParam(name="mountpath") String mountPath,
            @RequestParam(name="size") Long size         
            )
            throws Exception;
    
        
}
