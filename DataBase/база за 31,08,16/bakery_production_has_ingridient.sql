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
-- Table structure for table `production_has_ingridient`
--

DROP TABLE IF EXISTS `production_has_ingridient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `production_has_ingridient` (
  `Production_id` int(11) NOT NULL,
  `Ingridient_id` int(11) NOT NULL,
  `Count` float NOT NULL,
  PRIMARY KEY (`Production_id`,`Ingridient_id`),
  KEY `fk_Production_has_Ingridient_Ingridient1_idx` (`Ingridient_id`),
  KEY `fk_Production_has_Ingridient_Production1_idx` (`Production_id`),
  CONSTRAINT `fk_Production_has_Ingridient_Ingridient1` FOREIGN KEY (`Ingridient_id`) REFERENCES `ingridient` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Production_has_Ingridient_Production1` FOREIGN KEY (`Production_id`) REFERENCES `production` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `production_has_ingridient`
--

LOCK TABLES `production_has_ingridient` WRITE;
/*!40000 ALTER TABLE `production_has_ingridient` DISABLE KEYS */;
INSERT INTO `production_has_ingridient` VALUES (1,1,0.45),(2,1,0),(3,1,0),(4,1,0),(5,1,0),(6,1,0),(7,1,0),(8,1,0),(9,1,0),(10,1,0),(11,1,0),(12,1,0),(13,1,0),(14,1,0),(15,1,0),(16,1,0),(17,1,0),(18,1,0),(19,1,0),(20,1,0),(21,1,0),(22,1,0),(23,1,0),(24,1,0),(25,1,0),(26,1,0),(27,1,0),(28,1,0),(29,1,0),(30,1,0),(31,1,0),(32,1,0),(33,1,0),(34,1,0),(35,1,0),(36,1,0),(37,1,0),(38,1,0),(39,1,0),(40,1,0),(41,1,0),(42,1,0),(43,1,0),(44,1,0),(45,1,0),(46,1,0),(47,1,0),(48,1,0),(49,1,0),(50,1,0),(51,1,0),(52,1,0),(53,1,0),(54,1,0),(55,1,0),(56,1,0),(57,1,0),(58,1,0),(59,1,0),(60,1,0),(61,1,0),(62,1,0),(63,1,0),(64,1,0),(65,1,0),(66,1,0),(67,1,0),(68,1,0),(69,1,0),(70,1,0),(71,1,0),(72,1,0),(73,1,0),(74,1,0),(75,1,0),(76,1,0),(77,1,0),(78,1,0),(79,1,0),(80,1,0),(81,1,0),(82,1,0),(83,1,0),(84,1,0),(85,1,0),(86,1,0),(87,1,0),(88,1,0),(89,1,0),(90,1,0),(91,1,0),(92,1,0),(93,1,0),(94,1,0),(95,1,0),(96,1,0),(97,1,0),(98,1,0),(99,1,0),(100,1,0),(101,1,0),(102,1,0),(103,1,0),(104,1,0),(105,1,0),(106,1,0),(107,1,0),(108,1,0),(109,1,0),(110,1,0),(111,1,0),(112,1,0),(113,1,0),(114,1,0),(115,1,0),(116,1,0),(117,1,0),(118,1,0),(119,1,0),(120,1,0),(121,1,0),(122,1,0),(123,1,0),(124,1,0),(125,1,0),(126,1,0),(127,1,0),(128,1,0),(129,1,0),(130,1,0),(131,1,0),(132,1,0),(133,1,0),(134,1,0),(135,1,0),(136,1,0);
/*!40000 ALTER TABLE `production_has_ingridient` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-31 13:27:28
