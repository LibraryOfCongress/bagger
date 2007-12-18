create schema transport;

-- Users is a cache of people who are known to the transfer system
-- who are able to initiate transfer operations.  This is not used
-- for authentication.  Note however that all tickets must be filed
-- by a requester, which must appear in this list of users.
create table transport.users (
    username varchar(32) not null,
    realname text not null,
    email    text,
    primary key (username)
);

-- Enumerate all possible statuses a ticket describing a ticket.
create table transport.ticket_status (
    status varchar(32) not null,
    description text,
    primary key (status)
);

-- Initial status types used to describe tickets
INSERT INTO transport.ticket_status
VALUES ('registered', 'Ticket entered into the system');
INSERT INTO transport.ticket_status
VALUES ('queued', 'Ticket queued for processing');
INSERT INTO transport.ticket_status
VALUES ('started', 'Processing started for this ticket');
INSERT INTO transport.ticket_status
VALUES ('paused', 'Processing paused until resources become available');
INSERT INTO transport.ticket_status
VALUES ('failed', 'Permanent failure for this ticket');
INSERT INTO transport.ticket_status
VALUES ('completed', 'Processing successfully completed for this ticket');

-- A ticket describes a single copy operation.  The ticket links together
-- all information about an operation, including all of the participating
-- locations, and all participating log messages about that operation.
--
-- TBD: should package name and repository be constrained to values
-- present in the inventory database?  Is this a chicken-and-egg problem?
create table transport.tickets (
    ticket_id           serial,
    status              varchar(32) not null 
                        references transport.ticket_status
                        default 'registered',
    created_timestamp   timestamp not null default NOW(),
    updated_timestamp   timestamp not null default NOW(),
    initiated_timestamp timestamp,
    completed_timestamp timestamp,
    requester           varchar(32) references transport.users,
    notification_list   text,
    package_name        varchar(255) not null,
    repository          varchar(255) not null,
    estimated_size      integer not null,
    primary key (ticket_id)
);

-- Location Roles is a constraint on the kinds of locations 
-- that can appear attached to a ticket.  Some operations will
-- involve an intermediate workspace copy, while others will not.
--
-- Future expansion is possible, including synthesizing a package
-- by merging multiple sources, staging a merge from multiple
-- sources into a single location (and producing a manifest of the 
-- merge), and restoring a package from the archive (which has
-- different semantics and workflow rules).
create table transport.location_roles (
    role        varchar(32) not null,
    description text,
    primary key (role)
);

-- Initial roles used to handle simple copy-to-archive
INSERT INTO transport.location_roles 
VALUES ('source', 'original location of a package');
INSERT INTO transport.location_roles
VALUES ('workspace', 'intermediate location of a package, during archival');
INSERT INTO transport.location_roles
VALUES ('destination', 'final location of a package for a copy operation');

create table transport.systems (
    short_name  varchar(32) not null,
    hostname    varchar(64) not null,
    description text,
    primary key (short_name)
);

-- A location is an area on disk on some machine within the network
-- that participates in a copy operation.
-- Requires that the location_id is strictly ascending.
-- Note that the system_name => hostname relationship may vary
-- over time.  Store the specific location desired when the ticket
-- was created. 
create table transport.locations (
    location_id    serial,
    ticket_id      integer not null references transport.tickets,
    system_name    varchar(32)  not null references transport.systems,
    file_owner     varchar(16), 
    hostname       varchar(32)  not null, 
    root_directory varchar(255) not null,
    role           varchar(32)  not null references transport.location_roles,    
    unique(ticket_id, system_name, root_directory, role)
);

-- Event Types are a constraint on the kinds of event messages
-- the ticketing system can produce.
create table transport.event_types (
    event_type varchar(32) not null,
    primary key (event_type)
);

INSERT INTO transport.event_types VALUES ('fixity verification');
INSERT INTO transport.event_types VALUES ('package registration');
INSERT INTO transport.event_types VALUES ('workspace copy');
INSERT INTO transport.event_types VALUES ('archive copy');
INSERT INTO transport.event_types VALUES ('location registration');
INSERT INTO transport.event_types VALUES ('purge workspace copy');

-- Event Statuses are a constraint on the kinds of status message
-- that can be an associated with an event.  
--
-- TBD: The split of event types and event statuses leads to a
-- cartesian product of possible events.  This is not currently
-- a problem.
create table transport.event_status (
    event_status varchar(32) not null,
    primary key (event_status)
);

INSERT INTO transport.event_status VALUES ('started');
INSERT INTO transport.event_status VALUES ('waiting'); -- a busy wait for a resource
INSERT INTO transport.event_status VALUES ('warning');
INSERT INTO transport.event_status VALUES ('completed');
INSERT INTO transport.event_status VALUES ('pass');
INSERT INTO transport.event_status VALUES ('fail');

-- Events are periodic messages about the status of a single operation
-- within a transfer workflow
create table transport.events (
    event_id        serial,
    ticket_id       integer not null references transport.tickets,
    event_timestamp timestamp not null default now(),
    event_type      varchar(32) not null references transport.event_types,
    event_status    varchar(32) not null references transport.event_status,
    system_name     varchar(32)  not null references transport.systems,
    hostname        varchar(32)  not null, 
    root_directory  varchar(255) not null,
    event_details   text
);

