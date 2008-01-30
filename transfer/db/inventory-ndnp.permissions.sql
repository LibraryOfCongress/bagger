GRANT USAGE ON SCHEMA ndnp TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch TO GROUP package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch_lccn TO GROUP package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.batch_reel TO GROUP package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.lccn TO GROUP package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.reel TO GROUP package_modeler_data_writer_role;
GRANT SELECT ON TABLE ndnp.awardphase TO GROUP package_modeler_data_writer_role;

GRANT USAGE ON SCHEMA ndnp TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE ndnp.awardphase TO GROUP package_modeler_fixture_writer_role;

GRANT USAGE ON SCHEMA ndnp TO package_modeler_reader_role;
GRANT SELECT ON TABLE ndnp.batch TO GROUP package_modeler_reader_role;
GRANT SELECT ON TABLE ndnp.batch_lccn TO GROUP package_modeler_reader_role;
GRANT SELECT ON TABLE ndnp.batch_reel TO GROUP package_modeler_reader_role;
GRANT SELECT ON TABLE ndnp.lccn TO GROUP package_modeler_reader_role;
GRANT SELECT ON TABLE ndnp.reel TO GROUP package_modeler_reader_role;
GRANT SELECT ON TABLE ndnp.awardphase TO GROUP package_modeler_reader_role;

