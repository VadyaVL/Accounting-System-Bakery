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
-- Table structure for table `produced`
--

DROP TABLE IF EXISTS `produced`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `produced` (
  `PRT_id` int(11) NOT NULL,
  `Production_id` int(11) NOT NULL,
  `Count` int(11) NOT NULL,
  PRIMARY KEY (`PRT_id`,`Production_id`),
  KEY `fk_TheProductionReportTeam_has_Production_Production1_idx` (`Production_id`),
  KEY `fk_TheProductionReportTeam_has_Production_TheProductionRepo_idx` (`PRT_id`),
  CONSTRAINT `fk_TheProductionReportTeam_has_Production_Production1` FOREIGN KEY (`Production_id`) REFERENCES `production` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_TheProductionReportTeam_has_Production_TheProductionReport1` FOREIGN KEY (`PRT_id`) REFERENCES `theproductionreportteam` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produced`
--

LOCK TABLES `produced` WRITE;
/*!40000 ALTER TABLE `produced` DISABLE KEYS */;
INSERT INTO `produced` VALUES (7,1,200),(7,2,3300),(7,3,0),(7,4,0),(7,5,0),(7,6,0),(7,7,0),(7,8,0),(7,9,100),(7,10,0),(7,11,6000),(7,12,500),(7,13,0),(7,14,0),(7,15,0),(7,16,0),(7,17,0),(7,18,0),(7,19,0),(7,20,0),(7,21,0),(7,22,0),(7,23,0),(7,24,0),(7,25,0),(7,26,0),(7,27,0),(7,28,0),(7,29,0),(7,30,0),(7,31,0),(7,32,0),(7,33,0),(7,34,0),(7,35,0),(7,36,0),(7,37,0),(7,38,0),(7,39,0),(7,40,0),(7,41,0),(7,42,0),(7,43,0),(7,44,0),(7,45,0),(7,46,0),(7,47,0),(7,48,0),(7,49,0),(7,50,0),(7,51,0),(7,52,0),(7,53,0),(7,54,0),(7,55,0),(7,56,0),(7,57,0),(7,58,0),(7,59,0),(7,60,0),(7,61,0),(7,62,0),(7,63,0),(7,64,0),(7,65,0),(7,66,0),(7,67,0),(7,68,0),(7,69,0),(7,70,0),(7,71,0),(7,72,0),(7,73,0),(7,74,0),(7,76,0),(8,1,200),(8,2,300),(8,3,0),(8,4,0),(8,5,0),(8,6,0),(8,7,0),(8,8,0),(8,9,0),(8,10,0),(8,11,0),(8,12,0),(8,13,0),(8,14,0),(8,15,0),(8,16,0),(8,17,0),(8,18,0),(8,19,0),(8,20,0),(8,21,0),(8,22,0),(8,23,0),(8,24,0),(8,25,0),(8,26,0),(8,27,0),(8,28,0),(8,29,0),(8,30,0),(8,31,0),(8,32,0),(8,33,0),(8,34,0),(8,35,0),(8,36,0),(8,37,0),(8,38,0),(8,39,0),(8,40,0),(8,41,0),(8,42,0),(8,43,0),(8,44,0),(8,45,0),(8,46,0),(8,47,0),(8,48,0),(8,49,0),(8,50,0),(8,51,0),(8,52,0),(8,53,0),(8,54,0),(8,55,0),(8,56,0),(8,57,0),(8,58,0),(8,59,0),(8,60,0),(8,61,0),(8,62,0),(8,63,0),(8,64,0),(8,65,0),(8,66,0),(8,67,0),(8,68,0),(8,69,0),(8,70,0),(8,71,0),(8,72,0),(8,73,0),(8,74,0),(8,76,0),(9,1,5),(9,2,0),(9,3,6),(9,4,0),(9,5,0),(9,6,0),(9,7,0),(9,8,0),(9,9,0),(9,10,0),(9,11,0),(9,12,0),(9,13,0),(9,14,0),(9,15,0),(9,16,0),(9,17,0),(9,18,0),(9,19,0),(9,20,0),(9,21,0),(9,22,0),(9,23,0),(9,24,0),(9,25,0),(9,26,0),(9,27,0),(9,28,0),(9,29,0),(9,30,0),(9,31,0),(9,32,0),(9,33,0),(9,34,0),(9,35,0),(9,36,0),(9,37,0),(9,38,0),(9,39,0),(9,40,0),(9,41,0),(9,42,0),(9,43,0),(9,44,0),(9,45,0),(9,46,0),(9,47,0),(9,48,0),(9,49,0),(9,50,0),(9,51,0),(9,52,0),(9,53,0),(9,54,0),(9,55,0),(9,56,0),(9,57,0),(9,58,0),(9,59,0),(9,60,0),(9,61,0),(9,62,0),(9,63,0),(9,64,0),(9,65,0),(9,66,0),(9,67,0),(9,68,0),(9,69,0),(9,70,0),(9,71,0),(9,72,0),(9,73,0),(9,74,0),(9,76,0),(10,1,1),(10,2,3),(10,3,5),(10,4,7),(10,5,0),(10,6,0),(10,7,0),(10,8,0),(10,9,0),(10,10,0),(10,11,0),(10,12,0),(10,13,0),(10,14,0),(10,15,0),(10,16,0),(10,17,0),(10,18,0),(10,19,0),(10,20,0),(10,21,0),(10,22,0),(10,23,0),(10,24,0),(10,25,0),(10,26,0),(10,27,0),(10,28,0),(10,29,0),(10,30,0),(10,31,0),(10,32,0),(10,33,0),(10,34,0),(10,35,0),(10,36,0),(10,37,0),(10,38,0),(10,39,0),(10,40,0),(10,41,0),(10,42,0),(10,43,0),(10,44,0),(10,45,0),(10,46,0),(10,47,0),(10,48,0),(10,49,0),(10,50,0),(10,51,0),(10,52,0),(10,53,0),(10,54,0),(10,55,0),(10,56,0),(10,57,0),(10,58,0),(10,59,0),(10,60,0),(10,61,0),(10,62,0),(10,63,0),(10,64,0),(10,65,0),(10,66,0),(10,67,0),(10,68,0),(10,69,0),(10,70,0),(10,71,0),(10,72,0),(10,73,0),(10,74,0),(10,76,0);
/*!40000 ALTER TABLE `produced` ENABLE KEYS */;
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
