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
-- Table structure for table `brak`
--

DROP TABLE IF EXISTS `brak`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `brak` (
  `Production_id` int(11) NOT NULL,
  `PRT_id` int(11) NOT NULL,
  `Count` int(11) NOT NULL,
  PRIMARY KEY (`Production_id`,`PRT_id`),
  KEY `fk_Production_has_TheProductionReportTeam_TheProductionRepo_idx` (`PRT_id`),
  KEY `fk_Production_has_TheProductionReportTeam_Production1_idx` (`Production_id`),
  CONSTRAINT `fk_Production_has_TheProductionReportTeam_Production1` FOREIGN KEY (`Production_id`) REFERENCES `production` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Production_has_TheProductionReportTeam_TheProductionReport1` FOREIGN KEY (`PRT_id`) REFERENCES `theproductionreportteam` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brak`
--

LOCK TABLES `brak` WRITE;
/*!40000 ALTER TABLE `brak` DISABLE KEYS */;
INSERT INTO `brak` VALUES (1,7,5),(1,8,5),(1,9,5),(1,10,2),(2,7,20),(2,8,4),(2,9,0),(2,10,4),(3,7,0),(3,8,0),(3,9,6),(3,10,6),(4,7,0),(4,8,0),(4,9,0),(4,10,8),(5,7,0),(5,8,0),(5,9,0),(5,10,0),(6,7,0),(6,8,0),(6,9,0),(6,10,0),(7,7,0),(7,8,0),(7,9,0),(7,10,0),(8,7,0),(8,8,0),(8,9,0),(8,10,0),(9,7,7),(9,8,0),(9,9,0),(9,10,0),(10,7,0),(10,8,0),(10,9,0),(10,10,0),(11,7,9),(11,8,0),(11,9,0),(11,10,0),(12,7,8),(12,8,0),(12,9,0),(12,10,0),(13,7,0),(13,8,0),(13,9,0),(13,10,0),(14,7,0),(14,8,0),(14,9,0),(14,10,0),(15,7,0),(15,8,0),(15,9,0),(15,10,0),(16,7,0),(16,8,0),(16,9,0),(16,10,0),(17,7,0),(17,8,0),(17,9,0),(17,10,0),(18,7,0),(18,8,0),(18,9,0),(18,10,0),(19,7,0),(19,8,0),(19,9,0),(19,10,0),(20,7,0),(20,8,0),(20,9,0),(20,10,0),(21,7,0),(21,8,0),(21,9,0),(21,10,0),(22,7,0),(22,8,0),(22,9,0),(22,10,0),(23,7,0),(23,8,0),(23,9,0),(23,10,0),(24,7,0),(24,8,0),(24,9,0),(24,10,0),(25,7,0),(25,8,0),(25,9,0),(25,10,0),(26,7,0),(26,8,0),(26,9,0),(26,10,0),(27,7,0),(27,8,0),(27,9,0),(27,10,0),(28,7,0),(28,8,0),(28,9,0),(28,10,0),(29,7,0),(29,8,0),(29,9,0),(29,10,0),(30,7,0),(30,8,0),(30,9,0),(30,10,0),(31,7,0),(31,8,0),(31,9,0),(31,10,0),(32,7,0),(32,8,0),(32,9,0),(32,10,0),(33,7,0),(33,8,0),(33,9,0),(33,10,0),(34,7,0),(34,8,0),(34,9,0),(34,10,0),(35,7,0),(35,8,0),(35,9,0),(35,10,0),(36,7,0),(36,8,0),(36,9,0),(36,10,0),(37,7,0),(37,8,0),(37,9,0),(37,10,0),(38,7,0),(38,8,0),(38,9,0),(38,10,0),(39,7,0),(39,8,0),(39,9,0),(39,10,0),(40,7,0),(40,8,0),(40,9,0),(40,10,0),(41,7,0),(41,8,0),(41,9,0),(41,10,0),(42,7,0),(42,8,0),(42,9,0),(42,10,0),(43,7,0),(43,8,0),(43,9,0),(43,10,0),(44,7,0),(44,8,0),(44,9,0),(44,10,0),(45,7,0),(45,8,0),(45,9,0),(45,10,0),(46,7,0),(46,8,0),(46,9,0),(46,10,0),(47,7,0),(47,8,0),(47,9,0),(47,10,0),(48,7,0),(48,8,0),(48,9,0),(48,10,0),(49,7,0),(49,8,0),(49,9,0),(49,10,0),(50,7,0),(50,8,0),(50,9,0),(50,10,0),(51,7,0),(51,8,0),(51,9,0),(51,10,0),(52,7,0),(52,8,0),(52,9,0),(52,10,0),(53,7,0),(53,8,0),(53,9,0),(53,10,0),(54,7,0),(54,8,0),(54,9,0),(54,10,0),(55,7,0),(55,8,0),(55,9,0),(55,10,0),(56,7,0),(56,8,0),(56,9,0),(56,10,0),(57,7,0),(57,8,0),(57,9,0),(57,10,0),(58,7,0),(58,8,0),(58,9,0),(58,10,0),(59,7,0),(59,8,0),(59,9,0),(59,10,0),(60,7,0),(60,8,0),(60,9,0),(60,10,0),(61,7,0),(61,8,0),(61,9,0),(61,10,0),(62,7,0),(62,8,0),(62,9,0),(62,10,0),(63,7,0),(63,8,0),(63,9,0),(63,10,0),(64,7,0),(64,8,0),(64,9,0),(64,10,0),(65,7,0),(65,8,0),(65,9,0),(65,10,0),(66,7,0),(66,8,0),(66,9,0),(66,10,0),(67,7,0),(67,8,0),(67,9,0),(67,10,0),(68,7,0),(68,8,0),(68,9,0),(68,10,0),(69,7,0),(69,8,0),(69,9,0),(69,10,0),(70,7,0),(70,8,0),(70,9,0),(70,10,0),(71,7,0),(71,8,0),(71,9,0),(71,10,0),(72,7,0),(72,8,0),(72,9,0),(72,10,0),(73,7,0),(73,8,0),(73,9,0),(73,10,0),(74,7,0),(74,8,0),(74,9,0),(74,10,0),(76,7,0),(76,8,0),(76,9,0),(76,10,0);
/*!40000 ALTER TABLE `brak` ENABLE KEYS */;
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
