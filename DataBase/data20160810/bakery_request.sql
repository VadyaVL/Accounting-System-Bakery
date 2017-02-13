-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: bakery
-- ------------------------------------------------------
-- Server version	5.6.31-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `request`
--

DROP TABLE IF EXISTS `request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `request` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Client_id` int(11) NOT NULL,
  `Date_Request` datetime NOT NULL,
  `Date_Oformleniya` datetime NOT NULL,
  `OK` tinyint(1) NOT NULL,
  `User_id` int(11) NOT NULL,
  PRIMARY KEY (`id`,`User_id`),
  KEY `fk_Client_has_Production_Client_idx` (`Client_id`),
  KEY `fk_Request_User1_idx` (`User_id`),
  CONSTRAINT `fk_Client_has_Production_Client` FOREIGN KEY (`Client_id`) REFERENCES `client` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Request_User1` FOREIGN KEY (`User_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request`
--

LOCK TABLES `request` WRITE;
/*!40000 ALTER TABLE `request` DISABLE KEYS */;
INSERT INTO `request` VALUES (1,1,'2016-08-09 00:00:00','2016-08-10 01:09:58',0,8),(2,1,'2016-08-09 15:41:44','2016-08-09 15:41:53',0,1),(3,4,'2016-08-10 00:00:00','2016-08-09 15:50:33',0,1),(4,2,'2016-08-12 15:51:41','2016-08-09 15:51:45',0,1),(6,4,'2016-08-19 15:55:07','2016-08-09 15:55:11',0,1),(7,4,'2016-08-13 15:56:26','2016-08-09 15:56:33',0,1),(8,1,'2016-08-13 15:59:18','2016-08-09 15:59:22',0,1),(9,1,'2016-08-14 00:00:00','2016-08-09 15:59:30',0,1),(10,1,'2016-08-14 16:01:18','2016-08-09 16:01:20',0,1),(11,1,'2016-08-14 00:00:00','2016-08-09 16:02:32',0,1),(12,1,'2016-07-08 16:02:44','2016-08-10 01:10:18',0,8),(14,1,'2013-08-10 16:14:39','2016-08-09 16:14:44',0,1),(15,1,'2011-08-14 16:15:11','2016-08-09 16:15:15',0,1),(16,4,'2016-08-31 16:19:54','2016-08-09 16:20:00',0,1),(18,1,'2016-08-20 16:23:42','2016-08-09 16:23:43',0,1),(19,1,'2016-08-18 16:27:23','2016-08-09 16:27:25',0,1),(20,1,'2016-08-01 16:28:27','2016-08-09 16:28:31',0,1),(21,2,'2016-08-11 16:28:40','2016-08-09 17:40:07',0,1),(22,2,'2016-08-12 16:28:59','2016-08-09 16:29:04',0,1),(23,4,'2008-08-02 16:29:11','2016-08-09 16:29:17',0,1),(27,1,'2016-08-09 17:18:00','2016-08-09 20:38:39',1,1),(29,2,'2016-08-11 00:23:28','2016-08-10 00:23:33',0,1),(30,2,'2016-08-10 01:11:11','2016-08-10 01:11:33',1,8);
/*!40000 ALTER TABLE `request` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-10 14:57:59
