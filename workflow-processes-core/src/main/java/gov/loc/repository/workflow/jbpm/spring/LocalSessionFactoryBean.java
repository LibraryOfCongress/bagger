package gov.loc.repository.workflow.jbpm.spring;

import java.io.InputStream;

import org.jbpm.JbpmConfiguration;
import org.jbpm.configuration.ObjectFactory;
import org.jbpm.configuration.ObjectFactoryImpl;
import org.jbpm.configuration.ObjectFactoryParser;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

public class LocalSessionFactoryBean extends
		org.springframework.orm.hibernate3.LocalSessionFactoryBean {

	@Required
	public void setJbpmConfiguration(Resource configuration) throws Exception {
		InputStream stream = configuration.getInputStream();
		ObjectFactory jbpmObjectFactory = parseObjectFactory(stream);
		stream.close();
		JbpmConfiguration.Configs.setDefaultObjectFactory(jbpmObjectFactory);
	}

	private ObjectFactory parseObjectFactory(InputStream inputStream) {
	    logger.debug("loading defaults in jbpm configuration");
	    ObjectFactoryParser objectFactoryParser = new ObjectFactoryParser();
	    ObjectFactoryImpl objectFactoryImpl = new ObjectFactoryImpl();
	    //objectFactoryParser.parseElementsFromResource("org/jbpm/default.jbpm.cfg.xml", objectFactoryImpl);

	    if (inputStream!=null) {
	      logger.debug("loading specific configuration...");
	      objectFactoryParser.parseElementsStream(inputStream, objectFactoryImpl);
	    }
	    return objectFactoryImpl;
	  }
	
}
