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
-- Table structure for table `request_has_production`
--

DROP TABLE IF EXISTS `request_has_production`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `request_has_production` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Request_id` int(11) NOT NULL,
  `Production_id` int(11) NOT NULL,
  `Count` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Request_has_Production_Production1_idx` (`Production_id`),
  KEY `fk_Request_has_Production_Request1_idx` (`Request_id`),
  CONSTRAINT `fk_Request_has_Production_Production1` FOREIGN KEY (`Production_id`) REFERENCES `production` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Request_has_Production_Request1` FOREIGN KEY (`Request_id`) REFERENCES `request` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_has_production`
--

LOCK TABLES `request_has_production` WRITE;
/*!40000 ALTER TABLE `request_has_production` DISABLE KEYS */;
INSERT INTO `request_has_production` VALUES (1,2,1,1),(2,2,2,2),(3,2,3,4),(4,3,1,7),(5,3,2,5),(6,3,3,6),(7,4,1,4),(8,4,2,5),(10,7,1,1),(33,21,1,54),(39,27,1,100),(40,27,2,200),(41,27,3,300),(42,27,4,400),(43,27,5,500),(44,29,1,157),(45,1,1,2),(46,1,2,5),(47,1,3,7),(48,1,4,7),(49,1,5,7),(50,1,6,7),(51,1,7,54),(52,1,8,5),(53,1,9,5),(54,12,1,1),(55,12,2,4),(61,30,1,10),(62,30,2,15),(63,30,3,25),(64,30,4,30),(65,30,5,45),(66,30,6,60);
/*!40000 ALTER TABLE `request_has_production` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-10 14:58:00
