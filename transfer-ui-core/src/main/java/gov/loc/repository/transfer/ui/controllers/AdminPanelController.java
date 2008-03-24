package gov.loc.repository.transfer.ui.controllers;

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
import org.jbpm.JbpmContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminPanelController extends AbstractRestController {

	protected static final Log log = LogFactory.getLog(AdminPanelController.class);
	public static final String SERVICE_ID = "serviceId";
	
	@Override
	public String getUrlParameterDescription() {
		return "admin/{service}\\.{format}";
	}

	@RequestMapping("/admin/*.*")
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
		
		mav.setViewName("admin");
		mav.addObject("permissions", permissions);
		//This is a naive implementation for now.  The only system process I
		//know of is a the active mq 'job listener'.  I think the best solution
		//here is to use a Dao that loads xml based system process descriptions
		//from a specific path.
		List<SystemProcess> systemProcesses = new ArrayList<SystemProcess>();
		SystemProcess systemProcess = new SystemProcess();
		JmsCompletedJobListener listener = new ActiveMQJmsCompletedJobListener();	
		systemProcess.setId("amq-jsl");
		systemProcess.setName("Automated Task Monitor");
		systemProcess.setSuspended(new Boolean(!listener.isStarted()));
		systemProcess.setDescription(
		    "\n\tMany tasks, though configured manually, run automatically.  For example, the network " +
		    "transport, or verification of file checksums.  The Automated Task Monitor listens on a " +
		    "special network called a Message Queue so the manual workflow steps can continue when " + 
		    "the automated steps are completed." + 
		    "\n\tAdministrators should be able to start/stop/restart this network connection in the event "+
		    "that the Message Queue was brought down unexpectedly or requires scheduled maintenence."
		);
		systemProcesses.add(systemProcess);
		mav.addObject(
		    "systemProcesses", 
		    systemProcesses
		);
		
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
	}
	
	
	
}
