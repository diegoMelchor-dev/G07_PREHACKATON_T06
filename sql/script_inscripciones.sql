-- ============================================================
-- Script SQL del módulo INSCRIPCIONES (Integrante 3)
-- Práctica T06 Pre-Hackatón - Sistema de Gestión de Eventos
-- ============================================================
-- Requiere que ya existan las tablas `eventos` y `participantes`.
--
-- Orden de ejecución recomendado en una base nueva:
--   1) sql/script_participantes.sql   (rama feature/participantes)
--   2) tabla `eventos`                (rama feature/Gestion_Eventos)
--   3) este script
--
-- NOTA: si la base ya existe con otro nombre (por ejemplo
-- defaultdb en Aiven), elimina la línea USE y ejecuta el
-- script dentro de esa base.

USE db_eventos;

CREATE TABLE IF NOT EXISTS inscripciones (
    id_inscripcion    INT         NOT NULL AUTO_INCREMENT,
    id_evento         INT         NOT NULL,
    id_participante   INT         NOT NULL,
    fecha_inscripcion DATE        NOT NULL,
    estado            VARCHAR(30) NOT NULL DEFAULT 'Inscrito',
    PRIMARY KEY (id_inscripcion),
    CONSTRAINT uq_inscripcion_evento_participante
        UNIQUE (id_evento, id_participante),
    CONSTRAINT chk_inscripciones_estado
        CHECK (estado IN ('Inscrito', 'Confirmado', 'Cancelado')),
    CONSTRAINT fk_inscripciones_evento
        FOREIGN KEY (id_evento) REFERENCES eventos (id_evento)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_inscripciones_participante
        FOREIGN KEY (id_participante) REFERENCES participantes (id_participante)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE = InnoDB;
