package gov.loc.repository.transfer.components;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.FileListComparisonResult;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.events.packge.PackageReceivedEvent;
import gov.loc.repository.packagemodeler.impl.ModelerFactoryImpl;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.transfer.components.fileexamination.FileExaminer;
import gov.loc.repository.transfer.components.fileexamination.impl.FileExaminerImpl;
import gov.loc.repository.utilities.FilenameHelper;
import gov.loc.repository.utilities.FixityHelper;
import gov.loc.repository.utilities.ManifestHelper;
import gov.loc.repository.utilities.ManifestReader;
import gov.loc.repository.utilities.PackageHelper;
import gov.loc.repository.utilities.impl.JavaSecurityFixityHelper;
import gov.loc.repository.utilities.persistence.HibernateUtil;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class ComponentDriver {
	
	private static String OPT_HELP = "help";
	private static String OPT_REPOSITORY = "repository";
	private static String OPT_LCMANIFEST = "lcmanifest";
	private static String OPT_PACKAGE = "package";
	private static String OPT_STORAGE_SYSTEM = "storagesystem";
	private static String OPT_SOURCE_STORAGE_SYSTEM = "sourcestoragesystem";
	private static String OPT_CREATE_PACKAGE ="createpackage";
	private static String OPT_PACKAGE_TYPE = "packagetype";
	private static String OPT_REQUESTING_AGENT = "requestingagent";
	private static String OPT_PERFORMING_AGENT = "performingagent";
	private static String OPT_SUCCESS = "success";
	private static String OPT_CREATE_FILE_INSTANCES = "createfileinstances";
	private static String OPT_ALGORITHM = "algorithm";
	private static String OPT_BASEPATH = "basepath";
	private static String OPT_SOURCE_BASEPATH = "sourcebasepath";
	private static String OPT_EVENT_TYPE = "eventtype";
	private static String OPT_EVENT_START = "eventstart";
	private static String OPT_EVENT_END = "eventend";
	private static String OPT_SOURCE = "source";
	private static String OPT_TARGET = "target";
	private static String OPT_IS_MANAGED = "ismanaged";
	private static String OPT_IS_LC_PACKAGE_STRUCTURE = "islcpackagestructure";
	private static String ENUM_CANONICALFILES = "canonicalfiles";
	private static String ENUM_FILEINSTANCES = "fileinstances";
	private static String ENUM_FILEEXAMINATIONS = "fileexaminations";
	private static String OPT_FILEEXAMINATIONGROUP = "fileexaminationgroup";
	private static String ACTION_INVENTORY = "inventory";
	private static String ACTION_COMPLETE_EXAMINATION = "completeexamination";
	private static String ACTION_EVENT = "event";
	private static String ACTION_COMPARE = "compare";
	
	private static String PACKAGE_CLASS = "gov.loc.repository.modelers.packge.Package";
	private static String ALGORITHM = "MD5";
	private static final String REPORTING_AGENT_KEY = "components.agentid";
	private static String UNKNOWN = "unknown";
	
	private static Options options;
	private static CommandLine line;
	
	private static PackageModelDAO dao = new PackageModelDAOImpl();
	private static ModelerFactory factory = new ModelerFactoryImpl();
	
	private static final Log log = LogFactory.getLog(ComponentDriver.class);	
	
	private static Configuration configuration;
	static
	{
		try
		{
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			URL url = BaseComponent.class.getClassLoader().getResource("components.cfg.xml");
			builder.setURL(url);
			configuration = builder.getConfiguration(true);
		}
		catch(ConfigurationException ex)
		{
			throw new RuntimeException();
		}		
	}
	
	
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
			//Let's wrap this in a session
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try
			{					
				session.beginTransaction();		
				if (ACTION_INVENTORY.equalsIgnoreCase(action))
				{
					inventoryFromManifest();
				}
				else if (ACTION_COMPLETE_EXAMINATION.equalsIgnoreCase(action))
				{
					examine();
				}
				else if (ACTION_EVENT.equalsIgnoreCase(action))
				{
					createEvent();
				}
				else if (ACTION_COMPARE.equalsIgnoreCase(action))
				{
					compare();
				}
				else
				{
					throw new ParseException(action + " is an unrecognized action");
				}
				session.getTransaction().commit();
			}
			catch(Exception ex)
			{
				if (session != null && session.isOpen())
				{
					session.getTransaction().rollback();
				}
				throw ex;
			}
			finally
			{
				if (session != null && session.isOpen())
				{
					session.close();
				}
			}
		}
		catch(ParseException ex)
		{
			System.err.println("Parsing of commandline failed due to: " + ex.getMessage());
			printUsages();			
		}
		catch(Exception ex)
		{

			String msg = "An error occurred: " + ex.getMessage();
			System.err.println(msg);
			log.error(msg, ex);
		}
			
	}
	
	private static void printUsages()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(MessageFormat.format("driver {0}|{1}|{2}|{3} [options]", ACTION_INVENTORY, ACTION_COMPLETE_EXAMINATION, ACTION_EVENT, ACTION_COMPARE), options, false);
		System.out.println("To inventory a package from an lc manifest, generating canonical files and file instances, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} -{2} -{3} -{4} -{5} -{6} -{7} -{8} -{9}", ACTION_INVENTORY, OPT_LCMANIFEST, OPT_REPOSITORY, OPT_PACKAGE, OPT_STORAGE_SYSTEM, OPT_IS_MANAGED, OPT_IS_LC_PACKAGE_STRUCTURE, OPT_CREATE_PACKAGE, OPT_PACKAGE_TYPE, OPT_REQUESTING_AGENT));
		System.out.println("To perform a complete examination of a file location, generating a file examination group and file examinations, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} -{2} -{3} -{4} -{5} -{6} -{7} -{8}", ACTION_COMPLETE_EXAMINATION, OPT_REPOSITORY, OPT_PACKAGE, OPT_STORAGE_SYSTEM, OPT_BASEPATH, OPT_ALGORITHM, OPT_CREATE_PACKAGE, OPT_REQUESTING_AGENT, OPT_CREATE_FILE_INSTANCES));
		System.out.println("To record a FileCopy event, use:");
		System.out.println(MessageFormat.format("driver {0} -{1} {2} -{3} -{4} -{5} -{6} -{7} -{8} -{9} -{10} -{11} -{12} -{13}", ACTION_EVENT, OPT_EVENT_TYPE, FileCopyEvent.class.getName(), OPT_REPOSITORY, OPT_PACKAGE, OPT_STORAGE_SYSTEM, OPT_BASEPATH, OPT_SOURCE_STORAGE_SYSTEM, OPT_SOURCE_BASEPATH, OPT_REQUESTING_AGENT, OPT_PERFORMING_AGENT, OPT_SUCCESS, OPT_EVENT_START, OPT_EVENT_END));
		System.out.println("To perform a comparison of canonical files, file instances, and/or file examinations:");
		System.out.println(MessageFormat.format("driver {0} -{1} -{2} -{3} -{4} -{5} -{6} -{7}", ACTION_COMPARE, OPT_SOURCE, OPT_TARGET, OPT_REPOSITORY, OPT_PACKAGE, OPT_STORAGE_SYSTEM, OPT_BASEPATH, OPT_FILEEXAMINATIONGROUP));
		
	}
	
	@SuppressWarnings("static-access")
	private static void defineCommandLine()
	{
		options = new Options();
		options.addOption(new Option(OPT_HELP, "print this message"));
		options.addOption(OptionBuilder.withArgName("filename").hasArg().withDescription("filename of the lc manifest").create(OPT_LCMANIFEST));		
		options.addOption(OptionBuilder.withArgName("id").hasArg().withDescription("id of the repository").create(OPT_REPOSITORY));
		options.addOption(OptionBuilder.withArgName("id").hasArg().withDescription("id of the package").create(OPT_PACKAGE));
		options.addOption(OPT_CREATE_PACKAGE, false, "create package if it doesn't exist.  Default is false");
		options.addOption(OptionBuilder.withArgName("class").hasArg().withDescription("class of the package to create.  Default is " + PACKAGE_CLASS).create(OPT_PACKAGE_TYPE));
		options.addOption(OptionBuilder.withArgName("id").hasArg().withDescription("algorithm used to generate fixities.  Default is " + ALGORITHM).create(OPT_ALGORITHM));
		options.addOption(OPT_CREATE_FILE_INSTANCES, false, "create file instances.  Default is false");
		options.addOption(OptionBuilder.withArgName("class").hasArg().withDescription("class of the Event to create").create(OPT_EVENT_TYPE));
		options.addOption(OptionBuilder.withArgName("id").hasArg().withDescription("id of the agent requesting the action.  Use unknown for unknown").create(OPT_REQUESTING_AGENT));
		options.addOption(OptionBuilder.withArgName("id").hasArg().withDescription("id of the agent performing the action.  Use unknown for unknown").create(OPT_PERFORMING_AGENT));
		options.addOption(OptionBuilder.withArgName("id").hasArg().withDescription("id of the storage system").create(OPT_STORAGE_SYSTEM));
		options.addOption(OptionBuilder.withArgName("id").hasArg().withDescription("id of the source storage system for a FileCopy Event").create(OPT_SOURCE_STORAGE_SYSTEM));
		options.addOption(OptionBuilder.withArgName("path").hasArg().withDescription("basepath of the file location").create(OPT_BASEPATH));
		options.addOption(OptionBuilder.withArgName("path").hasArg().withDescription("basepath of the source file location for a FileCopy Event").create(OPT_SOURCE_BASEPATH));
		options.addOption(OptionBuilder.withArgName("true|false").hasArg().withDescription("success of an Event.  Default is true").create(OPT_SUCCESS));
		options.addOption(OptionBuilder.withArgName("timestamp").hasArg().withDescription("start timestamp an Event.  Default is now.  Use unknown for unknown").create(OPT_EVENT_START));
		options.addOption(OptionBuilder.withArgName("timestamp").hasArg().withDescription("end timestamp an Event.  Default is none").create(OPT_EVENT_END));
		options.addOption(OptionBuilder.withArgName(ENUM_CANONICALFILES + "|" + ENUM_FILEINSTANCES).hasArg().withDescription("source type of a comparison").create(OPT_SOURCE));
		options.addOption(OptionBuilder.withArgName(ENUM_FILEINSTANCES + "|" + ENUM_FILEEXAMINATIONS).hasArg().withDescription("target type of a comparison").create(OPT_TARGET));
		options.addOption(OptionBuilder.withArgName("index").hasArg().withDescription("index of the file examination group.  Default is most recent").create(OPT_FILEEXAMINATIONGROUP));
		options.addOption(OptionBuilder.withArgName("true|false").hasArg().withDescription("whether a file location is managed.  Default is true").create(OPT_IS_MANAGED));
		options.addOption(OptionBuilder.withArgName("true|false").hasArg().withDescription("whether a file location is lc package structured.  Default is true").create(OPT_IS_LC_PACKAGE_STRUCTURE));
	}
	
	private static String getRequiredOptionValue(String opt) throws Exception
	{
		String value = line.getOptionValue(opt);
		if (value == null)
		{
			throw new Exception(MessageFormat.format("Option {0} is missing from commandline", opt));			
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	private static Package getPackage() throws Exception
	{
		String repositoryId = getRequiredOptionValue(OPT_REPOSITORY);
		String packageId = getRequiredOptionValue(OPT_PACKAGE);
		Package packge = dao.findPackage(Package.class, repositoryId, packageId);
		if (packge == null)
		{
			if (line.hasOption(OPT_CREATE_PACKAGE))
			{
				Class packageClass = Class.forName(line.getOptionValue(OPT_PACKAGE_TYPE, PACKAGE_CLASS));
				packge = factory.createPackage(packageClass, dao.findRequiredRepository(repositoryId), packageId);
				PackageReceivedEvent event = factory.createPackageEvent(PackageReceivedEvent.class, packge, Calendar.getInstance().getTime(), getReportingAgent());
				event.setRequestingAgent(getRequestingAgent());
			}
			else
			{
				throw new Exception(MessageFormat.format("Package {0} for repository {1} does not exist", packageId, repositoryId));
			}
		}
		dao.save(packge);
		return packge;		
		
	}

	private static Agent getRequestingAgent() throws Exception
	{
		String agentId = getRequiredOptionValue(OPT_REQUESTING_AGENT);
		return dao.findRequiredAgent(Agent.class, agentId);
	}
	
	private static void inventoryFromManifest() throws Exception
	{
		FileLocation fileLocation = getFileLocation();
		Package packge = fileLocation.getPackage();
		if (! packge.getCanonicalFiles().isEmpty())
		{
			throw new Exception("Package already has canonical files");
		}
		if (! fileLocation.getFileInstances().isEmpty())
		{
			throw new Exception("File location already has file instances");
		}
		ManifestReader reader = new ManifestReader();
		reader.setFile(getLcManifest());
		factory.createCanonicalFiles(packge, reader);
		factory.createFileInstancesFromCanonicalFiles(fileLocation, packge.getCanonicalFiles());
		//Add FileInstances for files in package root
		if (fileLocation.isLCPackageStructure())
		{
			List<File> fileList = PackageHelper.discoverLCPackageRootFiles(new File(fileLocation.getBasePath()));
			for(File file : fileList)
			{
				String filename = FilenameHelper.removeBasePath(fileLocation.getBasePath(), FilenameHelper.normalize(file.toString()));
				factory.createFileInstance(fileLocation, new FileName(filename));
			}
			
		}
		
	}
	
	private static File getLcManifest() throws Exception
	{
		return new File(getRequiredOptionValue(OPT_LCMANIFEST));
	}
		
	private static StorageSystemFileLocation getFileLocation() throws Exception
	{
		Package packge = getPackage();
		String storageSystemId = getRequiredOptionValue(OPT_STORAGE_SYSTEM);
		String basePath;
		if (line.hasOption(OPT_LCMANIFEST))
		{
			File lcManifestFile = getLcManifest();
			basePath = ManifestHelper.getBasePath(lcManifestFile);
		}
		else
		{
			basePath = getRequiredOptionValue(OPT_BASEPATH);
		}
		StorageSystemFileLocation fileLocation = packge.getFileLocation(storageSystemId, basePath);
		if (fileLocation == null)
		{
			boolean isManaged = true;
			if ("false".equalsIgnoreCase(line.getOptionValue(OPT_IS_MANAGED, "true")))
			{
				isManaged = false;
			}
			
			boolean isLCPackageStructure = true;
			if ("false".equalsIgnoreCase(line.getOptionValue(OPT_IS_LC_PACKAGE_STRUCTURE, "true")))
			{
				isLCPackageStructure = false;
			}
			fileLocation = factory.createStorageSystemFileLocation(packge, dao.findRequiredAgent(gov.loc.repository.packagemodeler.agents.System.class, storageSystemId), basePath, isManaged, isLCPackageStructure);
		}
		return fileLocation;
	}
	
	private static Agent getReportingAgent() throws Exception
	{
		String reportingAgent = configuration.getString(REPORTING_AGENT_KEY);
		if (reportingAgent == null)
		{
			throw new Exception(MessageFormat.format("Property {0} is missing", REPORTING_AGENT_KEY));
		}
		return dao.findRequiredAgent(Agent.class, reportingAgent);
	}	
	
	private static void examine() throws Exception
	{
		StorageSystemFileLocation fileLocation = getFileLocation();
		if (! fileLocation.getFileInstances().isEmpty() && line.hasOption(OPT_CREATE_FILE_INSTANCES))
		{
			throw new Exception("File location already has file instances");
		}

		//Setup the examiner
		FileExaminer examiner = new FileExaminerImpl();
		examiner.setModelerFactory(new ModelerFactoryImpl());
		examiner.setPackageModelDao(new PackageModelDAOImpl());
		FixityHelper fixityHelper = new JavaSecurityFixityHelper();
		fixityHelper.setAlgorithm(getRequiredOptionValue(OPT_ALGORITHM));
		examiner.setFixityHelper(fixityHelper);
		boolean createFileInstances = false;
		if (line.hasOption(OPT_CREATE_FILE_INSTANCES))
		{
			createFileInstances = true;
		}
		
		examiner.examine(fileLocation, getRequestingAgent(), createFileInstances);
		
	}
	
	@SuppressWarnings("unchecked")
	private static void createEvent() throws Exception
	{
		Class eventClass = Class.forName(getRequiredOptionValue(OPT_EVENT_TYPE));
		
		//Event Start
		Date eventStart = Calendar.getInstance().getTime();
		boolean isUnknownEventStart = false;
		if (line.hasOption(OPT_EVENT_START))
		{
			if (UNKNOWN.equals(line.getOptionValue(OPT_EVENT_START)))
			{
				isUnknownEventStart = true;
			}
			else
			{
				eventStart = DateFormat.getDateTimeInstance().parse(line.getOptionValue(OPT_EVENT_START));
			}
		}
		Event event;		
		if (FileLocationEvent.class.isAssignableFrom(eventClass))
		{
			FileLocation fileLocation = getFileLocation();
			Package packge = fileLocation.getPackage();
			event = factory.createFileLocationEvent(eventClass, fileLocation, eventStart, getReportingAgent());
			
			if (FileCopyEvent.class.isAssignableFrom(eventClass))
			{
				String sourceStorageSystemId = getRequiredOptionValue(OPT_SOURCE_STORAGE_SYSTEM);
				String sourceBasePath = getRequiredOptionValue(OPT_SOURCE_BASEPATH);
				StorageSystemFileLocation sourceFileLocation = packge.getFileLocation(sourceStorageSystemId, sourceBasePath);
				if (sourceFileLocation == null)
				{
					throw new Exception(MessageFormat.format("Source file location not found with storage system id {0} and basepath {1}", sourceStorageSystemId, sourceBasePath));
				}
				((FileCopyEvent)event).setFileLocationSource(sourceFileLocation);
			}
			
		}
		else
		{
			throw new Exception("Not yet implemented");
		}

		if (isUnknownEventStart)
		{
			event.setEventStart(null);
			event.setUnknownEventStart(true);
		}
		
		//Success
		if ("false".equalsIgnoreCase(line.getOptionValue(OPT_SUCCESS, "true")))
		{
			event.setSuccess(false);
		}
		
		//Requesting Agent
		event.setRequestingAgent(getRequestingAgent());
		
		//Performing Agent
		if (line.hasOption(OPT_PERFORMING_AGENT))
		{
			String agentId = getRequiredOptionValue(OPT_PERFORMING_AGENT);
			event.setPerformingAgent(dao.findRequiredAgent(Agent.class, agentId));
		}
		
		//Event End
		if (line.hasOption(OPT_EVENT_END))
		{
			event.setEventEnd(DateFormat.getDateTimeInstance().parse(line.getOptionValue(OPT_EVENT_END)));
		}
		
		dao.save(event);
		
	}
	
	private static void compare() throws Exception
	{
		String source = getRequiredOptionValue(OPT_SOURCE);
		String target = getRequiredOptionValue(OPT_TARGET);
		FileListComparisonResult result = null;
		if (ENUM_CANONICALFILES.equalsIgnoreCase(source) && ENUM_FILEINSTANCES.equalsIgnoreCase(target))
		{
			FileLocation fileLocation = getFileLocation();
			result = dao.compare(fileLocation.getPackage(), fileLocation);
		}
		else if (ENUM_CANONICALFILES.equalsIgnoreCase(source) && ENUM_FILEEXAMINATIONS.equalsIgnoreCase(target))
		{
			throw new Exception("Not yet implemented");						
		}
		else if (ENUM_FILEINSTANCES.equalsIgnoreCase(source) && ENUM_FILEEXAMINATIONS.equalsIgnoreCase(target))
		{
			FileLocation fileLocation = getFileLocation();
			FileExaminationGroup fileExaminationGroup = null;
			if (line.hasOption(OPT_FILEEXAMINATIONGROUP))
			{
				int index = Integer.parseInt(getRequiredOptionValue(OPT_FILEEXAMINATIONGROUP));
				if (index < 0 && index >= fileLocation.getFileExaminationGroups().size())
				{
					throw new Exception("Bad file examination group index");
				}
				fileExaminationGroup = fileLocation.getFileExaminationGroups().get(index);
				
			}
			else
			{
				fileExaminationGroup = fileLocation.getFileExaminationGroups().get(fileLocation.getFileExaminationGroups().size()-1);
			}
			result = dao.compare(fileLocation, fileExaminationGroup);
		}
		else
		{
			throw new Exception("Combination of source and target not permitted or unknown values");
		}
		
		//Print the result
		for(FileName fileName : result.additionalInTargetList)
		{
			System.out.println("In target, but not source: " + fileName.getFilename());
		}
		for(FileName fileName : result.missingFromTargetList)
		{
			System.out.println("In source, but not target: " + fileName.getFilename());
		}
		for(FileName fileName : result.incomparableList)
		{
			System.out.println("Incomparable: " + fileName.getFilename());
		}
		for(FileName fileName : result.fixityMismatchList)
		{
			System.out.println("Fixity mismatch: " + fileName.getFilename());
		}
				
	}
}
