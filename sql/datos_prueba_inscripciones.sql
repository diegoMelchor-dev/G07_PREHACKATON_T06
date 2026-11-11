-- ============================================================
-- Datos de prueba del módulo INSCRIPCIONES (Integrante 3)
-- ============================================================
-- Ejecutar DESPUÉS de script_participantes.sql (usa sus DNI de
-- prueba), de la tabla `eventos` y de script_inscripciones.sql.
-- Es seguro ejecutarlo más de una vez (INSERT IGNORE).

USE db_eventos;

-- Eventos de ejemplo (solo si aún no existen)
INSERT IGNORE INTO eventos (nombre, descripcion, fecha, lugar, modalidad, estado) VALUES
    ('Feria Tecnológica T06',   'Presentación de proyectos de software', '2026-12-10', 'Campus Valle Grande',  'Presencial', 'Programado'),
    ('Congreso de Innovación',  'Ponencias sobre innovación educativa',  '2026-12-15', 'Auditorio Central',    'Híbrida',    'Programado'),
    ('Taller de JavaFX',        'Taller práctico de interfaces con JavaFX', '2026-11-20', 'Laboratorio 3',     'Presencial', 'Programado');

-- Inscripciones de ejemplo, enlazadas por nombre de evento y DNI
INSERT IGNORE INTO inscripciones (id_evento, id_participante, fecha_inscripcion, estado)
SELECT e.id_evento, p.id_participante, '2026-11-05', 'Inscrito'
FROM eventos e
JOIN participantes p ON p.dni IN ('78451236', '41258790', '74512098', '09876543')
WHERE e.nombre = 'Feria Tecnológica T06';

INSERT IGNORE INTO inscripciones (id_evento, id_participante, fecha_inscripcion, estado)
SELECT e.id_evento, p.id_participante, '2026-11-06', 'Confirmado'
FROM eventos e
JOIN participantes p ON p.dni IN ('45678123', '74125896', '85296374')
WHERE e.nombre = 'Congreso de Innovación';

INSERT IGNORE INTO inscripciones (id_evento, id_participante, fecha_inscripcion, estado)
SELECT e.id_evento, p.id_participante, '2026-11-07', 'Inscrito'
FROM eventos e
JOIN participantes p ON p.dni IN ('36985214', '15935746')
WHERE e.nombre = 'Taller de JavaFX';

-- Verificación rápida
SELECT e.nombre AS evento, COUNT(*) AS inscritos
FROM inscripciones i
JOIN eventos e ON e.id_evento = i.id_evento
GROUP BY e.id_evento, e.nombre;
