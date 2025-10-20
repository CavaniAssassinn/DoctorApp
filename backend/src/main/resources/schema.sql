-- Use your database
CREATE DATABASE IF NOT EXISTS doctorapp CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE doctorapp;

-- For consistent defaults
SET NAMES utf8mb4;

-- Drop in dependency order (safe re-run)
DROP TABLE IF EXISTS time_off;
DROP TABLE IF EXISTS doctor_availability;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;

-- PATIENT
CREATE TABLE patients (
                          id          CHAR(36)     NOT NULL,
                          full_name   VARCHAR(200) NOT NULL,
                          email       VARCHAR(200) NOT NULL,
                          phone       VARCHAR(40)  NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_pat_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- DOCTOR
CREATE TABLE doctors (
                         id           CHAR(36)     NOT NULL,
                         full_name    VARCHAR(200) NOT NULL,
                         speciality   VARCHAR(120) NOT NULL,
                         clinic_city  VARCHAR(120) NOT NULL,
                         PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- USER (Auth)
-- Links to patient/doctor by id when applicable
CREATE TABLE users (
                       id             CHAR(36)     NOT NULL,
                       email          VARCHAR(200) NOT NULL,
                       full_name      VARCHAR(200) NOT NULL,
                       password_hash  VARCHAR(255) NOT NULL,
                       role           ENUM('PATIENT','DOCTOR') NOT NULL,
                       patient_id     CHAR(36) NULL,
                       doctor_id      CHAR(36) NULL,
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_user_email (email),
                       UNIQUE KEY uk_user_full_name (full_name),     -- Remove if you don’t want this constraint
                       KEY idx_user_patient (patient_id),
                       KEY idx_user_doctor  (doctor_id),
                       CONSTRAINT fk_user_patient FOREIGN KEY (patient_id) REFERENCES patients(id)
                           ON UPDATE CASCADE ON DELETE SET NULL,
                       CONSTRAINT fk_user_doctor  FOREIGN KEY (doctor_id)  REFERENCES doctors(id)
                           ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- APPOINTMENT
-- Status aligns to your enum: BOOKED, COMPLETED, CANCELLED, FOLLOWUP (add more if you have)
CREATE TABLE appointments (
                              id          CHAR(36) NOT NULL,
                              doctor_id   CHAR(36) NOT NULL,
                              patient_id  CHAR(36) NOT NULL,
                              start_time  DATETIME  NOT NULL,
                              status      ENUM('BOOKED','COMPLETED','CANCELLED','FOLLOWUP') NOT NULL DEFAULT 'BOOKED',
                              reason      VARCHAR(255) NULL,
                              PRIMARY KEY (id),
                              UNIQUE KEY uk_doc_time (doctor_id, start_time),     -- prevent double booking
                              KEY idx_appt_patient (patient_id),
                              KEY idx_appt_start (start_time),
                              CONSTRAINT fk_appt_doctor  FOREIGN KEY (doctor_id)  REFERENCES doctors(id)
                                  ON UPDATE CASCADE ON DELETE CASCADE,
                              CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients(id)
                                  ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- OPTIONAL: Weekly availability definition for a doctor
CREATE TABLE doctor_availability (
                                     id            BIGINT      NOT NULL AUTO_INCREMENT,
                                     doctor_id     CHAR(36)    NOT NULL,
                                     day_of_week   TINYINT     NOT NULL,             -- 1..7 (Mon=1)
                                     start_time    TIME        NOT NULL,
                                     end_time      TIME        NOT NULL,
                                     slot_minutes  INT         NOT NULL,
                                     PRIMARY KEY (id),
                                     KEY idx_avail_doc (doctor_id, day_of_week),
                                     CONSTRAINT fk_avail_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
                                         ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- OPTIONAL: Specific time-off for a doctor
CREATE TABLE time_off (
                          id         BIGINT      NOT NULL AUTO_INCREMENT,
                          doctor_id  CHAR(36)    NOT NULL,
                          start_at   DATETIME    NOT NULL,
                          end_at     DATETIME    NOT NULL,
                          reason     VARCHAR(255) NULL,
                          PRIMARY KEY (id),
                          KEY idx_to_doc (doctor_id, start_at),
                          CONSTRAINT fk_to_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id)
                              ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
