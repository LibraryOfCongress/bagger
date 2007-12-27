package gov.loc.repository.packagemodeler.drivers;


import static gov.loc.repository.packagemodeler.drivers.MapDataDriver.ACTION_TEST;
import static gov.loc.repository.packagemodeler.drivers.MapFixtureDriver.*;

import java.text.MessageFormat;

import gov.loc.repository.utilities.EnhancedHashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommandLineFixtureDriver {
	
	
	
	private static Options options;
	private static CommandLine line;
		
	private static final Log log = LogFactory.getLog(CommandLineFixtureDriver.class);	
	
	private static final int RETURN_SUCCESS = 0;
	private static final int RETURN_ERROR = 1;
		
	public static void main(String args[]) throws Exception
	{
		defineCommandLine();
		CommandLineParser parser = new GnuParser();
		try
		{
			line = parser.parse(options, args);
		
			if (line.hasOption(OPT_HELP))
			{
				printUsages();
				return;
			}
			if (line.getArgList().size() != 1)
			{
				throw new ParseException("One and only one action may be provided");
			}
			String action = (String)line.getArgList().get(0);
			EnhancedHashMap<String,String> optionsMap = commandLineToEnhancedHashMap(line);
			MapFixtureDriver componentDriver = new MapFixtureDriver();
			componentDriver.execute(action, optionsMap);
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
		
	private static void printUsages()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(MessageFormat.format("fixturedriver {0}|{1}|{2}|{3}|{4}|{5} [options]", ACTION_REPOSITORY, ACTION_ROLE, ACTION_PERSON, ACTION_ORGANIZATION, ACTION_SYSTEM, ACTION_SOFTWARE), options, false);
		System.out.println("Lists are comma separated (without spaces).");
		System.out.println(MessageFormat.format("Returns {0} for success.", RETURN_SUCCESS));
		System.out.println(MessageFormat.format("Returns {0} for failure or error.", RETURN_ERROR));
		System.out.println("To test the database connection, use:");
		System.out.println(MessageFormat.format("driver {0}", 
				ACTION_TEST));
		System.out.println("To create a Repository, use:");
		System.out.println(MessageFormat.format("driver {0} -{1}", 
				ACTION_REPOSITORY,
				OPT_ID));
		System.out.println("To create a Role, use:");
		System.out.println(MessageFormat.format("driver {0} -{1}", 
				ACTION_ROLE,
				OPT_ID));
		System.out.println("To create a Person, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} -{2} -{3} -{4}", 
				ACTION_PERSON,
				OPT_ID,
				OPT_FIRSTNAME,
				OPT_SURNAME,
				OPT_ROLES));
		System.out.println("To create an Organization, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} -{2} -{3}", 
				ACTION_ORGANIZATION,
				OPT_ID,
				OPT_NAME,
				OPT_ROLES));
		System.out.println("To create a Software, use:");		
		System.out.println(MessageFormat.format("driver {0} -{1} -{2}", 
				ACTION_SOFTWARE,
				OPT_ID,
				OPT_ROLES));
		System.out.println("To create a System, use:");		
		System.out.println(MessageFormat.format("driver {0} -{1} -{2}", 
				ACTION_SYSTEM,
				OPT_ID,
				OPT_ROLES));


	}
	
	@SuppressWarnings("static-access")
	private static void defineCommandLine()
	{
		options = new Options();
		options.addOption(new Option(OPT_HELP, OPT_HELP_DESCRIPTION));
		options.addOption(OptionBuilder.withArgName(OPT_FIRSTNAME_TYPE).hasArg().withDescription(OPT_FIRSTNAME_DESCRIPTION).create(OPT_FIRSTNAME));		
		options.addOption(OptionBuilder.withArgName(OPT_ID_TYPE).hasArg().withDescription(OPT_ID_DESCRIPTION).create(OPT_ID));
		options.addOption(OptionBuilder.withArgName(OPT_NAME_TYPE).hasArg().withDescription(OPT_NAME_DESCRIPTION).create(OPT_NAME));		
		options.addOption(OptionBuilder.withArgName(OPT_ROLES_TYPE).hasArg().withDescription(OPT_ROLES_DESCRIPTION).create(OPT_ROLES));
		options.addOption(OptionBuilder.withArgName(OPT_SURNAME_TYPE).hasArg().withDescription(OPT_SURNAME_DESCRIPTION).create(OPT_SURNAME));		

	}
	
	private static EnhancedHashMap<String,String> commandLineToEnhancedHashMap(CommandLine line)
	{
		EnhancedHashMap<String,String> optionMap = new EnhancedHashMap<String, String>();
		for(Option option : line.getOptions())
		{
			optionMap.put(option.getOpt(), option.getValue());
		}				
		return optionMap;
	}
}
