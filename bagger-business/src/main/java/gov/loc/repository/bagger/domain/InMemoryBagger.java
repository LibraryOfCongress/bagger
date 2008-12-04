
package gov.loc.repository.bagger.domain;

import javax.sql.DataSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import gov.loc.repository.bagger.jdbc.JdbcBagger;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;

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
	private File baggerFile = null;

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

        // "CREATE TABLE projects (id, name");
        commandList.add("INSERT INTO projects VALUES (1, 'eDeposit')");
        commandList.add("INSERT INTO projects VALUES (2, 'ndiip')");
        commandList.add("INSERT INTO projects VALUES (3, 'ndnp')");
        commandList.add("INSERT INTO projects VALUES (4, 'transfer')");

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
				baggerFile = file;
				showConfirmation();
			}
		}
		catch(Exception e)
		{
			message = "InMemoryBagger.readCommandList: " + e.getMessage();
			e.printStackTrace();
		}
		return message;		
	}
	
	private void loadProfiles(File file) {
		String message = null;

		try
		{
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
			catch(Exception ex)
			{
				message = "InMemoryBagger.readCommandList: " + ex.getMessage();
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}				
		}
		catch(IOException e)
		{
			message = "InMemoryBagger.readCommandList: " + e.getMessage();
			e.printStackTrace();
		}
	}

	private void showConfirmation() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	loadProfiles(baggerFile);
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle("Load profiles confirmation.");
	    dialog.setConfirmationMessage("Saved profiles have been detected.  Would you like to load them?");
	    dialog.showDialog();
	}
}