create schema ndnp;

create table ndnp.batch (
	pkey int8 not null, 
	awardee_key int8, 
	primary key (pkey)
);

create table ndnp.batch_lccn (
	package_key int8 not null, 
	lccn_key int8 not null, 
	primary key (package_key, lccn_key)
);

create table ndnp.batch_reel (
	package_key int8 not null, 
	reel_key int8 not null, 
	primary key (package_key, reel_key)
);

create table ndnp.lccn (
	pkey int8 not null, 
	lccn varchar(255) not null, 
	primary key (pkey), 
	unique (lccn)
);

create table ndnp.reel (
	pkey int8 not null, 
	reelnumber varchar(255) not null, 
	primary key (pkey), 
	unique (reelnumber)
);

alter table ndnp.batch add constraint FK592D73A37D75616 foreign key (pkey) references core.package;
alter table ndnp.batch add constraint FK592D73AACF8C8E6 foreign key (awardee_key) references agent.agent;
alter table ndnp.batch_lccn add constraint FKF001EE0711BAB3D6 foreign key (lccn_key) references ndnp.lccn;
alter table ndnp.batch_lccn add constraint FKF001EE07446A83EE foreign key (package_key) references ndnp.batch;
alter table ndnp.batch_reel add constraint FKF004AFFF446A83EE foreign key (package_key) references ndnp.batch;
alter table ndnp.batch_reel add constraint FKF004AFFFCA7CBFC6 foreign key (reel_key) references ndnp.reel;
