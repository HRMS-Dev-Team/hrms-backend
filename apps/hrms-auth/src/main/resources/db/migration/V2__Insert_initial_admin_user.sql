-- Insert initial admin user
-- Password is: Admin@123
-- BCrypt hash generated with strength 10
INSERT INTO users (username, email, password, first_name, last_name, is_enabled, is_account_non_expired, is_account_non_locked, is_credentials_non_expired)
VALUES ('admin', 'admin@hrms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'User', true, true, true, true)
ON CONFLICT (username) DO NOTHING;

-- Assign ADMIN and HR roles to admin user
INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_ADMIN' FROM users WHERE username = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'ROLE_HR' FROM users WHERE username = 'admin'
ON CONFLICT DO NOTHING;
