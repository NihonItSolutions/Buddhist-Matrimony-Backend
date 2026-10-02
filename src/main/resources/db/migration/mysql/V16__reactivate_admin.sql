UPDATE users SET account_status = 'ACTIVE' WHERE role = 'ADMIN' OR email LIKE '%admin%';
