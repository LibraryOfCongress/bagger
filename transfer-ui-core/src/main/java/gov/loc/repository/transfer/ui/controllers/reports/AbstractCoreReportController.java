package gov.loc.repository.transfer.ui.controllers.reports;

import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCoreReportController extends AbstractReportController {

	PackageModelDAO packageModelDao;
		
	@Override
	public String getCategory() {
		return "Core";
	}
	
	@Autowired
	public void setPackageModelDao(PackageModelDAO dao)
	{
		this.packageModelDao = dao;
	}
	
}
