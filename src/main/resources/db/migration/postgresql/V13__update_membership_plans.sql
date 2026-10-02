-- Deactivate all old plans
UPDATE membership_plans SET active = FALSE;

-- Reactivate FREE
UPDATE membership_plans SET active = TRUE WHERE code = 'FREE';

-- Insert or update the two new plans requested by the user without deleting to avoid foreign key constraint errors
INSERT INTO membership_plans(name, code, description, price, currency, duration_days, daily_interest_limit, contact_view_limit, message_limit, profile_boost_allowed, assisted_service, active)
VALUES
('Single Profile', 'SINGLE', 'View contact details and message 1 profile.', 100.00, 'INR', 30, 10, 1, 10, FALSE, FALSE, TRUE),
('Standard', 'STANDARD', '15 members contact number and messages for 3 months.', 999.00, 'INR', 90, 50, 15, 150, TRUE, FALSE, TRUE)
ON CONFLICT (code) DO UPDATE SET
name = EXCLUDED.name,
description = EXCLUDED.description,
price = EXCLUDED.price,
currency = EXCLUDED.currency,
duration_days = EXCLUDED.duration_days,
daily_interest_limit = EXCLUDED.daily_interest_limit,
contact_view_limit = EXCLUDED.contact_view_limit,
message_limit = EXCLUDED.message_limit,
profile_boost_allowed = EXCLUDED.profile_boost_allowed,
assisted_service = EXCLUDED.assisted_service,
active = EXCLUDED.active;
