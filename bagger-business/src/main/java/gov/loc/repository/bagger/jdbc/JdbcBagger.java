package gov.loc.repository.bagger.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.orm.ObjectRetrievalFailureException;
import gov.loc.repository.bagger.Address;
import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.ContactType;
import gov.loc.repository.bagger.Person;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.PersonProjects;
import gov.loc.repository.bagger.Organization;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A simple JDBC-based implementation of the {@link Profile} interface.
 *
 * <p>This class uses Java 5 language features and the {@link SimpleJdbcTemplate}
 * plus {@link SimpleJdbcInsert}. It also takes advantage of classes like
 * {@link BeanPropertySqlParameterSource} and
 * {@link ParameterizedBeanPropertyRowMapper} which provide automatic mapping
 * between JavaBean properties and JDBC parameters or query results.
 *
 * @author Ken Krebs
 */
@Service
@ManagedResource("bagger:type=Bagger")
public class JdbcBagger implements Bagger, JdbcBaggerMBean {

	private final Log logger = LogFactory.getLog(getClass());

	private SimpleJdbcTemplate simpleJdbcTemplate;

	private SimpleJdbcInsert insertPerson;
	private SimpleJdbcInsert insertProject;
	private SimpleJdbcInsert insertContactTypes;
	private SimpleJdbcInsert insertAddress;
	private SimpleJdbcInsert insertOrganization;
	private SimpleJdbcInsert insertContact;
	private SimpleJdbcInsert insertProfile;
	private SimpleJdbcInsert insertBag;

	private final List<Organization> orgs = new ArrayList<Organization>();
	private final List<Project> projects = new ArrayList<Project>();


	@Autowired
	public void init(DataSource dataSource) {
		this.simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);

		this.insertPerson = new SimpleJdbcInsert(dataSource)
			.withTableName("person")
			.usingGeneratedKeyColumns("id");
		this.insertProject = new SimpleJdbcInsert(dataSource)
			.withTableName("projects")
			.usingGeneratedKeyColumns("id");
		this.insertContactTypes = new SimpleJdbcInsert(dataSource)
			.withTableName("contact_types")
			.usingGeneratedKeyColumns("id");
		this.insertAddress = new SimpleJdbcInsert(dataSource)
			.withTableName("address")
			.usingGeneratedKeyColumns("id");
		this.insertOrganization = new SimpleJdbcInsert(dataSource)
			.withTableName("organization")
			.usingGeneratedKeyColumns("id");
		this.insertContact = new SimpleJdbcInsert(dataSource)
			.withTableName("contact")
			.usingGeneratedKeyColumns("id");
		this.insertProfile = new SimpleJdbcInsert(dataSource)
			.withTableName("profile")
			.usingGeneratedKeyColumns("id");
		this.insertBag = new SimpleJdbcInsert(dataSource)
			.withTableName("bag")
			.usingGeneratedKeyColumns("id");
	}


	@ManagedOperation
	@Transactional(readOnly = true)
	public void refreshCache() throws DataAccessException {
		synchronized (this.orgs) {
			this.logger.info("Refreshing vets cache");
/*
			// Retrieve the list of all vets.
			this.orgs.clear();
			this.orgs.addAll(this.simpleJdbcTemplate.query(
					"SELECT id, first_name, last_name FROM vets ORDER BY last_name,first_name",
					ParameterizedBeanPropertyRowMapper.newInstance(Vet.class)));

			// Retrieve the list of all possible specialties.
			final List<Specialty> specialties = this.simpleJdbcTemplate.query(
					"SELECT id, name FROM specialties",
					ParameterizedBeanPropertyRowMapper.newInstance(Specialty.class));

			// Build each vet's list of specialties.
			for (Vet vet : this.vets) {
				final List<Integer> vetSpecialtiesIds = this.simpleJdbcTemplate.query(
						"SELECT specialty_id FROM vet_specialties WHERE vet_id=?",
						new ParameterizedRowMapper<Integer>() {
							public Integer mapRow(ResultSet rs, int row) throws SQLException {
								return Integer.valueOf(rs.getInt(1));
							}},
						vet.getId().intValue());
				for (int specialtyId : vetSpecialtiesIds) {
					Specialty specialty = EntityUtils.getById(specialties, Specialty.class, specialtyId);
					vet.addSpecialty(specialty);
				}
			}
*/
		}
	}

	@Transactional(readOnly = true)
	public Collection<Organization> getOrganizations() throws DataAccessException {
		return this.simpleJdbcTemplate.query(
				"SELECT * FROM organization ORDER BY name",
				ParameterizedBeanPropertyRowMapper.newInstance(Organization.class));
	}

	@Transactional(readOnly = true)
	public Collection<Project> getProjects() throws DataAccessException {
		return this.simpleJdbcTemplate.query(
				"SELECT * FROM projects ORDER BY name",
				ParameterizedBeanPropertyRowMapper.newInstance(Project.class));
	}
	
	@Transactional(readOnly = true)
	public Collection<ContactType> getContactTypes() throws DataAccessException {
		return this.simpleJdbcTemplate.query(
				"SELECT * FROM contact_types ORDER BY name",
				ParameterizedBeanPropertyRowMapper.newInstance(ContactType.class));
	}


	@Transactional(readOnly = true)
	public Collection<Organization> findOrganizations(String name) throws DataAccessException {
		List<Organization> orgs = this.simpleJdbcTemplate.query(
				"SELECT * FROM organization WHERE name like ?",
				ParameterizedBeanPropertyRowMapper.newInstance(Organization.class),
				name + "%");
		return orgs;
	}

	@Transactional(readOnly = true)
	public Collection<Profile> findProfiles(String name) throws DataAccessException {
		List<Profile> profiles = this.simpleJdbcTemplate.query(
				"SELECT * FROM profile WHERE status='A' AND username=?",
				ParameterizedBeanPropertyRowMapper.newInstance(Profile.class),
				name);
		for (int i=0; i < profiles.size(); i++) {
			int index = profiles.get(i).getId();
			Profile profile = loadProfile(index);
			profiles.set(i, profile);
		}
		return profiles;
	}

	@Transactional(readOnly = true)
	public Collection<Project> findProjects(int personId) throws DataAccessException {
		List<PersonProjects> personProjects = this.simpleJdbcTemplate.query(
				"SELECT * FROM person_projects WHERE person_id=?",
				ParameterizedBeanPropertyRowMapper.newInstance(PersonProjects.class),
				personId);
		ArrayList<Project> projectList = new ArrayList<Project>();
		for (int i=0; i < personProjects.size(); i++) {
			int projectId = personProjects.get(i).getProjectId();
			Project project = loadProject(projectId);
			projectList.add(project);
		}
		return projectList;
	}

	@Transactional(readOnly = true)
	public Profile loadProfile(int id) throws DataAccessException {
		Profile profile;
		try {
			profile = this.simpleJdbcTemplate.queryForObject(
					"SELECT * FROM profile WHERE id=?",
					ParameterizedBeanPropertyRowMapper.newInstance(Profile.class),
					id);
			Contact person = loadContact(profile.getProfilePersonId());
			profile.setPerson(person);
			Contact contact = loadContact(profile.getContactId());
			profile.setContact(contact);
			Project project = loadProject(profile.getProjectId());
			profile.setProject(project);
/*
			System.out.println("JdbcBagger.findProfiles id: " + profile.getId() + ", username: " + profile.getUsername() );
			System.out.println("JdbcBagger.findProfiles personId: " + profile.getProfilePersonId() + ", contactId: " + profile.getContactId() + ", projectId: " + profile.getProjectId());
			Organization org = loadOrganization(1);
			System.out.println("JdbcBagger.loadOrganization id: " + org.getId() + ", name: " + org.getName() + ", addressId: " + org.getAddressId() + ", address: " + org.getAddress() );
			person = loadPerson(1);
			System.out.println("JdbcBagger.loadPerson id: " + person.getId() + ", firstname: " + person.getFirstName() + ", lastname: " + person.getLastName() );
			Address address = loadAddress(1);
			System.out.println("jdbcBagger.loadAddress id: " + address.getId());
*/
		}
		catch (EmptyResultDataAccessException ex) {
			throw new ObjectRetrievalFailureException(Profile.class, new Integer(id));
		}
		return profile;
	}

	@Transactional(readOnly = true)
	public Contact loadContact(int id) throws DataAccessException {
		Contact contact;
		try {
			contact = this.simpleJdbcTemplate.queryForObject(
					"SELECT * FROM contact WHERE id=?",
					ParameterizedBeanPropertyRowMapper.newInstance(Contact.class),
					id);
			ContactType contactType = loadContactType(contact.getTypeId());
			contact.setContactType(contactType);
			Person person = loadPerson(contact.getPersonId());
			contact.setPerson(person);
			Organization organization = loadOrganization(contact.getOrganizationId());
			contact.setOrganization(organization);
		}
		catch (EmptyResultDataAccessException ex) {
			throw new ObjectRetrievalFailureException(Contact.class, new Integer(id));
		}
		return contact;
	}
	
	@Transactional(readOnly = true)
	public ContactType loadContactType(int id) throws DataAccessException {
		ContactType contactType;
		try {
			contactType = this.simpleJdbcTemplate.queryForObject(
					"SELECT * FROM contact_types WHERE id=?",
					ParameterizedBeanPropertyRowMapper.newInstance(ContactType.class),
					id);
		}
		catch (EmptyResultDataAccessException ex) {
			throw new ObjectRetrievalFailureException(ContactType.class, new Integer(id));
		}
		return contactType;
	}

	@Transactional(readOnly = true)
	public Person loadPerson(int id) throws DataAccessException {
		Person person;
		try {
			person = this.simpleJdbcTemplate.queryForObject(
					"SELECT * FROM person WHERE id=?",
					ParameterizedBeanPropertyRowMapper.newInstance(Person.class),
					id);
		}
		catch (EmptyResultDataAccessException ex) {
			throw new ObjectRetrievalFailureException(Person.class, new Integer(id));
		}
		return person;
	}

	@Transactional(readOnly = true)
	public Organization loadOrganization(int id) throws DataAccessException {
		Organization org;
		try {
			org = this.simpleJdbcTemplate.queryForObject(
					"SELECT * FROM organization WHERE id=?",
					ParameterizedBeanPropertyRowMapper.newInstance(Organization.class),
					id);
			Address address = loadAddress(org.getAddressId());
			org.setAddress(address);
		}
		catch (EmptyResultDataAccessException ex) {
			throw new ObjectRetrievalFailureException(Organization.class, new Integer(id));
		}
		return org;
	}

	@Transactional(readOnly = true)
	public Address loadAddress(int id) throws DataAccessException {
		Address address;
		try {
			address = this.simpleJdbcTemplate.queryForObject(
					"SELECT * FROM address WHERE id=?",
					ParameterizedBeanPropertyRowMapper.newInstance(Address.class),
					id);
		}
		catch (EmptyResultDataAccessException ex) {
			throw new ObjectRetrievalFailureException(Address.class, new Integer(id));
		}
		return address;
	}

	@Transactional(readOnly = true)
	public Project loadProject(int id) throws DataAccessException {
		Project project;
		try {
			project = this.simpleJdbcTemplate.queryForObject(
					"SELECT * FROM projects WHERE id=?",
					ParameterizedBeanPropertyRowMapper.newInstance(Project.class),
					id);
		}
		catch (EmptyResultDataAccessException ex) {
			throw new ObjectRetrievalFailureException(Project.class, new Integer(id));
		}
		return project;
	}
	
	@Transactional
	public void storeOrganization(Organization org) throws DataAccessException {
		try {
			Organization organization = loadOrganization(org.getId());
			Number newKey = this.insertOrganization.executeAndReturnKey(
						new BeanPropertySqlParameterSource(org));
			organization.setId(newKey.intValue());
		}
		catch (Exception ex) {
			try {
				this.simpleJdbcTemplate.update(
						"UPDATE organization SET name=:name, address_id=:addressId WHERE id=:id",
						new BeanPropertySqlParameterSource(org));
			}
			catch (Exception exception) {
				throw new UnsupportedOperationException("Organization update not supported");				
			}
		}
	}


	/**
	 * Creates a {@link MapSqlParameterSource} based on data values from the
	 * supplied {@link Profile} instance.
	 */
/* */
	private MapSqlParameterSource createProfileParameterSource(Profile profile) {
		return new MapSqlParameterSource()
			.addValue("id", profile.getId())
			.addValue("username", profile.getUsername())
			.addValue("person_id", profile.getProfilePersonId())
			.addValue("contact_id", profile.getContactId())
			.addValue("project_id", profile.getProjectId());
	}
/* */
}
