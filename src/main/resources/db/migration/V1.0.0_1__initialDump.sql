-- version: 10.2.14-MariaDB (Mysql)
-- innodb_version: 5.7.21
CREATE DATABASE `fpcollabman`CHARACTER SET utf8 COLLATE utf8_unicode_ci;

USE fpcollabman;

CREATE TABLE collaborator
(
    `id`                 INTEGER NOT NULL AUTO_INCREMENT,
    `name`               VARCHAR(255),
    `encrypted_password` VARCHAR(255),
    `password_score`     INTEGER NOT NULL,
    `tree_path`          LONGTEXT,
    `manager_id`         INTEGER,
     PRIMARY KEY (id)
) ENGINE=INNODB;

ALTER TABLE collaborator
    ADD CONSTRAINT FKptah6kogyx6mccw1lbvd9mltd
    FOREIGN KEY (manager_id)
    REFERENCES collaborator (id);
