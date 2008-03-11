package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.model.VariableUpdatingBean;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

public class VariableUpdateHelper {
	@SuppressWarnings("unchecked")
	public static boolean requestUpdatesVariables(HttpServletRequest request)
	{
		Iterator<String> keyIter = request.getParameterMap().keySet().iterator();
		while(keyIter.hasNext())
		{
			if (keyIter.next().startsWith(UIConstants.PREFIX_VARIABLE))
			{
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static void update(HttpServletRequest request, VariableUpdatingBean bean)
	{
		Iterator<String> iter = request.getParameterMap().keySet().iterator();
		while(iter.hasNext())
		{
			String key = iter.next();
			if (key.startsWith(UIConstants.PREFIX_VARIABLE))
			{
				String extractedKey = key.substring(UIConstants.PREFIX_VARIABLE.length());
				String value = request.getParameter(key);
				if (value != null && value.length() == 0)
				{
					value = null;
				}
				bean.setVariable(extractedKey, value);					
				
			}
		}
		
	}

}
