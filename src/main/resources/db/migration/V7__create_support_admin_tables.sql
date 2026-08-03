CREATE TABLE success_stories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bride_profile_id BIGINT NULL,
    groom_profile_id BIGINT NULL,
    bride_name VARCHAR(255) NULL,
    groom_name VARCHAR(255) NULL,
    story VARCHAR(3000) NOT NULL,
    marriage_date DATE NULL,
    photo_url VARCHAR(255) NULL,
    status VARCHAR(30) NOT NULL,
    submitted_by BIGINT NULL,
    approved_by BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP NULL,
    CONSTRAINT fk_story_bride FOREIGN KEY (bride_profile_id) REFERENCES matrimony_profiles(id),
    CONSTRAINT fk_story_groom FOREIGN KEY (groom_profile_id) REFERENCES matrimony_profiles(id),
    CONSTRAINT fk_story_submitter FOREIGN KEY (submitted_by) REFERENCES users(id),
    CONSTRAINT fk_story_approver FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category VARCHAR(255) NULL,
    subject VARCHAR(255) NOT NULL,
    description VARCHAR(3000) NOT NULL,
    status VARCHAR(30) NOT NULL,
    priority VARCHAR(30) NOT NULL,
    assigned_to BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ticket_assignee FOREIGN KEY (assigned_to) REFERENCES users(id)
);

CREATE TABLE support_ticket_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    sender_user_id BIGINT NOT NULL,
    message VARCHAR(3000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_message_ticket FOREIGN KEY (ticket_id) REFERENCES support_tickets(id),
    CONSTRAINT fk_ticket_message_sender FOREIGN KEY (sender_user_id) REFERENCES users(id)
);

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_user_id BIGINT NULL,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(255) NOT NULL,
    entity_id BIGINT NULL,
    old_value VARCHAR(4000) NULL,
    new_value VARCHAR(4000) NULL,
    ip_address VARCHAR(255) NULL,
    user_agent VARCHAR(500) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

CREATE TABLE relationship_manager_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    manager_id BIGINT NOT NULL,
    customer_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_rm_customer UNIQUE (manager_id, customer_user_id),
    CONSTRAINT fk_rm_assignment_manager FOREIGN KEY (manager_id) REFERENCES users(id),
    CONSTRAINT fk_rm_assignment_customer FOREIGN KEY (customer_user_id) REFERENCES users(id)
);

CREATE TABLE relationship_manager_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    manager_id BIGINT NOT NULL,
    customer_user_id BIGINT NOT NULL,
    note VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rm_note_manager FOREIGN KEY (manager_id) REFERENCES users(id),
    CONSTRAINT fk_rm_note_customer FOREIGN KEY (customer_user_id) REFERENCES users(id)
);

CREATE TABLE relationship_manager_suggestions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    manager_id BIGINT NOT NULL,
    customer_user_id BIGINT NOT NULL,
    suggested_profile_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rm_suggestion_manager FOREIGN KEY (manager_id) REFERENCES users(id),
    CONSTRAINT fk_rm_suggestion_customer FOREIGN KEY (customer_user_id) REFERENCES users(id),
    CONSTRAINT fk_rm_suggestion_profile FOREIGN KEY (suggested_profile_id) REFERENCES matrimony_profiles(id)
);
