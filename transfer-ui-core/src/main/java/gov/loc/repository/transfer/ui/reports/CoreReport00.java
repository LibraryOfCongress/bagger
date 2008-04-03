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
    
    protected String fileType = "all";//default
    protected ResultIterator resultSet;
    
    public CoreReport00(){
        super();
        setId(new Long(0));
        setName("File Count by Type");
        setDescription("This report allows you to view the number of files of a specified type for all transfered packages.");
        Map filters = new HashMap<String, List<String>>();
        List<String> fileTypeFilters = new ArrayList<String>(); 
        fileTypeFilters.add("all");
        fileTypeFilters.add("xml");
        fileTypeFilters.add("jp2");
        fileTypeFilters.add("tif");
        fileTypeFilters.add("pdf");
        filters.put("fileType", fileTypeFilters);
        setFilters(filters);
    }
    
    protected void gatherData() throws Exception{
        this.data = new HashMap<String, Object>();
        log.debug("Gathering Data for Core Report 00");
        if(this.fileType.equals("all") || this.fileType.equals("xml")){
            this.data.put("xml", this.packageDao.findPackagesWithFileCount(
                Package.class, "xml"
            ));
        }
        if(this.fileType.equals("all") || this.fileType.equals("jp2")) {
             this.data.put("jp2", this.packageDao.findPackagesWithFileCount(
                Package.class, "jp2"
            ));
        }
        if(this.fileType.equals("all") || this.fileType.equals("tif")) {
             this.data.put("tif", this.packageDao.findPackagesWithFileCount(
                Package.class, "tif"
            ));
        }
        if(this.fileType.equals("all") || this.fileType.equals("pdf")) {
             this.data.put("pdf", this.packageDao.findPackagesWithFileCount(
                Package.class, "pdf"
            ));
        }
        log.debug("Gathered Data for Core Report 00");
    }
    
    protected void processData(){
        log.debug("Processing Data for Core Report 00");
        Map<String, Object> globalStatistics = new HashMap<String, Object>();
        long total = 0;
        for(String filterType:this.data.keySet()){//think $report.data.jp2.stats
            ResultIterator resultSet = (ResultIterator)this.data.get(filterType);
            Map<String, Object> dataByFileType = new HashMap<String, Object>();
            List results = new ArrayList();
            List datapoints = new ArrayList();
            Map<String, Object> statistics = new HashMap<String, Object>();
            long subtotal = 0;
            long maxValue = 0;
            long minValue = -1;
            long avgValue = 0;
            long i = 0;
            
            //Save the resultSet as is
            while(resultSet.hasNext()){
    			Map<String, Object> result = resultSet.next();
    			results.add(result);
    			Package packge = (Package)result.get("package");
    			Long fileCount =  (Long)result.get("file_count");
    			datapoints.add(fileCount.longValue());
    			subtotal    += fileCount.longValue();
    			total       += fileCount.longValue();
    			avgValue    += fileCount.longValue();
    			if(fileCount.longValue() > maxValue){ maxValue = fileCount.longValue();}
    			if(minValue == -1 || (fileCount.longValue() < minValue) ){ 
    			    minValue = fileCount.longValue();
    			}
    			i++;
    			log.debug("Processing Data for Core Report 00. Added " + 
    			            "\n\tpackage: " + packge +
    			            "\n\tfileCount: " + fileCount );
    		}
    		dataByFileType.put("fileType", filterType);
            dataByFileType.put("results", results);
        
    		avgValue = avgValue/i;
    		log.debug("\n\tMaximum value: "  + maxValue 
    		        +"\n\tAverage value: " + avgValue
    		        +"\n\tMinimum value: " + minValue);
    		statistics.put("max", maxValue);
    		statistics.put("avg", avgValue);
    		statistics.put("min", minValue);
    		statistics.put("maxpercent", 100);
    		statistics.put("avgpercent", avgValue*100/(maxValue>0?maxValue:1));
    		statistics.put("minpercent", minValue*100/(maxValue>0?maxValue:1));
    		statistics.put("count", i);
    		statistics.put("subtotal", subtotal);
    		statistics.put("joinedpoints", join(datapoints,","));
    		dataByFileType.put("stats", statistics);
    		//replace the results iterator with the processed map of data
    		this.data.put(filterType, dataByFileType);
		}
		globalStatistics.put("total", total);
        this.data.put("globalStats", globalStatistics);
        this.data.put("fileType", this.fileType);//the selected filetype (* may be 'all' or should allow multi selection)!
        log.debug("Completed Processing Data for Core Report 00");
    }
    
    
    private String join(List datapoints, String seperator){
        String joined = "";
        log.debug("Joining data points:" + datapoints.toString());
        for(int i= 0; i < datapoints.size(); i++){
            joined+= datapoints.get(i) + (((i+1)<datapoints.size())?seperator:"");
        }
        log.debug("Joined data points:" + joined);
        return joined;
    }
    
    
    public String getFileType(){
        return this.fileType;
    }
    public void setFileType(String fileType){
        this.fileType = fileType;
    }
    
}
