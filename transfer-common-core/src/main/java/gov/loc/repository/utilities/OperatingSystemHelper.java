package gov.loc.repository.utilities;

public class OperatingSystemHelper {
	public static boolean isWindows()
	{
		if (System.getProperty("os.name").indexOf("Windows") != -1)
		{
			return true;
		}
		return false;
	}
}
