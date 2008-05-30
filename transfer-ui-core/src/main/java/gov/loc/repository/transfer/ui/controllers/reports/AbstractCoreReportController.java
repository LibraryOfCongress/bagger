package gov.loc.repository.transfer.ui.controllers.reports;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;

import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

public abstract class AbstractCoreReportController extends AbstractReportController {

	PackageModelDAO packageModelDao;
		
	@Override
	public String getCategory() {
		return "Core";
	}
	
	@Resource(name="packageModelDao")
	@Required
	public void setPackageModelDao(PackageModelDAO dao)
	{
		this.packageModelDao = dao;
	}
	
}
