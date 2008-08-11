package gov.loc.repository.transfer.ui.controllers;

import java.util.List;

import gov.loc.repository.transfer.ui.controllers.reports.Report;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReportsController
{
	List<Report> reportList;
	
	@Autowired
	public void setReportList(List<Report> reportList)
	{
		this.reportList = reportList;
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView handleIndex(HttpServletResponse resp) throws Exception
    {
    	ModelAndView mav = new ModelAndView("reports");
    	mav.addObject("reportList", this.reportList);
    	return mav;
    }
}
