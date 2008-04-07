package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.dao.WorkflowDao;
import gov.loc.repository.transfer.ui.model.WorkflowBeanFactory;
import gov.loc.repository.transfer.ui.models.SystemProcess;
import gov.loc.repository.transfer.ui.models.Variable;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {

	protected static final Log log = LogFactory.getLog(AdminController.class);

	@RequestMapping("/admin/index.html")
	public ModelAndView handleIndex(HttpServletRequest request) 
	{
		ModelAndView mav = new ModelAndView();
		log.debug("Handling request at /admin/index.html");
		
		mav.setViewName("admin");
		mav.addObject("contextPath", request.getContextPath());
		
		Runtime runtime = Runtime.getRuntime();
		mav.addObject("totalMemory", runtime.totalMemory()/1024);
		mav.addObject("maxMemory", runtime.maxMemory()/1024);
		mav.addObject("freeMemory", runtime.freeMemory()/1024);
		
		List<Variable> systemInfo = new ArrayList<Variable>();
		Properties systemProps = System.getProperties();
		for(Object prop:systemProps.keySet()){
		    Variable info = new Variable();
		    info.setName(prop.toString());
		    info.setValue(
		        //splits up long classpaths so wrapping isnt a hassle
		        ((String)systemProps.get(prop)).replace(":",": ")
		    );
		    systemInfo.add(info);
		}
		mav.addObject("systemInfo", systemInfo);
		return mav;
	}
	
}
