create table boolean_entries (request_key int8 not null, key varchar(50) not null, value bool);
create table integer_entries (request_key int8 not null, key varchar(50) not null, value int8);
create table service_container_registry (service_url varchar(100) not null, primary key (service_url));
create table service_request (pkey int8 not null, requester varchar(50) not null, responder varchar(50), correlation_key varchar(255) not null, queue varchar(50) not null, job_type varchar(50) not null, request_date timestamp not null, request_acknowledged_date timestamp, response_date timestamp, response_acknowledged_date timestamp, is_success bool, error_message varchar(255), error_detail text, primary key (pkey));
create table string_entries (request_key int8 not null, key varchar(50) not null, value varchar(255));
alter table boolean_entries add constraint FK3012EF797872DA61 foreign key (request_key) references service_request;
alter table integer_entries add constraint FK81E98E8F7872DA61 foreign key (request_key) references service_request;
alter table string_entries add constraint FKD5B2EA627872DA61 foreign key (request_key) references service_request;
create sequence hibernate_sequence;
