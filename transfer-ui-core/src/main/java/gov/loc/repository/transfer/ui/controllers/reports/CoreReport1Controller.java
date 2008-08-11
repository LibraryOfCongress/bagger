package gov.loc.repository.transfer.ui.controllers.reports;

import gov.loc.repository.packagemodeler.packge.Package;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CoreReport1Controller extends AbstractCoreReportController {

	@Override
	public String getDescription() {
		return "This report provides details on a package, including events and files.";
	}

	@Override
	public String getName() {
		return "Package detail report";
	}

	@RequestMapping("corereport1.html")
	public ModelAndView handle(@RequestParam(required=false) Long packageKey) throws Exception
	{
		ModelAndView mav = new ModelAndView();
		if (packageKey != null)
		{
			mav.addObject("package", this.packageModelDao.loadRequiredPackage(packageKey));
		}

		mav.addObject("packageList", this.packageModelDao.findPackages(Package.class));
				
		return mav;
	}
	
}
