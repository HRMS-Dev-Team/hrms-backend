-- Add employee_id column to users table for linking users to employees
ALTER TABLE users
    ADD COLUMN employee_id UUID;

-- Add index for employee_id lookups
CREATE INDEX idx_users_employee_id ON users(employee_id);

-- Add comment
COMMENT ON COLUMN users.employee_id IS 'Foreign key reference to employee in employee service';
