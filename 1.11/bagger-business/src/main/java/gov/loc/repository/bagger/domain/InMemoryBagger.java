
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
	private File baggerFile = null;
	private ArrayList<String> defaultList = new ArrayList<String>();

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
        template.execute("CREATE TABLE projects (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80), is_default boolean)");
        template.execute("CREATE TABLE organization (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80), address VARCHAR(256))");

        template.execute("CREATE TABLE contact (id INT NOT NULL IDENTITY PRIMARY KEY, person_id INT NOT NULL, organization_id INT NOT NULL, email VARCHAR(80), telephone VARCHAR(20))");
        template.execute("alter table contact add constraint fk_contact_person foreign key (person_id) references person(id)");
        template.execute("alter table contact add constraint fk_contact_org foreign key (organization_id) references organization(id)");

        template.execute("CREATE TABLE profile (id INT NOT NULL IDENTITY PRIMARY KEY, username VARCHAR(50) NOT NULL, profile_person_id INT NOT NULL, project_id INT NOT NULL, contact_id INT NOT NULL, status VARCHAR(2), create_date DATE)");
        template.execute("alter table profile add constraint fk_profile_user foreign key (username) references users(username)");
        template.execute("alter table profile add constraint fk_profile_person foreign key (profile_person_id) references contact(id)");
        template.execute("alter table profile add constraint fk_profile_project foreign key (project_id) references projects(id)");
        template.execute("alter table profile add constraint fk_profile_contact foreign key (contact_id) references contact(id)");

        template.execute("CREATE TABLE project_profile (id INT NOT NULL IDENTITY PRIMARY KEY, project_id INT NOT NULL, field_name VARCHAR(32), is_required boolean, field_value VARCHAR(100), is_value_required boolean, field_type VARCHAR(2), elements VARCHAR(500))");
        template.execute("alter table project_profile add constraint fk_project_profile_project foreign key (project_id) references projects(id)");

        template.execute("CREATE TABLE project_baginfo (id INT NOT NULL IDENTITY PRIMARY KEY, project_id INT NOT NULL, defaults VARCHAR(1000))");
        template.execute("alter table project_baginfo add constraint fk_project_baginfo_project foreign key (project_id) references projects(id)");

        template.execute("CREATE TABLE person_projects (person_id INT NOT NULL, project_id INT NOT NULL)");
        template.execute("alter table person_projects add constraint fk_person_projects_person foreign key (person_id) references person(id)");
        template.execute("alter table person_projects add constraint fk_person_projects_project foreign key (project_id) references projects(id)");

        template.execute("CREATE TABLE user_contact (username VARCHAR(50) NOT NULL, contact_id INT NOT NULL)");
        template.execute("alter table user_contact add constraint fk_user_contact_user foreign key (username) references users(username)");
        template.execute("alter table user_contact add constraint fk_user_contact_contact foreign key (contact_id) references contact(id)");

        template.execute("CREATE TABLE bag (id INT NOT NULL IDENTITY PRIMARY KEY, profile_id INT NOT NULL, name VARCHAR(80), visit_date DATE, location VARCHAR(255), description VARCHAR(255))");
        template.execute("alter table bag add constraint fk_bag_profile foreign key (profile_id) references profile(id)");

        getCommandList();
        for (int i=0; i < defaultList.size(); i++) {
        	String command = defaultList.get(i);
        	template.execute(command);
        }
        for (int i=0; i < commandList.size(); i++) {
        	String command = commandList.get(i);
        	template.execute(command);
        }
    }
    
    private List<String> getCommandList() {
        // Data: Acegi Security
        defaultList.add("INSERT INTO users VALUES ('user', 'user', true)");
        defaultList.add("INSERT INTO authorities VALUES ('user', 'ROLE_BAGGER_USER')");

        // "CREATE TABLE projects (id, name");
        int rowcount = 1;
        defaultList.add("INSERT INTO projects VALUES (" + rowcount++ + ", '<no profile>', true)");
        defaultList.add("INSERT INTO projects VALUES (" + rowcount++ + ", 'eDeposit', false)");
        defaultList.add("INSERT INTO projects VALUES (" + rowcount++ + ", 'ndiipp', false)");
        defaultList.add("INSERT INTO projects VALUES (" + rowcount++ + ", 'ndnp', false)");
        defaultList.add("INSERT INTO projects VALUES (" + rowcount++ + ", 'World Digital Library', false)");

        rowcount = 1;
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 2, 'LC-Project', true, 'eDeposit', true, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 2, 'Publisher', true, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 3, 'LC-Project', true, 'ndiipp', true, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 4, 'LC-Project', true, 'ndnp', true, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 4, 'Awardee-Phase', true, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'LC-Project', true, 'World Digital Library', true, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'External-Identifier', 'true', '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'Media-Identifiers', false, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'Number-Of-Media-Shipped', false, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'Additional-Equipment', false, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'Ship-Date', false, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'Ship-Method', false, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'Ship-Tracking-Number', false, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'Ship-Media', false, '', false, 'TF', '')");
        defaultList.add("INSERT INTO project_profile VALUES (" + rowcount++ + ", 5, 'Ship-To-Address', true, 'World Digital Library, Library of Congress, 101 Independence Ave, SE, Washington, DC 20540 USA', true, 'TA', '')");

        boolean loadOnStartup = false;
    	if (loadOnStartup) readCommandList();
        
    	return defaultList;
    }
    
	private String readCommandList() {
    	String homeDir = System.getProperty("user.home");
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
	
	private void showConfirmation() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	try {
		        	loadProfiles(baggerFile);	        		
	        	} catch (Exception e) {
	        		logger.error("InMemoryBagger.showConfirmation: " + e.getMessage());
	        	}
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle("Load profiles confirmation.");
	    dialog.setConfirmationMessage("Saved profiles have been detected.  Would you like to load them?");
	    dialog.showDialog();
	}

	private String loadProfiles(File file) {
		String message = null;

		try
		{
			InputStreamReader fr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader  reader = new BufferedReader(fr);
			try
			{
				logger.debug("JdbcBagger.read file: " + file);
				while(true)
				{
					String line = reader.readLine();
					if (line == null) break;
					logger.debug(line);
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
		return message;
	}
}