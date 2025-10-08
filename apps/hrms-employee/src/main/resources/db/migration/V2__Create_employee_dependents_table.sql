-- Employee Dependents table (ElementCollection)
CREATE TABLE IF NOT EXISTS employee_dependents (
    employee_id UUID NOT NULL,
    dependent_type_id UUID,
    date_of_birth VARCHAR(20),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    gender VARCHAR(20),
    phone_number VARCHAR(20),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
