-- Salary Advance Service Initial Schema

CREATE TABLE IF NOT EXISTS salary_advance_requests (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    request_date DATE NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    reason TEXT,
    repayment_months INTEGER NOT NULL,
    monthly_deduction DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by BIGINT,
    approved_at TIMESTAMP,
    disbursed_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS advance_repayments (
    id BIGSERIAL PRIMARY KEY,
    advance_request_id BIGINT NOT NULL REFERENCES salary_advance_requests(id),
    month_year VARCHAR(7) NOT NULL, -- Format: YYYY-MM
    scheduled_amount DECIMAL(12,2) NOT NULL,
    paid_amount DECIMAL(12,2) DEFAULT 0,
    payment_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(advance_request_id, month_year)
);

CREATE INDEX idx_salary_advance_employee ON salary_advance_requests(employee_id);
CREATE INDEX idx_salary_advance_status ON salary_advance_requests(status);
CREATE INDEX idx_advance_repayments_request ON advance_repayments(advance_request_id);
CREATE INDEX idx_advance_repayments_status ON advance_repayments(status);

-- Add trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_salary_advance_requests_updated_at BEFORE UPDATE ON salary_advance_requests
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_advance_repayments_updated_at BEFORE UPDATE ON advance_repayments
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
