CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE doctors (
    user_id BIGINT PRIMARY KEY,
    specialization VARCHAR(255) NOT NULL,
    availability BOOLEAN NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
);

CREATE TABLE patients (
    user_id BIGINT PRIMARY KEY, -- This will be the foreign key to users.id
    date_of_birth DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Users who will be doctors
INSERT INTO users (first_name, last_name, email, phone_number, password) VALUES
('Obe', 'Evans', 'obe.e@example.com', '0', 'hashedpass1'), -- User ID 1 (assuming auto-increment starts at 1)
('Sarah', 'Connor', 'sarah.c@example.com', '0823456789', 'hashedpass2'); -- User ID 2

-- Users who will be patients
INSERT INTO users (first_name, last_name, email, phone_number, password) VALUES
('Alice', 'Smith', 'alice.s@example.com', '0711112222', 'hashedpass3'), -- User ID 3
('Bob', 'Johnson', 'bob.j@example.com', '0722223333', 'hashedpass4'); -- User ID 4

INSERT INTO doctors (user_id, specialization, availability) VALUES
(1, 'Cardiology', TRUE),  -- Obe Evans is a Cardiologist
(2, 'Pediatrics', TRUE); -- Sarah Connor is a Pediatrician

INSERT INTO patients (user_id, date_of_birth) VALUES
(3, '1990-05-15'), -- Alice Smith
(4, '1985-11-22'); -- Bob Johnson