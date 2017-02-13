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
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `client` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(200) NOT NULL,
  `Place_id` int(11) NOT NULL,
  PRIMARY KEY (`id`,`Place_id`),
  KEY `fk_Client_Place1_idx` (`Place_id`),
  CONSTRAINT `fk_Client_Place1` FOREIGN KEY (`Place_id`) REFERENCES `place` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES (1,'\"Кузьменко\"',1),(2,'Тутченко',1),(3,'Магазин Бам',1),(4,'Шевченко',4),(5,'Подорожня',4),(6,'Магазин Ірина',4),(7,'Люда Базар',1),(8,'Магазин Лідія',1),(9,'Бусол',2),(10,'Сенявське-Першотравневе',2),(11,'Пономаренко',2),(12,'Калинівка Боровик',2),(13,'Калинівка Синявське',2),(14,'Мухіна Сільпо№1',3),(15,'Мухіна Сільпо№2',3),(16,'Піонерська Базар',3),(17,'Свіжий хліб',3),(18,'Арка11111',3),(19,'Мухіна Торговий Центр',3),(20,'Курсова',3),(21,'Магазин Снайпер',3),(22,'Жуковський',3),(23,'Леван Базар',3),(24,'Посьолок ',3),(25,'Піонерська',3),(26,'Таращанська',3),(27,'Критий ринок',3),(28,'Некрасова №2',3),(29,'Вокзальна',3),(30,'Супрун',3),(31,'Курочкіна',3),(32,'Магазин Юліана',3),(33,'Село Поправка',3),(34,'100 Метровка',3),(35,'Некрасова 99',3),(36,'Торговий Центр',3),(37,'Магазин Рось',3),(38,'Магазин Ваді',4),(39,'Воловик',1),(40,'Кагарлик Ринок',5),(41,'Фастов',9),(42,'Каменотрус',9),(43,'Валя Ніч',9),(44,'Магазин Центр',6),(45,'Магазин Пекарня',6),(46,'Агрофірма',11),(47,'Колгосп Нива',11),(48,'Дитячий будинок Світанок',11),(49,'Колгосп Росія ',11),(50,'Чебурашка',11),(51,'Дитсад. Насташка',11),(52,'Дитсад.Остров',11),(53,'Антон',9),(54,'Синявський',9),(55,'Тесленко',13),(56,'Тимофій',12),(57,'Сущев',12),(58,'Ватутіно',12),(59,'Рухаленко',12),(60,'Григоренко',12),(61,'Соловйов',12),(62,'Звенигородка',12),(63,'Кривенкова',12),(64,'Городня',12),(65,'Будова',12),(66,'Кусливий',12),(67,'Тетіїв',12),(68,'Салиха',12),(69,'Левада',12),(70,'Зайцева',7),(71,'Куручекова',7),(72,'Сидорчук 2',7),(73,'Василик',7),(74,'Гриценко',7),(75,'Зайцева 2',7),(76,'Румянцева',7),(77,'Шевченко',7),(78,'Соловей',7),(79,'Солдатенко',7),(80,'Сидорчук 1',7),(81,'Мілікян',7),(82,'Юхимець',7),(83,'Куцуляк',7),(84,'Богуслав',14),(85,'Богуслав№2',14),(86,'Миронівка',14),(87,'Тараща',15),(88,'Горлиця',1),(89,'Іванова',1),(90,'Паша Коровін',12),(91,'Хіцька',12),(92,'Воловик Ромашки',2),(93,'Піонерська Ларьок',3),(94,'Зарічна',12),(95,'Мухіна Піонерська',3),(96,'Дитячий будинок Рокитне',11),(97,'клієнт___________',12),(98,'на ЗП',12),(99,'Брак Зміни',12),(100,'Залишок Галі',17),(101,'Мухіна Сільпо Гребінки',3),(102,'Петюх',12),(103,'Саша Ніч',9),(104,'Магазин Продукти',12),(105,'калинівка вергун',2),(106,'вергун першотравневе',2);
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-31 13:27:30
