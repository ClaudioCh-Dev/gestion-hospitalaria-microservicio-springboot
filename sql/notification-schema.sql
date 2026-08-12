CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    patient_name VARCHAR(255),
    doctor_id BIGINT NOT NULL,
    doctor_name VARCHAR(255),
    specialty VARCHAR(255),
    event_type VARCHAR(100),
    status VARCHAR(50),
    reason VARCHAR(500),
    scheduled_at DATETIME,
    amount DECIMAL(10, 2),
    doctor_read BOOLEAN NOT NULL DEFAULT FALSE,
    admin_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL
);