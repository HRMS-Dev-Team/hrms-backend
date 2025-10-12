-- Create holidays table for managing public and company holidays

CREATE TABLE holidays (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    company_id UUID,
    country VARCHAR(100),
    is_recurring BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add check constraint for holiday type
ALTER TABLE holidays
    ADD CONSTRAINT check_holiday_type CHECK (type IN ('PUBLIC_HOLIDAY', 'COMPANY_HOLIDAY', 'OPTIONAL_HOLIDAY'));

-- Create indexes for better query performance
CREATE INDEX idx_holidays_date ON holidays(date);
CREATE INDEX idx_holidays_company_id ON holidays(company_id);
CREATE INDEX idx_holidays_date_company ON holidays(date, company_id);
CREATE INDEX idx_holidays_is_active ON holidays(is_active);

-- Add trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_holidays_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_holidays_updated_at
    BEFORE UPDATE ON holidays
    FOR EACH ROW
    EXECUTE FUNCTION update_holidays_updated_at();

-- Add comments
COMMENT ON TABLE holidays IS 'Public and company holidays for leave calculation';
COMMENT ON COLUMN holidays.type IS 'Holiday type: PUBLIC_HOLIDAY, COMPANY_HOLIDAY, or OPTIONAL_HOLIDAY';
COMMENT ON COLUMN holidays.is_recurring IS 'Whether holiday repeats every year';
COMMENT ON COLUMN holidays.company_id IS 'Company-specific holiday (NULL for public holidays)';
