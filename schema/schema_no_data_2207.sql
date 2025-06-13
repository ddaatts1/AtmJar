-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               5.7.24 - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for atmservice
CREATE DATABASE IF NOT EXISTS `atmservice` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `atmservice`;

-- Dumping structure for table atmservice.accessory
CREATE TABLE IF NOT EXISTS `accessory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `error_device_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_device_1_idx` (`error_device_id`),
  CONSTRAINT `fk_device_1` FOREIGN KEY (`error_device_id`) REFERENCES `error_device` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.accessory_series
CREATE TABLE IF NOT EXISTS `accessory_series` (
  `accessory_id` bigint(20) NOT NULL,
  `series_id` bigint(20) NOT NULL,
  KEY `fk_accessory_idx` (`accessory_id`),
  KEY `fk_series_idx` (`series_id`),
  CONSTRAINT `fk_accessory_1` FOREIGN KEY (`accessory_id`) REFERENCES `accessory` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_series_1` FOREIGN KEY (`series_id`) REFERENCES `series` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.atm
CREATE TABLE IF NOT EXISTS `atm` (
  `serial_number` varchar(50) NOT NULL,
  `atm_id` varchar(50) DEFAULT NULL,
  `address` varchar(255) NOT NULL,
  `series_id` bigint(20) DEFAULT NULL,
  `region_id` int(11) DEFAULT NULL,
  `department_id` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`serial_number`),
  UNIQUE KEY `serial_number_UNIQUE` (`serial_number`),
  KEY `fk_seri_idx` (`series_id`),
  KEY `fk_region_idx` (`region_id`),
  KEY `fk_department_idx` (`department_id`),
  CONSTRAINT `fk_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_region` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`),
  CONSTRAINT `fk_seri` FOREIGN KEY (`series_id`) REFERENCES `series` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.bank
CREATE TABLE IF NOT EXISTS `bank` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.contract
CREATE TABLE IF NOT EXISTS `contract` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `bank_id` bigint(20) NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` tinyint(1) NOT NULL,
  `maintenance_cycle` int(11) NOT NULL,
  `type` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bank_idx` (`bank_id`),
  CONSTRAINT `fk_bank` FOREIGN KEY (`bank_id`) REFERENCES `bank` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.contract_atm
CREATE TABLE IF NOT EXISTS `contract_atm` (
  `contract_id` bigint(20) NOT NULL,
  `serial_number` varchar(50) NOT NULL,
  KEY `fk_contract_idx` (`contract_id`),
  KEY `fk_atm_idx` (`serial_number`),
  CONSTRAINT `fk_atm` FOREIGN KEY (`serial_number`) REFERENCES `atm` (`serial_number`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_contract_2` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.customer_email
CREATE TABLE IF NOT EXISTS `customer_email` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `type` tinyint(1) NOT NULL,
  `contract_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contract_email_idx` (`contract_id`),
  CONSTRAINT `fk_contract_email` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.department
CREATE TABLE IF NOT EXISTS `department` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.device
CREATE TABLE IF NOT EXISTS `device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.email_config
CREATE TABLE IF NOT EXISTS `email_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `host` varchar(45) NOT NULL,
  `port` int(11) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(255) NOT NULL,
  `protocol` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.error
CREATE TABLE IF NOT EXISTS `error` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `device_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_error_device_idx` (`device_id`),
  CONSTRAINT `fk_error_device` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.error_device
CREATE TABLE IF NOT EXISTS `error_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.error_device_series
CREATE TABLE IF NOT EXISTS `error_device_series` (
  `error_device_id` bigint(20) NOT NULL,
  `series_id` bigint(20) NOT NULL,
  KEY `fk_device_series_1_idx` (`error_device_id`),
  KEY `fk_device_series_2_idx` (`series_id`),
  CONSTRAINT `fk_device_series_1` FOREIGN KEY (`error_device_id`) REFERENCES `error_device` (`id`),
  CONSTRAINT `fk_device_series_2` FOREIGN KEY (`series_id`) REFERENCES `series` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.history
CREATE TABLE IF NOT EXISTS `history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) NOT NULL,
  `serial_number` varchar(50) NOT NULL,
  `created_date` datetime NOT NULL,
  `created_by` varchar(50) NOT NULL,
  `series_id` bigint(20) DEFAULT NULL,
  `region_id` bigint(20) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `contract_id` bigint(20) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.job
CREATE TABLE IF NOT EXISTS `job` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `serial_number` varchar(50) NOT NULL,
  `check_in_time` datetime NOT NULL,
  `check_out_time` datetime DEFAULT NULL,
  `job_reason` varchar(255) DEFAULT NULL,
  `status` tinyint(1) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `job_error` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `complete_time` datetime DEFAULT NULL,
  `is_maintenance` tinyint(1) DEFAULT NULL,
  `is_kpsc` tinyint(1) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_atm_1_idx` (`serial_number`),
  KEY `fk_job_user_idx` (`user_id`),
  CONSTRAINT `fk_job_atm` FOREIGN KEY (`serial_number`) REFERENCES `atm` (`serial_number`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_job_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.job_perform
CREATE TABLE IF NOT EXISTS `job_perform` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.kpsc
CREATE TABLE IF NOT EXISTS `kpsc` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) NOT NULL,
  `job_perform_id` bigint(20) DEFAULT NULL,
  `error_desc` varchar(255) DEFAULT NULL,
  `device_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_kpsc_1_idx` (`job_id`),
  KEY `fk_kpsc_2_idx` (`job_perform_id`),
  KEY `fk_kpsc_4_idx` (`device_id`),
  CONSTRAINT `fk_kpsc_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_kpsc_2` FOREIGN KEY (`job_perform_id`) REFERENCES `job_perform` (`id`),
  CONSTRAINT `fk_kpsc_4` FOREIGN KEY (`device_id`) REFERENCES `device` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.period
CREATE TABLE IF NOT EXISTS `period` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contract_id` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `period_index` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_period_contract_idx` (`contract_id`),
  CONSTRAINT `fk_period_contract` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.persistent_logins
CREATE TABLE IF NOT EXISTS `persistent_logins` (
  `username` varchar(64) NOT NULL,
  `series` varchar(64) NOT NULL,
  `token` varchar(64) NOT NULL,
  `last_used` timestamp NOT NULL,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.region
CREATE TABLE IF NOT EXISTS `region` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.replacement_accessory
CREATE TABLE IF NOT EXISTS `replacement_accessory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `error_device_id` bigint(20) NOT NULL,
  `accessory_id` bigint(20) NOT NULL,
  `quantity` int(11) NOT NULL,
  `kpsc_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_device_2_idx` (`error_device_id`),
  KEY `fk_accessory_idx` (`accessory_id`),
  KEY `fk_kpsc_accessory_idx` (`kpsc_id`),
  CONSTRAINT `fk_accessory` FOREIGN KEY (`accessory_id`) REFERENCES `accessory` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_device_2` FOREIGN KEY (`error_device_id`) REFERENCES `error_device` (`id`),
  CONSTRAINT `fk_kpsc_accessory` FOREIGN KEY (`kpsc_id`) REFERENCES `kpsc` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.role
CREATE TABLE IF NOT EXISTS `role` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `desc` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.series
CREATE TABLE IF NOT EXISTS `series` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.statistical
CREATE TABLE IF NOT EXISTS `statistical` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` bigint(20) DEFAULT NULL,
  `contract_id` bigint(20) DEFAULT NULL,
  `serial_number` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `region_id` bigint(20) DEFAULT NULL,
  `job_complete_time` datetime DEFAULT NULL,
  `is_maintenance` tinyint(4) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_id_UNIQUE` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(255) NOT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `is_actived` tinyint(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  `department_id` int(11) DEFAULT NULL,
  `phone_number` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `device_id_UNIQUE` (`device_id`),
  KEY `fk_user_department_idx` (`department_id`),
  CONSTRAINT `fk_user_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

-- Dumping structure for table atmservice.user_role
CREATE TABLE IF NOT EXISTS `user_role` (
  `user_id` bigint(8) NOT NULL,
  `role_id` bigint(8) NOT NULL,
  KEY `fk_user_idx` (`user_id`),
  KEY `fk_role_idx` (`role_id`),
  CONSTRAINT `fk_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Data exporting was unselected.

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
