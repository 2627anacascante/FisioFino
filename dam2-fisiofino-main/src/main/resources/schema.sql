-- Model relacional de la pràctica (Patient, Patient_disease, Disease).

CREATE TABLE IF NOT EXISTS patient (
    id_patient   SERIAL PRIMARY KEY,
    given_name   VARCHAR(100) NOT NULL,
    family_name  VARCHAR(150) NOT NULL,
    nif          VARCHAR(20)  NOT NULL,
    birth_date   DATE,
    gender       VARCHAR(5),
    address      VARCHAR(200),
    city         VARCHAR(100),
    postal_code  VARCHAR(10),
    country      VARCHAR(5),
    telephone    VARCHAR(30),
    email        VARCHAR(150)
);

    -- private String treatment;
    -- private String dniPacient;
    -- private String status;
    -- private String code;
    -- private String name;
    -- private int dose;
    -- private String unit;
    -- private String frequency;

CREATE TABLE IF NOT EXISTS patient_disease (
    id_malaltia_pacient SERIAL PRIMARY KEY,
    treatment VARCHAR(100),
    dniPacient VARCHAR(20) NOT NULL,
    malaltia_status VARCHAR(20),
    code VARCHAR(30),
    malaltia_name VARCHAR(100),
    dose INT,
    unit VARCHAR(3),
    frequency VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS disease (
    id_malaltia SERIAL PRIMARY KEY,
    malaltia_name VARCHAR(100),
    code VARCHAR(30)
);

ALTER TABLE patient_disease
    ALTER COLUMN malaltia_name TYPE VARCHAR(100);

ALTER TABLE disease
    ALTER COLUMN malaltia_name TYPE VARCHAR(100);