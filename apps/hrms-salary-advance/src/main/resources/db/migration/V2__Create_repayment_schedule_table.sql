-- Create repayment_schedule table
CREATE TABLE IF NOT EXISTS repayment_schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    salary_advance_id UUID NOT NULL,
    installment_number INT NOT NULL,
    due_date DATE NOT NULL,
    due_amount NUMERIC(10, 2) NOT NULL,
    paid_amount NUMERIC(10, 2),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP,
    payment_reference VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_repayment_salary_advance FOREIGN KEY (salary_advance_id)
        REFERENCES salary_advance(id) ON DELETE CASCADE,
    CONSTRAINT chk_repayment_status CHECK (status IN ('PENDING', 'PAID', 'PARTIAL', 'FAILED')),
    CONSTRAINT chk_due_amount CHECK (due_amount > 0),
    CONSTRAINT chk_paid_amount CHECK (paid_amount IS NULL OR paid_amount >= 0),
    CONSTRAINT chk_installment_number CHECK (installment_number > 0)
);

-- Add trigger to repayment_schedule table
CREATE TRIGGER update_repayment_schedule_updated_at
    BEFORE UPDATE ON repayment_schedule
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create indexes for repayment_schedule
CREATE INDEX idx_repayment_salary_advance_id ON repayment_schedule(salary_advance_id);
CREATE INDEX idx_repayment_status ON repayment_schedule(status);
CREATE INDEX idx_repayment_due_date ON repayment_schedule(due_date);
CREATE INDEX idx_repayment_installment_number ON repayment_schedule(installment_number);
