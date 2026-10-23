CREATE TABLE notification_actions (
    notification_id BINARY(16) NOT NULL,
    action VARCHAR(50) NOT NULL,
    CONSTRAINT fk_notification_actions_notification
        FOREIGN KEY (notification_id)
        REFERENCES notification (id)
);

ALTER TABLE notification
    DROP COLUMN redirect_to;

INSERT INTO notification_actions (notification_id, action)
VALUES
    (UUID_TO_BIN('f39c1467-477d-4f3d-9f86-17b87ca72db1'), 'REDIRECT'),
    (UUID_TO_BIN('f39c1467-477d-4f3d-9f86-17b87ca72db1'), 'CLOSE'),
    (UUID_TO_BIN('72b69587-ec7e-4d78-b7e4-07dd6c76826e'), 'REDIRECT'),
    (UUID_TO_BIN('72b69587-ec7e-4d78-b7e4-07dd6c76826e'), 'CLOSE');

-- DROP TABLE IF EXISTS notification_actions;