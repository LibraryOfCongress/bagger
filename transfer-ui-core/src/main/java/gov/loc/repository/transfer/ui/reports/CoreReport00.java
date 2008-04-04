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
    
    protected String[] fileTypes = {"xml", "jp2", "tif", "pdf"};//default 'all'
    protected ResultIterator resultSet;
    protected Map<String, Object> localData;
    
    public CoreReport00(){
        super();
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
        data = new HashMap<String, Map>();
        this.localData = new HashMap<String, Object>();
        log.debug("Gathering Data for Core Report 00");
        for(int i = 0; i < fileTypes.length ; i++){
            if(this.fileTypes[i].equals("xml")){
                this.localData.put("xml", this.packageDao.findPackagesWithFileCount(
                    Package.class, "xml"
                ));
            }
            if(this.fileTypes[i].equals("jp2")) {
                 this.localData.put("jp2", this.packageDao.findPackagesWithFileCount(
                    Package.class, "jp2"
                ));
            }
            if(this.fileTypes[i].equals("tif")) {
                 this.localData.put("tif", this.packageDao.findPackagesWithFileCount(
                    Package.class, "tif"
                ));
            }
            if(this.fileTypes[i].equals("pdf")) {
                 this.localData.put("pdf", this.packageDao.findPackagesWithFileCount(
                    Package.class, "pdf"
                ));
            }
        }
        log.debug("Gathered Data for Core Report 00");
    }
    
    protected void processData(){
        log.debug("Processing Data for Core Report 00");
        summary  = new HashMap<String, Object>();
        List datasets = new ArrayList();
        List datasetTotals = new ArrayList();
        long globalTotal = 0;
        long globalMaxValue = 0;
        long globalMinValue = -1;
        long globalAvgValue = 0;
        
        //For each filter (filetype by extension) collect some data/stats regarding that type
        //Also add some global data/stats to the overview/summary datasets
        for(String filterType:this.fileTypes){
            ResultIterator resultSet = (ResultIterator)this.localData.get(filterType);
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
    			globalTotal += fileCount.longValue();
    			avgValue    += fileCount.longValue();
    			if(fileCount.longValue() > maxValue){ maxValue = fileCount.longValue();}
    			if(minValue == -1 || (fileCount.longValue() < minValue) ){ 
    			    minValue = fileCount.longValue();
    			}
    			i++;
    			log.debug("Processing Data for Core Report 00. Added " + 
    			            "\n\tpackage: "     + packge +
    			            "\n\tfileCount: "   + fileCount );
    		}
    		avgValue = avgValue/i;
    		log.debug("\n\tMaximum value: " + maxValue 
    		        +"\n\tAverage value: "  + avgValue
    		        +"\n\tMinimum value: "  + minValue);
    		
    		dataByFileType.put("fileType"   , filterType);
            dataByFileType.put("results"    , results);
    		statistics.put("max"            , maxValue);
    		statistics.put("avg"            , avgValue);
    		statistics.put("min"            , minValue);
    		statistics.put("maxpercent"     , 100);
    		statistics.put("avgpercent"     , avgValue*100/(maxValue>0?maxValue:1));
    		statistics.put("minpercent"     , minValue*100/(maxValue>0?maxValue:1));
    		statistics.put("filecount"      , i);
    		statistics.put("subtotal"       , subtotal);
    		statistics.put("joinedpoints"   , join(datapoints,","));
    		dataByFileType.put("stats"      , statistics);
    		data.put(filterType, dataByFileType);
    		
    		//take care of some global stats should look alot like
    		//the local stats
    		datasets.add(join(datapoints,","));
    		if(maxValue > globalMaxValue){ globalMaxValue = maxValue;}
    		if(globalMinValue == -1 || (minValue < globalMinValue) ){ 
    			    globalMinValue = minValue;
    		}
    		globalAvgValue    += avgValue;
    		datasetTotals.add(subtotal);
		}
		log.debug("\n\tGlobal Maximum value: " + globalMaxValue 
    		     +"\n\tGlobal Average value: "  + globalAvgValue
    		     +"\n\tGlobal Minimum value: "  + globalMinValue);
		summary.put("globalfilecount",  globalTotal);
		summary.put("globalmin",        globalMinValue);
		summary.put("globalmax",        globalMaxValue);
		summary.put("globalavg",        globalAvgValue);
    	summary.put("globalmaxpercent"     , 100);
    	summary.put("globalavgpercent"     , globalAvgValue*100/(globalMaxValue>0?globalMaxValue:1));
    	summary.put("globalminpercent"     , globalMinValue*100/(globalMaxValue>0?globalMaxValue:1));
    	summary.put("joinedsettotals",  join(datasetTotals,","));
    	summary.put("joinedsets",       join(datasets,"|"));
        summary.put("filetypes", this.fileTypes);//the selected filetype (* may be 'all' or should allow multi selection)!
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
    
    
}
