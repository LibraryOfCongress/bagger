package gov.loc.repository.packagemodeler.dao.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import gov.loc.repository.Ided;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.dao.FileListComparisonResult;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.impl.ExternalFileLocationImpl;
import gov.loc.repository.packagemodeler.packge.impl.StorageSystemFileLocationImpl;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;
import gov.loc.repository.utilities.results.ResultIterator;
import gov.loc.repository.utilities.FilenameHelper;

public class PackageModelDAOImpl implements PackageModelDAO {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(PackageModelDAOImpl.class);
	
	private SessionFactory sessionFactory;
	private Session session = null;
	

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;		
	}
	
	public void setSessionDatabaseRole(DatabaseRole databaseRole) {
		this.sessionFactory = HibernateUtil.getSessionFactory(databaseRole);
		
	}
	
	public void setSessionDatabaseRole(String databaseRole) {
		this.setSessionDatabaseRole(Enum.valueOf(DatabaseRole.class, databaseRole));		
	}
	
	public Session getSession() throws Exception
	{
		if (session != null)
		{
			return session;
		}
		if (sessionFactory == null)
		{
			throw new Exception("Neither session, session factory, nor database role was provided to PackageModelDao");
		}
		return sessionFactory.getCurrentSession();
	}
	
	public void setSession(Session session)
	{
		this.session = session;
	}

	public Long calculatePackageSize(Package packge) throws Exception {
		Query query = this.getSession().createQuery(
				"select sum(package.canonicalFileSet.bytes) " +
				"from Package as package " +
				"where package = :package"
				);
		query.setParameter("package", packge);
		return (Long)query.uniqueResult();
	}

	public Map<String, Long> countCanonicalFilesByExtension(Package packge) throws Exception {
		Map<String, Long> results = new HashMap<String, Long>();
		Query query = this.getSession().createQuery(
				"select cf.fileName.extension, count(*) " +
				"from Package as p " +
				"inner join p.canonicalFileSet cf " +
				"where p = :package " +
				"group by cf.fileName.extension"					
				);
		query.setParameter("package", packge);
		Iterator<?> resultIter = query.list().iterator();
		while (resultIter.hasNext())
		{
			Object[] row = (Object[])resultIter.next();
			results.put((String)row[0], (Long)row[1]);
		}
		return results;
	}

	public void delete(Object object) throws Exception {
		this.getSession().delete(object);
	}

	public <T extends Agent> T findAgent(Class<T> agentType, String agentId) throws Exception {
		return this.find(agentType, agentId);
	}

	public CanonicalFile findCanonicalFile(String repositoryId, String packageId, String filename) throws Exception {
		String queryString =
			"from CanonicalFile as cf " +
			"where cf.fileName = :filename " +
			"and cf.packge.repository.identifier = :repositoryid " + 
			"and cf.packge.packageId = :packageid";
		Query query = this.getSession().createQuery(queryString);						
		query.setParameter("filename", new FileName(filename));
		query.setString("repositoryid", repositoryId);
		query.setString("packageid", packageId);
		return (CanonicalFile)query.uniqueResult();
	}

	public FileInstance findFileInstance(FileLocation fileLocation, FileName fileName) throws Exception {
		String queryString =
			"select fi " +
			"from FileLocation as fl " + 
			"inner join fl.fileInstanceSet as fi " +
			"where fi.fileName = :filename " +
			"and fl.key = :pkey ";
		
		Query query = this.getSession().createQuery(queryString);						
		query.setParameter("filename", fileName);
		query.setLong("pkey", fileLocation.getKey());
		return (FileInstance)query.uniqueResult();
	}
	
	public FileInstance findFileInstance(String repositoryId, String packageId, String storageSystemId, String basePath, String filename) throws Exception {
		String queryString =
			"select fi " +
			"from StorageSystemFileLocation as fl " + 
			"inner join fl.fileInstanceSet as fi " +
			"where fi.fileName = :filename " +
			"and fl.packge.repository.identifier = :repositoryid " + 
			"and fl.packge.packageId = :packageid " +
			"and fl.storageSystem.identifier = :storagesystemid " +
			"and fl.basePath = :basepath";

		Query query = this.getSession().createQuery(queryString);						
		query.setParameter("filename", new FileName(filename));
		query.setString("repositoryid", repositoryId);
		query.setString("packageid", packageId);
		query.setString("storagesystemid", storageSystemId);
		query.setString("basepath", FilenameHelper.normalize(basePath));
		return (FileInstance)query.uniqueResult();
	}

	public FileExamination findFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName) throws Exception {
		String queryString =
			"from FileExamination as fe " + 
			"where fe.fileName = :filename " +
			"and fe.fileExaminationGroup.key = :pkey ";

		Query query = this.getSession().createQuery(queryString);						
		query.setParameter("filename", fileName);
		query.setLong("pkey", fileExaminationGroup.getKey());
		return (FileExamination)query.uniqueResult();
	}	
	
	@SuppressWarnings("unchecked")
	public List<FileInstance> findFileInstances(Repository repository, String relativePath, String baseName, String extension) throws Exception {
		String queryString =
			"select fi " +
			"from FileInstance as fi " +
			"where fi.fileLocation.packge.repository = :repository ";
		if (relativePath != null)
		{
			queryString += "and fi.fileName.relativePath = :relativepath ";
		}
		if (baseName != null)
		{
			queryString += "and fi.fileName.baseName = :basename "; 
		}
		if (extension != null)
		{
			queryString += "and fi.fileName.extension = :extension";
		}
		Query query = this.getSession().createQuery(queryString);
		query.setParameter("repository", repository);
		if (relativePath != null)
		{
			query.setString("relativepath", relativePath);
		}
		if (baseName != null)
		{
			query.setString("basename", baseName);
		}
		if (extension != null)
		{
			query.setString("extension", extension);
		}
		return query.list();
	}

	public <T> T findPackage(Class<T> packageType, Repository repository, String packageId) throws Exception {
		return this.findPackage(packageType, repository.getId(), packageId);
	}

	public <T> T findPackage(Class<T> packageType, String repositoryId, String packageId) throws Exception {
		Object packge = null;
		Query query = this.getSession().createQuery(
			"from " + getAlias(packageType) + " as p " +
			"where p.packageId = :packageid " +
			"and p.repository.identifier = :repositoryId"
				);
		query.setString("packageid", packageId);
		query.setParameter("repositoryId", repositoryId);
		packge = query.uniqueResult();
		return packageType.cast(packge);
	}

	@SuppressWarnings("unchecked")
	public List<Package> findPackages(Class<?> packageType) throws Exception {
		Query query = this.getSession().createQuery(
				"from " + getAlias(packageType)
					);
		return query.list();
	}

	public ResultIterator findPackagesWithFileCount(Class<Package> packageType, String extension) throws Exception {
  		Query query = this.getSession().createQuery(
  				"select p, " +
  				"( " +
  				"	select count(cf) " +
  				"	from CanonicalFile cf " +
  				"	where cf.fileName.extension = :extension " +
  				"	and cf.packge = p " +
  				") " +
  				"from " + this.getAlias(packageType) + " as p"
  				);
		query.setString("extension", extension);			
		List<?> resultList = query.list();
		String[] fieldNameArray = {"package", "file_count"};
		return new ResultIterator(resultList, fieldNameArray);
	}

	@SuppressWarnings("unchecked")
	public List<Repository> findRepositories() throws Exception {
		Query query = this.getSession().createQuery(
				"from Repository"
					);
		return query.list();
	}

	public Repository findRepository(String repositoryId) throws Exception {
		Query query = this.getSession().createQuery(
				"from Repository as r " + 
				"where r.identifier = :repositoryid"
			);
		query.setString("repositoryid", repositoryId);
		return (Repository)query.uniqueResult();
	}

	public <T extends Agent> T findRequiredAgent(Class<T> agentType, String agentId) throws Exception {
		Object agent = this.find(agentType, agentId);
		if (agent == null)
		{
			throw new Exception(MessageFormat.format("Agent not found with agentId {0}", agentId));
		}
		return agentType.cast(agent);
	}

	public <T> T findRequiredPackage(Class<T> packageType, Repository repository, String packageId) throws Exception {
		return this.findRequiredPackage(packageType, repository.getId(), packageId);
	}

	public <T> T findRequiredPackage(Class<T> packageType, String repositoryId, String packageId) throws Exception {
		Object packge = this.findPackage(packageType, repositoryId, packageId);
		if (packge == null)
		{
			throw new Exception(MessageFormat.format("Package not found with repositoryId {0} and packageId {1}", repositoryId, packageId));
		}
		return packageType.cast(packge);
	}

	public Repository findRequiredRepository(String repositoryId) throws Exception {
		Repository repository = this.findRepository(repositoryId);
		if (repository == null)
		{
			throw new Exception(MessageFormat.format("Repository not found with repositoryId {0}", repositoryId));
		}
		return repository;
	}

	public Role findRequiredRole(String roleId) throws Exception {
		Role role = this.findRole(roleId);
		if (role == null)
		{
			throw new Exception(MessageFormat.format("Role not found with roleId {0}", roleId));
		}
		return role;
	}

	public Role findRole(String roleId) throws Exception {
		return this.find(Role.class, roleId);
	}

	public void save(Object object) throws Exception {
		this.getSession().saveOrUpdate(object);		
	}

	protected String getAlias(Class<?> clazz)
	{
		return clazz.getSimpleName();
	}
		
	protected String getImplClassName(Class<?> clazz)
	{
		return clazz.getName().substring(0, (clazz.getName().length()-clazz.getSimpleName().length())) + "impl." + clazz.getSimpleName() + "Impl";		
	}

	protected <T extends Ided> T find(Class<T> clazz, String id) throws Exception
	{
		if (id == null)
		{
			return null;
		}
		Query query = this.getSession().createQuery(
				"from " + getImplClassName(clazz) + " as e " +
				"where e.identifier = :id"
					);
			query.setString("id", id);			
			return clazz.cast(query.uniqueResult());

	}

	@SuppressWarnings("unchecked")
	public FileListComparisonResult compare(FileLocation fileLocation, FileExaminationGroup fileExaminationGroup) throws Exception {
		if (! fileExaminationGroup.isComplete())
		{
			throw new Exception("Not implemented yet for incomplete File Examination Groups");
		}
		FileListComparisonResult result = new FileListComparisonResult();
		
		Query query1 = this.getSession().getNamedQuery("findFileInstancesMinusFileExaminations");
		query1.setLong("filelocationkey", fileLocation.getKey());
		query1.setLong("fileexaminationgroupkey", fileExaminationGroup.getKey());
		query1.setResultTransformer(Transformers.aliasToBean(FileName.class));
		result.missingFromTargetList = query1.list();

		Query query2 = this.getSession().getNamedQuery("findFileExaminationsMinusFileInstances");
		query2.setLong("filelocationkey", fileLocation.getKey());
		query2.setLong("fileexaminationgroupkey", fileExaminationGroup.getKey());
		query2.setResultTransformer(Transformers.aliasToBean(FileName.class));
		result.additionalInTargetList = query2.list();
				
		Query query3 = this.getSession().getNamedQuery("findFileExaminationsFileInstancesIncomparable");
		query3.setLong("filelocationkey", fileLocation.getKey());
		query3.setLong("fileexaminationgroupkey", fileExaminationGroup.getKey());
		query3.setResultTransformer(Transformers.aliasToBean(FileName.class));
		result.incomparableList = query3.list();

		Query query4 = this.getSession().getNamedQuery("findFileExaminationsFileInstancesMismatch");
		query4.setLong("filelocationkey", fileLocation.getKey());
		query4.setLong("fileexaminationgroupkey", fileExaminationGroup.getKey());
		query4.setResultTransformer(Transformers.aliasToBean(FileName.class));
		result.fixityMismatchList = query4.list();
		
		return result;		
	}
	
	@SuppressWarnings("unchecked")
	public FileListComparisonResult compare(Package packge, FileLocation fileLocation) throws Exception {
		FileListComparisonResult result = new FileListComparisonResult();
		
		Query query1 = this.getSession().getNamedQuery("findCanonicalFilesMinusFileInstances");
		query1.setLong("filelocationkey", fileLocation.getKey());
		query1.setLong("packagekey", packge.getKey());
		query1.setResultTransformer(Transformers.aliasToBean(FileName.class));
		result.missingFromTargetList = query1.list();

		Query query2 = this.getSession().getNamedQuery("findFileInstancesMinusCanonicalFiles");
		query2.setLong("filelocationkey", fileLocation.getKey());
		query2.setLong("packagekey", packge.getKey());
		query2.setResultTransformer(Transformers.aliasToBean(FileName.class));
		result.additionalInTargetList = query2.list();
				
		Query query3 = this.getSession().getNamedQuery("findCanonicalFilesFileInstancesIncomparable");
		query3.setLong("filelocationkey", fileLocation.getKey());
		query3.setLong("packagekey", packge.getKey());
		query3.setResultTransformer(Transformers.aliasToBean(FileName.class));
		result.incomparableList = query3.list();

		Query query4 = this.getSession().getNamedQuery("findCanonicalFilesFileInstancesMismatch");
		query4.setLong("filelocationkey", fileLocation.getKey());
		query4.setLong("packagekey", packge.getKey());
		query4.setResultTransformer(Transformers.aliasToBean(FileName.class));
		result.fixityMismatchList = query4.list();
				
		return result;
		
	}	
	
	@Override
	public FileLocation loadFileLocation(Long key) throws Exception {
		FileLocation fileLocation = (FileLocation)this.getSession().get(StorageSystemFileLocationImpl.class, key);
		if (fileLocation == null)
		{
			fileLocation = (FileLocation)this.getSession().get(ExternalFileLocationImpl.class, key);
		}
		if (fileLocation == null)
		{
			throw new Exception(MessageFormat.format("Could not load a FileLocation with key {0}", key));
		}
		return fileLocation;
	}	
	
}
