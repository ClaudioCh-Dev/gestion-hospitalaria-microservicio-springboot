-- ============================================================
-- SPECIALTIES
-- ============================================================

INSERT INTO specialties (name, description) VALUES
    ('Cardiología',
     'Enfermedades del corazón y sistema circulatorio'),

    ('Pediatría',
     'Medicina para niños y adolescentes'),

    ('Traumatología',
     'Lesiones del sistema músculo-esquelético'),

    ('Neurología',
     'Sistema nervioso central y periférico'),

    ('Medicina General',
     'Atención primaria y preventiva');


-- ============================================================
-- DOCTORS
-- ============================================================

INSERT INTO doctors (
    license_number,
    first_name,
    last_name,
    email,
    specialty_id,
    schedule_start,
    schedule_end,
    active
) VALUES
    ('CMP-001', 'Carlos', 'Mendoza',
     'c.mendoza@hospital.com', 1, '08:00', '16:00', true),

    ('CMP-002', 'Ana', 'García',
     'a.garcia@hospital.com', 2, '09:00', '17:00', true),

    ('CMP-003', 'Roberto', 'Quispe',
     'r.quispe@hospital.com', 3, '07:00', '15:00', true),

    ('CMP-004', 'María', 'Torres',
     'm.torres@hospital.com', 4, '10:00', '18:00', true),

    ('CMP-005', 'Luis', 'Vargas',
     'l.vargas@hospital.com', 5, '08:00', '16:00', true);


-- ============================================================
-- APPOINTMENT TYPES
-- ============================================================

INSERT INTO appointment_types (
    title,
    description,
    active
)
VALUES
    (
        'Consulta General',
        'Consulta médica general',
        true
    ),
    (
        'Consulta Especializada',
        'Consulta con médico especialista',
        true
    ),
    (
        'Control Médico',
        'Control y seguimiento del paciente',
        true
    ),
    (
        'Primera Consulta',
        'Primera evaluación médica del paciente',
        true
    ),
    (
        'Consulta de Emergencia',
        'Atención médica de emergencia',
        true
    );


-- ============================================================
-- BILLING TARIFFS
-- ============================================================

INSERT INTO billing_tariffs (
    billing_appointment_type_id,
    price,
    currency
)
SELECT id, 50.00, 'PEN'
FROM appointment_types
WHERE title = 'Consulta General'

UNION ALL

SELECT id, 100.00, 'PEN'
FROM appointment_types
WHERE title = 'Consulta Especializada'

UNION ALL

SELECT id, 70.00, 'PEN'
FROM appointment_types
WHERE title = 'Control Médico'

UNION ALL

SELECT id, 80.00, 'PEN'
FROM appointment_types
WHERE title = 'Primera Consulta'

UNION ALL

SELECT id, 150.00, 'PEN'
FROM appointment_types
WHERE title = 'Consulta de Emergencia';