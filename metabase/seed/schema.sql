
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `adoption_form_questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adoption_form_questions` (
  `adoption_form_id` binary(16) NOT NULL,
  `answer` varchar(1000) DEFAULT NULL,
  `question` varchar(500) NOT NULL,
  KEY `FK2aays6mt26qb9ohtyjcmxpunq` (`adoption_form_id`),
  CONSTRAINT `FK2aays6mt26qb9ohtyjcmxpunq` FOREIGN KEY (`adoption_form_id`) REFERENCES `adoption_forms` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `adoption_forms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adoption_forms` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `area_code` varchar(4) DEFAULT NULL,
  `phone_number` varchar(7) DEFAULT NULL,
  `adoption_post_id` binary(16) NOT NULL,
  `applicant_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtercjov7wtrib8e4o1weda7j` (`adoption_post_id`),
  KEY `FK5wr8qi1rl0l70jcnrblikr7dr` (`applicant_id`),
  CONSTRAINT `FK5wr8qi1rl0l70jcnrblikr7dr` FOREIGN KEY (`applicant_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKtercjov7wtrib8e4o1weda7j` FOREIGN KEY (`adoption_post_id`) REFERENCES `adoption_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `adoption_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adoption_post` (
  `id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKcer6850huxyj05anqvsvniif2` FOREIGN KEY (`id`) REFERENCES `animal_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `adoption_post_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adoption_post_status_history` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `status` enum('ADOPTED','CREATED','SEARCHING_ADOPT','SEARCHING_ADOPT_AND_TRANSIT') NOT NULL,
  `adoption_post_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjgiyw0vohy7q3hh6m6s0e81iu` (`adoption_post_id`),
  CONSTRAINT `FKjgiyw0vohy7q3hh6m6s0e81iu` FOREIGN KEY (`adoption_post_id`) REFERENCES `adoption_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `animal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal` (
  `id` binary(16) NOT NULL,
  `age` enum('ADULT','PUPPY','SENIOR','UNKNOWN') DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `gender` enum('FEMALE','MALE','UNKNOWN') DEFAULT NULL,
  `size` enum('LARGE','MEDIUM','SMALL') DEFAULT NULL,
  `type` enum('CAT','DOG','OTHER') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `animal_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `animal_post` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `area_code` varchar(4) DEFAULT NULL,
  `phone_number` varchar(7) DEFAULT NULL,
  `share_post_url` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `animal_id` binary(16) DEFAULT NULL,
  `location_id` binary(16) DEFAULT NULL,
  `owner_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8qx9fe29lg49el86a8g3808cq` (`animal_id`),
  KEY `FKa51qjbw8ys936vxygl30wupfp` (`location_id`),
  KEY `FK6yykopyt3o0kifnsr5fnfqshd` (`owner_id`),
  CONSTRAINT `FK6yykopyt3o0kifnsr5fnfqshd` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKa51qjbw8ys936vxygl30wupfp` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKeseh14kqh4upsamxf2jij96v7` FOREIGN KEY (`animal_id`) REFERENCES `animal` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `owner_id` binary(16) DEFAULT NULL,
  `post_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcjptd4mjai64kvah9b6cbquer` (`owner_id`),
  KEY `FKqy6c8r4m0fq60tdnn146v61v0` (`post_id`),
  CONSTRAINT `FKcjptd4mjai64kvah9b6cbquer` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqy6c8r4m0fq60tdnn146v61v0` FOREIGN KEY (`post_id`) REFERENCES `animal_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `donation_campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donation_campaign` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `image_id` varchar(255) DEFAULT NULL,
  `area_code` varchar(4) DEFAULT NULL,
  `phone_number` varchar(7) DEFAULT NULL,
  `share_campaign_url` varchar(255) DEFAULT NULL,
  `title` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `location_id` binary(16) DEFAULT NULL,
  `owner_id` binary(16) NOT NULL,
  `campaign_end_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmsn7x7f4utpeyymndjpqkpwvi` (`location_id`),
  KEY `FKnmmahwibix5pqbn7ni61vhcom` (`owner_id`),
  CONSTRAINT `FKmsn7x7f4utpeyymndjpqkpwvi` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FKnmmahwibix5pqbn7ni61vhcom` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `donation_campaign_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donation_campaign_status_history` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETED','CREATED','FINISHED') NOT NULL,
  `donation_campaign_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaqn8k0bluef34myc7e0s8e81e` (`donation_campaign_id`),
  CONSTRAINT `FKaqn8k0bluef34myc7e0s8e81e` FOREIGN KEY (`donation_campaign_id`) REFERENCES `donation_campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `donation_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donation_item` (
  `id` binary(16) NOT NULL,
  `category` enum('CLOTHING_AND_BLANKETS','FOOD','MEDICINE','OTHER','SHELTER_AND_BEDDING','TOYS_AND_ACCESSORIES') DEFAULT NULL,
  `is_completed` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `donation_campaign_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKopf9bvi5wfhxm8jj01ev5n7yy` (`donation_campaign_id`),
  CONSTRAINT `FKopf9bvi5wfhxm8jj01ev5n7yy` FOREIGN KEY (`donation_campaign_id`) REFERENCES `donation_campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `fundraising_campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fundraising_campaign` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `image_id` varchar(255) DEFAULT NULL,
  `area_code` varchar(4) DEFAULT NULL,
  `phone_number` varchar(7) DEFAULT NULL,
  `share_campaign_url` varchar(255) DEFAULT NULL,
  `title` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `location_id` binary(16) DEFAULT NULL,
  `owner_id` binary(16) NOT NULL,
  `account_alias` varchar(255) DEFAULT NULL,
  `amount_collected` bigint DEFAULT NULL,
  `amount_to_be_collected` bigint DEFAULT NULL,
  `campaign_end_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKda38xxybevvjf6w423gja5d5p` (`location_id`),
  KEY `FK8w4rkq2j1cebg9wfnuulbu71d` (`owner_id`),
  CONSTRAINT `FK8w4rkq2j1cebg9wfnuulbu71d` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKda38xxybevvjf6w423gja5d5p` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `fundraising_campaign_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fundraising_campaign_status_history` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETED','CREATED','FINISHED') NOT NULL,
  `fundraising_campaign_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKke2956mnm8b5q7k2t7p68dtaf` (`fundraising_campaign_id`),
  CONSTRAINT `FKke2956mnm8b5q7k2t7p68dtaf` FOREIGN KEY (`fundraising_campaign_id`) REFERENCES `fundraising_campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `location` (
  `id` binary(16) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `number` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `lost_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lost_post` (
  `has_owner` bit(1) NOT NULL,
  `reward` decimal(12,2) DEFAULT NULL,
  `id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKoyqowcj322ybnh4mtsvy09y76` FOREIGN KEY (`id`) REFERENCES `animal_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `lost_post_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lost_post_status_history` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `status` enum('CREATED','FOUND','RESCUED','SEARCHING','TO_RESCUE') NOT NULL,
  `lost_post_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKidp8l4nbkumifx5dtx06f4nbf` (`lost_post_id`),
  CONSTRAINT `FKidp8l4nbkumifx5dtx06f4nbf` FOREIGN KEY (`lost_post_id`) REFERENCES `lost_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `news_campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_campaign` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `image_id` varchar(255) DEFAULT NULL,
  `area_code` varchar(4) DEFAULT NULL,
  `phone_number` varchar(7) DEFAULT NULL,
  `share_campaign_url` varchar(255) DEFAULT NULL,
  `title` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `location_id` binary(16) DEFAULT NULL,
  `owner_id` binary(16) NOT NULL,
  `category` enum('CASTRATION','DEWORMING','OTHER','VACCINATION') DEFAULT NULL,
  `news_end_date_time` datetime(6) DEFAULT NULL,
  `news_start_date_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrxsd34evsa83wuwgdbvrceot7` (`location_id`),
  KEY `FKnrq77d7q61nicj2nskynr0phk` (`owner_id`),
  CONSTRAINT `FKnrq77d7q61nicj2nskynr0phk` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrxsd34evsa83wuwgdbvrceot7` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `news_campaign_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_campaign_status_history` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `status` enum('CREATED','FINISHED','STARTED') NOT NULL,
  `news_campaign_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7pq5nynj99rvx43xdraqql4ol` (`news_campaign_id`),
  CONSTRAINT `FK7pq5nynj99rvx43xdraqql4ol` FOREIGN KEY (`news_campaign_id`) REFERENCES `news_campaign` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `redirect_to` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `type` enum('FOUND_PET','IN_ADOPTION_AND_TRANSIT_PET','IN_ADOPTION_PET','LOST_PET','NEW_CARRIAGE_REQUEST','NEW_CASTRATION_CAMPAIGN','NEW_DONATION_CAMPAIGN','NEW_FUNDRAISING_CAMPAIGN','NEW_VACCINATION_CAMPAIGN','PING') DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKok189w3y2apfun7d0p16w8lnb` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `notification_delivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_delivery` (
  `id` binary(16) NOT NULL,
  `channel` enum('EMAIL','IN_APP','PUSH') NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `redirect_to` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `notification_id` binary(16) NOT NULL,
  `recipient_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9lrx02ouc003xoktuuvlqf15a` (`notification_id`),
  KEY `FK4n1c20tkmi8fxcxlabrlbil88` (`recipient_id`),
  CONSTRAINT `FK4n1c20tkmi8fxcxlabrlbil88` FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK9lrx02ouc003xoktuuvlqf15a` FOREIGN KEY (`notification_id`) REFERENCES `notification` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `notification_preference`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_preference` (
  `id` binary(16) NOT NULL,
  `type` tinyint DEFAULT NULL,
  `profile_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK596r5de21m6fnua2enirpdpu7` (`profile_id`),
  CONSTRAINT `FK596r5de21m6fnua2enirpdpu7` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`),
  CONSTRAINT `notification_preference_chk_1` CHECK ((`type` between 0 and 9))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `notification_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_status_history` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `finished_at` datetime(6) DEFAULT NULL,
  `status` enum('FAILED','PENDING','READ','SENT') NOT NULL,
  `notification_delivery_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgfrxjtbgc3pj4v9608gf5yphh` (`notification_delivery_id`),
  CONSTRAINT `FKgfrxjtbgc3pj4v9608gf5yphh` FOREIGN KEY (`notification_delivery_id`) REFERENCES `notification_delivery` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `password_recovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_recovery` (
  `id` binary(16) NOT NULL,
  `attempts` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `reset_token_expires_at` datetime(6) DEFAULT NULL,
  `revoked_at` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','REVOKED','USED','VERIFIED') NOT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  `verification_code` varchar(255) NOT NULL,
  `verified_at` datetime(6) DEFAULT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKjepn2gegckk38c7f6rn6f5wbf` (`reset_token`),
  KEY `FKe8rvirgchpmurh9y9sq1rkxsd` (`user_id`),
  CONSTRAINT `FKe8rvirgchpmurh9y9sq1rkxsd` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile` (
  `id` binary(16) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `area_code` varchar(4) DEFAULT NULL,
  `phone_number` varchar(7) DEFAULT NULL,
  `profile_imageurl` varchar(255) DEFAULT NULL,
  `user_notificationurl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9d5dpsf2ufa6rjbi3y0elkdcd` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `profile_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profile_roles` (
  `profile_id` binary(16) NOT NULL,
  `role` enum('CARRIAGE','COMMUNITY','RESCUER','TRANSITIONAL_HOME','VET') NOT NULL,
  PRIMARY KEY (`profile_id`,`role`),
  CONSTRAINT `FKgt2d29hgt9t816v5hm9u56h1d` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `push_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `push_subscription` (
  `id` binary(16) NOT NULL,
  `auth` varchar(512) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `endpoint` varchar(2048) NOT NULL,
  `endpoint_hash` binary(32) NOT NULL,
  `p256dh` varchar(512) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` binary(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2dcsa786daq4py4wn3094cr60` (`endpoint_hash`),
  KEY `FK5elt6885kaqe25vrlueb6w0v2` (`user_id`),
  CONSTRAINT `FK5elt6885kaqe25vrlueb6w0v2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `reaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reaction` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `type` tinyint DEFAULT NULL,
  `owner_id` binary(16) DEFAULT NULL,
  `post_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7qw0yftlo76h7jwrkdynu8t7j` (`owner_id`),
  KEY `FKlo2f4lkgmfi3kvxefh20e7g22` (`post_id`),
  CONSTRAINT `FK7qw0yftlo76h7jwrkdynu8t7j` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKlo2f4lkgmfi3kvxefh20e7g22` FOREIGN KEY (`post_id`) REFERENCES `animal_post` (`id`),
  CONSTRAINT `reaction_chk_1` CHECK ((`type` between 0 and 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `id` binary(16) NOT NULL,
  `closing_time` time DEFAULT NULL,
  `day_of_week` enum('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') DEFAULT NULL,
  `opening_time` time DEFAULT NULL,
  `vet_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr84mqpaa4taxow4rd5xhlihx7` (`vet_id`),
  CONSTRAINT `FKr84mqpaa4taxow4rd5xhlihx7` FOREIGN KEY (`vet_id`) REFERENCES `vet_information` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` binary(16) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `profile_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK7s5nlreekaxdbfml4ofky7utw` (`profile_id`),
  CONSTRAINT `FK5q3e9303ap1wvtia6sft7ht1s` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `vet_information`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vet_information` (
  `id` binary(16) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `area_code` varchar(4) DEFAULT NULL,
  `phone_number` varchar(7) DEFAULT NULL,
  `profile_picture_url` varchar(255) DEFAULT NULL,
  `vet_page_url` varchar(255) DEFAULT NULL,
  `location_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7rifkdypfcntmm4e14h7mfscd` (`location_id`),
  CONSTRAINT `FK7rifkdypfcntmm4e14h7mfscd` FOREIGN KEY (`location_id`) REFERENCES `location` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

