CREATE TABLE conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_one_id BIGINT NOT NULL,
    profile_two_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_conversation_pair UNIQUE (profile_one_id, profile_two_id),
    CONSTRAINT fk_conversation_one FOREIGN KEY (profile_one_id) REFERENCES matrimony_profiles(id),
    CONSTRAINT fk_conversation_two FOREIGN KEY (profile_two_id) REFERENCES matrimony_profiles(id)
);

CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_profile_id BIGINT NOT NULL,
    message_text VARCHAR(2000) NULL,
    message_type VARCHAR(30) NOT NULL,
    attachment_url VARCHAR(255) NULL,
    sent_at TIMESTAMP NOT NULL,
    read_at TIMESTAMP NULL,
    deleted_by_sender BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_by_receiver BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_profile_id) REFERENCES matrimony_profiles(id)
);

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(60) NOT NULL,
    title VARCHAR(180) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    reference_type VARCHAR(255) NULL,
    reference_id BIGINT NULL,
    read_flag BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
);
