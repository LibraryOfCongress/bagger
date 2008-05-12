create table boolean_map (request_key int8 not null, value bool, key varchar(50), primary key (request_key, key));
create table integer_map (request_key int8 not null, value int8, key varchar(50), primary key (request_key, key));
create table service_request (pkey int8 not null, requester varchar(50) not null, responder varchar(50), correlation_key varchar(255) not null, queue varchar(50) not null, job_type varchar(50) not null, request_date timestamp not null, request_acknowledged_date timestamp, response_date timestamp, response_acknowledged_date timestamp, is_success bool, error_message varchar(255), error_detail text, primary key (pkey));
create table string_map (request_key int8 not null, value varchar(255), key varchar(50), primary key (request_key, key));
alter table boolean_map add constraint FK92A5ECC57872DA61 foreign key (request_key) references service_request;
alter table integer_map add constraint FK2FA386DB7872DA61 foreign key (request_key) references service_request;
alter table string_map add constraint FKA241C42E7872DA61 foreign key (request_key) references service_request;

create table service_container_registry (service_url varchar(100) not null, primary key (service_url));

create sequence hibernate_sequence;