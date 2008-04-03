package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.dao.WorkflowDao;
import gov.loc.repository.transfer.ui.model.WorkflowBeanFactory;
import gov.loc.repository.transfer.ui.models.Report;
import gov.loc.repository.transfer.ui.reports.AbstractReport;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping; 

@Controller
public class ReportsController extends AbstractRestController {

	protected static final Log log = LogFactory.getLog(ReportsController.class);
	public static final String REPORT_ID = "reportId";
	public static final String REPORT_NAMESPACE = "reportNamespace";
	
	protected Map<String, AbstractReport> reports;
	public void setReports(Map<String, AbstractReport> reports){
	    this.reports = reports;
	}
	
	@Override
	public String getUrlParameterDescription() {
		return "reports/{reportNamespace}_{reportId}\\.{format}";
	}

	@RequestMapping("/reports/*.*")
	@Override
	public ModelAndView handleRequest(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception 
	{
		return this.handleRequestInternal(request, response);
	}
	
	@Override
	protected void handleIndex(
	        HttpServletRequest request, 
	        ModelAndView mav,
			WorkflowBeanFactory factory, 
			WorkflowDao dao, 
			PermissionsHelper permissions, Map<String, String> urlParameterMap) throws Exception 
	{
		mav.setViewName("reports");
		mav.addObject("permissions", permissions);
		mav.addObject(
		    "reports", 
		    reports.values()
		);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handleGet(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        WorkflowBeanFactory factory, 
	        WorkflowDao dao, 
	        PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap) throws Exception 
	{
		//If there is no reportId in urlParameterMap then 404
		if (! urlParameterMap.containsKey(this.REPORT_ID)
		    || ! urlParameterMap.containsKey(this.REPORT_NAMESPACE)) {
		    log.info("INVALID REQUEST: missing expected parameters: " + urlParameterMap);
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		//Otherwise handle reportId
		String reportId = urlParameterMap.get(this.REPORT_ID);
		String reportNamespace = urlParameterMap.get(this.REPORT_NAMESPACE);
		AbstractReport report = null;
		for(AbstractReport availableReport:reports.values()){
		    if ((availableReport.getNamespace().equals(reportNamespace)) && 
		        (availableReport.getId() == Long.parseLong(reportId)) ) {
		        report = availableReport;
		        log.debug("Found report. Namespace: " + report.getNamespace() + " ID: " +report.getId());
		        break;
		    }
	    }
	    if(report == null){
	        //Cant find a report with said id
	        log.warn("Can't find Report with namespace " + reportNamespace + " and ID: " +reportId);
	        mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		report.prepareReport();
		mav.addObject("report",(Report)report);
		mav.setViewName("report");

		//The session for packageModelDao should be set in an interceptor or filter
	    //packageModelDao.setSession(jbpmContext.getSession());
		//Throws Caused by: org.hibernate.hql.ast.QuerySyntaxException: Repository is not mapped [from Repository]
		//List<Repository> repositories = packageModelDao.findRepositories();
		/*
	    List<Repository> repositories = jbpmContext.getSession().createQuery(
				    "from gov.loc.repository.packagemodeler.packge.Repository"
				).list();

		mav.addObject("results",repositories);
				*/
		
		//Throws Caused by: org.hibernate.hql.ast.QuerySyntaxException: Package is not mapped
	}
		
	@Override
	protected void handlePut(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        WorkflowBeanFactory factory,
	        WorkflowDao dao, 
	        PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap ) throws Exception 
	{
		//If there is no reportId in urlParameterMap then 404
		if (! urlParameterMap.containsKey(this.REPORT_ID)) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		//Otherwise handle reportId
		String reportId = urlParameterMap.get(this.REPORT_ID);
		//TODO make sure report exists based on the reportId
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initApplicationContext() throws BeansException {
		ApplicationContext applicationContext = 
		    this.getApplicationContext();
		Map<String, AbstractReport> reports =
		        applicationContext.getBeansOfType(AbstractReport.class);
		setReports(reports);
		super.initApplicationContext();
	}
	
	
    
}
