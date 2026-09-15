ALTER TABLE notification ADD COLUMN icon VARCHAR(255);
ALTER TABLE notification ADD COLUMN redirect_to VARCHAR(255);

ALTER TABLE notification_delivery
    ADD COLUMN notification_id BINARY(16) NOT NULL;

ALTER TABLE notification_delivery
    ADD CONSTRAINT fk_notification_delivery_notification
        FOREIGN KEY (notification_id)
        REFERENCES notification(id);