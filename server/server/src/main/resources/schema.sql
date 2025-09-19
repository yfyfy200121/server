-- Schema initialization for H2 database
SET MODE MySQL;

-- Table structure for b_classification
CREATE TABLE IF NOT EXISTS `b_classification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_thing
CREATE TABLE IF NOT EXISTS `b_thing` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `cover` varchar(255) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `price` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `repertory` varchar(255) DEFAULT NULL,
  `score` varchar(255) DEFAULT NULL,
  `pv` varchar(255) DEFAULT NULL,
  `recommend_count` varchar(255) DEFAULT NULL,
  `wish_count` varchar(255) DEFAULT NULL,
  `collect_count` varchar(255) DEFAULT NULL,
  `classification_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_user
CREATE TABLE IF NOT EXISTS `b_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `push_email` varchar(255) DEFAULT NULL,
  `push_switch` tinyint DEFAULT NULL,
  `admin_token` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_comment
CREATE TABLE IF NOT EXISTS `b_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(1000) DEFAULT NULL,
  `comment_time` varchar(255) DEFAULT NULL,
  `like_count` int DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `thing_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_order
CREATE TABLE IF NOT EXISTS `b_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `order_time` varchar(255) DEFAULT NULL,
  `receiver_name` varchar(255) DEFAULT NULL,
  `receiver_address` varchar(255) DEFAULT NULL,
  `receiver_phone` varchar(255) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_thing_wish
CREATE TABLE IF NOT EXISTS `b_thing_wish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `thing_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_thing_collect
CREATE TABLE IF NOT EXISTS `b_thing_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `thing_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_ad
CREATE TABLE IF NOT EXISTS `b_ad` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image` varchar(255) DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_notice
CREATE TABLE IF NOT EXISTS `b_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_op_log
CREATE TABLE IF NOT EXISTS `b_op_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `re_ip` varchar(255) DEFAULT NULL,
  `re_time` varchar(255) DEFAULT NULL,
  `re_ua` varchar(500) DEFAULT NULL,
  `re_url` varchar(255) DEFAULT NULL,
  `re_method` varchar(255) DEFAULT NULL,
  `access_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_error_log
CREATE TABLE IF NOT EXISTS `b_error_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) DEFAULT NULL,
  `error_time` varchar(255) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `error_detail` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_tag
CREATE TABLE IF NOT EXISTS `b_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_thing_tag
CREATE TABLE IF NOT EXISTS `b_thing_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `thing_id` bigint DEFAULT NULL,
  `tag_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_address
CREATE TABLE IF NOT EXISTS `b_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `def` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_banner
CREATE TABLE IF NOT EXISTS `b_banner` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `thing_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_overview
CREATE TABLE IF NOT EXISTS `b_overview` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `count` bigint DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_order_log
CREATE TABLE IF NOT EXISTS `b_order_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_record
CREATE TABLE IF NOT EXISTS `b_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `thing_id` bigint DEFAULT NULL,
  `score` bigint DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- Table structure for b_login_log
CREATE TABLE IF NOT EXISTS `b_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `user_agent` varchar(500) DEFAULT NULL,
  `log_time` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);