INSERT INTO roles(name) VALUES ('USER'), ('ADMIN'), ('MODERATOR'), ('RELATIONSHIP_MANAGER');

INSERT INTO membership_plans(name, code, description, price, currency, duration_days, daily_interest_limit, contact_view_limit, message_limit, profile_boost_allowed, assisted_service, active)
VALUES
('Free', 'FREE', 'Starter plan with limited discovery features.', 0.00, 'INR', 3650, 5, 0, 0, FALSE, FALSE, TRUE),
('Basic', 'BASIC', 'Basic paid plan for profile interactions.', 999.00, 'INR', 90, 20, 10, 100, FALSE, FALSE, TRUE),
('Premium', 'PREMIUM', 'Premium plan with expanded contact and messaging limits.', 2499.00, 'INR', 180, 50, 40, 500, TRUE, FALSE, TRUE),
('Premium Plus', 'PREMIUM_PLUS', 'High visibility plan for serious matchmaking.', 4999.00, 'INR', 365, 100, 100, 2000, TRUE, FALSE, TRUE),
('Assisted', 'ASSISTED', 'Assisted matchmaking plan with relationship-manager support.', 9999.00, 'INR', 365, 150, 150, 5000, TRUE, TRUE, TRUE);

INSERT INTO master_data(type, name, code, parent_id, active, display_order) VALUES
('COUNTRY', 'India', 'IN', NULL, TRUE, 1),
('RELIGION', 'Buddhist', 'BUDDHIST', NULL, TRUE, 1),
('MOTHER_TONGUE', 'Marathi', 'MARATHI', NULL, TRUE, 1),
('EDUCATION_LEVEL', 'Engineering', 'ENGINEERING', NULL, TRUE, 1),
('EDUCATION_LEVEL', 'Medicine', 'MEDICINE', NULL, TRUE, 2),
('EDUCATION_LEVEL', 'Arts', 'ARTS', NULL, TRUE, 3),
('EDUCATION_LEVEL', 'Commerce', 'COMMERCE', NULL, TRUE, 4),
('OCCUPATION', 'Software Engineer', 'SOFTWARE_ENGINEER', NULL, TRUE, 1),
('OCCUPATION', 'Doctor', 'DOCTOR', NULL, TRUE, 2),
('OCCUPATION', 'Teacher', 'TEACHER', NULL, TRUE, 3),
('OCCUPATION', 'Business Owner', 'BUSINESS_OWNER', NULL, TRUE, 4),
('HOBBY', 'Reading', 'READING', NULL, TRUE, 1),
('HOBBY', 'Travel', 'TRAVEL', NULL, TRUE, 2),
('INTEREST', 'Music', 'MUSIC', NULL, TRUE, 1),
('INTEREST', 'Meditation', 'MEDITATION', NULL, TRUE, 2),
('RASHI', 'Mesh', 'MESH', NULL, TRUE, 1),
('NAKSHATRA', 'Ashwini', 'ASHWINI', NULL, TRUE, 1);

INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'STATE', 'Maharashtra', 'MH', id, TRUE, 1 FROM master_data WHERE type = 'COUNTRY' AND code = 'IN';

INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'DISTRICT', 'Pune', 'PUNE_DISTRICT', id, TRUE, 1 FROM master_data WHERE type = 'STATE' AND code = 'MH';
INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'DISTRICT', 'Mumbai', 'MUMBAI_DISTRICT', id, TRUE, 2 FROM master_data WHERE type = 'STATE' AND code = 'MH';
INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'DISTRICT', 'Nagpur', 'NAGPUR_DISTRICT', id, TRUE, 3 FROM master_data WHERE type = 'STATE' AND code = 'MH';

INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'CITY', 'Pune', 'PUNE', id, TRUE, 1 FROM master_data WHERE type = 'DISTRICT' AND code = 'PUNE_DISTRICT';
INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'CITY', 'Pimpri-Chinchwad', 'PIMPRI_CHINCHWAD', id, TRUE, 2 FROM master_data WHERE type = 'DISTRICT' AND code = 'PUNE_DISTRICT';
INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'CITY', 'Mumbai', 'MUMBAI', id, TRUE, 1 FROM master_data WHERE type = 'DISTRICT' AND code = 'MUMBAI_DISTRICT';
INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'CITY', 'Nagpur', 'NAGPUR', id, TRUE, 1 FROM master_data WHERE type = 'DISTRICT' AND code = 'NAGPUR_DISTRICT';

INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'COMMUNITY', 'Navayana Buddhist', 'NAVAYANA_BUDDHIST', id, TRUE, 1 FROM master_data WHERE type = 'RELIGION' AND code = 'BUDDHIST';
INSERT INTO master_data(type, name, code, parent_id, active, display_order)
SELECT 'SUB_COMMUNITY', 'Maharashtrian Buddhist', 'MAHARASHTRIAN_BUDDHIST', id, TRUE, 1 FROM master_data WHERE type = 'COMMUNITY' AND code = 'NAVAYANA_BUDDHIST';
