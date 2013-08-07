-- phpMyAdmin SQL Dump
-- version 4.0.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Aug 07, 2013 at 04:17 PM
-- Server version: 5.5.32-MariaDB-log
-- PHP Version: 5.4.17

SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `WebAlbums-New`
--

-- --------------------------------------------------------

--
-- Table structure for table `Album`
--

CREATE TABLE IF NOT EXISTS `Album` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `AlbumDate` varchar(10) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Nom` varchar(100) NOT NULL,
  `Droit` int(11) NOT NULL,
  `Picture` int(11) DEFAULT NULL,
  `Theme` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_3ckpjynh1bc67ldhf8sn5aowv` (`Droit`),
  KEY `FK_bm5dp44c6inrehlw48itw77uy` (`Picture`),
  KEY `FK_e4iy0p8antvxi1vg02n33gt9x` (`Theme`),
  KEY `Idx_date` (`AlbumDate`),
  KEY `Idx_name` (`Nom`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `Album`
--

INSERT INTO `Album` (`ID`, `AlbumDate`, `Description`, `Nom`, `Droit`, `Picture`, `Theme`) VALUES
(1, '2013-03-31', '', 'Antibes', 3, 1, 2),
(2, '2013-03-10', '', 'Ski Touring around Grenoble', 3, 6, 2),
(3, '2012-10-12', '', 'Sailing Journey', 3, 8, 2),
(4, '2006-03-31', '', 'Around Barcelona', 3, 20, 3),
(5, '2012-03-31', '', 'Around London', 3, 24, 3),
(6, '2008-03-31', '', 'Around Innsbruck', 3, 28, 3);

-- --------------------------------------------------------

--
-- Table structure for table `Carnet`
--

CREATE TABLE IF NOT EXISTS `Carnet` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CarnetDate` varchar(10) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Nom` varchar(100) NOT NULL,
  `Texte` longtext,
  `Droit` int(11) NOT NULL,
  `Picture` int(11) DEFAULT NULL,
  `Theme` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_rc8ncgms2xkougihxqbslnex` (`Droit`),
  KEY `FK_7bfgrcprn04ufh3j38novgbt0` (`Picture`),
  KEY `FK_e2ttc2le7vay5ipu1kluiso58` (`Theme`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `CarnetAlbum`
--

CREATE TABLE IF NOT EXISTS `CarnetAlbum` (
  `Carnet` int(11) NOT NULL,
  `Album` int(11) NOT NULL,
  KEY `FK_6uqnbig7e0fll8esrhcoys3kc` (`Album`),
  KEY `FK_740o0vb1njahadr4ncy3jmbs0` (`Carnet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `CarnetPhoto`
--

CREATE TABLE IF NOT EXISTS `CarnetPhoto` (
  `Carnet` int(11) NOT NULL,
  `Photo` int(11) NOT NULL,
  KEY `FK_q7t2ui5h1kk4j1tvcjomp2dc3` (`Photo`),
  KEY `FK_ap7vr4h0bmrtmf42xgkdvf2gk` (`Carnet`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `Geolocalisation`
--

CREATE TABLE IF NOT EXISTS `Geolocalisation` (
  `Tag` int(11) NOT NULL,
  `Lat` varchar(20) NOT NULL,
  `Longitude` varchar(20) NOT NULL,
  PRIMARY KEY (`Tag`),
  KEY `FK_movf8oinkbgw4wwg5f7l2v9lm` (`Tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Geolocalisation`
--

INSERT INTO `Geolocalisation` (`Tag`, `Lat`, `Longitude`) VALUES
(1, '45.166053280393456', '5.931777954101612'),
(2, '41.391480984410855', '2.1662139892576486'),
(3, '41.41507599800298', '2.1514511108397025'),
(4, '41.4064018831887', '2.162823677062784'),
(5, '43.58560342458311', '7.104568481445203'),
(6, '43.00187515984713', '6.209869384765522'),
(10, '47.254291396124025', '11.38374328613325'),
(11, '51.616515744581136', '0.02525329589834851');

-- --------------------------------------------------------

--
-- Table structure for table `Person`
--

CREATE TABLE IF NOT EXISTS `Person` (
  `Tag` int(11) NOT NULL,
  `Birthdate` varchar(10) DEFAULT NULL,
  `Contact` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`Tag`),
  KEY `FK_nvyanm28pthxarvb03i463ic5` (`Tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Person`
--

INSERT INTO `Person` (`Tag`, `Birthdate`, `Contact`) VALUES
(9, '1980-01-01', 'nobody@the.world');

-- --------------------------------------------------------

--
-- Table structure for table `Photo`
--

CREATE TABLE IF NOT EXISTS `Photo` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `DateMeta` varchar(50) DEFAULT NULL,
  `Description` varchar(200) DEFAULT NULL,
  `droit` int(11) DEFAULT NULL,
  `Exposure` varchar(50) DEFAULT NULL,
  `Flash` varchar(150) DEFAULT NULL,
  `Focal` varchar(50) DEFAULT NULL,
  `Height` varchar(50) DEFAULT NULL,
  `isGpx` tinyint(1) DEFAULT NULL,
  `Iso` varchar(50) DEFAULT NULL,
  `Model` varchar(100) DEFAULT NULL,
  `PhotoPath` varchar(100) NOT NULL,
  `Stars` int(11) NOT NULL,
  `Type` varchar(50) DEFAULT NULL,
  `Width` varchar(50) DEFAULT NULL,
  `Album` int(11) NOT NULL,
  `TagAuthor` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_8ue53xe8bitvvy35pkrwpy0v2` (`PhotoPath`),
  KEY `FK_spwds6v61qqeg2uhv1d8xkj6r` (`Album`),
  KEY `FK_mwy8xkf2wpxtasgnehcgr1hnj` (`TagAuthor`),
  KEY `Idx_album` (`Album`),
  KEY `Idx_path` (`PhotoPath`),
  KEY `Idx_stars` (`Stars`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=30 ;

--
-- Dumping data for table `Photo`
--

INSERT INTO `Photo` (`ID`, `DateMeta`, `Description`, `droit`, `Exposure`, `Flash`, `Focal`, `Height`, `isGpx`, `Iso`, `Model`, `PhotoPath`, `Stars`, `Type`, `Width`, `Album`, `TagAuthor`) VALUES
(1, 'Date/Time - 2013:03:31 13:44:15', '', NULL, 'Exposure Time - 1/320 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 18.0 mm', 'Exif Image Height - 2575 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DSLR-A290', '2013/2013-03-31 Antibes/DSC01139.JPG', 3, 'image/jpeg', 'Exif Image Width - 4583 pixels', 1, NULL),
(2, 'Date/Time - 2013:03:31 14:10:33', '', NULL, 'Exposure Time - 1/250 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 18.0 mm', 'Exif Image Height - 2579 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DSLR-A290', '2013/2013-03-31 Antibes/DSC01161.JPG', 3, 'image/jpeg', 'Exif Image Width - 4588 pixels', 1, NULL),
(3, 'Date/Time - 2013:03:31 11:07:17', '', NULL, 'Exposure Time - 1/60 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 35.0 mm', 'Exif Image Height - 4066 pixels', 0, 'ISO Speed Ratings - 400', 'Model - DSLR-A290', '2013/2013-03-31 Antibes/DSC01099b.JPG', 3, 'image/jpeg', 'Exif Image Width - 3050 pixels', 1, NULL),
(4, NULL, '', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2013/2013-03-10 Ski Touring around Grenoble/2013-03-10 07_21.gpx', 3, 'application/xml', NULL, 2, NULL),
(5, NULL, 'I did''t check this picture, Nobody did. Mouse over its name to get its contact details.\n', NULL, NULL, NULL, NULL, NULL, 0, NULL, 'Model - DSLR-A290', '2013/2013-03-10 Ski Touring around Grenoble/DSC00617-DSC00620.jpg', 3, 'image/jpeg', NULL, 2, 9),
(6, 'Date/Time - 2013:03:10 10:11:40', '', NULL, 'Exposure Time - 1/500 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 18.0 mm', 'Exif Image Height - 4067 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DSLR-A290', '2013/2013-03-10 Ski Touring around Grenoble/DSC00669.JPG', 3, 'image/jpeg', 'Exif Image Width - 3052 pixels', 2, NULL),
(7, 'Date/Time - 2013:03:10 10:31:29', '', NULL, 'Exposure Time - 1/500 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 18.0 mm', 'Exif Image Height - 2064 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DSLR-A290', '2013/2013-03-10 Ski Touring around Grenoble/DSC00681.JPG', 3, 'image/jpeg', 'Exif Image Width - 3671 pixels', 2, NULL),
(8, 'Date/Time - 2012:10:13 16:53:43', '', NULL, 'Exposure Time - 1/160 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 18.0 mm', 'Exif Image Height - 3056 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DSLR-A290', '2012/2012-10-12 Sailing Journey/DSC07755.JPG', 3, 'image/jpeg', 'Exif Image Width - 4592 pixels', 3, NULL),
(9, 'Date/Time - 2012:10:12 18:24:39', 'Check Nobody''s age at that time, by mousing over its name (32 y/o)\n', NULL, 'Exposure Time - 1/80 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 18.0 mm', 'Exif Image Height - 3056 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DSLR-A290', '2012/2012-10-12 Sailing Journey/DSC07658.JPG', 3, 'image/jpeg', 'Exif Image Width - 4592 pixels', 3, NULL),
(10, NULL, '', NULL, NULL, NULL, NULL, NULL, 0, NULL, 'Model - DSLR-A290', '2012/2012-10-12 Sailing Journey/DSC07661-DSC07663.jpg', 3, 'image/jpeg', NULL, 3, NULL),
(11, 'Date/Time - 2012:10:12 18:26:18', '', NULL, 'Exposure Time - 1/80 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 18.0 mm', 'Exif Image Height - 3056 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DSLR-A290', '2012/2012-10-12 Sailing Journey/DSC07668.JPG', 3, 'image/jpeg', 'Exif Image Width - 4592 pixels', 3, NULL),
(12, NULL, '', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2012/2012-10-12 Sailing Journey/Vendredi.gpx', 3, 'application/xml', NULL, 3, NULL),
(13, NULL, '', NULL, NULL, NULL, NULL, NULL, 0, NULL, 'Model - DSLR-A290', '2012/2012-10-12 Sailing Journey/DSC07587-DSC07591.jpg', 3, 'image/jpeg', NULL, 3, NULL),
(14, NULL, '', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2012/2012-10-12 Sailing Journey/Samedi.gpx', 3, 'application/xml', NULL, 3, NULL),
(15, NULL, '', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2012/2012-10-12 Sailing Journey/Dimanche.gpx', 3, 'application/xml', NULL, 3, NULL),
(16, 'Date/Time - 2006:05:30 18:26:48', '', NULL, 'Exposure Time - 1/200 sec', 'Flash - Flash did not fire', 'Focal Length - 5.406 mm', 'Exif Image Height - 1600 pixels', 0, NULL, 'Model - Canon PowerShot A20', '2006/2006-03-31 Around Barcelona/barcelone20.jpg', 3, 'image/jpeg', 'Exif Image Width - 1200 pixels', 4, NULL),
(17, 'Date/Time - 2007:03:18 12:07:37', '', NULL, 'Exposure Time - 1/200 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 7.812 mm', 'Exif Image Height - 1600 pixels', 0, NULL, 'Model - Canon PowerShot A95', '2006/2006-03-31 Around Barcelona/IMG_0996.JPG', 3, 'image/jpeg', 'Exif Image Width - 1200 pixels', 4, NULL),
(18, NULL, '', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, '2006/2006-03-31 Around Barcelona/bcn.gpx', 3, 'application/xml', NULL, 4, NULL),
(19, 'Date/Time - 2007:03:16 19:43:54', '', NULL, 'Exposure Time - 0.02 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 7.812 mm', 'Exif Image Height - 1944 pixels', 0, NULL, 'Model - Canon PowerShot A95', '2006/2006-03-31 Around Barcelona/IMG_0948.JPG', 3, 'image/jpeg', 'Exif Image Width - 2592 pixels', 4, NULL),
(20, 'Date/Time - 2006:06:01 15:44:22', '', NULL, 'Exposure Time - 1/400 sec', 'Flash - Flash did not fire', 'Focal Length - 5.406 mm', 'Exif Image Height - 1200 pixels', 0, NULL, 'Model - Canon PowerShot A20', '2006/2006-03-31 Around Barcelona/tatou 043.jpg', 3, 'image/jpeg', 'Exif Image Width - 1600 pixels', 4, NULL),
(21, 'Date/Time - 2009:11:11 10:28:46', '', NULL, 'Exposure Time - 1/30 sec', 'Flash - Flash fired, auto', 'Focal Length - 5.8 mm', 'Exif Image Height - 1728 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DMC-FX12', '2012/2012-03-31 Around London/P1030955.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 5, NULL),
(22, 'Date/Time - 2009:12:04 18:18:29', '', NULL, 'Exposure Time - 1/30 sec', 'Flash - Flash fired, auto', 'Focal Length - 5.8 mm', 'Exif Image Height - 2304 pixels', 0, 'ISO Speed Ratings - 400', 'Model - DMC-FX12', '2012/2012-03-31 Around London/P1040065.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 5, NULL),
(23, 'Date/Time - 2009:12:21 15:53:06', '', NULL, 'Exposure Time - 0.04 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 5.8 mm', 'Exif Image Height - 2304 pixels', 0, 'ISO Speed Ratings - 800', 'Model - DMC-FX12', '2012/2012-03-31 Around London/P1040214.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 5, NULL),
(24, 'Date/Time - 2009:12:04 17:43:46', '', NULL, 'Exposure Time - 1/30 sec', 'Flash - Flash fired, auto', 'Focal Length - 5.8 mm', 'Exif Image Height - 2304 pixels', 0, 'ISO Speed Ratings - 800', 'Model - DMC-FX12', '2012/2012-03-31 Around London/P1040064.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 5, NULL),
(25, 'Date/Time - 2008:07:01 11:43:15', '', NULL, 'Exposure Time - 1/125 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 5.8 mm', 'Exif Image Height - 2304 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DMC-FX12', '2008/2008-03-31 Around Innsbruck/P1000696.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 6, NULL),
(26, 'Date/Time - 2008:02:15 13:14:31', '', NULL, 'Exposure Time - 1/160 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 63.0 mm', 'Exif Image Height - 2304 pixels', 0, 'ISO Speed Ratings - 64', 'Model - PENTAX Optio W20', '2008/2008-03-31 Around Innsbruck/IMGP2103.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 6, NULL),
(27, 'Date/Time - 2008:04:02 14:49:26', '', NULL, 'Exposure Time - 1/400 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 5.8 mm', 'Exif Image Height - 2304 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DMC-FX12', '2008/2008-03-31 Around Innsbruck/P1000216.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 6, NULL),
(28, 'Date/Time - 2008:06:22 11:45:49', '', NULL, 'Exposure Time - 1/250 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 5.8 mm', 'Exif Image Height - 1728 pixels', 0, 'ISO Speed Ratings - 100', 'Model - DMC-FX12', '2008/2008-03-31 Around Innsbruck/P1000593.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 6, NULL),
(29, 'Date/Time - 2008:02:10 15:07:29', 'Check Nobody''s age at that time, by mousing over its name (28 y/o)', NULL, 'Exposure Time - 1/200 sec', 'Flash - Flash did not fire, auto', 'Focal Length - 63.0 mm', 'Exif Image Height - 2304 pixels', 0, 'ISO Speed Ratings - 64', 'Model - PENTAX Optio W20', '2008/2008-03-31 Around Innsbruck/IMGP2059.JPG', 3, 'image/jpeg', 'Exif Image Width - 3072 pixels', 6, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `Tag`
--

CREATE TABLE IF NOT EXISTS `Tag` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `IsMinor` tinyint(1) DEFAULT NULL,
  `Nom` varchar(40) NOT NULL,
  `TagType` int(11) NOT NULL,
  `Parent` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_4cgcwbnpisor7fg4xm351pts8` (`Nom`),
  KEY `FK_jvc6gdcyrdfejuhe5j2oh8iyg` (`Parent`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=12 ;

--
-- Dumping data for table `Tag`
--

INSERT INTO `Tag` (`ID`, `IsMinor`, `Nom`, `TagType`, `Parent`) VALUES
(1, NULL, 'Grand Colon', 3, NULL),
(2, NULL, 'Barcelona', 3, NULL),
(3, NULL, 'Parc Guell', 3, 2),
(4, NULL, 'Salgrada Famillia', 3, 2),
(5, NULL, 'Antibes', 3, NULL),
(6, NULL, 'Porquerolles', 3, NULL),
(7, NULL, 'Skiers', 1, NULL),
(8, NULL, 'Panoramique', 2, NULL),
(9, NULL, 'Nobody', 1, NULL),
(10, NULL, 'Innsbruck', 3, NULL),
(11, NULL, 'London', 3, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `TagPhoto`
--

CREATE TABLE IF NOT EXISTS `TagPhoto` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Photo` int(11) NOT NULL,
  `Tag` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_m4q8s58sgs8lbukujevtb9x3u` (`Tag`,`Photo`),
  KEY `FK_mnygrgm7sptiehh8mysa171k3` (`Photo`),
  KEY `FK_g9wcuhkqsln576il5ootdrtck` (`Tag`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=33 ;

--
-- Dumping data for table `TagPhoto`
--

INSERT INTO `TagPhoto` (`ID`, `Photo`, `Tag`) VALUES
(8, 4, 1),
(9, 5, 1),
(10, 6, 1),
(11, 7, 1),
(4, 19, 2),
(3, 20, 3),
(1, 16, 4),
(2, 17, 4),
(6, 1, 5),
(7, 2, 5),
(5, 3, 5),
(17, 8, 6),
(14, 9, 6),
(15, 10, 6),
(16, 11, 6),
(13, 13, 6),
(18, 5, 7),
(19, 7, 7),
(12, 13, 8),
(20, 6, 9),
(21, 9, 9),
(23, 27, 9),
(22, 29, 9),
(28, 25, 10),
(25, 26, 10),
(26, 27, 10),
(27, 28, 10),
(24, 29, 10),
(29, 21, 11),
(31, 22, 11),
(32, 23, 11),
(30, 24, 11);

-- --------------------------------------------------------

--
-- Table structure for table `TagTheme`
--

CREATE TABLE IF NOT EXISTS `TagTheme` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `isVisible` tinyint(1) DEFAULT NULL,
  `Photo` int(11) DEFAULT NULL,
  `Tag` int(11) NOT NULL,
  `Theme` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_5uq9qxoyqr2fhl83djiqi06kw` (`Tag`,`Theme`),
  KEY `FK_h8qemslm4eklk1rti6ybq4i8t` (`Photo`),
  KEY `FK_gcemwuk4y2ac6q27fvl84h5ad` (`Tag`),
  KEY `FK_n3edm8pvbu0s21ya6659lrgcq` (`Theme`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=12 ;

--
-- Dumping data for table `TagTheme`
--

INSERT INTO `TagTheme` (`ID`, `isVisible`, `Photo`, `Tag`, `Theme`) VALUES
(1, 1, 17, 4, 3),
(2, 1, 20, 3, 3),
(3, 1, 19, 2, 3),
(4, 1, 3, 5, 2),
(5, 1, 9, 9, 2),
(6, 1, 7, 7, 2),
(7, 1, 6, 1, 2),
(8, 1, 10, 6, 2),
(9, 1, 29, 10, 3),
(10, 1, 23, 11, 3),
(11, 1, 13, 8, 2);

-- --------------------------------------------------------

--
-- Table structure for table `Theme`
--

CREATE TABLE IF NOT EXISTS `Theme` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Lat` varchar(20) DEFAULT NULL,
  `Longitude` varchar(20) DEFAULT NULL,
  `Nom` varchar(100) NOT NULL,
  `Background` int(11) DEFAULT NULL,
  `Picture` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_nvd4a3bsufi8eni2wgrh34jo4` (`Nom`),
  KEY `FK_aqa32l6bqrsf46kt8dkxey30o` (`Background`),
  KEY `FK_eqya70x8k0m4inexw9a9lbo4a` (`Picture`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `Theme`
--

INSERT INTO `Theme` (`ID`, `Lat`, `Longitude`, `Nom`, `Background`, `Picture`) VALUES
(1, '43.581624346512065', '7.112808227538913', 'Root', NULL, 25),
(2, NULL, NULL, 'France', 10, 11),
(3, NULL, NULL, 'Travel', 27, 26);

-- --------------------------------------------------------

--
-- Table structure for table `Utilisateur`
--

CREATE TABLE IF NOT EXISTS `Utilisateur` (
  `ID` int(11) NOT NULL,
  `Nom` varchar(100) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_1fthv9cc7e4oomqctqbgcghnw` (`Nom`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Utilisateur`
--

INSERT INTO `Utilisateur` (`ID`, `Nom`) VALUES
(1, 'Admin'),
(3, 'Amis'),
(4, 'Autres'),
(2, 'Famille');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Album`
--
ALTER TABLE `Album`
  ADD CONSTRAINT `FK_3ckpjynh1bc67ldhf8sn5aowv` FOREIGN KEY (`Droit`) REFERENCES `Utilisateur` (`ID`),
  ADD CONSTRAINT `FK_bm5dp44c6inrehlw48itw77uy` FOREIGN KEY (`Picture`) REFERENCES `Photo` (`ID`),
  ADD CONSTRAINT `FK_e4iy0p8antvxi1vg02n33gt9x` FOREIGN KEY (`Theme`) REFERENCES `Theme` (`ID`);

--
-- Constraints for table `Carnet`
--
ALTER TABLE `Carnet`
  ADD CONSTRAINT `FK_7bfgrcprn04ufh3j38novgbt0` FOREIGN KEY (`Picture`) REFERENCES `Photo` (`ID`),
  ADD CONSTRAINT `FK_e2ttc2le7vay5ipu1kluiso58` FOREIGN KEY (`Theme`) REFERENCES `Theme` (`ID`),
  ADD CONSTRAINT `FK_rc8ncgms2xkougihxqbslnex` FOREIGN KEY (`Droit`) REFERENCES `Utilisateur` (`ID`);

--
-- Constraints for table `CarnetAlbum`
--
ALTER TABLE `CarnetAlbum`
  ADD CONSTRAINT `FK_6uqnbig7e0fll8esrhcoys3kc` FOREIGN KEY (`Album`) REFERENCES `Album` (`ID`),
  ADD CONSTRAINT `FK_740o0vb1njahadr4ncy3jmbs0` FOREIGN KEY (`Carnet`) REFERENCES `Carnet` (`ID`);

--
-- Constraints for table `CarnetPhoto`
--
ALTER TABLE `CarnetPhoto`
  ADD CONSTRAINT `FK_ap7vr4h0bmrtmf42xgkdvf2gk` FOREIGN KEY (`Carnet`) REFERENCES `Carnet` (`ID`),
  ADD CONSTRAINT `FK_q7t2ui5h1kk4j1tvcjomp2dc3` FOREIGN KEY (`Photo`) REFERENCES `Photo` (`ID`);

--
-- Constraints for table `Geolocalisation`
--
ALTER TABLE `Geolocalisation`
  ADD CONSTRAINT `FK_movf8oinkbgw4wwg5f7l2v9lm` FOREIGN KEY (`Tag`) REFERENCES `Tag` (`ID`);

--
-- Constraints for table `Person`
--
ALTER TABLE `Person`
  ADD CONSTRAINT `FK_nvyanm28pthxarvb03i463ic5` FOREIGN KEY (`Tag`) REFERENCES `Tag` (`ID`);

--
-- Constraints for table `Photo`
--
ALTER TABLE `Photo`
  ADD CONSTRAINT `FK_mwy8xkf2wpxtasgnehcgr1hnj` FOREIGN KEY (`TagAuthor`) REFERENCES `Tag` (`ID`),
  ADD CONSTRAINT `FK_spwds6v61qqeg2uhv1d8xkj6r` FOREIGN KEY (`Album`) REFERENCES `Album` (`ID`);

--
-- Constraints for table `Tag`
--
ALTER TABLE `Tag`
  ADD CONSTRAINT `FK_jvc6gdcyrdfejuhe5j2oh8iyg` FOREIGN KEY (`Parent`) REFERENCES `Tag` (`ID`);

--
-- Constraints for table `TagPhoto`
--
ALTER TABLE `TagPhoto`
  ADD CONSTRAINT `FK_g9wcuhkqsln576il5ootdrtck` FOREIGN KEY (`Tag`) REFERENCES `Tag` (`ID`),
  ADD CONSTRAINT `FK_mnygrgm7sptiehh8mysa171k3` FOREIGN KEY (`Photo`) REFERENCES `Photo` (`ID`);

--
-- Constraints for table `TagTheme`
--
ALTER TABLE `TagTheme`
  ADD CONSTRAINT `FK_gcemwuk4y2ac6q27fvl84h5ad` FOREIGN KEY (`Tag`) REFERENCES `Tag` (`ID`),
  ADD CONSTRAINT `FK_h8qemslm4eklk1rti6ybq4i8t` FOREIGN KEY (`Photo`) REFERENCES `Photo` (`ID`),
  ADD CONSTRAINT `FK_n3edm8pvbu0s21ya6659lrgcq` FOREIGN KEY (`Theme`) REFERENCES `Theme` (`ID`);

--
-- Constraints for table `Theme`
--
ALTER TABLE `Theme`
  ADD CONSTRAINT `FK_aqa32l6bqrsf46kt8dkxey30o` FOREIGN KEY (`Background`) REFERENCES `Photo` (`ID`),
  ADD CONSTRAINT `FK_eqya70x8k0m4inexw9a9lbo4a` FOREIGN KEY (`Picture`) REFERENCES `Photo` (`ID`);
SET FOREIGN_KEY_CHECKS=1;
