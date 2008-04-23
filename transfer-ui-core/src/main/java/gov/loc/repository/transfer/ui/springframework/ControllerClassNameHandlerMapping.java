package gov.loc.repository.transfer.ui.springframework;

import java.text.MessageFormat;

public class ControllerClassNameHandlerMapping
extends org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping
{
	
	private Class<?> controllerMarker;

	@Override
	protected String[] buildUrlsForHandler(String beanName, Class beanClass) {
		String[] urlArray = super.buildUrlsForHandler(beanName, beanClass);
		if (this.controllerMarker != null && ! controllerMarker.isAssignableFrom(beanClass))
		{
			logger.debug(MessageFormat.format("Returning no urls for {0} is not assignable from {1}", beanName, this.controllerMarker));
			return new String[0];
		}
		//Edit the urlArray
		for(int i=0; i < urlArray.length; i++)
		{
			if (urlArray[i].endsWith("/*"))
			{
				urlArray[i] = urlArray[i].substring(0, urlArray[i].length()-2) + ".*";
				logger.debug("Changed url to " + urlArray[i]);
			}
		}
		return urlArray;
	}

	public void setControllerMarker(Class<?> clazz)
	{
		this.controllerMarker = clazz;
	}	
	
}
