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
-- Table structure for table `production`
--

DROP TABLE IF EXISTS `production`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `production` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(250) NOT NULL,
  `CountOnStorage` float NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `production`
--

LOCK TABLES `production` WRITE;
/*!40000 ALTER TABLE `production` DISABLE KEYS */;
INSERT INTO `production` VALUES (1,'Хліб пшеничний білий 0,8кг',0.014),(2,'Хліб пшеничний формовий 0,5',0),(3,'ХЛІБ 0,7',0),(4,'Хліб домашній 0,9',0),(5,'Хліб арнаут Київський 0,6',0),(6,'хліб арнаут різаний 0,6',0),(7,'Хліб арнаут 0,6 БАБ',0),(8,'ХЛІБ КУКУРУДЗЯНИЙ',0),(9,'ЗДОРОВЯ',0),(10,'Хліб білоруський 0,5 різан',0),(11,'Хліб рівенський круглий різаний',0),(12,'Хліб Рівенський  КРУГ',0),(13,'Хліб Рівенський з кмином 0,65 р',0),(14,'Хліб Висівковий 0,4',0),(15,'Хліб висівковий 0,4 різан',0),(16,'Багет висівковий 0,2',0),(17,'Ріжок висівковий 0,1',0),(18,'Хліб український 0,9',0),(19,'Хліб український 1,0(форм)',0),(20,'Хліб Діамант-шампань 0,4різ',0),(21,'Хліб Колосок 0,8',0),(22,'Хліб Мозаїка 0,4різ',0),(23,'Батон Дорожній 0,45',0),(24,'Батон Дорожній 0,45 різ',0),(25,'Пампушка  0,550',0),(26,'Пампушка з часником 0,3',0),(27,'Плетінка здобна 0,45',0),(28,'Плетінка здобна 0,65',0),(29,'Калач 0,4  МИК',0),(30,'Ріжок з повидлом 0,4',0),(31,'Булка Сімейка 0,4',0),(32,'Заготовка для піци 0,150',0),(33,'Хліб житн пшеничний 0,45',0),(34,'Хліб заварний 0,7',0),(35,'Хліб заварний 0,45',0),(36,'Булочка звичайна 0,1',0),(37,'Булочка з повидлом ,0,1',0),(38,'Булочка з маком 0,1',0),(39,'Плюшка 0,1',0),(40,'Плюшка формова 0,4',0),(41,'Бублик з маком 0,1',0),(42,'Ватрушки 0,075',0),(43,'ВАТРУШКА 0,100',0),(44,'Лапки',0),(45,'КОКОС',0),(46,'Пиріжки 0,1 кап,яб,мак.карт',0),(47,'Пиріжки з маком 0,075',0),(48,'Пиріжки з  капустою 0,075',0),(49,'ПИРІЖКИ з яблуками 0,075',0),(50,'Пироги з маком 0,45',0),(51,'ПИРОГИ З повидлом 0,45',0),(52,'КРЕНДЕЛЬ МАК',0),(53,'Крендель  вишня',0),(54,'ПИРІГ  ТВОРОГ+АБРИКОС',0),(55,'МАЛЬВА 0,400',0),(56,'ПИРІЖКИ У ФОРМІ',0),(57,'КЕКС 0,075',0),(58,'КЕКС ЛІТО ',0),(59,'ВАГОВА',0),(60,'Піца з ковбасою 0,120',0),(61,'БАГЕТ 0,3',0),(62,'ШИШКИ',0),(63,'ЗАВИТОК',0),(64,'СОЛОНІ ПАЛ ',0),(65,'М\'ЯСО',0),(66,'КОТЛЕТА',0),(67,'Булочка з яблуком ікорицею',0),(68,'СЛИВА',0),(69,'ВИШНЯ',0),(70,'КЕКС  1,0',0),(71,'ПОДІЛЬСЬКА',0),(72,'МАЛЯТКО  ПО  9 ШТ',0),(73,'КОТЛЕТА МАЛЕНЬКА',0),(74,'тест',0.04),(76,'Новинка 2015',4);
/*!40000 ALTER TABLE `production` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-10 14:58:02
