-- Create leave_types table
CREATE TABLE IF NOT EXISTS leave_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    company_id UUID NOT NULL,
    default_days_per_year INT,
    max_consecutive_days INT,
    requires_document BOOLEAN DEFAULT FALSE,
    min_notice_days INT DEFAULT 0,
    is_paid BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    allow_carry_forward BOOLEAN DEFAULT FALSE,
    max_carry_forward_days INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create trigger function for updating timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add trigger to leave_types table
CREATE TRIGGER update_leave_types_updated_at
    BEFORE UPDATE ON leave_types
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create indexes for leave_types
CREATE INDEX idx_leave_type_code ON leave_types(code);
CREATE INDEX idx_leave_type_company_id ON leave_types(company_id);
CREATE INDEX idx_leave_type_category ON leave_types(category);
CREATE INDEX idx_leave_type_is_active ON leave_types(is_active);
