-- ============================================
-- ENUM: GENDER
-- ============================================

CREATE TYPE gender AS ENUM (
    'MALE',
    'FEMALE'
);


-- ============================================
-- ENUM: BLOOD TYPE
-- ============================================

CREATE TYPE blood_type AS ENUM (
    'A_POSITIVE',
    'A_NEGATIVE',
    'B_POSITIVE',
    'B_NEGATIVE',
    'AB_POSITIVE',
    'AB_NEGATIVE',
    'O_POSITIVE',
    'O_NEGATIVE'
);


-- ============================================
-- ENUM: APPOINTMENT STATUS
-- ============================================

CREATE TYPE appointment_status AS ENUM (
    'SCHEDULED',
    'CONFIRMED',
    'COMPLETED',
    'CANCELLED'
);


-- ============================================
-- ENUM: BILLING STATUS
-- ============================================

CREATE TYPE billing_status AS ENUM (
    'PENDING',
    'PAID',
    'CANCELLED'
);


-- ============================================
-- TABLA: patients
-- ============================================

CREATE TABLE IF NOT EXISTS patients (
    id              BIGSERIAL PRIMARY KEY,
    document_number VARCHAR(20)  NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    birth_date      DATE         NOT NULL,
    gender          gender,
    phone           VARCHAR(20),
    email           VARCHAR(150) UNIQUE,
    address         VARCHAR(255),
    blood_type      blood_type,
    allergies       TEXT,
    active          BOOLEAN      DEFAULT TRUE,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP    DEFAULT NOW()
);


-- ============================================
-- TABLA: specialties
-- ============================================

CREATE TABLE IF NOT EXISTS specialties (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);


-- ============================================
-- TABLA: doctors
-- ============================================

CREATE TABLE IF NOT EXISTS doctors (
    id             BIGSERIAL PRIMARY KEY,
    license_number VARCHAR(30)  NOT NULL UNIQUE,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(150) UNIQUE,
    phone          VARCHAR(20),
    specialty_id   BIGINT REFERENCES specialties(id),
    schedule_start TIME,
    schedule_end   TIME,
    active         BOOLEAN DEFAULT TRUE,
    created_at     TIMESTAMP DEFAULT NOW()
);

-- ============================================
-- TABLA: appointment_types
-- ============================================

CREATE TABLE IF NOT EXISTS appointment_types (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE
);


-- ============================================
-- TABLA: appointments
-- ============================================

CREATE TABLE IF NOT EXISTS appointments (
    id                  BIGSERIAL PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    doctor_id           BIGINT NOT NULL,
    scheduled_at        TIMESTAMP NOT NULL,
    duration_minutes   INT DEFAULT 30,
    reason              TEXT,
    status              appointment_status DEFAULT 'SCHEDULED',
    notes               TEXT,
    appointment_type_id BIGINT NOT NULL,
    created_at          TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_appointment_type
        FOREIGN KEY (appointment_type_id)
        REFERENCES appointment_types(id)
);

-- ============================================
-- TABLA: billing_records
-- ============================================

CREATE TABLE IF NOT EXISTS billing_records (
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    patient_id     BIGINT NOT NULL,
    amount         DECIMAL(10, 2) NOT NULL,
    currency       VARCHAR(5) DEFAULT 'PEN',
    status         billing_status DEFAULT 'PENDING',
    issued_at      TIMESTAMP DEFAULT NOW(),
    paid_at        TIMESTAMP
);