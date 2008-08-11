package gov.loc.repository.transfer.ui.controllers.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.ModelAttribute;

import gov.loc.repository.results.Result;

public abstract class AbstractReportController implements Report {

	private static final String CONTROLLER_SUFFIX = "controller";
	

	@ModelAttribute("reportName")
	public String populateReportName()
	{
		return this.getName();
	}

	@ModelAttribute("reportId")
	public String populateReportId()
	{
		return this.getId();
	}
	
	@ModelAttribute("reportDescription")
	public String populateReportDescription()
	{
		return this.getDescription();
	}
			
	public class ReportSection
	{
		private Map<String, String> sectionLabelMap = new HashMap<String, String>();
		private List<ReportSection> reportSectionList = new ArrayList<ReportSection>();
		private List<Result> resultList = new ArrayList<Result>();
		private Map<String, Object> summaryMap = new HashMap<String, Object>();
		
		public Map<String,String> getSectionLabelMap()
		{
			return this.sectionLabelMap;
		}
		
		public List<ReportSection> getReportSectionList()
		{
			return this.reportSectionList;
		}
		
		public Map<String, Object> getSummaryMap()
		{
			return this.summaryMap;
		}

		public void setResultList(List<Result> resultList) {
			this.resultList = resultList;
		}

		public List<Result> getResultList() {
			return resultList;
		}
	}
	
	@Override
	public String getId() {
		
		String className = this.getClass().getSimpleName().toLowerCase(); 
		return (className.endsWith(CONTROLLER_SUFFIX) ?
				className.substring(0, className.indexOf(CONTROLLER_SUFFIX)) : className);		
	}
}
