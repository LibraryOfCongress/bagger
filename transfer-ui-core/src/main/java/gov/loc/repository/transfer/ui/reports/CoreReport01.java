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
public class CoreReport01 extends CoreReport {
    
    protected String fileType = "packageId";//default
    protected ResultIterator resultSet;
    protected Map<String, String> chart;
    protected String chartUri;
    
    public CoreReport01(){
        super();
        setId(new Long(1));
        setName("Package Details");
        setDescription("This report allows you to view the general statistics for a given package.");
    }
    
    protected void gatherData() throws Exception{
        Map filters = new HashMap<String, List<String>>();
        List<String> fileTypeFilters = new ArrayList<String>(); 
        filters.put("packageId", fileTypeFilters);
        setFilters(filters);
        
         this.resultSet = this.packageDao.findPackagesWithFileCount(
            Package.class,
            this.fileType
        );
        log.debug("Gathered Data for Core Report 00");
    }
    
    protected void processData(){
        chart = new HashMap<String, String>();
        chart.put("cht","bvg");//vertical bar graph
        chart.put("chd","t:");//text encoded data (use simple encoding to reduce url size)
        chart.put("chds","0,");//data scale( still need to set max)"
        chart.put("chbh","10");//width of bars in pixels
        chart.put("chxt","y,r");//axis label types to be included
        chart.put("chxr","1,0,");//range for right axis labels (max still needs to be set)
        chart.put("chxl","0:|min|average|max");//labels for x-axis (0)
        chart.put("chxp","0,");//positions for x-axis labels (0) values still need to be set
        
        this.data = new HashMap<String, Map>();
        long maxValue = 0;
        long minValue = -1;
        long avgValue = 0;
        long i = 0;
        String chd = chart.get("chd");
        while(this.resultSet.hasNext()){
			Map<String, Object> result = this.resultSet.next();
			Package packge = (Package)result.get("package");
			Long fileCount =  (Long)result.get("file_count");
			chd += fileCount.toString() + (resultSet.hasNext()?",":"");
			chart.put("chd", chd);
			avgValue += fileCount.longValue();
			if(fileCount.longValue() > maxValue){ maxValue = fileCount.longValue();}
			if(minValue == -1 || (fileCount.longValue() < minValue) ){ 
			    minValue = fileCount.longValue();
			}
			data.put(packge.getPackageId(), result);
			i++;
			log.debug("Processing Data for Core Report 00. Added " + 
			            "\n\tpackage: " + packge +
			            "\n\tfileCount: " + fileCount +
			            "\n\tchartUri: " + chart2uri(this.chart));
		}
		avgValue = avgValue/i;
		log.debug("Average value: " + avgValue);
		String chds = chart.get("chds");
		chds +=  maxValue;
		chart.put("chds", chds);
		String chxr = chart.get("chxr");
		chxr += new Long(maxValue).toString();
		chart.put("chxr", chxr);
		String chxp = chart.get("chxp");
		chxp += new Long((minValue*100/maxValue)).toString()+",";
		chxp += new Long((avgValue*100/maxValue)).toString()+",";
		chxp += 100;
		chart.put("chxp", chxp);
	    setChartUri( chart2uri(this.chart) );
		log.debug("chart uri: " + getChartUri());
    }
    
    private String chart2uri(Map<String,String> chart){
        String url = "";
        for(String key:chart.keySet()){
            url+="&"+key;
            url+="="+chart.get(key);
        }
        return url;
    }
    
    public String getChartUri(){
        return this.chartUri;
    }
    public void setChartUri(String chartUri){
        this.chartUri = chartUri;
    }
    public String getFileType(){
        return this.fileType;
    }
    public void setFileType(String fileType){
        this.fileType = fileType;
    }
}
