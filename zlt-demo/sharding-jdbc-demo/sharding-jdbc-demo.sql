-----------------------------------------sharding-databases
CREATE SCHEMA IF NOT EXISTS demo_ds_0;
CREATE SCHEMA IF NOT EXISTS demo_ds_1;
CREATE TABLE IF NOT EXISTS demo_ds_0.user (
	id BIGINT NOT NULL AUTO_INCREMENT,
	company_id varchar(32) NOT NULL, 
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS demo_ds_1.user (
	id BIGINT NOT NULL AUTO_INCREMENT, 
	company_id varchar(32) NOT NULL,
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);


-----------------------------------------sharding-databases2
CREATE SCHEMA IF NOT EXISTS demo_ds_alibaba;
CREATE SCHEMA IF NOT EXISTS demo_ds_baidu;
CREATE TABLE IF NOT EXISTS demo_ds_alibaba.user (
	id BIGINT NOT NULL AUTO_INCREMENT, 
	company_id varchar(32) NOT NULL,
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS demo_ds_baidu.user (
	id BIGINT NOT NULL AUTO_INCREMENT, 
	company_id varchar(32) NOT NULL,
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);

-----------------------------------------master-slave
CREATE SCHEMA IF NOT EXISTS demo_ds_master;
CREATE SCHEMA IF NOT EXISTS demo_ds_slave_0;
CREATE SCHEMA IF NOT EXISTS demo_ds_slave_1;
CREATE TABLE IF NOT EXISTS demo_ds_master.user (
	id BIGINT NOT NULL AUTO_INCREMENT,
	company_id varchar(32) NOT NULL, 
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS demo_ds_slave_0.user (
	id BIGINT NOT NULL AUTO_INCREMENT, 
	company_id varchar(32) NOT NULL,
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS demo_ds_slave_1.user (
	id BIGINT NOT NULL AUTO_INCREMENT, 
	company_id varchar(32) NOT NULL,
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);

-----------------------------------------sharding-tables
CREATE SCHEMA IF NOT EXISTS demo_ds;
CREATE TABLE IF NOT EXISTS demo_ds.user_0 (
	id BIGINT NOT NULL AUTO_INCREMENT,
	company_id varchar(32) NOT NULL, 
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS demo_ds.user_1 (
	id BIGINT NOT NULL AUTO_INCREMENT,
	company_id varchar(32) NOT NULL, 
	name varchar(50) NULL, 
	create_time datetime(0) NULL,
    update_time datetime(0) NULL,
	PRIMARY KEY (id)
);