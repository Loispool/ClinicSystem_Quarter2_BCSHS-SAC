-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 16, 2025 at 02:56 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `students_data`
--

-- --------------------------------------------------------

--
-- Table structure for table `diagnostics`
--

CREATE TABLE `diagnostics` (
  `record_id` int(11) NOT NULL,
  `student_lrn_fk` char(12) NOT NULL,
  `visit_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `student_name` varchar(201) NOT NULL,
  `diagnostic_notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `diagnostics`
--

INSERT INTO `diagnostics` (`record_id`, `student_lrn_fk`, `visit_date`, `student_name`, `diagnostic_notes`) VALUES
(1, '123456789021', '2025-10-25 08:25:19', 'Jane Doe', 'She is Dead As of now');

-- --------------------------------------------------------

--
-- Table structure for table `student`
--

CREATE TABLE `student` (
  `LRN` char(12) NOT NULL,
  `FirstName` text NOT NULL,
  `LastName` text NOT NULL,
  `Sex` text NOT NULL,
  `Weight` int(120) NOT NULL,
  `Height` int(220) NOT NULL,
  `Grade` int(12) NOT NULL,
  `Section` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student`
--

INSERT INTO `student` (`LRN`, `FirstName`, `LastName`, `Sex`, `Weight`, `Height`, `Grade`, `Section`) VALUES
('123456789012', 'Juan', 'Dela cruz', 'M', 64, 169, 12, 'Class 7'),
('123456789021', 'Jane', 'Doe', 'Female', 50, 171, 12, 'Class 5'),
('2147483647', 'Louise Gabriel', 'Caraw', 'Male', 58, 159, 12, 'Class 10');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `diagnostics`
--
ALTER TABLE `diagnostics`
  ADD PRIMARY KEY (`record_id`),
  ADD KEY `student_lrn_fk` (`student_lrn_fk`);

--
-- Indexes for table `student`
--
ALTER TABLE `student`
  ADD PRIMARY KEY (`LRN`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `diagnostics`
--
ALTER TABLE `diagnostics`
  MODIFY `record_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `diagnostics`
--
ALTER TABLE `diagnostics`
  ADD CONSTRAINT `fk_student_lrn` FOREIGN KEY (`student_lrn_fk`) REFERENCES `student` (`LRN`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
