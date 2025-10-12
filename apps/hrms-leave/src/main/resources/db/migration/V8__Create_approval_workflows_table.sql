-- Create approval_workflows table for multi-level leave approval

CREATE TABLE approval_workflows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    leave_request_id UUID NOT NULL,
    approval_level VARCHAR(20) NOT NULL,
    approver_id UUID NOT NULL,
    approver_name VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    comments TEXT,
    action_at TIMESTAMP,
    sequence_order INTEGER NOT NULL,
    is_required BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_approval_workflow_leave_request FOREIGN KEY (leave_request_id)
        REFERENCES leave_requests(id) ON DELETE CASCADE
);

-- Add check constraints
ALTER TABLE approval_workflows
    ADD CONSTRAINT check_approval_level CHECK (approval_level IN ('LEVEL_1', 'LEVEL_2', 'LEVEL_3', 'LEVEL_4'));

ALTER TABLE approval_workflows
    ADD CONSTRAINT check_workflow_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'MODIFICATION_REQUESTED'));

-- Create indexes for better query performance
CREATE INDEX idx_approval_workflows_leave_request ON approval_workflows(leave_request_id);
CREATE INDEX idx_approval_workflows_approver ON approval_workflows(approver_id);
CREATE INDEX idx_approval_workflows_status ON approval_workflows(status);
CREATE INDEX idx_approval_workflows_sequence ON approval_workflows(leave_request_id, sequence_order);

-- Add trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_approval_workflows_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_approval_workflows_updated_at
    BEFORE UPDATE ON approval_workflows
    FOR EACH ROW
    EXECUTE FUNCTION update_approval_workflows_updated_at();

-- Add comments
COMMENT ON TABLE approval_workflows IS 'Multi-level approval workflow for leave requests';
COMMENT ON COLUMN approval_workflows.approval_level IS 'Approval level: LEVEL_1, LEVEL_2, LEVEL_3, or LEVEL_4';
COMMENT ON COLUMN approval_workflows.sequence_order IS 'Order in which approvals must be processed';
COMMENT ON COLUMN approval_workflows.is_required IS 'Whether this approval level is required';
