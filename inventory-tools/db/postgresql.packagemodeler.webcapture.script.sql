create table webcapture.collection (pkey int8 not null, collection_id varchar(255) not null, primary key (pkey), unique (collection_id));
create table webcapture.segment (pkey int8 not null, collection_key int8 not null, primary key (pkey));
alter table webcapture.segment add constraint FK75A49F3337D75616 foreign key (pkey) references core.package;
alter table webcapture.segment add constraint FK75A49F3314FF3AC7 foreign key (collection_key) references webcapture.collection;
