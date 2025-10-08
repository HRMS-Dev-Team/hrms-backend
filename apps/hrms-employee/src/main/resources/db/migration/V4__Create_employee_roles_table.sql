-- Employee Roles table (ElementCollection)
CREATE TABLE IF NOT EXISTS employee_roles (
    employee_id UUID NOT NULL,
    role_id UUID,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
