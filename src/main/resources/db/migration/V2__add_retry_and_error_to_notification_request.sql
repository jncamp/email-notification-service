ALTER TABLE notification_request
ADD COLUMN retry_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE notification_request
ADD COLUMN last_error TEXT;
