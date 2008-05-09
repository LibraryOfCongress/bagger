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
}
