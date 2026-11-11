CREATE DATABASE IF NOT EXISTS db_eventos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_eventos;

CREATE TABLE IF NOT EXISTS eventos (
    id_evento INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha DATE NOT NULL,
    lugar VARCHAR(150) NOT NULL,
    modalidad VARCHAR(30) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Programado',
    CONSTRAINT chk_eventos_modalidad
        CHECK (modalidad IN ('Presencial', 'Virtual', 'Híbrida')),
    CONSTRAINT chk_eventos_estado
        CHECK (estado IN ('Programado', 'En curso', 'Finalizado', 'Cancelado')),
    CONSTRAINT uq_evento_nombre_fecha_lugar
        UNIQUE (nombre, fecha, lugar)
);

CREATE TABLE IF NOT EXISTS participantes (
    id_participante INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(120) NOT NULL,
    apellidos VARCHAR(120) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(30),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inscripciones (
    id_inscripcion INT AUTO_INCREMENT PRIMARY KEY,
    id_evento INT NOT NULL,
    id_participante INT NOT NULL,
    fecha_inscripcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(30) NOT NULL DEFAULT 'Inscrito',
    CONSTRAINT fk_inscripciones_evento
        FOREIGN KEY (id_evento) REFERENCES eventos(id_evento)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_inscripciones_participante
        FOREIGN KEY (id_participante) REFERENCES participantes(id_participante)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_inscripcion_evento_participante
        UNIQUE (id_evento, id_participante)
);

-- Datos de demostración opcionales:
-- INSERT INTO eventos (nombre, descripcion, fecha, lugar, modalidad, estado)
-- VALUES ('Feria Tecnológica T06', 'Presentación de proyectos', '2026-12-10',
--         'Campus Valle Grande', 'Presencial', 'Programado');
