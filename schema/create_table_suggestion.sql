CREATE TABLE `atmservice`.`suggestion` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NULL,
  `phone` VARCHAR(50) NULL,
  `department` VARCHAR(255) NULL,
  `address` VARCHAR(500) NULL,
  PRIMARY KEY (`id`));

  
DROP TRIGGER IF EXISTS `atmservice`.`trg_nhapHang`;

DELIMITER $$
USE `atmservice`$$
CREATE TRIGGER trg_nhapHang
    AFTER INSERT
    ON tracking_detail FOR EACH ROW
BEGIN
	DECLARE trackingType INT default 0;
    DECLARE partQuantity INT default 0;
    SET trackingType = (select type from tracking where id = NEW.tracking_id);
    SET partQuantity = (select quantity from part_inventory where id = NEW.part_inventory_id);
	
    IF(trackingType = 1) THEN
		# Nhập hàng
		UPDATE part_inventory
		SET quantity = quantity + NEW.quantity
		where id = NEW.part_inventory_id;
	ELSE
		IF ((partQuantity - NEW.quantity) >= 0) THEN
			UPDATE part_inventory
			SET quantity = quantity - NEW.quantity
			where id = NEW.part_inventory_id;
		ELSE 
			SIGNAL sqlstate '45001' set message_text = "No way ! You cannot do this !";
		END IF;
	END IF;
END$$
DELIMITER ;
  