package gov.loc.repository.workflow.utilities;


import java.io.FileInputStream;
import java.text.MessageFormat;

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
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;

public class ProcessDeployer {
	private static Options options;
	private static CommandLine line;
			
	private static final int RETURN_SUCCESS = 0;
	private static final int RETURN_ERROR = 1;

	public static final String ACTION_DEPLOY = "deploy";
	
	public static final String TYPE_PATH = "path";
	
	public static final String OPT_HELP = "help";
	public static final String OPT_HELP_DESCRIPTION = "Print this message";

	public static final String OPT_PROCESS_FILE = "file";
	public static final String OPT_PROCESS_FILE_DESCRIPTION = "File that contains the process definition.";
	public static final String OPT_PROCESS_FILE_TYPE = TYPE_PATH;
		
	public static final String OPT_PROCESS_RESOURCE = "resource";
	public static final String OPT_PROCESS_RESOURCE_DESCRIPTION = "Resource that contains the process definition.";
	public static final String OPT_PROCESS_RESOURCE_TYPE = TYPE_PATH;
	
	private static final Log log = LogFactory.getLog(ProcessDeployer.class);
	
	public static void main(String[] args) throws Exception {
		
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
			if (ACTION_DEPLOY.equalsIgnoreCase(action))
			{
				JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
				
				JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();		
				try
				{
					ProcessDefinition processDefinition;
					if(options.getOption(OPT_PROCESS_RESOURCE).getValue() != null)
					{
						processDefinition = ProcessDefinition.parseXmlResource(options.getOption(OPT_PROCESS_RESOURCE).getValue());
					}
					else if (options.getOption(OPT_PROCESS_FILE).getValue() != null)
					{
						processDefinition = ProcessDefinition.parseXmlInputStream(new FileInputStream(options.getOption(OPT_PROCESS_FILE).getValue()));
					}
					else
					{
						throw new ParseException(MessageFormat.format("When performing a {0}, a {1} or a {2} must be provided.", ACTION_DEPLOY, OPT_PROCESS_FILE, OPT_PROCESS_RESOURCE));
					}
					jbpmContext.deployProcessDefinition(processDefinition);
					
				}
				finally
				{
					jbpmContext.close();
				}
				System.out.println("Deployment succeeded");
			}
			else
			{
				throw new ParseException("Unknown action: " + action);
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

	@SuppressWarnings("static-access")
	private static void defineCommandLine()
	{
		options = new Options();
		options.addOption(new Option(OPT_HELP, OPT_HELP_DESCRIPTION));
		options.addOption(OptionBuilder.withArgName(OPT_PROCESS_RESOURCE_TYPE).hasArg().withDescription(OPT_PROCESS_RESOURCE_DESCRIPTION).create(OPT_PROCESS_RESOURCE));			
		options.addOption(OptionBuilder.withArgName(OPT_PROCESS_FILE_TYPE).hasArg().withDescription(OPT_PROCESS_FILE_DESCRIPTION).create(OPT_PROCESS_FILE));
	}
	
	private static void printUsages()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(MessageFormat.format("ProcessDeployer {0} [options]", ACTION_DEPLOY), options, false);
		System.out.println(MessageFormat.format("Returns {0} for success.", RETURN_SUCCESS));
		System.out.println(MessageFormat.format("Returns {0} for failure or error.", RETURN_ERROR));
		System.out.println("To deploy a process definition, use:");
		System.out.println(MessageFormat.format("ProcessDeployer {0} -{1}|-{2}", 
				ACTION_DEPLOY,
				OPT_PROCESS_FILE,
				OPT_PROCESS_RESOURCE));
	}
	
}
