-- Fix admin user roles (remove ROLE_ prefix)
DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin') AND role LIKE 'ROLE_%';

-- Insert correct roles without ROLE_ prefix
INSERT INTO user_roles (user_id, role)
SELECT id, 'ADMIN' FROM users WHERE username = 'admin'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT id, 'HR' FROM users WHERE username = 'admin'
ON CONFLICT DO NOTHING;
