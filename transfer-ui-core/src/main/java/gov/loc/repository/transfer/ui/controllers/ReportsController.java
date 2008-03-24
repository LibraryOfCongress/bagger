package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.DaoAwareModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.impl.DaoAwareModelerFactoryImpl;
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
import org.jbpm.JbpmContext;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping; 

@Controller
public class ReportsController extends AbstractRestController {

	protected static final Log log = LogFactory.getLog(ReportsController.class);
	public static final String REPORT_ID = "reportId";
	
	
	protected DaoAwareModelerFactory modelerFactory = new DaoAwareModelerFactoryImpl();
	protected PackageModelDAO packageModelDao = new PackageModelDAOImpl();
	public void setPackagemodeldao(PackageModelDAO packageModelDao){
	    this.packageModelDao = packageModelDao;
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
			JbpmContext jbpmContext, 
			PermissionsHelper permissions, 
			Map<String, String> urlParameterMap) throws Exception 
	{
		if (request.getUserPrincipal() == null) {
			mav.setViewName("redirect:/login/login.html");
			return;
		}
		
		packageModelDao.setSession(jbpmContext.getSession());
		
		mav.setViewName("reports");
		mav.addObject("permissions", permissions);
		
		//Throws Caused by: org.hibernate.hql.ast.QuerySyntaxException: Repository is not mapped [from Repository]
		//List<Repository> repositories = packageModelDao.findRepositories();
		List<Repository> repositories = jbpmContext.getSession().createQuery(
				"from gov.loc.repository.packagemodeler.packge.Repository"
					).list();
		mav.addObject("repositories",repositories);
	}
	
	
	
}
