package gov.loc.repository.packagemodeler.drivers;


import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.PackageModelerConstants;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.events.filelocation.InventoryFromManifestEvent;
import gov.loc.repository.packagemodeler.events.packge.PackageEvent;
import gov.loc.repository.packagemodeler.impl.ModelerFactoryImpl;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.utilities.EnhancedHashMap;
import gov.loc.repository.utilities.FilenameHelper;
import gov.loc.repository.utilities.ManifestReader;
import gov.loc.repository.utilities.PackageHelper;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;

import org.hibernate.Session;
import org.joda.time.format.ISODateTimeFormat;

public class MapDataDriver {

	//Arg types
	public static final String TYPE_TIMESTAMP = "timestamp";
	public static final String TYPE_ID = "id";
	public static final String TYPE_CLASS = "class";
	public static final String TYPE_BOOLEAN = "true|false";
	public static final String TYPE_PATH = "path";
		
	//Actions
	public static final String ACTION_FILELOCATION = "createfilelocation";
	public static final String ACTION_PACKAGE_EVENT = "createpackageevent";
	public static final String ACTION_PACKAGE = "createpackage";
	public static final String ACTION_FILELOCATION_EVENT = "createfilelocationevent";
	public static final String ACTION_INVENTORY_FROM_MANIFEST = "inventoryfrommanifest";
	public static final String ACTION_TEST = "test";
	public static final String ACTION_CANONICALIZE_FROM_FILELOCATION = "canonicalizefromfilelocation";
	
	//Options
	public static final String OPT_BASEPATH = "basepath";
	public static final String OPT_BASEPATH_DESCRIPTION = "Basepath of the file location.";
	public static final String OPT_BASEPATH_TYPE = TYPE_PATH;

	public static final String OPT_CREATE_CANONICAL_FILES = "createcanonicalfiles";
	public static final String OPT_CREATE_CANONICAL_FILES_DESCRIPTION = "In addition, create Canonical Files.  Default is false.";
	public static final String OPT_CREATE_CANONICAL_FILES_TYPE = TYPE_BOOLEAN;	
	
	public static final String OPT_EVENT_END = "eventend";
	public static final String OPT_EVENT_END_DESCRIPTION = "End timestamp an Event.  Default is none.";
	public static final String OPT_EVENT_END_TYPE = TYPE_TIMESTAMP;

	public static final String OPT_EVENT_START = "eventstart";
	public static final String OPT_EVENT_START_DESCRIPTION = "Start timestamp an Event.  Default is now.  Use unknown for unknown.";
	public static final String OPT_EVENT_START_TYPE = TYPE_TIMESTAMP;

	public static final String OPT_EVENT_TYPE = "eventtype";
	public static final String OPT_EVENT_TYPE_DESCRIPTION = "Class of the Event to create.";
	public static final String OPT_EVENT_TYPE_TYPE = TYPE_CLASS;

	public static final String OPT_HELP = "help";
	public static final String OPT_HELP_DESCRIPTION = "Print this message";

	public static final String OPT_IS_LC_PACKAGE_STRUCTURE = "islcpackagestructure";
	public static final String OPT_IS_LC_PACKAGE_STRUCTURE_DESCRIPTION = "Whether a file location is lc package structured.  Default is true.";
	public static final String OPT_IS_LC_PACKAGE_STRUCTURE_TYPE = TYPE_BOOLEAN;

	public static final String OPT_IS_MANAGED = "ismanaged";
	public static final String OPT_IS_MANAGED_DESCRIPTION = "Whether a file location is managed.  Default is true.";
	public static final String OPT_IS_MANAGED_TYPE = TYPE_BOOLEAN;	
	
	public static final String OPT_MESSAGE = "message";
	public static final String OPT_MESSAGE_DESCRIPTION = "Message providing details on the Event.";
	public static final String OPT_MESSAGE_TYPE = "message";
		
	public static final String OPT_PACKAGE = "package";
	public static final String OPT_PACKAGE_DESCRIPTION = "Id of the package.";
	public static final String OPT_PACKAGE_TYPE = TYPE_ID;

	public static final String OPT_PACKAGE_CLASS = "packagetype";
	public static final String OPT_PACKAGE_CLASS_DESCRIPTION = "Class of the package to create.  Default is " + Package.class.getName() + ".";
	public static final String OPT_PACKAGE_CLASS_TYPE = TYPE_CLASS;
	
	public static final String OPT_PERFORMING_AGENT = "performingagent";
	public static final String OPT_PERFORMING_AGENT_DESCRIPTION = "Id of the agent performing the action.  Use unknown for unknown.";
	public static final String OPT_PERFORMING_AGENT_TYPE = TYPE_ID;
	
	public static final String OPT_REPOSITORY = "repository";
	public static final String OPT_REPOSITORY_DESCRIPTION = "Id of the repository.";
	public static final String OPT_REPOSITORY_TYPE = TYPE_ID;

	public static final String OPT_REQUESTING_AGENT = "requestingagent";
	public static final String OPT_REQUESTING_AGENT_DESCRIPTION = "Id of the agent requesting the action.  Use unknown for unknown.";
	public static final String OPT_REQUESTING_AGENT_TYPE = TYPE_ID;

	public static final String OPT_SOURCE_BASEPATH = "sourcebasepath";
	public static final String OPT_SOURCE_BASEPATH_DESCRIPTION = "Basepath of the source file location.";
	public static final String OPT_SOURCE_BASEPATH_TYPE = TYPE_PATH;
		
	public static final String OPT_SOURCE_STORAGE_SYSTEM = "sourcestoragesystem";
	public static final String OPT_SOURCE_STORAGE_SYSTEM_DESCRIPTION = "Id of the source storage system";
	public static final String OPT_SOURCE_STORAGE_SYSTEM_TYPE = TYPE_ID;
		
	public static final String OPT_STORAGE_SYSTEM = "storagesystem";
	public static final String OPT_STORAGE_SYSTEM_DESCRIPTION = "Id of the storage system";
	public static final String OPT_STORAGE_SYSTEM_TYPE = TYPE_ID;
	
	public static final String OPT_SUCCESS = "success";
	public static final String OPT_SUCCESS_DESCRIPTION = "Success of an Event.  Default is true.";
	public static final String OPT_SUCCESS_TYPE = TYPE_BOOLEAN;

	private static final String UNKNOWN = "unknown";
	private static final String REPORTING_AGENT_KEY = "agent.packagemodeler.id";
	private static final String FALSE = "false";
			
	private static PackageModelDAO dao = new PackageModelDAOImpl();
	private static ModelerFactory factory = new ModelerFactoryImpl();

	private EnhancedHashMap<String,String> options;
	
	public void execute(String action, EnhancedHashMap<String,String> options) throws Exception
	{
		
		this.options = options;
		Session session = HibernateUtil.getSessionFactory(DatabaseRole.DATA_WRITER).getCurrentSession();
		try
		{					
			dao.setSession(session);
			session.beginTransaction();
			if (ACTION_TEST.equalsIgnoreCase(action))
			{
				dao.findRepository("foo");
				java.lang.System.out.println("Database connection is good.");
				
			}
			else if (ACTION_PACKAGE.equalsIgnoreCase(action))
			{
				createPackage();
			}
			else if (ACTION_FILELOCATION.equalsIgnoreCase(action))
			{
				createFileLocation();
			}
			else if (ACTION_PACKAGE_EVENT.equalsIgnoreCase(action))
			{
				createPackageEvent();
			}
			else if (ACTION_FILELOCATION_EVENT.equalsIgnoreCase(action))
			{
				createFileLocationEvent();
			}
			else if (ACTION_INVENTORY_FROM_MANIFEST.equalsIgnoreCase(action))
			{
				inventoryFromManifest();
			}
			else if (ACTION_CANONICALIZE_FROM_FILELOCATION.equalsIgnoreCase(action))
			{
				canonicalizeFromFileLocation();
			}
			else
			{
				throw new Exception(action + " is an unrecognized action");
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

	@SuppressWarnings("unchecked")
	private void createPackage() throws Exception
	{
		String packageId = this.options.getRequired(OPT_PACKAGE);
		String packageClassName = this.options.get(OPT_PACKAGE_CLASS, Package.class.getName());
		Class packageClass = Class.forName(packageClassName);
		Package packge = factory.createPackage(packageClass, getRepository(), packageId);
		dao.save(packge);
	}
	
	private void createFileLocation() throws Exception
	{
		boolean isManaged = true;
		if (options.containsKey(OPT_IS_MANAGED) && FALSE.equalsIgnoreCase(options.get(OPT_IS_MANAGED)))
		{
			isManaged = false;
		}
		boolean isLCPackageStructure = true;
		if (options.containsKey(OPT_IS_LC_PACKAGE_STRUCTURE) && FALSE.equalsIgnoreCase(options.get(OPT_IS_LC_PACKAGE_STRUCTURE)))
		{
			isLCPackageStructure = false;
		}
		FileLocation fileLocation;
		if (options.containsKey(OPT_STORAGE_SYSTEM))
		{
			String storageSystemId = options.getRequired(OPT_STORAGE_SYSTEM);
			String basePath = options.getRequired(OPT_BASEPATH);
			fileLocation = factory.createStorageSystemFileLocation(getPackage(), dao.findRequiredAgent(System.class, storageSystemId), basePath, isManaged, isLCPackageStructure);
			
		}
		else
		{
			throw new Exception("Only creating Storage System File Location is currently supported");
		}
		dao.save(fileLocation);
	}
	
	private Package getPackage() throws Exception
	{
		String packageId = this.options.getRequired(OPT_PACKAGE);
		return dao.findRequiredPackage(Package.class, getRepository(), packageId);
	}
	
	private Repository getRepository() throws Exception
	{
		String repositoryId = this.options.getRequired(OPT_REPOSITORY);
		return dao.findRequiredRepository(repositoryId);
	}
	
	private FileLocation getFileLocation() throws Exception
	{
		if (options.containsKey(OPT_STORAGE_SYSTEM))
		{
			String storageSystemId = options.getRequired(OPT_STORAGE_SYSTEM);
			String basePath = options.getRequired(OPT_BASEPATH);
			Package packge = getPackage();
			return packge.getFileLocation(storageSystemId, basePath);			
		}
		else
		{
			throw new Exception("Only Storage System File Location is currently supported");
		}
		
	}
	
	private Class getEventClass() throws Exception
	{
		String eventClassName = this.options.get(OPT_EVENT_TYPE);
		return Class.forName(eventClassName);
	}

	private Date getEventStart() throws Exception
	{
		String eventStart = this.options.get(OPT_EVENT_START);
		if (eventStart == null)
		{
			return Calendar.getInstance().getTime();
		}
		else if (UNKNOWN.equalsIgnoreCase(eventStart))
		{
			throw new Exception("Unknown event starts not yet supported");
		}
		return parseDate(eventStart);
		
	}
	
	private Date parseDate(String dateString) throws Exception
	{
		return ISODateTimeFormat.dateTimeParser().parseDateTime(dateString).toDate();
	}
	
	@SuppressWarnings("unchecked")
	private void createPackageEvent() throws Exception
	{				
		PackageEvent event = factory.createPackageEvent(getEventClass(), getPackage(), getEventStart(), getReportingAgent());
		populateEvent(event);
		dao.save(event);
		
	}

	@SuppressWarnings("unchecked")
	private void createFileLocationEvent() throws Exception
	{
		FileLocationEvent event = factory.createFileLocationEvent(getEventClass(), getFileLocation(), getEventStart(), getReportingAgent());
		populateEvent(event);
		if (event instanceof FileCopyEvent)
		{
			populateFileCopyEvent((FileCopyEvent)event);
		}
		
		dao.save(event);
		
	}
			
	private void populateEvent(Event event) throws Exception
	{
		//Is success
		if (options.containsKey(OPT_SUCCESS) && options.get(OPT_SUCCESS).equalsIgnoreCase(FALSE))
		{
			event.setSuccess(false);
		}
		else
		{
			event.setSuccess(true);
		}
		//Performing agent
		if (options.containsKey(OPT_PERFORMING_AGENT))
		{
			Agent performingAgent = dao.findRequiredAgent(Agent.class, options.getRequired(OPT_PERFORMING_AGENT));
			event.setPerformingAgent(performingAgent);
		}
		//Requesting agent
		if (options.containsKey(OPT_REQUESTING_AGENT))
		{
			Agent requestingAgent = dao.findRequiredAgent(Agent.class, options.getRequired(OPT_REQUESTING_AGENT));
			event.setPerformingAgent(requestingAgent);
		}		
		//Event end
		if (options.containsKey(OPT_EVENT_END))
		{
			event.setEventEnd(parseDate(options.get(OPT_EVENT_END)));
		}
		//Message
		if (options.containsKey(OPT_MESSAGE))
		{
			event.setMessage(options.get(OPT_MESSAGE));
		}
	}
	
	private void populateFileCopyEvent(FileCopyEvent event) throws Exception
	{
		String storageSystemId = options.getRequired(OPT_SOURCE_STORAGE_SYSTEM);
		String basePath = options.getRequired(OPT_SOURCE_BASEPATH);
		Package packge = getPackage();
		FileLocation fileLocation = packge.getFileLocation(storageSystemId, basePath);
		event.setFileLocationSource(fileLocation);

	}
	
	private Agent getReportingAgent() throws Exception
	{
		String reportingAgent = ConfigurationFactory.getConfiguration(PackageModelerConstants.PROPERTIES_NAME).getString(REPORTING_AGENT_KEY);
		if (reportingAgent == null)
		{
			throw new Exception(MessageFormat.format("Property {0} is missing", REPORTING_AGENT_KEY));
		}
		return dao.findRequiredAgent(Agent.class, reportingAgent);
	}	
	
	private void inventoryFromManifest() throws Exception
	{
		Date eventStart = Calendar.getInstance().getTime();
		FileLocation fileLocation = getFileLocation();
		ManifestReader reader = new ManifestReader();
		File packageDirectory = new File(fileLocation.getBasePath());
		File manifestFile = PackageHelper.discoverManifest(packageDirectory);
		reader.setFile(manifestFile);
		factory.createFileInstances(fileLocation, reader);
		
		//Add files from the package root
		if (fileLocation.isLCPackageStructure())
		{
			List<File> fileList = PackageHelper.discoverLCPackageRootFiles(packageDirectory);
			for(File file : fileList)
			{
				String filename = FilenameHelper.removeBasePath(fileLocation.getBasePath(), FilenameHelper.normalize(file.toString()));
				factory.createFileInstance(fileLocation, new FileName(filename));
			}			
		}
		
		//Record InventoryFromManifestEvent
		InventoryFromManifestEvent event = factory.createFileLocationEvent(InventoryFromManifestEvent.class, fileLocation, eventStart, this.getReportingAgent());
		event.setPerformingAgent(this.getReportingAgent());
		event.setEventEnd(Calendar.getInstance().getTime());
		//Requesting agent
		if (options.containsKey(OPT_REQUESTING_AGENT))
		{
			Agent requestingAgent = dao.findRequiredAgent(Agent.class, options.getRequired(OPT_REQUESTING_AGENT));
			event.setPerformingAgent(requestingAgent);
		}		

		if (options.containsKey(OPT_CREATE_CANONICAL_FILES) && "true".equalsIgnoreCase(options.getRequired(OPT_CREATE_CANONICAL_FILES)))
		{
			factory.createCanonicalFilesFromFileInstances(fileLocation.getPackage(), fileLocation.getFileInstances());
		}
				
		dao.save(fileLocation.getPackage());
		
	}
	
	private void canonicalizeFromFileLocation() throws Exception
	{
		FileLocation fileLocation = getFileLocation();
		factory.createCanonicalFilesFromFileInstances(fileLocation.getPackage(), fileLocation.getFileInstances());
		
		dao.save(fileLocation.getPackage());
		
	}
	
}
