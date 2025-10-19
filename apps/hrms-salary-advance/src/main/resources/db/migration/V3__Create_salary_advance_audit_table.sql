-- Create salary_advance_audit table
CREATE TABLE IF NOT EXISTS salary_advance_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    salary_advance_id UUID NOT NULL,
    action VARCHAR(100) NOT NULL,
    actor VARCHAR(100),
    details JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_salary_advance FOREIGN KEY (salary_advance_id)
        REFERENCES salary_advance(id) ON DELETE CASCADE
);

-- Create indexes for salary_advance_audit
CREATE INDEX idx_audit_salary_advance_id ON salary_advance_audit(salary_advance_id);
CREATE INDEX idx_audit_action ON salary_advance_audit(action);
CREATE INDEX idx_audit_actor ON salary_advance_audit(actor);
CREATE INDEX idx_audit_created_at ON salary_advance_audit(created_at);
CREATE INDEX idx_audit_details ON salary_advance_audit USING GIN (details);
