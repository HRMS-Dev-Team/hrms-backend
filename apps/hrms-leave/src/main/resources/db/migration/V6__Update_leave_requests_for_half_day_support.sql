-- Update leave_requests table to support half-day leaves

-- Add start_day_type column
ALTER TABLE leave_requests
    ADD COLUMN start_day_type VARCHAR(20) NOT NULL DEFAULT 'FULL_DAY';

-- Add end_day_type column
ALTER TABLE leave_requests
    ADD COLUMN end_day_type VARCHAR(20) NOT NULL DEFAULT 'FULL_DAY';

-- Add check constraint for valid day types
ALTER TABLE leave_requests
    ADD CONSTRAINT check_start_day_type CHECK (start_day_type IN ('FULL_DAY', 'FIRST_HALF', 'SECOND_HALF'));

ALTER TABLE leave_requests
    ADD CONSTRAINT check_end_day_type CHECK (end_day_type IN ('FULL_DAY', 'FIRST_HALF', 'SECOND_HALF'));

-- Add comments
COMMENT ON COLUMN leave_requests.start_day_type IS 'Type of leave for start date: FULL_DAY, FIRST_HALF, or SECOND_HALF';
COMMENT ON COLUMN leave_requests.end_day_type IS 'Type of leave for end date: FULL_DAY, FIRST_HALF, or SECOND_HALF';
