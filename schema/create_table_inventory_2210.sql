CREATE TABLE `atmservice`.`inventory` (
  `id` BIGINT(8) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC));

ALTER TABLE `atmservice`.`inventory` 
ADD COLUMN `region_id` BIGINT NULL AFTER `name`,
ADD COLUMN `region_name` VARCHAR(255) NULL AFTER `region_id`,
ADD COLUMN `address` VARCHAR(255) NULL AFTER `region_name`;
  
ALTER TABLE `atmservice`.`inventory` 
CHANGE COLUMN `id` `id` BIGINT(8) NOT NULL AUTO_INCREMENT ;
  
CREATE TABLE `atmservice`.`part_inventory` (
  `id` BIGINT(8) NOT NULL AUTO_INCREMENT,
  `inventory_id` BIGINT(8) NOT NULL,
  `part_id` BIGINT(8) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `type` INT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
COMMENT = 'Số lượng part còn trong kho';
  
CREATE TABLE `atmservice`.`checking` (
  `id` BIGINT(8) NOT NULL AUTO_INCREMENT,
  `inventory_id` BIGINT(8) NOT NULL,
  `sender` VARCHAR(255) NULL,
  `receiver` VARCHAR(255) NULL,
  `status` INT NULL,
  `send_date` TIMESTAMP NULL,
  `received_date` TIMESTAMP NULL,
  `type` INT NULL,
  `received_phone` VARCHAR(15) NULL,
  `received_department` VARCHAR(255) NULL,
  `received_address` VARCHAR(255) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
COMMENT = 'Thông tin giao dịch xuất nhập kho' ;

CREATE TABLE `atmservice`.`checking_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `checking_id` BIGINT(8) NOT NULL,
  `part_id` BIGINT(8) NOT NULL,
  `part_name` VARCHAR(255) NOT NULL,
  `type` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
COMMENT = 'Chi tiết số lượng part trong 1 giao dịch xuất nhập kho';

ALTER TABLE `atmservice`.`part_inventory` 
ADD INDEX `fk_part_inventory_idx` (`inventory_id` ASC);

ALTER TABLE `atmservice`.`part_inventory` 
ADD CONSTRAINT `fk_part_inventory`
  FOREIGN KEY (`inventory_id`)
  REFERENCES `atmservice`.`inventory` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `atmservice`.`checking` 
ADD INDEX `fk_checking_inventory_idx` (`inventory_id` ASC);

ALTER TABLE `atmservice`.`checking_detail` 
ADD INDEX `fk_checking_checking_detail_idx` (`checking_id` ASC);

ALTER TABLE `atmservice`.`checking_detail` 
ADD CONSTRAINT `fk_checking_checking_detail`
  FOREIGN KEY (`checking_id`)
  REFERENCES `atmservice`.`checking` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

CREATE TABLE `atmservice`.`user_inventory` (
  `user_id` BIGINT(8) NOT NULL,
  `inventory_id` BIGINT(8) NOT NULL,
  INDEX `fk_user_user_inventory_idx` (`user_id` ASC),
  INDEX `fk_inventory_user_inventory_idx` (`inventory_id` ASC),
  CONSTRAINT `fk_user_user_inventory`
    FOREIGN KEY (`user_id`)
    REFERENCES `atmservice`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_inventory_user_inventory`
    FOREIGN KEY (`inventory_id`)
    REFERENCES `atmservice`.`inventory` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

ALTER TABLE `atmservice`.`checking` 
RENAME TO  `atmservice`.`tracking` ;

ALTER TABLE `atmservice`.`checking_detail` 
RENAME TO  `atmservice`.`tracking_detail` ;

ALTER TABLE `atmservice`.`tracking` 
ADD COLUMN `received_inventory` BIGINT(8) NULL AFTER `sended_inventory`,
CHANGE COLUMN `inventory_id` `sended_inventory` BIGINT(8) NULL ;

ALTER TABLE `atmservice`.`tracking_detail` 
DROP FOREIGN KEY `fk_checking_checking_detail`;
ALTER TABLE `atmservice`.`tracking_detail` 
CHANGE COLUMN `checking_id` `tracking_id` BIGINT(8) NOT NULL ;
ALTER TABLE `atmservice`.`tracking_detail` 
ADD CONSTRAINT `fk_tracking_tracking_detail`
  FOREIGN KEY (`tracking_id`)
  REFERENCES `atmservice`.`tracking` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `atmservice`.`tracking` 
ADD COLUMN `note` MEDIUMTEXT NULL AFTER `received_address`;


# 28/10/2021
ALTER TABLE `atmservice`.`tracking_detail` 
ADD COLUMN `part_inventory_id` BIGINT NOT NULL AFTER `quantity`;

ALTER TABLE `atmservice`.`tracking` 
ADD COLUMN `trading_code` VARCHAR(20) NOT NULL AFTER `note`;

# Trigger 
DELIMITER $$

CREATE TRIGGER trg_nhapHang
    AFTER INSERT
    ON tracking_detail FOR EACH ROW
BEGIN
	DECLARE trackingType INT default 0;
    SET trackingType = (select type from tracking where id = NEW.tracking_id);
	
    IF(trackingType = 1) THEN
		# Nhập kho
		UPDATE part_inventory
		SET quantity = quantity + NEW.quantity
		where id = NEW.part_inventory_id;
	ELSE
		# Xuất kho
		UPDATE part_inventory
		SET quantity = quantity - NEW.quantity
		where id = NEW.part_inventory_id;
	END IF;
END$$    

DELIMITER ;
 