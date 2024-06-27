CREATE DATABASE  IF NOT EXISTS `tw1` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `tw1`;
-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: tw1
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `locker`
--

DROP TABLE IF EXISTS `locker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `locker` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) DEFAULT NULL,
  `latitud` double NOT NULL,
  `longitud` double NOT NULL,
  `seleccionado` bit(1) NOT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `codigo_postal` varchar(10) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `distancia` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=503 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `locker`
--

LOCK TABLES `locker` WRITE;
/*!40000 ALTER TABLE `locker` DISABLE KEYS */;
INSERT INTO `locker` VALUES (1,'Locker San Justo 1',-34.6853,-58.5588,_binary '\0','PEQUENIO','1754',NULL,0),(2,'Locker San Justo 2',-34.686,-58.5631,_binary '','PEQUENIO','1754',NULL,0),(3,'Locker Ramos Mejía 1',-34.6485,-58.5617,_binary '','GRANDE','1704',NULL,0),(4,'Locker Ramos Mejía 2',-34.644,-58.5683,_binary '','PEQUENIO','1704',NULL,0),(5,'Locker Luis Guillon',-34.7954444,-58.4492554,_binary '','PEQUENIO','1838',NULL,0.2591206693897158),(6,'Locker Luis Guillon 1',-34.8006186,-58.4555308,_binary '','GRANDE','1838',NULL,0.8022924399890756),(7,'Locker Luis Guillon 2',-34.7974037,-58.4476196,_binary '','GRANDE','1838',NULL,0.005879816247085675),(502,'Locker Villa Luzuriaga',-34.6730866,-58.594023,_binary '','MEDIANO','1753',NULL,0);
/*!40000 ALTER TABLE `locker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reserva`
--

DROP TABLE IF EXISTS `reserva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reserva` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fechaFinalizacion` datetime(6) DEFAULT NULL,
  `fechaReserva` datetime(6) DEFAULT NULL,
  `locker_id` bigint DEFAULT NULL,
  `usuario_id` bigint DEFAULT NULL,
  `costo` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhqq9kr3a51ml8oufgxonwwwcb` (`locker_id`),
  KEY `FKkrxqwqvewd2pls5tdigwqprj2` (`usuario_id`),
  CONSTRAINT `FKhqq9kr3a51ml8oufgxonwwwcb` FOREIGN KEY (`locker_id`) REFERENCES `locker` (`id`),
  CONSTRAINT `FKkrxqwqvewd2pls5tdigwqprj2` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reserva`
--

LOCK TABLES `reserva` WRITE;
/*!40000 ALTER TABLE `reserva` DISABLE KEYS */;
INSERT INTO `reserva` VALUES (37,'2024-06-30 00:00:00.000000','2024-06-27 00:00:00.000000',3,33,300),(38,'2024-06-29 00:00:00.000000','2024-06-27 00:00:00.000000',2,34,200),(39,'2024-06-30 00:00:00.000000','2024-06-27 00:00:00.000000',2,33,300),(40,'2024-06-30 00:00:00.000000','2024-06-27 00:00:00.000000',502,33,300);
/*!40000 ALTER TABLE `reserva` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `rol` varchar(255) DEFAULT NULL,
  `codigo_postal` varchar(255) DEFAULT NULL,
  `latitud` double DEFAULT NULL,
  `longitud` double DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,_binary '','test@unlam.edu.ar','test','ADMIN',NULL,NULL,NULL,NULL),(33,_binary '\0','maxii.rabenko@gmail.com','3ff50442a8f34ad536e05695fe6f6c0e',NULL,NULL,NULL,NULL,NULL),(34,_binary '\0','chester9322@gmail.com','dd6b51edff723657c79d0271cbecbf64',NULL,NULL,NULL,NULL,'Maxi'),(35,_binary '\0','test@unlam.com.ar','77113982a5c2a667aef63c98aac817c4',NULL,NULL,NULL,NULL,'maxi');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-27 20:00:31
