package gov.loc.repository.packagemodeler.agents.impl;

import gov.loc.repository.packagemodeler.AbstractCoreModelersTest;
import gov.loc.repository.packagemodeler.agents.Role;

import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static gov.loc.repository.constants.Roles.*;

public class RoleImplTest extends AbstractCoreModelersTest {

	@Test(expected = DataIntegrityViolationException.class)
	public void testNoDuplicates() throws Exception {
		Role role1 = new RoleImpl();
		role1.setId(STORAGE_SYSTEM);
		this.template.save(role1);
		Role role2 = new RoleImpl();
		role2.setId(STORAGE_SYSTEM);
		this.template.save(role2);

	}

}
