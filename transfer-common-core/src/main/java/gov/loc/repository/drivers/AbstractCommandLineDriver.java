package gov.loc.repository.drivers;

import java.io.BufferedReader;
import java.io.FileReader;

import gov.loc.repository.utilities.EnhancedHashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractCommandLineDriver {

    public static final String OPT_HELP = "help";
    public static final String OPT_HELP_DESCRIPTION = "Print this message";
	
	protected static final Log log = LogFactory.getLog(AbstractCommandLineDriver.class);	
	
	public static final int RETURN_SUCCESS = 0;
	public static final int RETURN_ERROR = 1;
	
    public static final String OPT_FILE = "file";
    public static final String OPT_FILE_DESCRIPTION = "A file containing a list of fixturedriver commands to execute.";
    public static final String OPT_FILE_TYPE = "file path";
        	
	public void parse(String[] args)
	{
		CommandLineParser parser = new GnuParser();
		try
		{
			CommandLine line = parser.parse(this.getOptions(), args);
		
			if (line.hasOption(OPT_HELP))
			{
				printUsages();
				return;
			}
			
			if (line.getArgList().size() != 1 && ! line.hasOption(OPT_FILE))
			{
				throw new ParseException("One and only one action may be provided");
			}
			if (line.hasOption(OPT_FILE))
			{
				BufferedReader reader = new BufferedReader(new FileReader(line.getOptionValue(OPT_FILE)));
				String l = null;
				while ((l = reader.readLine()) != null)
				{					
					String[] argsL = l.split(" ");
					CommandLine cl = parser.parse(this.getOptions(), argsL);
					this.getMapDriver().execute((String)cl.getArgList().get(0), commandLineToEnhancedHashMap(cl));
				}
				reader.close();
			}
			else
			{
				this.getMapDriver().execute((String)line.getArgList().get(0), commandLineToEnhancedHashMap(line));
			}
			System.exit(RETURN_SUCCESS);
		}
		catch(ParseException ex)
		{
			System.err.println("Parsing of commandline failed due to: " + ex.getMessage());
			printUsages();
			System.exit(RETURN_ERROR);
		}
		catch(Exception ex)
		{

			String msg = "An error occurred: " + ex.getMessage();
			System.err.println(msg);
			log.error(msg, ex);
			System.exit(RETURN_ERROR);
		}
		
	}
	
	public abstract void printUsages();
	
	protected abstract Options getOptions();
	
	protected abstract MapDriver getMapDriver();
	
	private EnhancedHashMap<String,String> commandLineToEnhancedHashMap(CommandLine line)
	{
		EnhancedHashMap<String,String> optionMap = new EnhancedHashMap<String, String>();
		for(Option option : line.getOptions())
		{
			optionMap.put(option.getOpt(), option.getValue());
		}				
		return optionMap;
	}
	
	public interface MapDriver
	{
		void execute(String action, EnhancedHashMap<String,String> options) throws Exception;
	}
	
}
