-- Employee Contact Persons table (ElementCollection)
CREATE TABLE IF NOT EXISTS employee_contact_persons (
    employee_id UUID NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(255),
    relationship VARCHAR(100),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);
