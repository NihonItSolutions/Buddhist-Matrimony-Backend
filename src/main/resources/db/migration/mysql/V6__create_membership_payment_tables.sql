CREATE TABLE membership_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(40) NOT NULL,
    description VARCHAR(1200) NULL,
    price DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    duration_days INT NOT NULL,
    daily_interest_limit INT NOT NULL,
    contact_view_limit INT NOT NULL,
    message_limit INT NOT NULL,
    profile_boost_allowed BOOLEAN NOT NULL DEFAULT FALSE,
    assisted_service BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_membership_plan_code UNIQUE (code)
);

CREATE TABLE user_subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    membership_plan_id BIGINT NOT NULL,
    start_date TIMESTAMP NULL,
    end_date TIMESTAMP NULL,
    status VARCHAR(30) NOT NULL,
    remaining_contact_views INT NULL,
    remaining_messages INT NULL,
    remaining_interests_today INT NULL,
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscription_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_subscription_plan FOREIGN KEY (membership_plan_id) REFERENCES membership_plans(id)
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT NULL,
    payment_gateway VARCHAR(255) NULL,
    gateway_order_id VARCHAR(255) NULL,
    gateway_payment_id VARCHAR(255) NULL,
    gateway_signature VARCHAR(255) NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(30) NOT NULL,
    failure_reason VARCHAR(800) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_payment_subscription FOREIGN KEY (subscription_id) REFERENCES user_subscriptions(id)
);

CREATE TABLE payment_webhook_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL,
    gateway VARCHAR(255) NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_webhook_event UNIQUE (event_id)
);
