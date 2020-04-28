CREATE DATABASE  IF NOT EXISTS `omw` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `omw`;

CREATE USER 'omw'@'localhost' IDENTIFIED BY 'Insoft!23';
GRANT ALL PRIVILEGES ON omw.* TO 'omw'@'localhost';
CREATE USER 'omw'@'localhost.localdomain' IDENTIFIED BY 'Insoft!23';
GRANT ALL PRIVILEGES ON omw.* TO 'omw'@'localhost.localdomain';
CREATE USER 'omw'@'%' IDENTIFIED BY 'Insoft!23';
GRANT ALL PRIVILEGES ON omw.* TO 'omw'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON `mysql`.`proc` TO 'omw'@'%';

FLUSH PRIVILEGES;