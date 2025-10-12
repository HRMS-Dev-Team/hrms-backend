-- Create departments table
CREATE TABLE IF NOT EXISTS departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    company_id UUID NOT NULL,
    manager_id UUID,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX idx_department_code ON departments(code);
CREATE INDEX idx_department_company_id ON departments(company_id);
CREATE INDEX idx_department_is_active ON departments(is_active);
CREATE INDEX idx_department_name ON departments(name);

-- Add trigger to update updated_at timestamp
CREATE TRIGGER update_departments_updated_at BEFORE UPDATE ON departments
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Add department_id column to employees table
ALTER TABLE employees ADD COLUMN IF NOT EXISTS department_id UUID;

-- Add foreign key constraint
ALTER TABLE employees ADD CONSTRAINT fk_employee_department
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL;

-- Create index on department_id in employees table
CREATE INDEX idx_employee_department_id ON employees(department_id);
