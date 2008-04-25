package gov.loc.repository.springframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesEditor extends
		org.springframework.beans.propertyeditors.PropertiesEditor {

	static final String CLASSPATH_PREFIX = "classpath:";
	static final Log log = LogFactory.getLog(PropertiesEditor.class);
	
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		log.debug("Text to convert to property is " + text);
		if (text.startsWith(CLASSPATH_PREFIX))
		{
			log.debug("Loading property from resource");
			InputStream in = PropertiesEditor.class.getClassLoader().getResourceAsStream(text.substring(CLASSPATH_PREFIX.length()));
			if (in == null)
			{
				throw new IllegalArgumentException("Resource not found: " + text);
			}
			Properties props = new Properties();
			try
			{
				props.load(in);
			}
			catch(IOException ex)
			{
				throw new IllegalArgumentException(ex);
			}
			log.debug("Props is " + props.toString());
			this.setValue(props);
		}
		else
		{
			super.setAsText(text);
		}
	}
}
