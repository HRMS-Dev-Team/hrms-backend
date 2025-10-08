-- Employee Service Initial Schema

CREATE TABLE IF NOT EXISTS employees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_number VARCHAR(50) UNIQUE NOT NULL,
    document_type VARCHAR(50),
    document_number VARCHAR(100),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20),
    father_names VARCHAR(200),
    mother_names VARCHAR(200),
    rssb_number VARCHAR(50),
    company_id UUID NOT NULL,
    company_name VARCHAR(255),
    nationality VARCHAR(100),
    date_of_birth VARCHAR(20),
    gender VARCHAR(20),
    marital_status VARCHAR(20),
    country VARCHAR(100),
    address TEXT,
    profile_picture UUID,

    -- Contract Details (Embedded)
    contract_document_id UUID,
    contract_start_date VARCHAR(20),
    contract_end_date VARCHAR(20),
    contract_status VARCHAR(20),
    is_in_probation_period BOOLEAN,
    probation_start_date VARCHAR(20),
    probation_end_date VARCHAR(20),
    probation_period_remarks TEXT,
    salary_type VARCHAR(20),
    salary_period_cycle_period INTEGER,
    base_salary DECIMAL(19,2),
    salary_currency UUID,
    level_id UUID,
    contract_job_type VARCHAR(20),
    contract_period_type VARCHAR(20),
    supervisor_id UUID,
    is_badging_member BOOLEAN,
    position_code VARCHAR(50),
    position_id UUID,
    level_code VARCHAR(50),

    -- Bank Details (Embedded)
    bank_id UUID,
    account_number VARCHAR(50),
    account_names VARCHAR(255),
    reference_code VARCHAR(50),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Dependents table (ElementCollection)
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

-- Dependent Documents table (nested ElementCollection)
CREATE TABLE IF NOT EXISTS dependent_documents (
    employee_id UUID NOT NULL,
    document_type UUID,
    document_number VARCHAR(100),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Contact Persons table (ElementCollection)
CREATE TABLE IF NOT EXISTS employee_contact_persons (
    employee_id UUID NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(255),
    relationship VARCHAR(100),
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Employee Roles table (ElementCollection)
CREATE TABLE IF NOT EXISTS employee_roles (
    employee_id UUID NOT NULL,
    role_id UUID,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_employee_number ON employees(employee_number);
CREATE INDEX idx_employee_email ON employees(email);
CREATE INDEX idx_employee_company_id ON employees(company_id);
CREATE INDEX idx_employee_contract_status ON employees(contract_status);
CREATE INDEX idx_employee_first_name ON employees(first_name);
CREATE INDEX idx_employee_last_name ON employees(last_name);

-- Add trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_employees_updated_at BEFORE UPDATE ON employees
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
