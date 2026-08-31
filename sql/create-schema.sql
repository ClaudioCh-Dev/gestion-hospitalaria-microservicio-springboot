-- ============================================================
-- ENUM: GENDER
-- ============================================================

CREATE TYPE gender AS ENUM (
    'MALE',
    'FEMALE'
);


-- ============================================================
-- ENUM: BLOOD TYPE
-- ============================================================

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


-- ============================================================
-- ENUM: APPOINTMENT STATUS
-- ============================================================

CREATE TYPE appointment_status AS ENUM (
    'SCHEDULED',
    'CONFIRMED',
    'COMPLETED',
    'CANCELLED'
);


-- ============================================================
-- ENUM: BILLING STATUS
-- ============================================================

CREATE TYPE billing_status AS ENUM (
    'PENDING',
    'PAID',
    'CANCELLED'
);


-- ============================================================
-- ============================================================
-- PATIENT MS
-- ============================================================
-- ============================================================


-- ============================================================
-- TABLE: patients
-- ============================================================

CREATE TABLE IF NOT EXISTS patients (
    id               BIGSERIAL PRIMARY KEY,
    document_number  VARCHAR(20) NOT NULL UNIQUE,
    first_name       VARCHAR(100) NOT NULL,
    last_name        VARCHAR(100) NOT NULL,
    birth_date       DATE NOT NULL,
    gender           gender,
    phone            VARCHAR(20),
    email            VARCHAR(150) UNIQUE,
    address          VARCHAR(255),
    blood_type       blood_type,
    allergies        TEXT,
    active           BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW()
);


-- ============================================================
-- ============================================================
-- DOCTOR MS
-- ============================================================
-- ============================================================


-- ============================================================
-- TABLE: specialties
-- ============================================================

CREATE TABLE IF NOT EXISTS specialties (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  TEXT
);


-- ============================================================
-- TABLE: doctors
-- ============================================================

CREATE TABLE IF NOT EXISTS doctors (
    id              BIGSERIAL PRIMARY KEY,
    license_number  VARCHAR(30) NOT NULL UNIQUE,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(150) UNIQUE,
    phone           VARCHAR(20),
    specialty_id    BIGINT REFERENCES specialties(id),
    schedule_start  TIME,
    schedule_end    TIME,
    active          BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT NOW()
);


-- ============================================================
-- ============================================================
-- APPOINTMENT MS
-- ============================================================
-- ============================================================


-- ============================================================
-- TABLE: appointment_types
-- ============================================================

CREATE TABLE IF NOT EXISTS appointment_types (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(150) NOT NULL UNIQUE,
    description  TEXT,
    active       BOOLEAN NOT NULL DEFAULT TRUE
);


-- ============================================================
-- TABLE: doctors_appointment
-- ============================================================

CREATE TABLE IF NOT EXISTS doctors_appointment (
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT NOT NULL UNIQUE,
    full_name  VARCHAR(255),
    specialty  VARCHAR(255)
);


-- ============================================================
-- TABLE: patients_appointment
-- ============================================================

CREATE TABLE IF NOT EXISTS patients_appointment (
    id         BIGINT PRIMARY KEY,
    full_name  VARCHAR(255)
);


-- ============================================================
-- TABLE: appointments
-- ============================================================

CREATE TABLE IF NOT EXISTS appointments (
    id                  BIGSERIAL PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    doctor_id           BIGINT NOT NULL,
    scheduled_at        TIMESTAMP NOT NULL,
    duration_minutes    INT DEFAULT 30,
    reason              TEXT,
    status              appointment_status NOT NULL
                        DEFAULT 'SCHEDULED',
    notes               TEXT,
    appointment_type_id BIGINT NOT NULL,

    created_at          TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_appointment_type
        FOREIGN KEY (appointment_type_id)
        REFERENCES appointment_types(id)
);


-- ============================================================
-- INDEXES: APPOINTMENT MS
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_appointments_patient_id
    ON appointments(patient_id);

CREATE INDEX IF NOT EXISTS idx_appointments_doctor_id
    ON appointments(doctor_id);

CREATE INDEX IF NOT EXISTS idx_appointments_type_id
    ON appointments(appointment_type_id);

CREATE INDEX IF NOT EXISTS idx_doctors_appointment_user_id
    ON doctors_appointment(user_id);


-- ============================================================
-- ============================================================
-- BILLING MS
-- ============================================================
-- ============================================================


-- ============================================================
-- TABLE: billing_tariffs
-- ============================================================

CREATE TABLE IF NOT EXISTS billing_tariffs (
    billing_appointment_type_id BIGINT PRIMARY KEY,
    price                       DECIMAL(10, 2) NOT NULL,
    currency                    VARCHAR(3) NOT NULL DEFAULT 'PEN'
);


-- ============================================================
-- TABLE: billing_records
-- ============================================================

CREATE TABLE IF NOT EXISTS billing_records (
    id              BIGSERIAL PRIMARY KEY,
    appointment_id  BIGINT NOT NULL UNIQUE,
    patient_id      BIGINT NOT NULL,
    amount          DECIMAL(10, 2) NOT NULL,
    currency        VARCHAR(5) NOT NULL DEFAULT 'PEN',
    status          billing_status NOT NULL DEFAULT 'PENDING',
    issued_at       TIMESTAMP DEFAULT NOW(),
    paid_at         TIMESTAMP
);


-- ============================================================
-- INDEXES: BILLING MS
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_billing_records_patient_id
    ON billing_records(patient_id);

CREATE INDEX IF NOT EXISTS idx_billing_records_appointment_id
    ON billing_records(appointment_id);


-- ============================================================
-- ============================================================
-- NOTIFICATION MS
-- ============================================================
-- ============================================================


-- ============================================================
-- TABLE: notifications
-- ============================================================

CREATE TABLE IF NOT EXISTS notifications (
    id               BIGSERIAL PRIMARY KEY,
    type             VARCHAR(100) NOT NULL,
    title            VARCHAR(255) NOT NULL,
    message          TEXT NOT NULL,
    reference_type   VARCHAR(50),
    reference_id     BIGINT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);


-- ============================================================
-- TABLE: notification_recipients
-- ============================================================

CREATE TABLE IF NOT EXISTS notification_recipients (
    id               BIGSERIAL PRIMARY KEY,
    notification_id  BIGINT NOT NULL,
    user_id          BIGINT NOT NULL,
    read             BOOLEAN NOT NULL DEFAULT FALSE,
    read_at          TIMESTAMP,

    CONSTRAINT fk_notification_recipient
        FOREIGN KEY (notification_id)
        REFERENCES notifications(id)
        ON DELETE CASCADE
);


-- ============================================================
-- INDEXES: NOTIFICATION MS
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_notifications_reference
    ON notifications(reference_type, reference_id);

CREATE INDEX IF NOT EXISTS idx_notification_recipients_user_id
    ON notification_recipients(user_id);

CREATE INDEX IF NOT EXISTS idx_notification_recipients_notification_id
    ON notification_recipients(notification_id);

CREATE INDEX IF NOT EXISTS idx_notification_recipients_user_read
    ON notification_recipients(user_id, read);