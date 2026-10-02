ALTER TABLE payments ADD COLUMN amount_received DECIMAL(12, 2) NULL;
ALTER TABLE payments ADD COLUMN balance_utr VARCHAR(255) NULL;
