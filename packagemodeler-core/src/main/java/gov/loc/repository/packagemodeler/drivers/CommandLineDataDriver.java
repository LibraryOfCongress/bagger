package gov.loc.repository.packagemodeler.drivers;


import static gov.loc.repository.packagemodeler.drivers.MapDataDriver.*;

import java.text.MessageFormat;

import gov.loc.repository.drivers.AbstractCommandLineDriver;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.events.filelocation.FileDeleteEvent;
import gov.loc.repository.packagemodeler.events.filelocation.FileLocationAnomalyEvent;
import gov.loc.repository.packagemodeler.events.filelocation.VerifyAgainstManifestEvent;
import gov.loc.repository.packagemodeler.events.packge.PackageReceivedEvent;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CommandLineDataDriver extends AbstractCommandLineDriver {
		
	Options options;
	MapDriver mapDriver;

	public CommandLineDataDriver() {
		this.defineOptions();
		ApplicationContext context = new ClassPathXmlApplicationContext("conf/packagemodeler-core-context.xml"); 
		this.mapDriver = (MapDriver)context.getBean("mapDataDriver");
	}
	
	public static void main(String args[]) throws Exception
	{
		CommandLineDataDriver driver = new CommandLineDataDriver();
		driver.parse(args);		
	}
		
	public void printUsages()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(MessageFormat.format("datadriver {0}|{1}|{2}|{3}|{4}|{5} [options]", ACTION_PACKAGE, ACTION_FILELOCATION, ACTION_PACKAGE_EVENT, ACTION_FILELOCATION_EVENT, ACTION_INVENTORY_FROM_MANIFEST, ACTION_CANONICALIZE_FROM_FILELOCATION), options, false);
		System.out.println("Dates are in ISO 8601 format.  For example, 2001-12-05T12:24:55.  Remember that hours, minutes, and seconds are zero based.");
		System.out.println(MessageFormat.format("Returns {0} for success.", RETURN_SUCCESS));
		System.out.println(MessageFormat.format("Returns {0} for failure or error.", RETURN_ERROR));
		System.out.println("To test the database connection, use:");
		System.out.println(MessageFormat.format("driver {0}", 
				ACTION_TEST));
		System.out.println("To execute a list of commands from a file, use:");
		System.out.println(MessageFormat.format("driver -{0}", OPT_FILE));		
		System.out.println("To create a Package, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} -{2} -{3}", 
				ACTION_PACKAGE,
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_PACKAGE_CLASS));
		System.out.println("To create a Storage System File Location, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} -{2} -{3} -{4} -{5} -{6}", 
				ACTION_FILELOCATION,
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_STORAGE_SYSTEM,
				OPT_BASEPATH,
				OPT_IS_MANAGED,
				OPT_IS_LC_PACKAGE_STRUCTURE));
		
		System.out.println("To record a PackageReceived event, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} {2} -{3} -{4} -{5} -{6} -{7} -{8} -{9} -{10}", 
				ACTION_PACKAGE_EVENT,
				OPT_EVENT_TYPE, PackageReceivedEvent.class.getName(),
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_REQUESTING_AGENT,
				OPT_PERFORMING_AGENT,
				OPT_SUCCESS,
				OPT_EVENT_START,
				OPT_EVENT_END,
				OPT_MESSAGE));
		System.out.println("To record a VerifyAgainstManifest event, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} {2} -{3} -{4} -{5} -{6} -{7} -{8} -{9} -{10} -{11} -{12}", 
				ACTION_FILELOCATION_EVENT,
				OPT_EVENT_TYPE, VerifyAgainstManifestEvent.class.getName(),
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_STORAGE_SYSTEM,
				OPT_BASEPATH,
				OPT_REQUESTING_AGENT,
				OPT_PERFORMING_AGENT,
				OPT_SUCCESS,
				OPT_EVENT_START,
				OPT_EVENT_END,
				OPT_MESSAGE));
		System.out.println("To record a FileLocationAnomaly event, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} {2} -{3} -{4} -{5} -{6} -{7} -{8} -{9} -{10} -{11} -{12}", 
				ACTION_FILELOCATION_EVENT,
				OPT_EVENT_TYPE, FileLocationAnomalyEvent.class.getName(),
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_STORAGE_SYSTEM,
				OPT_BASEPATH,
				OPT_REQUESTING_AGENT,
				OPT_PERFORMING_AGENT,
				OPT_SUCCESS,
				OPT_EVENT_START,
				OPT_EVENT_END,
				OPT_MESSAGE));
		System.out.println("To record a FileCopy event, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} {2} -{3} -{4} -{5} -{6} -{7} -{8} -{9} -{10} -{11} -{12} -{13} -{14}", 
				ACTION_FILELOCATION_EVENT,
				OPT_EVENT_TYPE, FileCopyEvent.class.getName(),
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_SOURCE_STORAGE_SYSTEM,
				OPT_SOURCE_BASEPATH,
				OPT_STORAGE_SYSTEM,
				OPT_BASEPATH,
				OPT_REQUESTING_AGENT,
				OPT_PERFORMING_AGENT,
				OPT_SUCCESS,
				OPT_EVENT_START,
				OPT_EVENT_END,
				OPT_MESSAGE));
		System.out.println("To record a FileDelete event, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} {2} -{3} -{4} -{5} -{6} -{7} -{8} -{9} -{10} -{11} -{12}", 
				ACTION_FILELOCATION_EVENT,
				OPT_EVENT_TYPE, FileDeleteEvent.class.getName(),
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_STORAGE_SYSTEM,
				OPT_BASEPATH,
				OPT_REQUESTING_AGENT,
				OPT_PERFORMING_AGENT,
				OPT_SUCCESS,
				OPT_EVENT_START,
				OPT_EVENT_END,
				OPT_MESSAGE));
		System.out.println("To canonicalize from a File Location, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} -{2} -{3} -{4}", 
				ACTION_CANONICALIZE_FROM_FILELOCATION,
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_STORAGE_SYSTEM,
				OPT_BASEPATH));		
		System.out.println("To inventory against an LC manifest and record an InventoryAgainstManifest event, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} {2} -{3} -{4} -{5} -{6}", 
				ACTION_INVENTORY_FROM_MANIFEST,
				OPT_REPOSITORY,
				OPT_PACKAGE,
				OPT_STORAGE_SYSTEM,
				OPT_BASEPATH,
				OPT_REQUESTING_AGENT,
				OPT_CREATE_CANONICAL_FILES));

	}
	
	@SuppressWarnings("static-access")
	private void defineOptions()
	{
		options = new Options();
		options.addOption(new Option(OPT_HELP, OPT_HELP_DESCRIPTION));
		options.addOption(OptionBuilder.withArgName(OPT_BASEPATH_TYPE).hasArg().withDescription(OPT_BASEPATH_DESCRIPTION).create(OPT_BASEPATH));			
		options.addOption(OptionBuilder.withArgName(OPT_CREATE_CANONICAL_FILES_TYPE).hasArg().withDescription(OPT_CREATE_CANONICAL_FILES_DESCRIPTION).create(OPT_CREATE_CANONICAL_FILES));
		options.addOption(OptionBuilder.withArgName(OPT_EVENT_END_TYPE).hasArg().withDescription(OPT_EVENT_END_DESCRIPTION).create(OPT_EVENT_END));
		options.addOption(OptionBuilder.withArgName(OPT_EVENT_START_TYPE).hasArg().withDescription(OPT_EVENT_START_DESCRIPTION).create(OPT_EVENT_START));
		options.addOption(OptionBuilder.withArgName(OPT_EVENT_TYPE_TYPE).hasArg().withDescription(OPT_EVENT_TYPE_DESCRIPTION).create(OPT_EVENT_TYPE));
		options.addOption(OptionBuilder.withArgName(OPT_IS_LC_PACKAGE_STRUCTURE_TYPE).hasArg().withDescription(OPT_IS_LC_PACKAGE_STRUCTURE_DESCRIPTION).create(OPT_IS_LC_PACKAGE_STRUCTURE));
		options.addOption(OptionBuilder.withArgName(OPT_IS_MANAGED_TYPE).hasArg().withDescription(OPT_IS_MANAGED_DESCRIPTION).create(OPT_IS_MANAGED));		
		options.addOption(OptionBuilder.withArgName(OPT_MESSAGE_TYPE).hasArgs().withDescription(OPT_MESSAGE_DESCRIPTION).create(OPT_MESSAGE));
		options.addOption(OptionBuilder.withArgName(OPT_PACKAGE_TYPE).hasArg().withDescription(OPT_PACKAGE_DESCRIPTION).create(OPT_PACKAGE));
		options.addOption(OptionBuilder.withArgName(OPT_PACKAGE_CLASS_TYPE).hasArg().withDescription(OPT_PACKAGE_CLASS_DESCRIPTION).create(OPT_PACKAGE_CLASS));		
		options.addOption(OptionBuilder.withArgName(OPT_PERFORMING_AGENT_TYPE).hasArg().withDescription(OPT_PERFORMING_AGENT_DESCRIPTION).create(OPT_PERFORMING_AGENT));
		options.addOption(OptionBuilder.withArgName(OPT_REPOSITORY_TYPE).hasArg().withDescription(OPT_REPOSITORY_DESCRIPTION).create(OPT_REPOSITORY));
		options.addOption(OptionBuilder.withArgName(OPT_REQUESTING_AGENT_TYPE).hasArg().withDescription(OPT_REQUESTING_AGENT_DESCRIPTION).create(OPT_REQUESTING_AGENT));
		options.addOption(OptionBuilder.withArgName(OPT_SOURCE_BASEPATH_TYPE).hasArg().withDescription(OPT_SOURCE_BASEPATH_DESCRIPTION).create(OPT_SOURCE_BASEPATH));		
		options.addOption(OptionBuilder.withArgName(OPT_SOURCE_STORAGE_SYSTEM_TYPE).hasArg().withDescription(OPT_SOURCE_STORAGE_SYSTEM_DESCRIPTION).create(OPT_SOURCE_STORAGE_SYSTEM));		
		options.addOption(OptionBuilder.withArgName(OPT_STORAGE_SYSTEM_TYPE).hasArg().withDescription(OPT_STORAGE_SYSTEM_DESCRIPTION).create(OPT_STORAGE_SYSTEM));		
		options.addOption(OptionBuilder.withArgName(OPT_SUCCESS_TYPE).hasArg().withDescription(OPT_SUCCESS_DESCRIPTION).create(OPT_SUCCESS));
		options.addOption(OptionBuilder.withArgName(OPT_FILE_TYPE).hasArg().withDescription(OPT_FILE_DESCRIPTION).create(OPT_FILE));

	}
	
	@Override
	protected MapDriver getMapDriver() {
		return this.mapDriver;
	}
	
	@Override
	protected Options getOptions() {
		return this.options;
	}
	
}
