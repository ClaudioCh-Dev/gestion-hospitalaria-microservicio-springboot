-- ============================================
-- TABLA: patients  (patient-ms)
-- ============================================
CREATE TABLE IF NOT EXISTS patients (
    id              BIGSERIAL    PRIMARY KEY,
    document_number VARCHAR(20)  NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    birth_date      DATE         NOT NULL,
    gender          VARCHAR(10),
    phone           VARCHAR(20),
    email           VARCHAR(150) UNIQUE,
    address         VARCHAR(255),
    blood_type      VARCHAR(5),
    allergies       TEXT,
    active          BOOLEAN      DEFAULT TRUE,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP    DEFAULT NOW()
);

-- ============================================
-- TABLA: specialties  (doctor-ms)
-- ============================================
CREATE TABLE IF NOT EXISTS specialties (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- ============================================
-- TABLA: doctors  (doctor-ms)
-- ============================================
CREATE TABLE IF NOT EXISTS doctors (
    id             BIGSERIAL    PRIMARY KEY,
    license_number VARCHAR(30)  NOT NULL UNIQUE,
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(150) UNIQUE,
    phone          VARCHAR(20),
    specialty_id   BIGINT REFERENCES specialties(id),
    schedule_start TIME,
    schedule_end   TIME,
    active         BOOLEAN      DEFAULT TRUE,
    created_at     TIMESTAMP    DEFAULT NOW()
);

-- ============================================
-- TABLA: appointments  (appointment-ms)
-- ============================================
CREATE TABLE IF NOT EXISTS appointments (
    id               BIGSERIAL   PRIMARY KEY,
    patient_id       BIGINT      NOT NULL,
    doctor_id        BIGINT      NOT NULL,
    scheduled_at     TIMESTAMP   NOT NULL,
    duration_minutes INT         DEFAULT 30,
    reason           TEXT,
    status           VARCHAR(20) DEFAULT 'SCHEDULED',
    -- SCHEDULED | CONFIRMED | COMPLETED | CANCELLED
    notes            TEXT,
    created_at       TIMESTAMP   DEFAULT NOW()
);

-- ============================================
-- TABLA: billing_records  (billing-ms)
-- ============================================
CREATE TABLE IF NOT EXISTS billing_records (
    id             BIGSERIAL      PRIMARY KEY,
    appointment_id BIGINT         NOT NULL,
    patient_id     BIGINT         NOT NULL,
    amount         DECIMAL(10, 2) NOT NULL,
    currency       VARCHAR(5)     DEFAULT 'PEN',
    status         VARCHAR(20)    DEFAULT 'PENDING',
    -- PENDING | PAID | CANCELLED
    issued_at      TIMESTAMP      DEFAULT NOW(),
    paid_at        TIMESTAMP
);