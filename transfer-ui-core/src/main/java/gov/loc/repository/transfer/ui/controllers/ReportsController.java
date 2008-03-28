package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.DaoAwareModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.impl.DaoAwareModelerFactoryImpl;
import gov.loc.repository.transfer.ui.dao.WorkflowDao;
import gov.loc.repository.transfer.ui.model.WorkflowBeanFactory;
import gov.loc.repository.transfer.ui.models.Report;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import gov.loc.repository.workflow.listeners.JmsCompletedJobListener;
import gov.loc.repository.workflow.listeners.impl.ActiveMQJmsCompletedJobListener;
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
	
	protected Map<String, Report> reports;
	public void setReports(Map<String, Report> reports){
	    this.reports = reports;
	}
	
	@Override
	public String getUrlParameterDescription() {
		return "reports/{reportId}\\.{format}";
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
		if (! urlParameterMap.containsKey(this.REPORT_ID)) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		//Otherwise handle reportId
		String reportId = urlParameterMap.get(this.REPORT_ID);
		Report report = null;
		for(Report availableReport:reports.values()){
		    if (availableReport.getId() == Long.parseLong(reportId)) {
		        report = availableReport;
		        break;
		    }
	    }
	    if(report == null){
	        //Cant find a report with said id
	        mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
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
		Map< String, Report> reports =
		        applicationContext.getBeansOfType(Report.class);
		setReports(reports);
		super.initApplicationContext();
	}
	
	
	protected DaoAwareModelerFactory modelerFactory = new DaoAwareModelerFactoryImpl();
	protected PackageModelDAO packageModelDao = new PackageModelDAOImpl();
	public void setPackagemodeldao(PackageModelDAO packageModelDao){
	    this.packageModelDao = packageModelDao;
	}
}
