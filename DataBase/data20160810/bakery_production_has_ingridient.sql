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
INSERT INTO `production_has_ingridient` VALUES (1,1,0.01),(1,2,0.02),(1,3,0.03),(1,5,0.04),(1,6,0.05),(2,1,0.02),(2,2,0.06),(2,3,0.07),(2,5,0.08),(2,6,0.09),(2,7,0.07),(6,1,0),(6,2,0),(6,3,0),(6,5,0),(6,6,0),(74,1,0.06),(74,2,0.06),(74,3,0.06),(74,5,0.06),(74,6,0.06),(76,1,0.04),(76,2,0.05),(76,3,0.06),(76,5,0.07),(76,6,0.08);
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

-- Dump completed on 2016-08-10 14:57:59
