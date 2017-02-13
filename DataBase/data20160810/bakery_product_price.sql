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
-- Table structure for table `product_price`
--

DROP TABLE IF EXISTS `product_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_price` (
  `Production_id` int(11) NOT NULL,
  `Place_id` int(11) NOT NULL,
  `Price` float NOT NULL,
  PRIMARY KEY (`Production_id`,`Place_id`),
  KEY `fk_Production_has_Place_Place1_idx` (`Place_id`),
  KEY `fk_Production_has_Place_Production1_idx` (`Production_id`),
  CONSTRAINT `fk_Production_has_Place_Place1` FOREIGN KEY (`Place_id`) REFERENCES `place` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Production_has_Place_Production1` FOREIGN KEY (`Production_id`) REFERENCES `production` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_price`
--

LOCK TABLES `product_price` WRITE;
/*!40000 ALTER TABLE `product_price` DISABLE KEYS */;
INSERT INTO `product_price` VALUES (1,1,245),(1,2,0),(1,4,0),(1,5,0),(1,6,0),(2,1,0.04),(2,2,0),(2,4,0),(2,5,0),(2,6,0),(3,1,0.05),(3,2,0),(3,4,0),(3,5,0),(3,6,0),(4,1,0.04),(4,2,0),(4,4,0),(4,5,0),(4,6,0),(5,1,0.04),(5,2,0),(5,4,0),(5,5,0),(5,6,0),(6,1,55),(6,2,0),(6,4,0),(6,5,0),(6,6,0),(7,1,0),(7,2,0),(7,4,0),(7,5,0),(7,6,0),(8,1,0),(8,2,0),(8,4,0),(8,5,0),(8,6,0),(9,1,0),(9,2,0),(9,4,0),(9,5,0),(9,6,0),(10,1,0),(10,2,0),(10,4,0),(10,5,0),(10,6,0),(11,1,0),(11,2,0),(11,4,0),(11,5,0),(11,6,0),(12,1,0),(12,2,0),(12,4,0),(12,5,0),(12,6,0),(13,1,0),(13,2,0),(13,4,0),(13,5,0),(13,6,0),(14,1,0),(14,2,0),(14,4,0),(14,5,0),(14,6,0),(15,1,0),(15,2,0),(15,4,0),(15,5,0),(15,6,0),(16,1,0),(16,2,0),(16,4,0),(16,5,0),(16,6,0),(17,1,0),(17,2,0),(17,4,0),(17,5,0),(17,6,0),(18,1,0),(18,2,0),(18,4,0),(18,5,0),(18,6,0),(19,1,0),(19,2,0),(19,4,0),(19,5,0),(19,6,0),(20,1,0),(20,2,0),(20,4,0),(20,5,0),(20,6,0),(21,1,0),(21,2,0),(21,4,0),(21,5,0),(21,6,0),(22,1,0),(22,2,0),(22,4,0),(22,5,0),(22,6,0),(23,1,0),(23,2,0),(23,4,0),(23,5,0),(23,6,0),(24,1,0),(24,2,0),(24,4,0),(24,5,0),(24,6,0),(25,1,0),(25,2,0),(25,4,0),(25,5,0),(25,6,0),(26,1,0),(26,2,0),(26,4,0),(26,5,0),(26,6,0),(27,1,0),(27,2,0),(27,4,0),(27,5,0),(27,6,0),(28,1,0),(28,2,0),(28,4,0),(28,5,0),(28,6,0),(29,1,0),(29,2,0),(29,4,0),(29,5,0),(29,6,0),(30,1,0),(30,2,0),(30,4,0),(30,5,0),(30,6,0),(31,1,0),(31,2,0),(31,4,0),(31,5,0),(31,6,0),(32,1,0),(32,2,0),(32,4,0),(32,5,0),(32,6,0),(33,1,0),(33,2,0),(33,4,0),(33,5,0),(33,6,0),(34,1,0),(34,2,0),(34,4,0),(34,5,0),(34,6,0),(35,1,0),(35,2,0),(35,4,0),(35,5,0),(35,6,0),(36,1,0),(36,2,0),(36,4,0),(36,5,0),(36,6,0),(37,1,0),(37,2,0),(37,4,0),(37,5,0),(37,6,0),(38,1,0),(38,2,0),(38,4,0),(38,5,0),(38,6,0),(39,1,0),(39,2,0),(39,4,0),(39,5,0),(39,6,0),(40,1,0),(40,2,0),(40,4,0),(40,5,0),(40,6,0),(41,1,0),(41,2,0),(41,4,0),(41,5,0),(41,6,0),(42,1,0),(42,2,0),(42,4,0),(42,5,0),(42,6,0),(43,1,0),(43,2,0),(43,4,0),(43,5,0),(43,6,0),(44,1,0),(44,2,0),(44,4,0),(44,5,0),(44,6,0),(45,1,0),(45,2,0),(45,4,0),(45,5,0),(45,6,0),(46,1,0),(46,2,0),(46,4,0),(46,5,0),(46,6,0),(47,1,0),(47,2,0),(47,4,0),(47,5,0),(47,6,0),(48,1,0),(48,2,0),(48,4,0),(48,5,0),(48,6,0),(49,1,0),(49,2,0),(49,4,0),(49,5,0),(49,6,0),(50,1,0),(50,2,0),(50,4,0),(50,5,0),(50,6,0),(51,1,0),(51,2,0),(51,4,0),(51,5,0),(51,6,0),(52,1,0),(52,2,0),(52,4,0),(52,5,0),(52,6,0),(53,1,0),(53,2,0),(53,4,0),(53,5,0),(53,6,0),(54,1,0),(54,2,0),(54,4,0),(54,5,0),(54,6,0),(55,1,0),(55,2,0),(55,4,0),(55,5,0),(55,6,0),(56,1,0),(56,2,0),(56,4,0),(56,5,0),(56,6,0),(57,1,0),(57,2,0),(57,4,0),(57,5,0),(57,6,0),(58,1,0),(58,2,0),(58,4,0),(58,5,0),(58,6,0),(59,1,0),(59,2,0),(59,4,0),(59,5,0),(59,6,0),(60,1,0),(60,2,0),(60,4,0),(60,5,0),(60,6,0),(61,1,0),(61,2,0),(61,4,0),(61,5,0),(61,6,0),(62,1,0),(62,2,0),(62,4,0),(62,5,0),(62,6,0),(63,1,0),(63,2,0),(63,4,0),(63,5,0),(63,6,0),(64,1,0),(64,2,0),(64,4,0),(64,5,0),(64,6,0),(65,1,0),(65,2,0),(65,4,0),(65,5,0),(65,6,0),(66,1,0),(66,2,0),(66,4,0),(66,5,0),(66,6,0),(67,1,0),(67,2,0),(67,4,0),(67,5,0),(67,6,0),(68,1,0),(68,2,0),(68,4,0),(68,5,0),(68,6,0),(69,1,0),(69,2,0),(69,4,0),(69,5,0),(69,6,0),(70,1,0),(70,2,0),(70,4,0),(70,5,0),(70,6,0),(71,1,0),(71,2,0),(71,4,0),(71,5,0),(71,6,0),(72,1,0),(72,2,0),(72,4,0),(72,5,0),(72,6,0),(73,1,0),(73,2,0),(73,4,0),(73,5,0),(73,6,0),(74,1,0),(74,2,0),(74,4,0),(74,6,0),(76,1,4.46),(76,2,0),(76,4,0);
/*!40000 ALTER TABLE `product_price` ENABLE KEYS */;
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
