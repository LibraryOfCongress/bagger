#!/bin/sh

PSQL="/usr/bin/psql"
export PGUSER="postgres"
export PGPASSWORD=""

# DATABASE
DATABASE="package_modeler"

# USER ROLES
ROLE_PRIVS="NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE LOGIN"
ADMIN="transfer_fixture_writer_user"
ADMIN_PASSWD=""
READER="transfer_reader_user"
READER_PASSWD=""
WRITER="transfer_data_writer_user"
WRITER_PASSWD=""
JBPM="jbpm_user"
JBPM_PASSWD=""

# OWNERS
OWNER_PRIVS="NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE"
ADMIN_OWNER="package_modeler_fixture_writer_role"
READER_OWNER="package_modeler_reader_role"
WRITER_OWNER="package_modeler_data_writer_role"
JBPM_OWNER="jbpm_role"

sanity_checks () {
# ARE YOU 'postgres'?
if [ $LOGNAME != "postgres" ]
  then echo "*** You are not using the correct account ***"
  exit 1;
fi
# CAN I CONNECT?
	echo "\q" | $PGSQL
if [[ \$? != '0' ]] 
  then echo "*** Cannot connect to your PostgreSQL Database ***"
  exit 1;
fi
# ARE ALL FILES PRESENT IN ./
# TODO: IMPLIMENT IN A MANIFEST WITH MD5SUMS
if [ -f "./packagemodeler-core-1.0-bin.zip" ]
  then printf "\n!!! Missing ./packagemodeler-core-1.0-bin.zip\nPlease fix this and try again.\nExitintg....\n"
  exit 1;
if [ -f "./inventory-core.sql" ]
  then printf "\n!!! Missing ./inventory-core.sql\nPlease fix this and try again.\nExitintg....\n"
  exit 1;
if [ -f "./inventory-ndnp.sql"]
  then printf "\n!!! Missing ./inventory-ndnp.sql\nPlease fix this and try again.\nExitintg....\n"
  exit 1;
if [ -f "./jbpm.jpdl.postgresql.sql" ]
  then printf "\n!!! Missing ./jbpm.jpdl.postgresql.sql\nPlease fix this and try again.\nExitintg....\n"
  exit 1;
if [ -f "./transfer.war" ]
  then printf "\n!!! Missing ./transfer.war\nPlease fix this and try again.\nExitintg....\n"
  exit 1;
}

init_dbs () {
  echo "CREATE DATABASE package_modeler ENCODING = 'UTF8';" | $PSQL
  echo "CREATE DATABASE jbpm32 ENCODING = 'UTF8';" | $PSQL

}

init_schemas () {
  export PGDATABASE="package_modeler"
  $PSQL -f ./inventory-core.sql
  $PSQL -f ./inventory-ndnp.sql
  export PGDATABASE="jbpm32"
  $PSQL -f ./jbpm.jpdl.postgresql.sql
}

init_roles () {
  # OWNER ROLES FIRST
  echo "CREATE ROLE $ADMIN_OWNER $OWNER_PRIVS;" | $PSQL
  echo "CREATE ROLE $READER_OWNER $OWNER_PRIVS;" | $PSQL
  echo "CREATE ROLE $WRITER_OWNER $OWNER_PRIVS;" | $PSQL
  echo "CREATE ROLE $JBPM_OWNER $OWNER_PRIVS;" | $PSQL

  # NOW USER ROLES
  echo "CREATE ROLE $ADMIN WITH PASSWORD $ADMIN_PASSWD $ROLE_PRIVS;" | $PSQL
  echo "CREATE ROLE $READER WITH PASSWORD $READER_PASSWD $ROLE_PRIVS;" | $PSQL
  echo "CREATE ROLE $WRITER WITH PASSWORD $WRITER_PASSWORD $ROLE_PRIVS;" | $PSQL
  echo "CREATE ROLE $JBPM WITH PASSWORD $JBPM_PASSWORD $ROLE_PRIVS;" | $PSQL

# ADD USERS TO OWNER ROLES
	echo "GRANT $ADMIN_OWNER TO $ADMIN;" | $PSQL
	echo "GRANT $READER_OWNER TO $READER;" | $PSQL
	echo "GRANT $WRITER_OWNER TO $WRITER;" | $PSQL
	echo "GRANT $JBPM_OWNER TO $JBPM;" | $PSQL
}

init_core_perms () {
  export PGDATABASE="package_modeler"
	echo "GRANT CONNECT ON DATABASE package_modeler TO $WRITER_OWNER;" | $PSQL
	echo "GRANT USAGE ON SCHEMA core TO $WRITER_OWNER;" | $PSQL
	echo "GRANT USAGE ON SCHEMA agent TO $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE agent.agent TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE agent.agent_role TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE agent.'role' TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.repository TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.canonicalfile TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.canonicalfile_fixity TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_file_examination_group TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_file_location TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_package TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.external_filelocation TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination_fixity TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination_group TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileinstance TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileinstance_fixity TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.filelocation TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.package TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.storagesystem_filelocation TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT CONNECT ON DATABASE package_modeler TO $ADMIN_OWNER;" | $PSQL
	echo "GRANT USAGE ON SCHEMA core TO $ADMIN_OWNER;" | $PSQL
	echo "GRANT USAGE ON SCHEMA agent TO $ADMIN_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE agent.agent TO GROUP $ADMIN_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE agent.agent_role TO GROUP $ADMIN_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE agent.'role' TO GROUP $ADMIN_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.repository TO GROUP $ADMIN_OWNER;" | $PSQL
	echo "GRANT CONNECT ON DATABASE package_modeler TO $READER_OWNER;" | $PSQL
	echo "GRANT USAGE ON SCHEMA core TO $READER_OWNER;" | $PSQL
	echo "GRANT USAGE ON SCHEMA agent TO $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE agent.agent TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE agent.agent_role TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE agent.'role' TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.repository TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.canonicalfile TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.canonicalfile_fixity TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.event_file_examination_group TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.event_file_location TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.event_package TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.external_filelocation TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.fileexamination TO public;" | $PSQL
	echo "GRANT SELECT ON TABLE core.fileexamination_fixity TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.fileexamination_group TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.fileinstance TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.fileinstance_fixity TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.filelocation TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.package TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE core.storagesystem_filelocation TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE hibernate_sequence TO public;" | $PSQL
}

init_ndnp_perms () {
	echo "GRANT USAGE ON SCHEMA ndnp TO $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch_lccn TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch_reel TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.lccn TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.reel TO GROUP $WRITER_OWNER;" | $PSQL
	echo "GRANT USAGE ON SCHEMA ndnp TO $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE ndnp.batch TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE ndnp.batch_lccn TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE ndnp.batch_reel TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE ndnp.lccn TO GROUP $READER_OWNER;" | $PSQL
	echo "GRANT SELECT ON TABLE ndnp.reel TO GROUP $READER_OWNER;" | $PSQL
}

init_jbpm_perms () {
  export PGDATABASE="jbpm32"
	echo "GRANT CONNECT ON DATABASE jbpm32 TO $JBPM;" | $PGSQL
	echo "GRANT ALL ON TABLE hibernate_sequence TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_action TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_bytearray TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_byteblock TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_comment TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_decisionconditions TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_delegation TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_event TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_exceptionhandler TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_id_group TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_id_membership TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_id_permissions TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_id_user TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_job TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_log TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_moduledefinition TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_moduleinstance TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_node TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_pooledactor TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_processdefinition TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_processinstance TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_runtimeaction TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_swimlane TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_swimlaneinstance TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_task TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_taskactorpool TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_taskcontroller TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_taskinstance TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_token TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_tokenvariablemap TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_transition TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_variableaccess TO GROUP $JBPM_OWNER;" | $PSQL
	echo "GRANT ALL ON TABLE jbpm_variableinstance TO GROUP $JBPM_OWNER;" | $PSQL
}

sanity_checks
init_dbs
init_schemas
init_roles
init_core_perms
init_ndnp_perms
init_jbpm_perms
