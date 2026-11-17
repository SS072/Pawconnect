-- ========================================================
-- PawConnect Pet Adoption Management System
-- MySQL Database Creation and Schema Initialization Script
-- ========================================================

-- 1. Create and select the database
CREATE DATABASE IF NOT EXISTS pawconnect_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE pawconnect_db;

-- 2. Drop existing tables in reverse dependency order (if re-initializing)
DROP TABLE IF EXISTS medical_records;
DROP TABLE IF EXISTS adoption_applications;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS users;

-- 3. Users Table (Authentication & Role Management)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ADOPTER'
);

-- 4. Pets Table (Core Animal Inventory)
CREATE TABLE pets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100) NOT NULL,
    age_months INT NOT NULL DEFAULT 12,
    gender VARCHAR(20) NOT NULL,
    size VARCHAR(20) NOT NULL,
    description TEXT,
    adoption_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    intake_date VARCHAR(20) NOT NULL,
    good_with_children BOOLEAN NOT NULL DEFAULT FALSE,
    good_with_dogs BOOLEAN NOT NULL DEFAULT FALSE,
    good_with_cats BOOLEAN NOT NULL DEFAULT FALSE,
    house_trained BOOLEAN NOT NULL DEFAULT FALSE,
    activity_level INT NOT NULL DEFAULT 3,
    health_status VARCHAR(50) NOT NULL DEFAULT 'Good'
);

-- 5. Adoption Applications Table (Petitions submitted by prospective adopters)
CREATE TABLE adoption_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    pet_name VARCHAR(100),
    pet_breed VARCHAR(100),
    applicant_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    contact_email VARCHAR(100) NOT NULL,
    housing_type VARCHAR(50) NOT NULL,
    has_fenced_yard BOOLEAN NOT NULL DEFAULT FALSE,
    has_children BOOLEAN NOT NULL DEFAULT FALSE,
    other_pets VARCHAR(100) DEFAULT 'None',
    household_agreement BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    submission_date VARCHAR(20) NOT NULL,
    review_notes TEXT,
    CONSTRAINT fk_app_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);

-- 6. Medical & Vaccination Records Table
CREATE TABLE medical_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    pet_name VARCHAR(100),
    record_date VARCHAR(20) NOT NULL,
    record_type VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    veterinarian VARCHAR(100) NOT NULL,
    next_due_date VARCHAR(20),
    CONSTRAINT fk_med_pet FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);

-- ========================================================
-- Seed Initial Sample Data
-- ========================================================

-- Seed Users
INSERT INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'Dr. Sarah Jenkins (Shelter Director)', 'ADMIN'),
('adopter', 'user123', 'Rahul Sharma', 'ADOPTER');

-- Seed Pets
INSERT INTO pets (name, species, breed, age_months, gender, size, description, adoption_status, intake_date, good_with_children, good_with_dogs, good_with_cats, house_trained, activity_level, health_status) VALUES
('Bruno', 'Dog', 'Labrador Retriever', 24, 'Male', 'Large', 'Friendly, high energy, loves outdoor fetch, obedient with basic commands.', 'AVAILABLE', '2026-06-15', TRUE, TRUE, FALSE, TRUE, 4, 'Excellent'),
('Luna', 'Cat', 'Domestic Shorthair', 14, 'Female', 'Small', 'Gentle, quiet companion, litter-trained, ideal for calm apartment living.', 'AVAILABLE', '2026-07-02', TRUE, FALSE, TRUE, TRUE, 2, 'Good'),
('Max', 'Dog', 'Beagle', 36, 'Male', 'Medium', 'Affectionate scent hound, very social with other canines, loves walks.', 'AVAILABLE', '2026-07-20', TRUE, TRUE, TRUE, TRUE, 3, 'Good'),
('Bella', 'Dog', 'Golden Retriever', 48, 'Female', 'Large', 'Calm family dog, certified gentle demeanor, excellent around young children.', 'PENDING', '2026-08-01', TRUE, TRUE, TRUE, TRUE, 3, 'Excellent'),
('Milo', 'Cat', 'Tabby', 8, 'Male', 'Small', 'Playful kitten, curious explorer, fully vaccinated and litter trained.', 'AVAILABLE', '2026-08-10', TRUE, TRUE, TRUE, TRUE, 3, 'Excellent'),
('Rocky', 'Dog', 'German Shepherd', 30, 'Male', 'Large', 'Intelligent, protective, requires experienced handler and active yard. Hip dysplasia monitored.', 'AVAILABLE', '2026-08-15', FALSE, TRUE, FALSE, TRUE, 5, 'Special Needs'),
('Daisy', 'Rabbit', 'Holland Lop', 12, 'Female', 'Small', 'Gentle herbivore, indoor habitat required, accustomed to gentle handling.', 'AVAILABLE', '2026-09-01', TRUE, FALSE, FALSE, TRUE, 1, 'Good'),
('Charlie', 'Dog', 'Cocker Spaniel', 60, 'Male', 'Medium', 'Adopted into a quiet family home. Affectionate lap companion.', 'ADOPTED', '2026-05-10', TRUE, TRUE, TRUE, TRUE, 2, 'Good');

-- Seed Applications
INSERT INTO adoption_applications (pet_id, pet_name, pet_breed, applicant_name, contact_phone, contact_email, housing_type, has_fenced_yard, has_children, other_pets, household_agreement, notes, status, submission_date, review_notes) VALUES
(4, 'Bella', 'Golden Retriever', 'Rahul Sharma', '+91 98401 23456', 'rahul.sharma@example.com', 'Independent House', TRUE, TRUE, 'None', TRUE, 'We have a large fenced garden and children aged 7 and 10 who love Golden Retrievers.', 'PENDING', '2026-09-15', 'Initial screening completed. Home visit recommended.'),
(1, 'Bruno', 'Labrador Retriever', 'Priya Sundaram', '+91 97890 54321', 'priya.sundaram@example.com', 'Apartment', FALSE, FALSE, '1 Cat', TRUE, 'Experienced dog lover looking for an active companion for weekend runs.', 'UNDER_REVIEW', '2026-09-18', 'Reviewing compatibility with existing cat.');

-- Seed Medical Records
INSERT INTO medical_records (pet_id, pet_name, record_date, record_type, title, description, veterinarian, next_due_date) VALUES
(1, 'Bruno', '2026-06-18', 'VACCINATION', 'Annual Rabies & DHPP Booster', 'Administered subcutaneous Rabvac 3 and Nobivac DHPP. No adverse reaction observed.', 'Dr. K. Ramanathan, MVSc', '2027-06-18'),
(4, 'Bella', '2026-08-03', 'EXAMINATION', 'Routine Pre-Adoption Physical', 'Weight: 28.5 kg, heart rhythm normal, teeth clean, clear eyes and ears. Microchip #985141002384912 verified.', 'Dr. S. Meenakshi, BVSc', '2027-02-03'),
(6, 'Rocky', '2026-08-16', 'TREATMENT', 'Hip Dysplasia Joint Therapy', 'Bi-weekly Cartrophen injection and prescribed daily Dasuquin joint chewables. Moderate hip mobility noted.', 'Dr. K. Ramanathan, MVSc', '2026-09-16');
