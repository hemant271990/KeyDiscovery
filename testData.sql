DROP DATABASE IF EXISTS keyDiscovery;
CREATE DATABASE keyDiscovery;
connect keyDiscovery;

GRANT ALL ON keyDiscovery.* TO 'keyDiscovery'@'%' IDENTIFIED BY 'keyDiscovery';
GRANT ALL ON keyDiscovery.* TO 'keyDiscovery'@'localhost' IDENTIFIED BY 'keyDiscovery';

CREATE TABLE data (
   A     INTEGER UNSIGNED NOT NULL,
   B     INTEGER UNSIGNED NOT NULL,
   C     INTEGER UNSIGNED NOT NULL,
   D     INTEGER UNSIGNED NOT NULL,
   PRIMARY KEY(A,D)
);
