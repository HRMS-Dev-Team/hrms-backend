-- Create salary_advance table
CREATE TABLE IF NOT EXISTS salary_advance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    requested_amount NUMERIC(10, 2) NOT NULL,
    approved_amount NUMERIC(10, 2),
    installments INT NOT NULL DEFAULT 3,
    installment_amount NUMERIC(10, 2),
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(20) NOT NULL DEFAULT 'REQUESTED',
    reason TEXT,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    scheduled_repayment_start DATE,
    paid_off_at TIMESTAMP,
    rejection_reason TEXT,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_status CHECK (status IN ('REQUESTED', 'APPROVED', 'ACTIVE', 'PAID_OFF', 'REJECTED', 'CANCELLED')),
    CONSTRAINT chk_requested_amount CHECK (requested_amount > 0),
    CONSTRAINT chk_approved_amount CHECK (approved_amount IS NULL OR approved_amount > 0),
    CONSTRAINT chk_installments CHECK (installments BETWEEN 1 AND 12)
);

-- Create trigger function for updating timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Add trigger to salary_advance table
CREATE TRIGGER update_salary_advance_updated_at
    BEFORE UPDATE ON salary_advance
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create indexes for salary_advance
CREATE INDEX idx_salary_advance_employee_id ON salary_advance(employee_id);
CREATE INDEX idx_salary_advance_status ON salary_advance(status);
CREATE INDEX idx_salary_advance_requested_at ON salary_advance(requested_at);
CREATE INDEX idx_salary_advance_approved_at ON salary_advance(approved_at);
