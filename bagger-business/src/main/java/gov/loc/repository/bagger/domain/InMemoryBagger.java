
package gov.loc.repository.bagger.domain;

import javax.sql.DataSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import gov.loc.repository.bagger.jdbc.JdbcBagger;

/**
 * Provides an in-memory Bagger business object.
 *
 * <P>
 * Leverages HSQL database's in-memory option and uses the Spring-supplied
 * <code>SimpleJdbcOrganization</code>. This class simply inserts the schema and base
 * data into the in-memory instance at startup time. It also inserts data
 * required for security.
 *
 * @author Ben Alex
 */
public class InMemoryBagger extends JdbcBagger {

    private final Log logger = LogFactory.getLog(getClass());

    private DataSource dataSource;
	private ArrayList<String> commandList = new ArrayList<String>();

    /**
     * Note: the SimpleJdbcOrganization uses autowiring, we could do the same here.
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource=dataSource;
        logger.info("InMemoryBagger.init");
        init();
    }

    public void init() {
        super.init(dataSource);
        JdbcTemplate template = new JdbcTemplate(dataSource);

        // Schema: Acegi Security
        template.execute("CREATE TABLE users (username VARCHAR(50) NOT NULL PRIMARY KEY, password VARCHAR(50) NOT NULL, enabled BIT NOT NULL)");
        template.execute("CREATE TABLE authorities (username VARCHAR(50) NOT NULL, authority VARCHAR(50) NOT NULL)");
        template.execute("alter table authorities add constraint fk_authorities_users foreign key (username) references users(username)");

        // Schema: Bagger
        template.execute("CREATE TABLE person (id INT NOT NULL IDENTITY PRIMARY KEY, first_name VARCHAR(30), middle_init VARCHAR(1), last_name VARCHAR(30))");
        template.execute("CREATE TABLE projects (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80))");
        template.execute("CREATE TABLE organization (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80), address VARCHAR(256))");

        template.execute("CREATE TABLE contact (id INT NOT NULL IDENTITY PRIMARY KEY, person_id INT NOT NULL, organization_id INT NOT NULL, email VARCHAR(80), telephone VARCHAR(20))");
        template.execute("alter table contact add constraint fk_contact_person foreign key (person_id) references person(id)");
        template.execute("alter table contact add constraint fk_contact_org foreign key (organization_id) references organization(id)");

        template.execute("CREATE TABLE profile (id INT NOT NULL IDENTITY PRIMARY KEY, username VARCHAR(50) NOT NULL, profile_person_id INT NOT NULL, project_id INT NOT NULL, contact_id INT NOT NULL, status VARCHAR(2), create_date DATE)");
        template.execute("alter table profile add constraint fk_profile_user foreign key (username) references users(username)");
        template.execute("alter table profile add constraint fk_profile_person foreign key (profile_person_id) references contact(id)");
        template.execute("alter table profile add constraint fk_profile_project foreign key (project_id) references projects(id)");
        template.execute("alter table profile add constraint fk_profile_contact foreign key (contact_id) references contact(id)");

        template.execute("CREATE TABLE person_projects (person_id INT NOT NULL, project_id INT NOT NULL)");
        template.execute("alter table person_projects add constraint fk_person_projects_person foreign key (person_id) references person(id)");
        template.execute("alter table person_projects add constraint fk_person_projects_project foreign key (project_id) references projects(id)");

        template.execute("CREATE TABLE user_contact (username VARCHAR(50) NOT NULL, contact_id INT NOT NULL)");
        template.execute("alter table user_contact add constraint fk_user_contact_user foreign key (username) references users(username)");
        template.execute("alter table user_contact add constraint fk_user_contact_contact foreign key (contact_id) references contact(id)");

        template.execute("CREATE TABLE bag (id INT NOT NULL IDENTITY PRIMARY KEY, profile_id INT NOT NULL, name VARCHAR(80), visit_date DATE, location VARCHAR(255), description VARCHAR(255))");
        template.execute("alter table bag add constraint fk_bag_profile foreign key (profile_id) references profile(id)");

        getCommandList();
        for (int i=0; i < commandList.size(); i++) {
        	String command = commandList.get(i);
        	template.execute(command);
        }
    }
    
    private List<String> getCommandList() {
        // Data: Acegi Security
    	commandList.add("INSERT INTO users VALUES ('lesliej', 'lesliej', true)");
        commandList.add("INSERT INTO users VALUES ('aboyko', 'aboyko', false)");
        commandList.add("INSERT INTO users VALUES ('justin', 'justin', true)");
        commandList.add("INSERT INTO users VALUES ('liz', 'liz', true)");
        commandList.add("INSERT INTO users VALUES ('jkunze', 'jkunze', true)");
        commandList.add("INSERT INTO users VALUES ('tvoy', 'tvoy', true)");
        commandList.add("INSERT INTO users VALUES ('user', 'user', true)");
        commandList.add("INSERT INTO authorities VALUES ('lesliej', 'ROLE_BAGGER_STAFF')");
        commandList.add("INSERT INTO authorities VALUES ('aboyko', 'ROLE_BAGGER_USER')");
        commandList.add("INSERT INTO authorities VALUES ('justin', 'ROLE_BAGGER_STAFF')");
        commandList.add("INSERT INTO authorities VALUES ('liz', 'ROLE_BAGGER_STAFF')");
        commandList.add("INSERT INTO authorities VALUES ('jkunze', 'ROLE_BAGGER_USER')");
        commandList.add("INSERT INTO authorities VALUES ('tvoy', 'ROLE_BAGGER_STAFF')");
        commandList.add("INSERT INTO authorities VALUES ('user', 'ROLE_BAGGER_USER')");

        // Data: Bagger
        // "CREATE TABLE person (id, first_name, middle_init, last_name");
/*        commandList.add("INSERT INTO person VALUES (1, 'Jon', '', 'Steinbach')");
        commandList.add("INSERT INTO person VALUES (2, 'Leslie', '', 'Johnston')");
        commandList.add("INSERT INTO person VALUES (3, 'Andy', '', 'Boyko')");
        commandList.add("INSERT INTO person VALUES (4, 'Justin', '', 'Littman')");
        commandList.add("INSERT INTO person VALUES (5, 'Ty', '', 'Voy')");
        commandList.add("INSERT INTO person VALUES (6, 'John', 'A', 'Kunze')");
*/
        // "CREATE TABLE projects (id, name");
        commandList.add("INSERT INTO projects VALUES (1, 'eDeposit')");
        commandList.add("INSERT INTO projects VALUES (2, 'ndiip')");
        commandList.add("INSERT INTO projects VALUES (3, 'ndnp')");
        commandList.add("INSERT INTO projects VALUES (4, 'transfer')");

        // "CREATE TABLE organization (id, name, address");
/*        commandList.add("INSERT INTO organization VALUES (1, 'Library of Congress', '101 Independence Ave. SE, Washington, DC 20540, USA');");
        commandList.add("INSERT INTO organization VALUES (2, 'U.S. Copyright Office', '101 Independence Ave. S.E., Washington, D.C. 20559-6000');");
        commandList.add("INSERT INTO organization VALUES (3, 'California Digital Library', '415 20th St, 4th Floor, Oakland, CA 94612, USA');");
*/
        // "CREATE TABLE contact (id, person_id, organization_id, email, telephone");
/*        commandList.add("INSERT INTO contact VALUES (1, 1, 1, 'jste@loc.gov', '202-555-7371');");
        commandList.add("INSERT INTO contact VALUES (2, 2, 1, 'lesliej@loc.gov', '202-555-7372');");
        commandList.add("INSERT INTO contact VALUES (3, 3, 1, 'aboyko@loc.gov', '202-555-7373');");
        commandList.add("INSERT INTO contact VALUES (4, 4, 1, 'jlit@loc.gov', '202-555-7374');");
        commandList.add("INSERT INTO contact VALUES (5, 5, 1, 'tvoy@loc.gov', '202-555-7375');");
        commandList.add("INSERT INTO contact VALUES (6, 6, 3, 'jak@ucop.edu', '202-555-7373');");
*/
        // "CREATE TABLE profile (id, username, profile_person_id, project_id, contact_id, status, create_date");
        // "(1, 'eDeposit')(2, 'ndiip')(3, 'ndnp')(4, 'transfer')");
/*        commandList.add("INSERT INTO profile VALUES (1, 'user',    1, 4, 2, 'A', '2008-09-18')");
        commandList.add("INSERT INTO profile VALUES (2, 'user',    1, 1, 3, 'A', '2008-10-20')");
        commandList.add("INSERT INTO profile VALUES (3, 'user',    1, 3, 4, 'A', '2008-10-20')");
        commandList.add("INSERT INTO profile VALUES (4, 'lesliej', 2, 4, 6, 'A', '2008-08-06')");
        commandList.add("INSERT INTO profile VALUES (5, 'jkunze',  4, 3, 3, 'A', '2002-04-17')");
        commandList.add("INSERT INTO profile VALUES (6, 'tvoy',    5, 1, 5, 'A', '2002-04-17')");
*/
        // "CREATE TABLE person_projects (person_id, project_id");
/*        commandList.add("INSERT INTO person_projects VALUES (1, 1);");
        commandList.add("INSERT INTO person_projects VALUES (1, 3);");
        commandList.add("INSERT INTO person_projects VALUES (1, 4);");
        commandList.add("INSERT INTO person_projects VALUES (5, 1);");
        commandList.add("INSERT INTO person_projects VALUES (6, 2);");
*/
        // "CREATE TABLE user_contact (username, contact_id");
//        commandList.add("INSERT INTO user_contact VALUES ('user', 1);");
    	String userHomeDir = System.getProperty("user.home");
    	readCommandList(userHomeDir);
        
    	return commandList;
    }
    
	private String readCommandList(String homeDir) {
		String message = null;
		String name = "bagger.sql";
		try
		{
			File file = new File(homeDir, name);
			if (file.exists()) {
				InputStreamReader fr = new InputStreamReader(new FileInputStream(file), "UTF-8");
				BufferedReader  reader = new BufferedReader(fr);
				try
				{
					while(true)
					{
						String line = reader.readLine();
						if (line == null) break;
						this.commandList.add(line);
					}
				}
				catch(IOException ex)
				{
					message = "InMemoryBagger.readCommandList: " + ex.getMessage();
					ex.printStackTrace();
					throw new RuntimeException(ex);
				}				
			}
		}
		catch(IOException e)
		{
			message = "InMemoryBagger.readCommandList: " + e.getMessage();
			e.printStackTrace();
		}
		return message;		
	}
}