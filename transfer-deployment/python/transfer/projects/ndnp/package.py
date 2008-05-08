import os
from transfer import utils

class PackageModeler():
    def __init__(self, settings):
        db_prefix = settings.DB_PREFIX + "_" if settings.DB_PREFIX else ""
        role_prefix = settings.ROLE_PREFIX + "_" if settings.ROLE_PREFIX else ""
        self.project_name = "packagemodeler-ndnp"
        self.sql_file = "files/inventory-ndnp.sql"
        self.db_name = db_prefix + "package_modeler"
        self.reader_role = role_prefix + "package_modeler_reader_role"
        self.writer_role = role_prefix + "package_modeler_data_writer_role"
        self.psql = settings.PSQL
        os.environ['PGUSER'] = settings.PGUSER
        os.environ['PGHOST'] = settings.PGHOST
        os.environ['PGPORT'] = settings.PGPORT
        os.environ['PGPASSWORD'] = settings.PGPASSWORD

    def create_database(self):
        """ create package modeler database """
        return True

    def populate_roles(self):
        """ populates roles """
        return True

    def grant_permissions(self):
        """ grant database permissions """
        os.environ['PGDATABASE'] = self.db_name
        # TODO: specify a file w/ same info or checkout from SVN
        # TODO: and/or look for more DRY way to handle templating?
        sql = ""
        sql += "GRANT USAGE ON SCHEMA ndnp TO %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch_lccn TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch_reel TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.lccn TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.reel TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT ON TABLE ndnp.awardphase TO GROUP %s;" % (self.writer_role)
        sql += "GRANT USAGE ON SCHEMA ndnp TO %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE ndnp.batch TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE ndnp.batch_lccn TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE ndnp.batch_reel TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE ndnp.lccn TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE ndnp.reel TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE ndnp.awardphase TO GROUP %s;" % (self.reader_role)
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Granting privileges to ndnp - %s" % (result)

    def deploy_database(self):
        """ deploy database """
        os.environ['PGDATABASE'] = self.db_name
        result = utils.load_sqlfile(self.psql, self.sql_file)
        return "Creating ndnp tables - %s" % (result)
