-- Create leave_balances table
CREATE TABLE IF NOT EXISTS leave_balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    leave_type_id UUID NOT NULL,
    year INT NOT NULL,
    total_allocated DECIMAL(5,2) NOT NULL,
    used DECIMAL(5,2) NOT NULL DEFAULT 0,
    pending DECIMAL(5,2) NOT NULL DEFAULT 0,
    available DECIMAL(5,2) NOT NULL,
    carried_forward DECIMAL(5,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    CONSTRAINT fk_leave_balance_leave_type
        FOREIGN KEY (leave_type_id)
        REFERENCES leave_types(id)
        ON DELETE CASCADE,

    -- Unique constraint to prevent duplicate balances
    CONSTRAINT uq_employee_leavetype_year
        UNIQUE (employee_id, leave_type_id, year)
);

-- Add trigger to leave_balances table
CREATE TRIGGER update_leave_balances_updated_at
    BEFORE UPDATE ON leave_balances
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create indexes for leave_balances
CREATE INDEX idx_leave_balance_employee_id ON leave_balances(employee_id);
CREATE INDEX idx_leave_balance_leave_type_id ON leave_balances(leave_type_id);
CREATE INDEX idx_leave_balance_year ON leave_balances(year);
CREATE INDEX idx_leave_balance_employee_year ON leave_balances(employee_id, year);
