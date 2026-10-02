ALTER TABLE matrimony_profiles
    ADD COLUMN leaving_certificate_url VARCHAR(255) NULL AFTER residency_status,
    ADD COLUMN aadhar_card_url VARCHAR(255) NULL AFTER leaving_certificate_url;
