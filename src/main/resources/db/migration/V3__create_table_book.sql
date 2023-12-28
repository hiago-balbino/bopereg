CREATE TABLE IF NOT EXISTS `book` (
    `id` INT(10) AUTO_INCREMENT PRIMARY KEY,
    `author` longtext,
    `title` longtext,
    `price` decimal(65,2) NOT NULL,
    `launch_date` datetime(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;