-- Insert default leave types for demonstration
-- Note: In production, these would be created per company via the API

-- Annual Leave
INSERT INTO leave_types (
    id, name, code, category, description, company_id,
    default_days_per_year, max_consecutive_days, requires_document,
    min_notice_days, is_paid, is_active, allow_carry_forward, max_carry_forward_days
) VALUES (
    gen_random_uuid(),
    'Annual Leave',
    'ANNUAL',
    'ANNUAL',
    'Regular annual leave for employees',
    '550e8400-e29b-41d4-a716-446655440000',
    21,
    14,
    false,
    3,
    true,
    true,
    true,
    5
) ON CONFLICT (code) DO NOTHING;

-- Sick Leave
INSERT INTO leave_types (
    id, name, code, category, description, company_id,
    default_days_per_year, max_consecutive_days, requires_document,
    min_notice_days, is_paid, is_active, allow_carry_forward, max_carry_forward_days
) VALUES (
    gen_random_uuid(),
    'Sick Leave',
    'SICK',
    'SICK',
    'Leave for medical reasons',
    '550e8400-e29b-41d4-a716-446655440000',
    10,
    7,
    true,
    0,
    true,
    true,
    false,
    null
) ON CONFLICT (code) DO NOTHING;

-- Maternity Leave
INSERT INTO leave_types (
    id, name, code, category, description, company_id,
    default_days_per_year, max_consecutive_days, requires_document,
    min_notice_days, is_paid, is_active, allow_carry_forward, max_carry_forward_days
) VALUES (
    gen_random_uuid(),
    'Maternity Leave',
    'MATERNITY',
    'MATERNITY',
    'Leave for maternity purposes',
    '550e8400-e29b-41d4-a716-446655440000',
    90,
    90,
    true,
    30,
    true,
    true,
    false,
    null
) ON CONFLICT (code) DO NOTHING;

-- Paternity Leave
INSERT INTO leave_types (
    id, name, code, category, description, company_id,
    default_days_per_year, max_consecutive_days, requires_document,
    min_notice_days, is_paid, is_active, allow_carry_forward, max_carry_forward_days
) VALUES (
    gen_random_uuid(),
    'Paternity Leave',
    'PATERNITY',
    'PATERNITY',
    'Leave for paternity purposes',
    '550e8400-e29b-41d4-a716-446655440000',
    14,
    14,
    true,
    7,
    true,
    true,
    false,
    null
) ON CONFLICT (code) DO NOTHING;

-- Compassionate Leave
INSERT INTO leave_types (
    id, name, code, category, description, company_id,
    default_days_per_year, max_consecutive_days, requires_document,
    min_notice_days, is_paid, is_active, allow_carry_forward, max_carry_forward_days
) VALUES (
    gen_random_uuid(),
    'Compassionate Leave',
    'COMPASSIONATE',
    'COMPASSIONATE',
    'Leave for family emergencies or bereavement',
    '550e8400-e29b-41d4-a716-446655440000',
    5,
    5,
    false,
    0,
    true,
    true,
    false,
    null
) ON CONFLICT (code) DO NOTHING;

-- Unpaid Leave
INSERT INTO leave_types (
    id, name, code, category, description, company_id,
    default_days_per_year, max_consecutive_days, requires_document,
    min_notice_days, is_paid, is_active, allow_carry_forward, max_carry_forward_days
) VALUES (
    gen_random_uuid(),
    'Unpaid Leave',
    'UNPAID',
    'UNPAID',
    'Leave without pay',
    '550e8400-e29b-41d4-a716-446655440000',
    null,
    30,
    false,
    14,
    false,
    true,
    false,
    null
) ON CONFLICT (code) DO NOTHING;
