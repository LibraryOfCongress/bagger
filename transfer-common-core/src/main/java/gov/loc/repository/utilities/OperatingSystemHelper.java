package gov.loc.repository.utilities;

public class OperatingSystemHelper {
	public static boolean isWindows()
	{
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isSolaris()
	{
		if (System.getProperty("os.name").toLowerCase().indexOf("sunos") != -1)
		{
			return true;
		}
		return false;
		
	}
	
	public static boolean isLinux()
	{
		if (System.getProperty("os.name").toLowerCase().indexOf("linux") != -1)
		{
			return true;
		}
		return false;
		
	}
	
}
