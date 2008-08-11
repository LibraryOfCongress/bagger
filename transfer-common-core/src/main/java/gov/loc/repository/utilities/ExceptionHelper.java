package gov.loc.repository.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHelper {
	public static String stackTraceToString(Throwable ex)
	{
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
	public static boolean hasCause(Throwable ex, Class<?> cause)
	{		
		while(ex != null)
		{
			if (ex.getClass().equals(cause))
			{
				return true;
			}
			ex = ex.getCause();
		}
		return false;		
	}
}
