-- Create leave_requests table
CREATE TABLE IF NOT EXISTS leave_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    employee_name VARCHAR(255),
    leave_type_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days DECIMAL(5,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    document_url VARCHAR(500),
    approver_id UUID,
    approver_name VARCHAR(255),
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    modification_note TEXT,
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    CONSTRAINT fk_leave_request_leave_type
        FOREIGN KEY (leave_type_id)
        REFERENCES leave_types(id)
        ON DELETE CASCADE,

    -- Check constraint for dates
    CONSTRAINT chk_leave_request_dates
        CHECK (end_date >= start_date)
);

-- Add trigger to leave_requests table
CREATE TRIGGER update_leave_requests_updated_at
    BEFORE UPDATE ON leave_requests
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create indexes for leave_requests
CREATE INDEX idx_leave_request_employee_id ON leave_requests(employee_id);
CREATE INDEX idx_leave_request_leave_type_id ON leave_requests(leave_type_id);
CREATE INDEX idx_leave_request_status ON leave_requests(status);
CREATE INDEX idx_leave_request_approver_id ON leave_requests(approver_id);
CREATE INDEX idx_leave_request_start_date ON leave_requests(start_date);
CREATE INDEX idx_leave_request_end_date ON leave_requests(end_date);
CREATE INDEX idx_leave_request_date_range ON leave_requests(start_date, end_date);
CREATE INDEX idx_leave_request_employee_status ON leave_requests(employee_id, status);
