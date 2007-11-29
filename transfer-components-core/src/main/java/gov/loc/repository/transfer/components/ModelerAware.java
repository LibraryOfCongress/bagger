package gov.loc.repository.transfer.components;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

public interface ModelerAware {
	public void setPackageModelDao(PackageModelDAO dao);
	
	public void setModelerFactory(ModelerFactory factory);

}
