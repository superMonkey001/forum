alter table user add password varchar(100) null;
alter table user add email varchar(32) null;
alter table user add salt char(6) null;