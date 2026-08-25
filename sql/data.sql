INSERT INTO specialties (name, description) VALUES
    ('Cardiología',     'Enfermedades del corazón y sistema circulatorio'),
    ('Pediatría',       'Medicina para niños y adolescentes'),
    ('Traumatología',   'Lesiones del sistema músculo-esquelético'),
    ('Neurología',      'Sistema nervioso central y periférico'),
    ('Medicina General','Atención primaria y preventiva');

INSERT INTO doctors (license_number, first_name, last_name, email, specialty_id, schedule_start, schedule_end) VALUES
    ('CMP-001', 'Carlos',   'Mendoza', 'c.mendoza@hospital.com', 1, '08:00', '16:00'),
    ('CMP-002', 'Ana',      'García',  'a.garcia@hospital.com',  2, '09:00', '17:00'),
    ('CMP-003', 'Roberto',  'Quispe',  'r.quispe@hospital.com',  3, '07:00', '15:00'),
    ('CMP-004', 'María',    'Torres',  'm.torres@hospital.com',  4, '10:00', '18:00'),
    ('CMP-005', 'Luis',     'Vargas',  'l.vargas@hospital.com',  5, '08:00', '16:00');

INSERT INTO appointment_types (title, description, price) VALUES
    ('Consulta General', 
     'Consulta médica general y evaluación inicial del paciente', 
     50.00),

    ('Consulta Especializada', 
     'Evaluación médica realizada por un especialista', 
     80.00),

    ('Consulta Cardiológica', 
     'Evaluación y diagnóstico de enfermedades cardiovasculares', 
     120.00),

    ('Consulta Pediátrica', 
     'Evaluación médica para niños y adolescentes', 
     70.00),

    ('Control Médico', 
     'Consulta de seguimiento y control del estado de salud del paciente', 
     40.00),

    ('Evaluación Traumatológica', 
     'Evaluación de lesiones y problemas del sistema músculo-esquelético', 
     100.00),

    ('Evaluación Neurológica', 
     'Evaluación médica del sistema nervioso central y periférico', 
     110.00);