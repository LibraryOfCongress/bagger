package gov.loc.repository.transfer.ui.reports;

import gov.loc.repository.transfer.ui.models.Report; 
import gov.loc.repository.utilities.results.ResultIterator;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Component
public class CoreReport00 extends CoreReport {
    
    protected String fileType = "tif";//default
    protected ResultIterator resultSet;
    protected Map<String, Long> data;
    
    public CoreReport00(){
        setId(new Long(0));
        setName("File Count by Type");
        setDescription("This report allows you to view the number of files of a specified type for all transfered packages.");
        Map filters = new HashMap<String, List<String>>();
        List<String> fileTypeFilters = new ArrayList<String>(); 
        fileTypeFilters.add("xml");
        fileTypeFilters.add("jp2");
        fileTypeFilters.add("tif");
        fileTypeFilters.add("pdf");
        filters.put("fileType", fileTypeFilters);
        setFilters(filters);
    }
    
    protected void gatherData() throws Exception{
         this.resultSet = this.packageDao.findPackagesWithFileCount(
            Package.class,
            this.fileType
        );
        log.debug("Gathered Data for Core Report 00");
    }
    
    protected void processData(){
        this.data = new HashMap<String, Long>();
        while(this.resultSet.hasNext()){
			Map<String, Object> result = this.resultSet.next();
			Package packge = (Package)result.get("package");
			Long fileCount =  (Long)result.get("file_count");
			this.data.put(packge.getPackageId(), fileCount);
			log.debug("Processing Data for Core Report 00. Added " + 
			            "\n\tpackage: " + packge +
			            "\n\tfileCount: " + fileCount );
		}
    }
    
    public String getFileType(){
        return this.fileType;
    }
    public void setFileType(String fileType){
        this.fileType = fileType;
    }
}
