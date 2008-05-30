package gov.loc.repository.transfer.components;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/components-core-context.xml", "classpath:conf/packagemodeler-core-test-context.xml"})
public abstract class AbstractCorePackageModelerAwareComponentTest extends AbstractPackageModelerAwareComponentTest {
	
}
