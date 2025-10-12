-- Update leave_types table to support accrual frequency and carry-forward enhancements

-- Change default_days_per_year from INTEGER to DECIMAL for half-day support
ALTER TABLE leave_types
    ALTER COLUMN default_days_per_year TYPE DECIMAL(5,2);

-- Add accrual frequency field
ALTER TABLE leave_types
    ADD COLUMN accrual_frequency VARCHAR(20) DEFAULT 'YEARLY';

-- Add carry-forward expiry months
ALTER TABLE leave_types
    ADD COLUMN carry_forward_expiry_months INTEGER;

-- Add comments
COMMENT ON COLUMN leave_types.default_days_per_year IS 'Default days allocated per year (supports half-days)';
COMMENT ON COLUMN leave_types.accrual_frequency IS 'Accrual frequency: MONTHLY or YEARLY';
COMMENT ON COLUMN leave_types.carry_forward_expiry_months IS 'Number of months until carry-forward balance expires';

-- Update existing leave types to use DECIMAL values
UPDATE leave_types SET default_days_per_year = 20.00 WHERE code = 'ANNUAL';
UPDATE leave_types SET default_days_per_year = 10.00 WHERE code = 'SICK';
UPDATE leave_types SET default_days_per_year = 90.00 WHERE code = 'MATERNITY';
UPDATE leave_types SET default_days_per_year = 7.00 WHERE code = 'PATERNITY';
UPDATE leave_types SET default_days_per_year = 3.00 WHERE code = 'COMPASSIONATE';
UPDATE leave_types SET default_days_per_year = 0.00 WHERE code = 'UNPAID';
