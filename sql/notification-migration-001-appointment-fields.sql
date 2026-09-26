-- ============================================================
-- NOTIFICATION MS — MIGRACIÓN 001
-- ============================================================
-- Solo para una BD notification_db que ya existía: notification-schema.sql se ejecuta
-- únicamente al crear el volumen de Docker. En una instalación nueva no hace falta.
--
--   docker exec -i db-notification mysql -uroot -p123456 notification_db \
--     < sql/notification-migration-001-appointment-fields.sql
--
-- Las notificaciones anteriores quedan con estas columnas en NULL: el admin las sigue
-- viendo, pero no aparecen en "mis notificaciones" del médico (no tienen doctor_user_id).
-- ============================================================

ALTER TABLE notifications
    ADD COLUMN doctor_id BIGINT,
    ADD COLUMN doctor_user_id BIGINT,
    ADD COLUMN patient_name VARCHAR(255),
    ADD COLUMN doctor_name VARCHAR(255),
    ADD COLUMN specialty VARCHAR(255),
    ADD COLUMN reason TEXT,
    ADD COLUMN appointment_status VARCHAR(50),
    ADD COLUMN scheduled_at TIMESTAMP NULL;

CREATE INDEX idx_notifications_doctor_user
    ON notifications(doctor_user_id, created_at);
