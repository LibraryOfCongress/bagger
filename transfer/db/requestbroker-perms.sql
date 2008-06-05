GRANT CONNECT ON DATABASE service_request_broker TO service_request_broker_role;
GRANT ALL ON TABLE boolean_entries TO GROUP service_request_broker_role;
GRANT ALL ON TABLE integer_entries TO GROUP service_request_broker_role;
GRANT ALL ON TABLE service_container_registry TO GROUP service_request_broker_role;
GRANT ALL ON TABLE service_request TO GROUP service_request_broker_role;
GRANT ALL ON TABLE string_entries TO GROUP service_request_broker_role;
GRANT ALL ON TABLE hibernate_sequence TO GROUP service_request_broker_role;
