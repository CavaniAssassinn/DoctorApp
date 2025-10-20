USE doctorapp;

-- Doctors
INSERT INTO doctors (id, full_name, speciality, clinic_city) VALUES
                                                                 ('11111111-1111-1111-1111-111111111111','Dr Nathan Antha','General Practitioner','Cape Town'),
                                                                 ('22222222-2222-2222-2222-222222222222','Dr Matthew Engelbrecht','Cardiology','Cape Town'),
                                                                 ('33333333-3333-3333-3333-333333333333','Dr Bruneez Appolis','Dermatology','Johannesburg'),
                                                                 ('44444444-4444-4444-4444-444444444444','Dr Nompu Bhebhe','Pediatrics','Durban');

-- Patients
INSERT INTO patients (id, full_name, email, phone) VALUES
                                                       ('aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1','Alice Patient','alice@example.com','0710000001'),
                                                       ('aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2','Bob Patient','bob@example.com','0710000002'),
                                                       ('aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3','Carla Patient','carla@example.com','0710000003');

-- Users (bcrypt hash for Passw0rd! => $2a$10$7q6C0h0z3aXy8R9wY1C2Qe8y3f9x2o7yZ9r2m9C6TQ3mQYJ7h2w8W)
-- If your encoder produces different prefix ($2a$, $2b$, $2y$) that’s fine.
-- DOCTORS (linked by doctor_id)
INSERT INTO users (id, email, full_name, password_hash, role, doctor_id, patient_id) VALUES
                                                                                         ('u-d1-0000-0000-0000-000000000001','nathan.antha@clinic.example','Dr Nathan Antha',
                                                                                          '$2a$10$7q6C0h0z3aXy8R9wY1C2Qe8y3f9x2o7yZ9r2m9C6TQ3mQYJ7h2w8W','DOCTOR',
                                                                                          '11111111-1111-1111-1111-111111111111', NULL),

                                                                                         ('u-d2-0000-0000-0000-000000000002','matthew.engelbrecht@clinic.example','Dr Matthew Engelbrecht',
                                                                                          '$2a$10$7q6C0h0z3aXy8R9wY1C2Qe8y3f9x2o7yZ9r2m9C6TQ3mQYJ7h2w8W','DOCTOR',
                                                                                          '22222222-2222-2222-2222-222222222222', NULL),

                                                                                         ('u-d3-0000-0000-0000-000000000003','bruneez.appolis@clinic.example','Dr Bruneez Appolis',
                                                                                          '$2a$10$7q6C0h0z3aXy8R9wY1C2Qe8y3f9x2o7yZ9r2m9C6TQ3mQYJ7h2w8W','DOCTOR',
                                                                                          '33333333-3333-3333-3333-333333333333', NULL),

                                                                                         ('u-d4-0000-0000-0000-000000000004','nompu.bhebhe@clinic.example','Dr Nompu Bhebhe',
                                                                                          '$2a$10$7q6C0h0z3aXy8R9wY1C2Qe8y3f9x2o7yZ9r2m9C6TQ3mQYJ7h2w8W','DOCTOR',
                                                                                          '44444444-4444-4444-4444-444444444444', NULL);

-- PATIENTS (linked by patient_id)
INSERT INTO users (id, email, full_name, password_hash, role, patient_id, doctor_id) VALUES
                                                                                         ('u-p1-0000-0000-0000-000000000001','alice@example.com','Alice Patient',
                                                                                          '$2a$10$7q6C0h0z3aXy8R9wY1C2Qe8y3f9x2o7yZ9r2m9C6TQ3mQYJ7h2w8W','PATIENT',
                                                                                          'aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1', NULL),

                                                                                         ('u-p2-0000-0000-0000-000000000002','bob@example.com','Bob Patient',
                                                                                          '$2a$10$7q6C0h0z3aXy8R9wY1C2Qe8y3f9x2o7yZ9r2m9C6TQ3mQYJ7h2w8W','PATIENT',
                                                                                          'aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2', NULL),

                                                                                         ('u-p3-0000-0000-0000-000000000003','carla@example.com','Carla Patient',
                                                                                          '$2a$10$7q6C0h0z3aXy8R9wY1C2Qe8y3f9x2o7yZ9r2m9C6TQ3mQYJ7h2w8W','PATIENT',
                                                                                          'aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3', NULL);

-- Sample appointments (today/tomorrow adjust as you like)
-- Use concrete timestamps or NOW()+INTERVAL math if preferred
INSERT INTO appointments (id, doctor_id, patient_id, start_time, status, reason) VALUES
                                                                                     ('appt-0001-0000-0000-0000-000000000001','11111111-1111-1111-1111-111111111111','aaaaaaa1-aaaa-aaaa-aaaa-aaaaaaaaaaa1','2025-10-22 09:00:00','BOOKED','Consultation'),
                                                                                     ('appt-0001-0000-0000-0000-000000000002','11111111-1111-1111-1111-111111111111','aaaaaaa2-aaaa-aaaa-aaaa-aaaaaaaaaaa2','2025-10-22 09:20:00','BOOKED','Follow-up'),
                                                                                     ('appt-0001-0000-0000-0000-000000000003','22222222-2222-2222-2222-222222222222','aaaaaaa3-aaaa-aaaa-aaaa-aaaaaaaaaaa3','2025-10-22 10:00:00','BOOKED','Check-up');
