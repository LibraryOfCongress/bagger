create table agentmodeler_agent (
	agent_type varchar(31) not null, 
	pkey int8 not null, 
	agent_id varchar(255) not null, 
	name varchar(255), 
	first_name varchar(255), 
	surname varchar(255), 
	primary key (pkey)
);

create table agentmodeler_agent_role (
	agent_key int8 not null, 
	role_key int8 not null, 
	primary key (agent_key, role_key)
);

create table agentmodeler_role (
	pkey int8 not null, 
	role_id varchar(255) not null, 
	primary key (pkey)
);

create table eventmodeler_file_examination_group_event (
	DTYPE varchar(31) not null, 
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	event_start timestamp not null, 
	is_unknown_event_start bool not null, 
	event_end timestamp, 
	is_unknown_performing_agent bool not null, 
	is_unknown_requesting_agent bool not null, 
	is_success bool not null, 
	reporting_agent_key int8 not null, 
	requesting_agent_key int8, 
	file_examination_group_key int8 not null, 
	performing_agent_key int8, 
	primary key (pkey)
);

create table eventmodeler_file_location_event (
	DTYPE varchar(31) not null, 
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	event_start timestamp not null, 
	is_unknown_event_start bool not null, 
	event_end timestamp, 
	is_unknown_performing_agent bool not null, 
	is_unknown_requesting_agent bool not null, 
	is_success bool not null, 
	reporting_agent_key int8 not null, 
	requesting_agent_key int8, 
	repository_system_key int8, 
	performing_agent_key int8, 
	file_location_key int8 not null, 
	source_filelocation_key int8, 
	primary key (pkey)
);

create table eventmodeler_package_event (
	DTYPE varchar(31) not null, 
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	event_start timestamp not null, 
	is_unknown_event_start bool not null, 
	event_end timestamp, 
	is_unknown_performing_agent bool not null, 
	is_unknown_requesting_agent bool not null, 
	is_success bool not null, 
	reporting_agent_key int8 not null, 
	package_key int8 not null, 
	requesting_agent_key int8, 
	performing_agent_key int8, 
	primary key (pkey)
);

create table packagemodeler_canonicalfile (
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	bytes int8, 
	relative_path varchar(255) not null, 
	base_name varchar(255) not null, 
	extension varchar(10), 
	package_key int8 not null, 
	primary key (pkey), 
	unique (package_key, relative_path, base_name, extension)
);

create table packagemodeler_canonicalfile_fixity (
	canonicalfile_key int8 not null, 
	fixity_value varchar(255) not null, 
	algorithm varchar(255) not null, 
	primary key (canonicalfile_key, fixity_value, algorithm), 
	unique (canonicalfile_key, algorithm)
);

create table packagemodeler_external_file_location (
	pkey int8 not null, 
	base_path varchar(255), 
	relative_content_path varchar(255), 
	identifier_type varchar(255), 
	identifier_value varchar(255), 
	media_type varchar(255), 
	primary key (pkey), 
	unique (identifier_value, identifier_type, media_type, base_path)
);

create table packagemodeler_file_examination_group (
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	is_complete bool not null, 
	file_location_key int8 not null, 
	primary key (pkey)
);

create table packagemodeler_file_location (
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	is_managed bool not null, 
	package_key int8 not null, 
	primary key (pkey)
);

create table packagemodeler_fileexamination (
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	bytes int8, 
	relative_path varchar(255) not null, 
	base_name varchar(255) not null, 
	extension varchar(10), 
	file_modified_timestamp timestamp, 
	present_flag bool not null, 
	file_examination_group_key int8 not null, 
	primary key (pkey), 
	unique (file_examination_group_key, relative_path, base_name, extension)
);

create table packagemodeler_fileexamination_fixity (
	fileobservation_key int8 not null, 
	fixity_value varchar(255) not null, 
	algorithm varchar(255) not null, 
	primary key (fileobservation_key, fixity_value, algorithm), 
	unique (fileobservation_key, algorithm)
);

create table packagemodeler_fileinstance (
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	file_create_timestamp timestamp, 
	relative_path varchar(255) not null, 
	base_name varchar(255) not null, 
	extension varchar(10), 
	filelocation_key int8 not null, 
	primary key (pkey), 
	unique (filelocation_key, relative_path, base_name, extension)
);

create table packagemodeler_fileinstance_fixity (
	fileinstance_key int8 not null, 
	fixity_value varchar(255) not null, 
	algorithm varchar(255) not null, 
	primary key (fileinstance_key, fixity_value, algorithm), 
	unique (fileinstance_key, algorithm)
);

create table packagemodeler_ndnp_batch (
	pkey int8 not null, 
	awardee_key int8, 
	primary key (pkey)
);

create table packagemodeler_ndnp_batch_lccn (
	package_key int8 not null, 
	lccn_key int8 not null, 
	primary key (package_key, lccn_key)
);

create table packagemodeler_ndnp_batch_reel (
	package_key int8 not null, 
	reel_key int8 not null, 
	primary key (package_key, reel_key)
);

create table packagemodeler_ndnp_lccn (
	pkey int8 not null, 
	lccn varchar(255) not null, 
	primary key (pkey), 
	unique (lccn)
);

create table packagemodeler_ndnp_reel (
	pkey int8 not null, 
	reelnumber varchar(255) not null, 
	primary key (pkey), 
	unique (reelnumber)
);

create table packagemodeler_package (
	pkey int8 not null, 
	create_timestamp timestamp not null, 
	update_timestamp timestamp not null, 
	package_id varchar(255) not null, 
	processinstance_id int8, 
	repository_key int8 not null, 
	primary key (pkey), 
	unique (repository_key, package_id)
);

create table packagemodeler_repository (
	pkey int8 not null, 
	repository_id varchar(255) not null, 
	primary key (pkey), 
	unique (repository_id)
);

create table packagemodeler_storagesystem_file_location (
	pkey int8 not null, 
	base_path varchar(255) not null, 
	relative_content_path varchar(255), 
	storagesystem_key int8 not null, 
	primary key (pkey), 
	unique (storagesystem_key, base_path)
);

create table packagemodeler_webcapture_collection (
	pkey int8 not null, 
	collection_id varchar(255) not null, 
	primary key (pkey), 
	unique (collection_id)
);

create table packagemodeler_webcapture_segment (
	pkey int8 not null, 
	collection_key int8 not null, 
	primary key (pkey)
);

alter table agentmodeler_agent_role
	add constraint FKF4C0377E84533375
	foreign key (role_key)
	references agentmodeler_role;

alter table agentmodeler_agent_role
	add constraint FKF4C0377E1479C561
	foreign key (agent_key)
	references agentmodeler_agent;

alter table eventmodeler_file_examination_group_event
	add constraint FK3A5F0F6CEAE04383
	foreign key (performing_agent_key)
	references agentmodeler_agent;

alter table eventmodeler_file_examination_group_event
	add constraint FK3A5F0F6C6A9CFACA
	foreign key (file_examination_group_key)
	references packagemodeler_file_examination_group;

alter table eventmodeler_file_examination_group_event
	add constraint FK3A5F0F6C5E042490
	foreign key (reporting_agent_key)
	references agentmodeler_agent;

alter table eventmodeler_file_examination_group_event
	add constraint FK3A5F0F6C66BF5315
	foreign key (requesting_agent_key)
	references agentmodeler_agent;

alter table eventmodeler_file_location_event
	add constraint FKAE78EFB0EAE04383
	foreign key (performing_agent_key)
	references agentmodeler_agent;

alter table eventmodeler_file_location_event
	add constraint FKAE78EFB0ECBF8298
	foreign key (source_filelocation_key)
	references packagemodeler_file_location;

alter table eventmodeler_file_location_event
	add constraint FKAE78EFB068DA515B
	foreign key (file_location_key)
	references packagemodeler_file_location;

alter table eventmodeler_file_location_event
	add constraint FKAE78EFB0C41EFBC
	foreign key (repository_system_key)
	references agentmodeler_agent;

alter table eventmodeler_file_location_event
	add constraint FKAE78EFB05E042490
	foreign key (reporting_agent_key)
	references agentmodeler_agent;

alter table eventmodeler_file_location_event
	add constraint FKAE78EFB066BF5315
	foreign key (requesting_agent_key)
	references agentmodeler_agent;

alter table eventmodeler_package_event
	add constraint FKEA2E841EEAE04383
	foreign key (performing_agent_key)
	references agentmodeler_agent;

alter table eventmodeler_package_event
	add constraint FKEA2E841EB1FF2D9A
	foreign key (package_key)
	references packagemodeler_package;

alter table eventmodeler_package_event
	add constraint FKEA2E841E5E042490
	foreign key (reporting_agent_key)
	references agentmodeler_agent;

alter table eventmodeler_package_event
	add constraint FKEA2E841E66BF5315
	foreign key (requesting_agent_key)
	references agentmodeler_agent;

alter table packagemodeler_canonicalfile
	add constraint FK7DE28901B1FF2D9A
	foreign key (package_key)
	references packagemodeler_package;

alter table packagemodeler_canonicalfile_fixity
	add constraint FKBE4099779808458E
	foreign key (canonicalfile_key)
	references packagemodeler_canonicalfile;

alter table packagemodeler_external_file_location
	add constraint FK937208333C501092
	foreign key (pkey)
	references packagemodeler_file_location;

alter table packagemodeler_file_examination_group
	add constraint FK1859B73D68DA515B
	foreign key (file_location_key)
	references packagemodeler_file_location;

alter table packagemodeler_file_location
	add constraint FKD791CFA9B1FF2D9A
	foreign key (package_key)
	references packagemodeler_package;

alter table packagemodeler_fileexamination
	add constraint FKBABE01466A9CFACA
	foreign key (file_examination_group_key)
	references packagemodeler_file_examination_group;

alter table packagemodeler_fileexamination_fixity
	add constraint FKF1BF3992E70D3093
	foreign key (fileobservation_key)
	references packagemodeler_fileexamination;

alter table packagemodeler_fileinstance
	add constraint FK5E04EF605706AEF4
	foreign key (filelocation_key)
	references packagemodeler_file_location;

alter table packagemodeler_fileinstance_fixity
	add constraint FK184F1A38609B8B4
	foreign key (fileinstance_key)
	references packagemodeler_fileinstance;

alter table packagemodeler_ndnp_batch
	add constraint FKF8180F427C0146A3
	foreign key (pkey)
	references packagemodeler_package;

alter table packagemodeler_ndnp_batch
	add constraint FKF8180F4216202879
	foreign key (awardee_key)
	references agentmodeler_agent;

alter table packagemodeler_ndnp_batch_lccn
	add constraint FK65A67AFFCF460E1
	foreign key (package_key)
	references packagemodeler_ndnp_batch;

alter table packagemodeler_ndnp_batch_lccn
	add constraint FK65A67AFFACD7EC83
	foreign key (lccn_key)
	references packagemodeler_ndnp_lccn;

alter table packagemodeler_ndnp_batch_reel
	add constraint FK65A93CF76599F873
	foreign key (reel_key)
	references packagemodeler_ndnp_reel;

alter table packagemodeler_ndnp_batch_reel
	add constraint FK65A93CF7CF460E1
	foreign key (package_key)
	references packagemodeler_ndnp_batch;

alter table packagemodeler_package
	add constraint FK12ADBD972F6F3286
	foreign key (repository_key)
	references packagemodeler_repository;

alter table packagemodeler_storagesystem_file_location
	add constraint FK43477AD43C501092
	foreign key (pkey)
	references packagemodeler_file_location;

alter table packagemodeler_storagesystem_file_location
	add constraint FK43477AD4893F2C62
	foreign key (storagesystem_key)
	references agentmodeler_agent;

alter table packagemodeler_webcapture_segment
	add constraint FK3EBC33757C0146A3
	foreign key (pkey)
	references packagemodeler_package;

alter table packagemodeler_webcapture_segment
	add constraint FK3EBC33757F4F1AAE
	foreign key (collection_key)
	references packagemodeler_webcapture_collection;

create sequence hibernate_sequence;
