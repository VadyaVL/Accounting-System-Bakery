-- MySQL dump 10.13  Distrib 5.7.12, for Win32 (AMD64)
--
-- Host: localhost    Database: bakery
-- ------------------------------------------------------
-- Server version	5.7.14-log

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
-- Table structure for table `theproductionreportteam`
--

DROP TABLE IF EXISTS `theproductionreportteam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `theproductionreportteam` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `User_id` int(11) NOT NULL,
  `Datetime` datetime NOT NULL,
  PRIMARY KEY (`id`,`User_id`),
  KEY `fk_TheProductionReportTeam_User1_idx` (`User_id`),
  CONSTRAINT `fk_TheProductionReportTeam_User1` FOREIGN KEY (`User_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `theproductionreportteam`
--

LOCK TABLES `theproductionreportteam` WRITE;
/*!40000 ALTER TABLE `theproductionreportteam` DISABLE KEYS */;
INSERT INTO `theproductionreportteam` VALUES (1,1,'2016-08-22 19:57:30'),(2,1,'2016-08-23 18:57:08'),(3,2,'2016-08-24 11:40:47'),(4,4,'2016-08-24 19:43:40'),(5,4,'2016-08-25 12:58:47'),(6,4,'2016-08-25 13:16:38'),(7,4,'2016-08-25 14:40:09'),(8,3,'2016-08-25 19:38:33'),(9,4,'2016-08-26 11:48:34'),(10,4,'2016-08-26 20:22:06'),(11,4,'2016-08-27 18:30:14'),(12,4,'2016-08-27 18:35:07'),(13,3,'2016-08-28 19:01:40'),(14,3,'2016-08-28 19:14:48'),(15,3,'2016-08-28 19:54:16'),(16,5,'2016-08-29 19:19:53'),(17,5,'2016-08-29 19:24:56'),(18,4,'2016-08-30 19:31:24'),(19,4,'2016-08-30 20:03:52'),(20,4,'2016-08-30 20:10:09');
/*!40000 ALTER TABLE `theproductionreportteam` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-31 13:27:31
