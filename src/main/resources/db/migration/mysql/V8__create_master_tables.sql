CREATE TABLE master_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(60) NOT NULL,
    name VARCHAR(160) NOT NULL,
    code VARCHAR(160) NOT NULL,
    parent_id BIGINT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_master_parent FOREIGN KEY (parent_id) REFERENCES master_data(id)
);
