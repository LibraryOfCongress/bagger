import os
from transfer.decorators import project_name
from transfer.database import AbstractDB
from transfer import utils

class Jbpm(AbstractDB):
    @project_name("jbpm")
    def __init__(self, config):
        AbstractDB.__init__(self, config)
        self.original_db_name = "jbpm32"
        self.db_name = self.db_prefix + self.original_db_name
        self.roles = {
            'owner': "jbpm_role",
            'user': "jbpm_user",
        }
        self.passwds = {
            'jbpm': config['JBPM_PASSWD'] if config['JBPM_PASSWD'] else "jbpm_user",
        }

    def create_fixtures(self, env, project):
        """ creates database fixtures """
        os.environ['PGDATABASE'] = self.db_prefix + self.db_name
        fixtures_file = self.sql_files['fixtures'].replace("-fixtures", "-%s-%s-fixtures" % (project, env))
        sql = file(fixtures_file).read()
        result = utils.load_sqlstr(self.psql, sql, self.debug)
        return "Installing %s database fixtures\n====================\n%s" % (self.project_name, result)

