GRANT USAGE ON SCHEMA transport TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.users TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.ticket_status TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.tickets TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.location_roles TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.systems TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.locations TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.event_types TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.event_status TO package_modeler_fixture_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.events TO package_modeler_fixture_writer_role;

GRANT USAGE ON SCHEMA transport TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.users TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.ticket_status TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.tickets TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.location_roles TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.systems TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.locations TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.event_types TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.event_status TO package_modeler_data_writer_role;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE transport.events TO package_modeler_data_writer_role;

GRANT USAGE ON SCHEMA transport TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.users TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.ticket_status TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.tickets TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.location_roles TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.systems TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.locations TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.event_types TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.event_status TO package_modeler_reader_role;
GRANT SELECT ON TABLE transport.events TO package_modeler_reader_role;

GRANT ALL ON TABLE transport.tickets_ticket_id_seq TO PUBLIC;
GRANT ALL ON TABLE transport.locations_location_id_seq TO PUBLIC;
GRANT ALL ON TABLE transport.events_event_id_seq TO PUBLIC;
