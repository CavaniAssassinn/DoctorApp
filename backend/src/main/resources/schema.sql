-- Doctor table
CREATE TABLE IF NOT EXISTS doctor (
                                      id          UUID PRIMARY KEY,
                                      full_name   VARCHAR(255) NOT NULL,
    speciality  VARCHAR(100) NOT NULL,
    clinic_city VARCHAR(100) NOT NULL
    );

-- Patient table
CREATE TABLE IF NOT EXISTS patient (
                                       id         UUID PRIMARY KEY,
                                       full_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    phone      VARCHAR(50)
    );

-- Appointment table
CREATE TABLE IF NOT EXISTS appointment (
                                           id          UUID PRIMARY KEY,
                                           doctor_id   UUID NOT NULL,
                                           patient_id  UUID NOT NULL,
                                           start_time  TIMESTAMP NOT NULL,
                                           status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_appt_doctor  FOREIGN KEY (doctor_id)  REFERENCES doctor(id)  ON DELETE CASCADE,
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    CONSTRAINT uk_doctor_start UNIQUE (doctor_id, start_time)
    );
