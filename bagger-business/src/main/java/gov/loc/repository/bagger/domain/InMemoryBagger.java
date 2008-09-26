/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.loc.repository.bagger.domain;

import javax.sql.DataSource;

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

        // Data: Acegi Security
        template.execute("INSERT INTO users VALUES ('jste', 'jste', true)");
        template.execute("INSERT INTO users VALUES ('lesliej', 'lesliej', true)");
        template.execute("INSERT INTO users VALUES ('aboyko', 'aboyko', false)");
        template.execute("INSERT INTO users VALUES ('justin', 'justin', true)");
        template.execute("INSERT INTO users VALUES ('liz', 'liz', true)");
        template.execute("INSERT INTO users VALUES ('jkunze', 'jkunze', true)");
        template.execute("INSERT INTO authorities VALUES ('jste', 'ROLE_BAGIT_STAFF')");
        template.execute("INSERT INTO authorities VALUES ('lesliej', 'ROLE_BAGIT_STAFF')");
        template.execute("INSERT INTO authorities VALUES ('aboyko', 'ROLE_BAGIT_USER')");
        template.execute("INSERT INTO authorities VALUES ('justin', 'ROLE_BAGIT_STAFF')");
        template.execute("INSERT INTO authorities VALUES ('liz', 'ROLE_BAGIT_STAFF')");
        template.execute("INSERT INTO authorities VALUES ('jkunze', 'ROLE_BAGIT_USER')");

        // Schema: Bagger
        template.execute("CREATE TABLE person (id INT NOT NULL IDENTITY PRIMARY KEY, first_name VARCHAR(30), middle_init VARCHAR(1), last_name VARCHAR(30))");
        template.execute("CREATE TABLE projects (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80))");
        template.execute("CREATE TABLE contact_types (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80))");
        template.execute("CREATE TABLE address (id INT NOT NULL IDENTITY PRIMARY KEY, address1 VARCHAR(64), address2 VARCHAR(65), city VARCHAR(80), state VARCHAR(80), country VARCHAR(80), postal_code VARCHAR(12))");

        template.execute("CREATE TABLE organization (id INT NOT NULL IDENTITY PRIMARY KEY, name VARCHAR(80), address_id INT NOT NULL)");
        template.execute("alter table organization add constraint fk_org_address foreign key (address_id) references address(id)");

        template.execute("CREATE TABLE contact (id INT NOT NULL IDENTITY PRIMARY KEY, type_id INT NOT NULL, person_id INT NOT NULL, organization_id INT NOT NULL, email VARCHAR(80), telephone VARCHAR(20))");
        template.execute("alter table contact add constraint fk_contact_type foreign key (type_id) references contact_types(id)");
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

        // Data: Bagger
        template.execute("INSERT INTO person VALUES (1, 'Jon', '', 'Steinbach')");
        template.execute("INSERT INTO person VALUES (2, 'Leslie', '', 'Johnston')");
        template.execute("INSERT INTO person VALUES (3, 'Andy', '', 'Boyko')");
        template.execute("INSERT INTO person VALUES (4, 'Justin', '', 'Littman')");
        template.execute("INSERT INTO person VALUES (5, 'Liz', '', 'Madden')");
        template.execute("INSERT INTO person VALUES (6, 'John', 'A', 'Kunze')");

        template.execute("INSERT INTO projects VALUES (1, 'copyright')");
        template.execute("INSERT INTO projects VALUES (2, 'ndiip')");
        template.execute("INSERT INTO projects VALUES (3, 'ndnp')");
        template.execute("INSERT INTO projects VALUES (4, 'wdl')");
        template.execute("INSERT INTO projects VALUES (5, 'transfer')");
        template.execute("INSERT INTO projects VALUES (6, 'admin')");

        template.execute("INSERT INTO contact_types VALUES (1, 'library of congress');");
        template.execute("INSERT INTO contact_types VALUES (2, 'copyright office');");
        template.execute("INSERT INTO contact_types VALUES (3, 'publisher');");
        template.execute("INSERT INTO contact_types VALUES (4, 'library');");
        template.execute("INSERT INTO contact_types VALUES (5, 'organization');");

        template.execute("INSERT INTO address VALUES (1, '101 Independence Ave. SE', '', 'Washington', 'DC', 'US', '20540');");
        template.execute("INSERT INTO address VALUES (2, '101 Independence Ave. SE', '', 'Washington', 'DC', 'US', '20559-6000');");
        template.execute("INSERT INTO address VALUES (3, '415 20th St, 4th Floor', '', 'Oakland', 'CA', 'US', '94612');");

        template.execute("INSERT INTO organization VALUES (1, 'Library of Congress', 1);");
        template.execute("INSERT INTO organization VALUES (2, 'U.S. Copyright Office', 2);");
        template.execute("INSERT INTO organization VALUES (3, 'California Digital Library', 3);");

        template.execute("INSERT INTO contact VALUES (1, 1, 1, 1, 'jste@loc.gov', '202-555-7371');");
        template.execute("INSERT INTO contact VALUES (2, 1, 2, 1, 'lesliej@loc.gov', '202-555-7372');");
        template.execute("INSERT INTO contact VALUES (3, 1, 3, 1, 'aboyko@loc.gov', '202-555-7373');");
        template.execute("INSERT INTO contact VALUES (4, 1, 4, 1, 'jlit@loc.gov', '202-555-7374');");
        template.execute("INSERT INTO contact VALUES (5, 1, 5, 1, 'lizm@loc.gov', '202-555-7375');");
        template.execute("INSERT INTO contact VALUES (6, 4, 6, 3, 'jak@ucop.edu', '202-555-7373');");

        template.execute("INSERT INTO profile VALUES (1, 'jste', 1, 6, 2, 'A', '2008-09-18')");
        template.execute("INSERT INTO profile VALUES (2, 'lesliej', 2, 5, 2, 'A', '2008-08-06')");
        template.execute("INSERT INTO profile VALUES (3, 'jkunze', 6, 3, 4, 'A', '2002-04-17')");
        template.execute("INSERT INTO profile VALUES (4, 'aboyko', 3, 5, 3, 'D', '2002-04-17')");

        template.execute("INSERT INTO person_projects VALUES (1, 5);");
        template.execute("INSERT INTO person_projects VALUES (1, 6);");
        template.execute("INSERT INTO person_projects VALUES (2, 5);");
        template.execute("INSERT INTO person_projects VALUES (6, 5);");

        template.execute("INSERT INTO user_contact VALUES ('jste', 1);");
    }
}