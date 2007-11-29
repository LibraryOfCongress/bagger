use jbpm32;

truncate table JBPM_COMMENT;
truncate table JBPM_LOG;
truncate table JBPM_VARIABLEINSTANCE;
truncate table JBPM_TOKENVARIABLEMAP;
truncate table JBPM_TASKACTORPOOL;
truncate table JBPM_POOLEDACTOR;
truncate table JBPM_TASKINSTANCE;
truncate table JBPM_SWIMLANEINSTANCE;
truncate table JBPM_MODULEINSTANCE;
truncate table JBPM_JOB;
update JBPM_TOKEN set PARENT_=null;
update JBPM_PROCESSINSTANCE set ROOTTOKEN_=null;
truncate table JBPM_TOKEN;
truncate table JBPM_PROCESSINSTANCE;
