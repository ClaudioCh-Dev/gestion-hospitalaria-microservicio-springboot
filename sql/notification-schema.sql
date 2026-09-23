-- ============================================================
-- ============================================================
-- NOTIFICATION MS
-- ============================================================
-- ============================================================

-- ============================================================
-- TABLE: notifications
-- ============================================================

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE: notification_recipients
-- ============================================================

CREATE TABLE IF NOT EXISTS notification_recipients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    `read` BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP NULL,

    CONSTRAINT fk_notification_recipient
        FOREIGN KEY (notification_id)
        REFERENCES notifications(id)
        ON DELETE CASCADE
);

-- ============================================================
-- INDEXES: NOTIFICATION MS
-- ============================================================

CREATE INDEX idx_notifications_reference
    ON notifications(reference_type, reference_id);

CREATE INDEX idx_notification_recipients_user_id
    ON notification_recipients(user_id);

CREATE INDEX idx_notification_recipients_notification_id
    ON notification_recipients(notification_id);

CREATE INDEX idx_notification_recipients_user_read
    ON notification_recipients(user_id, `read`);