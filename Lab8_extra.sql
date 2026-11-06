USE `Lab5`;

CREATE TABLE IF NOT EXISTS `friend_request` (
  `id_request` INT(11) NOT NULL AUTO_INCREMENT,
  `sender_id` INT(11) NOT NULL,
  `receiver_id` INT(11) NOT NULL,
  `sent_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_request`),
  UNIQUE INDEX `uq_friend_request` (`sender_id` ASC, `receiver_id` ASC),
  INDEX `fk_friend_request_receiver_idx` (`receiver_id` ASC),
  CONSTRAINT `fk_friend_request_sender`
    FOREIGN KEY (`sender_id`)
    REFERENCES `Lab5`.`user` (`id_user`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_friend_request_receiver`
    FOREIGN KEY (`receiver_id`)
    REFERENCES `Lab5`.`user` (`id_user`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `ck_friend_request_distinct` CHECK (`sender_id` <> `receiver_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

UPDATE `user` SET `password` = '$2b$12$OtZ3HXAnq6Zu.nAA0RHHTOol99j3AYtMHmrkMBfZY8fbxKZdunfWy' WHERE `id_user` = 10;
UPDATE `user` SET `password` = '$2b$12$T4q/b4rUkY5M/dwyknvfI.4QMY7HJBrZbXglxpRcXXjLfQO813RHa' WHERE `id_user` IN (1, 2, 3);

CREATE USER IF NOT EXISTS 'lab8_app'@'%' IDENTIFIED BY 'lucia123';
GRANT ALL PRIVILEGES ON `Lab5`.* TO 'lab8_app'@'%';
FLUSH PRIVILEGES;
