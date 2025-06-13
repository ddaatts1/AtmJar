INSERT INTO `atmservice`.`role` (`id`, `name`, `desc`) VALUES ('5', 'ROLE_TAKE_REPORT', 'Nhận biên bản');


ALTER TABLE `atmservice`.`job` 
ADD COLUMN `take_report` TINYINT(1) NULL DEFAULT 0 AFTER `file_path`;
