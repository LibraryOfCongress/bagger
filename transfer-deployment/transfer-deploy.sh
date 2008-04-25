#!/bin/bash

. ./conf

init_vars () {
    # BASE TRUNK NAMES
    PM_CORE="packagemodeler-core"
    PM_NDNP="packagemodeler-ndnp"
    WORKFLOW_CORE="workflow-processes-core"

    # PROGRAMS TO USE
    PSQL="/usr/bin/psql"
    PM_CORE_CLI="${TRANSFER_INSTALL_DIR}/${PM_CORE}-${VERSION}/bin/fixturedriver"
    PM_NDNP_CLI="${TRANSFER_INSTALL_DIR}/${PM_NDNP}-${VERSION}/bin/fixturedriver"
    PROCESS_DEPLOYER="${TRANSFER_INSTALL_DIR}/${WORKFLOW_CORE}-${VERSION}/bin/processdeployer"
    
    # FILE LOCATIONS 
    PM_CORE_CLI_PKG="files/${PM_CORE}-${VERSION}-bin.zip"
    PM_NDNP_CLI_PKG="files/${PM_NDNP}-${VERSION}-bin.zip"
    WORKFLOW_CORE_PKG="files/${WORKFLOW_CORE}-${VERSION}-bin.zip"
    PM_CORE_SQL="files/inventory-core.sql"
    PM_NDNP_SQL="files/inventory-ndnp.sql"
    JBPM_SQL="files/jbpm.sql"
    TRANSFER_UI_WAR="files/transfer-ui-ndnp-${VERSION}-template.war"
    PROCESS_DEFINITION="files/processdefinition.xml"
    PM_CORE_HIBERNATE_CONF="${TRANSFER_INSTALL_DIR}/${PM_CORE}-${VERSION}/conf/data_writer.packagemodeler.hibernate.properties"
    PM_CORE_FIXTURE_HIBERNATE_CONF="${TRANSFER_INSTALL_DIR}/${PM_CORE}-${VERSION}/conf/fixture_writer.packagemodeler.hibernate.properties"
    PM_NDNP_HIBERNATE_CONF="${TRANSFER_INSTALL_DIR}/${PM_NDNP}-${VERSION}/conf/data_writer.packagemodeler.hibernate.properties"
    PM_NDNP_FIXTURE_HIBERNATE_CONF="${TRANSFER_INSTALL_DIR}/${PM_NDNP}-${VERSION}/conf/fixture_writer.packagemodeler.hibernate.properties"
    JBPM_HIBERNATE_CONF="${TRANSFER_INSTALL_DIR}/${WORKFLOW_CORE}-${VERSION}/conf/jbpm.hibernate.properties"
    TRANSFER_DW_HIBERNATE_CONF="${CATALINA_HOME}/webapps/transfer/WEB-INF/classes/conf/data_writer.packagemodeler.hibernate.properties"
    TRANSFER_RO_HIBERNATE_CONF="${CATALINA_HOME}/webapps/transfer/WEB-INF/classes/conf/read_only.packagemodeler.hibernate.properties"
    TRANSFER_JBPM_HIBERNATE_CONF="${CATALINA_HOME}/webapps/transfer/WEB-INF/classes/jbpm.hibernate.properties"
    TRANSFER_LOG4J_CONF="${CATALINA_HOME}/webapps/transfer/WEB-INF/classes/log4j.properties"
    TRANSFER_WORKFLOW_CONF="${CATALINA_HOME}/webapps/transfer/WEB-INF/classes/conf/workflow.local.properties"

    # ENVIRONMENT VARS
    export PGUSER=$PGUSER
    export PGHOST=$PGHOST
    export PGPORT=$PGPORT
    export PGPASSWORD=$PGPASSWORD
    export JAVA_HOME=$JAVA_HOME
    export CATALINA_HOME=$CATALINA_HOME
    
    if [[ $DB_PREFIX ]]; then
        DB_PREFIX="${DB_PREFIX}_"
        ROLE_PREFIX="${ROLE_PREFIX}_"
    fi

    # DATABASES    
    PM_DB="${DB_PREFIX}package_modeler"
    JBPM_DB="${DB_PREFIX}jbpm32"

    ROLE_PRIVS="NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE LOGIN"
    XFER_FIXTURE_WRITER="${ROLE_PREFIX}transfer_fixture_writer_user"
    XFER_READER="${ROLE_PREFIX}transfer_reader_user"
    XFER_WRITER="${ROLE_PREFIX}transfer_data_writer_user"
    JBPM="${ROLE_PREFIX}jbpm_user"

    # PACKAGE MODLER ROLES
    OWNER_PRIVS="NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE"
    PKG_MODEL_FIXTURE_WRITER="${ROLE_PREFIX}package_modeler_fixture_writer_role"
    PKG_MODEL_READER="${ROLE_PREFIX}package_modeler_reader_role"
    PKG_MODEL_WRITER="${ROLE_PREFIX}package_modeler_data_writer_role"
    JBPM_OWNER="${ROLE_PREFIX}jbpm_role"

    # PACKAGE MODELER HIBERNATE PROPERTIES
    PM_WRITER_HIBERNATE_PROPS="#Hibernate Core Settings\n
hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect\n
hibernate.connection.driver_class=org.postgresql.Driver\n
hibernate.connection.url=jdbc:postgresql://${PGHOST}:${PGPORT}/${PM_DB}\n
hibernate.connection.username=${XFER_WRITER}\n
hibernate.connection.password=${XFER_WRITER_PASSWD}"

    # PACKAGE MODELER HIBERNATE PROPERTIES
    PM_READER_HIBERNATE_PROPS="#Hibernate Core Settings\n
hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect\n
hibernate.connection.driver_class=org.postgresql.Driver\n
hibernate.connection.url=jdbc:postgresql://${PGHOST}:${PGPORT}/${PM_DB}\n
hibernate.connection.username=${XFER_READER}\n
hibernate.connection.password=${XFER_READER_PASSWD}"

    # PACKAGE MODELER HIBERNATE PROPERTIES
    PM_FIXTURE_HIBERNATE_PROPS="#Hibernate Core Settings\n
hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect\n
hibernate.connection.driver_class=org.postgresql.Driver\n
hibernate.connection.url=jdbc:postgresql://${PGHOST}:${PGPORT}/${PM_DB}\n
hibernate.connection.username=${XFER_FIXTURE_WRITER}\n
hibernate.connection.password=${XFER_FIXTURE_WRITER_PASSWD}"

    # JBPM HIBERNATE PROPERTIES
    JBPM_HIBERNATE_PROPS="hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect\n
hibernate.connection.driver_class=org.postgresql.Driver\n
hibernate.connection.url=jdbc:postgresql://${PGHOST}:${PGPORT}/${JBPM_DB}\n
hibernate.connection.username=${JBPM}\n
hibernate.connection.password=${JBPM_PASSWD}\n
hibernate.cache.provider_class=org.hibernate.cache.HashtableCacheProvider"

    # TRANSFER LOG4J PROPERTIES
    LOG4J_PROPS="# Set root logger level to DEBUG and its only appender to CONSOLE\n
log4j.rootLogger=DEBUG, CONSOLE, R\n
# CONSOLE\n
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender\n
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout\n
log4j.appender.CONSOLE.layout.ConversionPattern=%d{HH:mm:ss,SSS} [%t] %-5p %C{1} : %m%n\n
log4j.appender.R=org.apache.log4j.RollingFileAppender\n
log4j.appender.R.File=${CATALINA_HOME}/logs/transfer-console.log\n
log4j.appender.R.MaxFileSize=10000KB\n
# Keep one backup file\n
log4j.appender.R.MaxBackupIndex=1\n
log4j.appender.R.layout=org.apache.log4j.PatternLayout\n
log4j.appender.R.layout.ConversionPattern=%p %t %c - %m%n\n
# LIMIT CATEGORIES\n
log4j.logger=WARN\n
log4j.logger.gov.loc.repository.workflow.actionhandlers.BaseActionHandler=WARN\n
log4j.logger.gov.loc.repository=DEBUG\n
log4j.logger.gov.loc.ndnp=WARN\n
log4j.logger.components=INFO\n
log4j.logger.org.jbpm=WARN\n
log4j.logger.org.hibernate=WARN\n
#log4j.logger.org.hibernate.SQL=DEBUG\n
#log4j.logger.org.hibernate.type=DEBUG\n
log4j.logger.org.apache=WARN\n
log4j.logger.org.jcp=WARN\n
log4j.logger.org.springframework=WARN"

# TRANSFER WORKFLOW PROPERTIES
#TODO:  The storage.staging.basedirectory and jms.connection needs to be set correctly.
WORKFLOW_LOCAL_PROPS="
#The JMS connection used by the CompletedJobListener
jms.connection=failover:(tcp://localhost:61616)?maxReconnectAttempts=4
jms.replytoqueue=completedjobqueue

#The default algorithm used for inventorying, etc.
fixity.algorithm=md5

#The basepath of staging storage
storage.staging.basedirectory=/tmp/staging
agent.staging.id=rdc
agent.backup.id=rs25
agent.production.id=rs15"

sanity_checks () {
    # CHECK RBAC
    #if [[ `profiles |grep Postgres\ Administration; echo "$?"` -ne 0 ]]
    #    then printf "ERROR: *** You must be assigned the 'Postgres Administration' profile ***\n"
    #    usage
    #    exit 1;
    #fi
  
    # CAN I FIND JAVA?
   if [[ ! -e ${JAVA_HOME}/bin/java ]]
        then printf "ERROR: *** Cannot locate java.\nPlease set JAVA_HOME correctly in the config\n"
        usage
        exit 1;
    else
        printf "INFO:  Java is all good!\n"
    fi
 

    # CAN I CONNECT?
    if [[ `echo "\q" | $PSQL postgres 2>/dev/null; echo $?` -ne 0 ]]
        then printf "ERROR: *** Cannot connect to the PostgreSQL database\n"
        usage
        exit 1;
    else
        printf "INFO:  Successfully connected to PostgreSQL database\n"
    fi

    # IS PM CLI DEPLOY DIR WRITABLE?
    if [[ ! -e ${TRANSFER_INSTALL_DIR} ]]
        then read -p "${TRANSFER_INSTALL_DIR} not present. Shall I create it? (Y/n)" MKDIR
            if [[ $MKDIR -ne "Y" ]] 
                then printf "Exiting...\n"
            else 
                mkdir -p $TRANSFER_INSTALL_DIR
            fi
    else
        printf "INFO:  ${TRANSFER_INSTALL_DIR} present.\n"            
    fi
    
    # TOMCAT WRITABLE?
    if [ ! -w $CATALINA_HOME/webapps ]
        then printf "\n!!! Can't write to %s/webapps\nPlease fix this and try again.\nExitintg....\n" $CATALINA_HOME
       exit 1;
    fi

    # ARE REQUIRED FILES READABLE?
    if [ ! -r $PM_CORE_SQL ]
       then printf "\n!!! Can't read %s\nPlease fix this and try again.\nExitintg....\n" $PM_CORE_SQL
       exit 1;
    else
        printf "INFO:  Can read %s.\n" $PM_CORE_SQL
    fi
    if [ ! -r $PM_NDNP_SQL ]
        then printf "\n!!! Can't read %s\nPlease fix this and try again.\nExitintg....\n" $PM_NDNP_SQL
        exit 1;
    else
        printf "INFO:  Can read %s.\n" $PM_NDNP_SQL
    fi
    if [ ! -r $JBPM_SQL ]
        then printf "\n!!! Can't read %s\nPlease fix this and try again.\nExitintg....\n" $JBPM_SQL
        exit 1;
    else
        printf "INFO:  Can read %s.\n" $JBPM_SQL        
    fi
    if [ ! -r $PM_CORE_CLI_PKG ]
        then printf "\n!!! Can't read %s\nPlease fix this and try again.\nExitintg....\n" $PM_CORE_CLI_PKG
        exit 1;
    else
        printf "INFO:  Can read %s.\n" $PM_CORE_CLI_PKG        
    fi
    if [ ! -r $PM_NDNP_CLI_PKG ]
        then printf "\n!!! Can't read %s\nPlease fix this and try again.\nExitintg....\n" $PM_NDNP_CLI_PKG
        exit 1;
    else
        printf "INFO:  Can read %s.\n" $PM_NDNP_CLI_PKG        
    fi
    if [ ! -r $WORKFLOW_CORE_PKG ]
        then printf "\n!!! Can't read %s\nPlease fix this and try again.\nExitintg....\n" $WORKFLOW_CORE_PKG
        exit 1;
    else
        printf "INFO:  Can read %s.\n" $WORKFLOW_CORE_PKG        
    fi
    if [ ! -r $PROCESS_DEFINITION ]
        then printf "\n!!! Can't read %s\nPlease fix this and try again.\nExitintg....\n" $PROCESS_DEFINITION
        exit 1;
    else
        printf "INFO:  Can read %s.\n" $PROCESS_DEFINITION        
    fi
    if [ ! -r $TRANSFER_UI_WAR ]
        then printf "\n!!! Can't read %s\nPlease fix this and try again.\nExitintg....\n" $TRANSFER_UI_WAR
        exit 1;
    else
        printf "INFO:  Can read %s.\n" $TRANSFER_UI_WAR        
    fi
}

#chech_manifest () {
#  digest -a md5 -v << ./Manifest
#  if [[ \$? != '0' ]]
#    then printf "ERROR: *** MD5 Checksum mismatch in \n"
#    usage
#    exit 1;
#  fi
#}

# Create Databases
create_dbs () {
    export PGDATABASE="postgres"
    EXIT="false"
    # Check if the databases exist
    if [[ `echo "\l" | $PSQL |grep $PM_DB ; echo $?` -eq 0  ]]
        then printf "ERROR:  *** The ${PM_DB} database exists!\n"
        EXIT="true";
    else
        printf "${PM_DB} database does not exist\n"
    fi
    if [[ `echo "\l" | $PSQL |grep $JBPM_DB ; echo $?` -eq 0 ]]
        then printf "ERROR:  *** The ${JBPM_DB} database exists!\n"
        EXIT="true";
    else
        printf "${JBPM_DB} database does not exist\n"        
    fi
    if [[ $EXIT -ne "false" ]]
        then
            printf "Exiting\n"        
            exit 1;
    else
        echo "INFO:  Creating Databases"
        echo "CREATE DATABASE ${PM_DB} ENCODING = 'UTF8';" | $PSQL
        echo "CREATE DATABASE ${JBPM_DB} ENCODING = 'UTF8';" | $PSQL
    fi
}


# Create Database Roles 
init_roles () {
    export PGDATABASE="postgres"
    printf "INFO:  Creating roles\n"
    # OWNER ROLES FIRST
    echo "CREATE ROLE $PKG_MODEL_FIXTURE_WRITER $OWNER_PRIVS;" | $PSQL
    echo "CREATE ROLE $PKG_MODEL_READER $OWNER_PRIVS;" | $PSQL
    echo "CREATE ROLE $PKG_MODEL_WRITER $OWNER_PRIVS;" | $PSQL
    echo "CREATE ROLE $JBPM_OWNER $OWNER_PRIVS;" | $PSQL

    # NOW USER ROLES
    echo "CREATE ROLE $XFER_FIXTURE_WRITER WITH PASSWORD '$XFER_FIXTURE_WRITER_PASSWD' $ROLE_PRIVS;" | $PSQL
    echo "CREATE ROLE $XFER_READER WITH PASSWORD '$XFER_READER_PASSWD' $ROLE_PRIVS;" | $PSQL
    echo "CREATE ROLE $XFER_WRITER WITH PASSWORD '$XFER_WRITER_PASSWD' $ROLE_PRIVS;" | $PSQL
    echo "CREATE ROLE $JBPM WITH PASSWORD '$JBPM_PASSWD' $ROLE_PRIVS;" | $PSQL

    # GRANT PERMISSIONS TO ROLES
    echo "GRANT $PKG_MODEL_FIXTURE_WRITER TO $XFER_FIXTURE_WRITER;" | $PSQL
    echo "GRANT $PKG_MODEL_READER TO $XFER_READER;" | $PSQL
    echo "GRANT $PKG_MODEL_WRITER TO $XFER_WRITER;" | $PSQL
    echo "GRANT $JBPM_OWNER TO $JBPM;" | $PSQL
}

# Grant PM Core Permissions
init_core_perms () {
    export PGDATABASE=$PM_DB
    printf "INFO:  Granting privileges to core\n"
    
    echo "GRANT CONNECT ON DATABASE $PM_DB TO $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT USAGE ON SCHEMA core TO $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT USAGE ON SCHEMA agent TO $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT ON TABLE agent.agent TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT ON TABLE agent.agent_role TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT ON TABLE agent.role TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.repository TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.canonicalfile TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.canonicalfile_fixity TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_file_examination_group TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_file_location TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_package TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.external_filelocation TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination_fixity TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination_group TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileinstance TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileinstance_fixity TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.filelocation TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.package TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.storagesystem_filelocation TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT CONNECT ON DATABASE $PM_DB TO $PKG_MODEL_FIXTURE_WRITER;" | $PSQL
    echo "GRANT USAGE ON SCHEMA core TO $PKG_MODEL_FIXTURE_WRITER;" | $PSQL
    echo "GRANT USAGE ON SCHEMA agent TO $PKG_MODEL_FIXTURE_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE agent.agent TO GROUP $PKG_MODEL_FIXTURE_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE agent.agent_role TO GROUP $PKG_MODEL_FIXTURE_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE agent.role TO GROUP $PKG_MODEL_FIXTURE_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.repository TO GROUP $PKG_MODEL_FIXTURE_WRITER;" | $PSQL
    echo "GRANT CONNECT ON DATABASE $PM_DB TO $PKG_MODEL_READER;" | $PSQL
    echo "GRANT USAGE ON SCHEMA core TO $PKG_MODEL_READER;" | $PSQL
    echo "GRANT USAGE ON SCHEMA agent TO $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE agent.agent TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE agent.agent_role TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE agent.role TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.repository TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.canonicalfile TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.canonicalfile_fixity TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.event_file_examination_group TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.event_file_location TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.event_package TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.external_filelocation TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.fileexamination TO public;" | $PSQL
    echo "GRANT SELECT ON TABLE core.fileexamination_fixity TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.fileexamination_group TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.fileinstance TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.fileinstance_fixity TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.filelocation TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.package TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE core.storagesystem_filelocation TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT ALL ON TABLE hibernate_sequence TO public;" | $PSQL
}

# Grant PM NDNP Permissions
init_ndnp_perms () {
    export PGDATABASE=$PM_DB
    printf "INFO:  Granting privileges to ndnp\n"
    
    echo "GRANT USAGE ON SCHEMA ndnp TO $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch_lccn TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch_reel TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.lccn TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.reel TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT SELECT ON TABLE ndnp.awardphase TO GROUP $PKG_MODEL_WRITER;" | $PSQL
    echo "GRANT USAGE ON SCHEMA ndnp TO $PKG_MODEL_FIXTURE_WRITER;" | $PSQL
    echo "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.awardphase TO GROUP $PKG_MODEL_FIXTURE_WRITER;" | $PSQL        
    echo "GRANT USAGE ON SCHEMA ndnp TO $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE ndnp.batch TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE ndnp.batch_lccn TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE ndnp.batch_reel TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE ndnp.lccn TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE ndnp.reel TO GROUP $PKG_MODEL_READER;" | $PSQL
    echo "GRANT SELECT ON TABLE ndnp.awardphase TO GROUP $PKG_MODEL_READER;" | $PSQL    
}

# Grant JBPM Permissions
init_jbpm_perms () {  
    export PGDATABASE=$JBPM_DB
    echo "GRANT CONNECT ON DATABASE $JBPM_DB TO $JBPM;" | $PGSQL
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

# Deploy The Package Modeler Core DB
deploy_pm_core () {
    export PGDATABASE=$PM_DB
    printf "Creating core tables\n"
    $PSQL -f $PM_CORE_SQL
}

# Deploy The Package Modeler NDNP 
deploy_pm_ndnp () {
    export PGDATABASE=$PM_DB
    printf "Creating ndnp tables\n"    
    $PSQL -f $PM_NDNP_SQL
}

# Deploy The Package Modeler NDNP 
deploy_jbpm () {
    export PGDATABASE=$JBPM_DB
    printf "Creating jbpm tables\n"
    $PSQL -f $JBPM_SQL
}

#TODO  ************** Configure log4j for all three below ***********
#TODO  ************** Test the setup by invoking the commandline driver ***********
# Deploy the Package Modeler Command Line Tool
deploy_pm_core_cli () {
    printf "INFO:  Deploying Pakage Modler Core CLI Package\n"
    unzip $PM_CORE_CLI_PKG -d $TRANSFER_INSTALL_DIR
    printf "INFO:  Making %s executable\n" $PM_CORE_CLI
    chmod +x $PM_CORE_CLI
    echo -e $PM_WRITER_HIBERNATE_PROPS > $PM_CORE_HIBERNATE_CONF
    echo -e $PM_FIXTURE_HIBERNATE_PROPS > $PM_CORE_FIXTURE_HIBERNATE_CONF
}

# Deploy the Package Modler NDNP Zip
deploy_pm_ndnp_cli () {
    printf "INFO:  Deploying Pakage Modler NDNP CLI Package\n"
    unzip $PM_NDNP_CLI_PKG -d $TRANSFER_INSTALL_DIR 
    printf "INFO:  Making %s executable\n" $PM_NDNP_CLI
    chmod +x $PM_NDNP_CLI
    echo -e $PM_WRITER_HIBERNATE_PROPS > $PM_NDNP_HIBERNATE_CONF
    echo -e $PM_FIXTURE_HIBERNATE_PROPS > $PM_NDNP_FIXTURE_HIBERNATE_CONF
}

# Deploy the Workflow Processes Core Zip
deploy_workflow_core () {
    printf "INFO:  Deploying Workflow Core Package\n"
    unzip $WORKFLOW_CORE_PKG -d $TRANSFER_INSTALL_DIR 
    printf "INFO:  Making %s executable\n" $PROCESS_DEPLOYER
    chmod +x $PROCESS_DEPLOYER
    echo -e $JBPM_HIBERNATE_PROPS > $JBPM_HIBERNATE_CONF
}

# Create the package modler database fixtures
install_pm_fixtures () {
    printf "INFO:  Installing package modler database fixtures\n"
    $PM_CORE_CLI createrepository -id ndnp
    $PM_CORE_CLI createperson -id ray -firstname Ray -surname Murray
    $PM_CORE_CLI createperson -id myron -firstname Myron -surname Briggs
    $PM_CORE_CLI createperson -id scott -firstname Scott -surname Phelps
    $PM_CORE_CLI createperson -id brian -firstname Brian -surname Vargas
    $PM_CORE_CLI createperson -id jjoyner-qr -firstname JoKeeta -surname Joyner
    $PM_CORE_CLI createperson -id jjoyner-sysadmin -firstname JoKeeta -surname Joyner
    $PM_CORE_CLI createperson -id jjoyner-ingest -firstname JoKeeta -surname Joyner
    $PM_CORE_CLI createperson -id tami-qr -firstname Tasmin -surname Mills
    $PM_CORE_CLI createperson -id tami-sysadmin -firstname Tasmin -surname Mills
    $PM_CORE_CLI createperson -id tami-ingest -firstname Tasmin -surname Mills
    $PM_CORE_CLI createperson -id lfre-qr -firstname LaTonya -surname Freeman
    $PM_CORE_CLI createperson -id lfre-sysadmin -firstname LaTonya -surname Freeman
    $PM_CORE_CLI createperson -id lfre-ingest -firstname LaTonya -surname Freeman
    $PM_CORE_CLI createsystem -id rdc-workflow
    $PM_CORE_CLI createrole -id repository_system
    $PM_CORE_CLI createsystem -id ndnp-staging-repository -roles repository_system
    $PM_CORE_CLI createrole -id storage_system
    $PM_CORE_CLI createsystem -id rdc -roles storage_system
    $PM_CORE_CLI createsystem -id rs15 -roles storage_system
    $PM_CORE_CLI createsystem -id rs25 -roles storage_system
    $PM_CORE_CLI createrole -id ndnp_awardee
    $PM_CORE_CLI createorganization -id CU-Riv -name "University of California, Riverside" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id FUG -name "University of Florida Libraries, Gainesville" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id KyU -name "University of Kentucky Libraries, Lexington" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id NN -name "New York Public Library, New York City" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id UUML -name "University of Utah, Salt Lake City" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id VIC -name "Library of Virginia, Richmond" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id DLC -name "Library of Congress" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id MnHi -name "Minnesota Historical Society" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id NbU -name "University of Nebraska, Lincoln" -roles ndnp_awardee
    $PM_CORE_CLI createorganization -id TxDN -name "University of North Texas, Denton" -roles ndnp_awardee
}

# Create the package modler NDNP database fixtures
install_pm_ndnp_fixtures () {
    printf "INFO:  Installing package modler NDNP database fixtures\n"
    $PM_NDNP_CLI createawardphase -name "Phase 1"
    $PM_NDNP_CLI createawardphase -name "Phase 2"    
}

# Create the JBPM identity fixtures
install_jbpm_fixtures () {
    printf "INFO:  Installing JBPM database fixtures\n"
    export PGDATABASE=$JBPM_DB
    echo "INSERT INTO JBPM_ID_GROUP VALUES(1,'G','ndnp-qr','organisation',NULL);" | $PSQL
    echo "INSERT INTO JBPM_ID_GROUP VALUES(2,'G','ndnp-sysadmin','organisation',NULL);" | $PSQL
    echo "INSERT INTO JBPM_ID_GROUP VALUES(3,'G','ROLE_USER','security-role',NULL);" | $PSQL
    echo "INSERT INTO JBPM_ID_GROUP VALUES(4,'G','ROLE_ADMIN','security-role',NULL);" | $PSQL
    echo "INSERT INTO JBPM_ID_GROUP VALUES(5,'G','ndnp-ingest','organisation',NULL);" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(1,'U','ray','foo@loc.gov','ray');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(2,'U','myron','foo@loc.gov','myron');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(3,'U','scott','foo@loc.gov','scott');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(4,'U','brian','foo@loc.gov','brian');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(5,'U','jjoyner-qr','foo@loc.gov','jjoyner');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(6,'U','jjoyner-sysadmin','foo@loc.gov','jjoyner');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(7,'U','jjoyner-ingest','foo@loc.gov','jjoyner');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(8,'U','tami-qr','foo@loc.gov','tami');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(9,'U','tami-sysadmin','foo@loc.gov','tami');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(10,'U','tami-ingest','foo@loc.gov','tami');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(11,'U','lfre-qr','foo@loc.gov','lfre');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(12,'U','lfre-sysadmin','foo@loc.gov','lfre');" | $PSQL
    echo "INSERT INTO JBPM_ID_USER VALUES(13,'U','lfre-ingest','foo@loc.gov','lfre');" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(1,'M','ray','ROLE_USER',1,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(2,'M','ray','ndnp-qr',1,1);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(3,'M','myron','ROLE_USER',2,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(4,'M','myron','ndnp-qr',2,1);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(5,'M','scott','ndnp-sysadmin',3,2);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(6,'M','scott','ROLE_USER',3,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(7,'M','scott','ROLE_ADMIN',3,4);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(8,'M','brian','ndnp-ingest',4,5);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(9,'M','brian','ROLE_USER',4,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(10,'M','jjoyner-qr','ROLE_USER',5,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(11,'M','jjoyner-qr','ndnp-qr',5,1);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(12,'M','jjoyner-sysadmin','ROLE_USER',6,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(13,'M','jjoyner-sysadmin','ROLE_ADMIN',6,4);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(14,'M','jjoyner-sysadmin','ndnp-sysadmin',6,2);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(15,'M','jjoyner-ingest','ROLE_USER',7,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(16,'M','jjoyner-ingest','ndnp-ingest',7,5);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(17,'M','tami-qr','ROLE_USER',8,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(18,'M','tami-qr','ndnp-qr',8,1);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(19,'M','tami-sysadmin','ROLE_USER',9,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(20,'M','tami-sysadmin','ROLE_ADMIN',9,4);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(21,'M','tami-sysadmin','ndnp-sysadmin',9,2);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(22,'M','tami-ingest','ROLE_USER',10,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(23,'M','tami-ingest','ndnp-ingest',10,5);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(24,'M','lfre-qr','ROLE_USER',11,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(25,'M','lfre-qr','ndnp-qr',11,1);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(26,'M','lfre-sysadmin','ROLE_USER',12,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(27,'M','lfre-sysadmin','ROLE_ADMIN',12,4);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(28,'M','lfre-sysadmin','ndnp-sysadmin',12,2);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(29,'M','lfre-ingest','ROLE_USER',13,3);" | $PSQL
    echo "INSERT INTO JBPM_ID_MEMBERSHIP VALUES(30,'M','lfre-ingest','ndnp-ingest',13,5);" | $PSQL
    echo "INSERT INTO jbpm_id_permissions(entity_, class_, name_) VALUES (1, 'java.lang.RuntimePermission', 'processdefinition.ndnp1.initiate');" | $PSQL
}

# Deploy NDNP Process Definition
install_process_definition () {
    printf "INFO:  Deploying the NDNP Process Definition\n"
    $PROCESS_DEPLOYER deploy -file $PROCESS_DEFINITION
}

# Deploy the Transfer UI App
deploy_console () {
    /usr/sbin/svcadm disable svc:/application/csk-tomcat
    sleep 10
    mkdir $CATALINA_HOME/webapps/transfer
    unzip -q -d  $CATALINA_HOME/webapps/transfer $TRANSFER_UI_WAR 
    echo -e $PM_WRITER_HIBERNATE_PROPS > $TRANSFER_DW_HIBERNATE_CONF
    echo -e $PM_READER_HIBERNATE_PROPS > $TRANSFER_RO_HIBERNATE_CONF
    echo -e $JBPM_HIBERNATE_PROPS > $TRANSFER_JBPM_HIBERNATE_CONF
    echo -e $LOG4J_PROPS > $TRANSFER_LOG4J_CONF
    echo -e $WORKFLOW_LOCAL_PROPS > $TRANSFER_WORKFLOW_CONF
    /usr/sbin/svcadm enable svc:/application/csk-tomcat
}

process_opts () {
    CALLER=`basename $0`
    while getopts ut ARG
        do case ${ARG} in
        u)  UPGRADE="true";;
        t)  TRUNCATE="true";;
      [?])  usage;;
        *)  usage;;  
        esac
    done
#shift $(($OPTIND - 1))
# Tests true if no switch is used at all
#if [ $1 ]; then
#  usage
#fi
}

usage() {
    cat << EOF

  NDNP Transfer Deployment Script

  USAGE: $CALLER [-i -q -d -r] [-b] [-s SOURCE_DIRECTORY] [-d DESTINATION DIRECTORY]
  WHERE: -h = 
         -u = Upgrede 
         
EOF
}

process_opts
init_vars
sanity_checks

create_dbs
init_roles

deploy_pm_core
deploy_pm_ndnp
deploy_jbpm

init_core_perms
init_ndnp_perms
init_jbpm_perms

deploy_pm_core_cli
deploy_pm_ndnp_cli
deploy_workflow_core

install_pm_fixtures
install_jbpm_fixtures
install_process_definition
deploy_console
