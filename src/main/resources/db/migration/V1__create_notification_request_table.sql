CREATE TABLE notification_request (
    id BIGSERIAL PRIMARY KEY,
    recipient_email VARCHAR(320) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    template_id VARCHAR(100) NOT NULL,
    template_data TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
