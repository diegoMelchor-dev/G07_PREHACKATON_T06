-- ============================================================
-- Script SQL del módulo PARTICIPANTES (Integrante 2)
-- Práctica T06 Pre-Hackatón - Sistema de Gestión de Eventos
-- ============================================================
-- Ejecutar este script en MySQL (local o en el servicio en la
-- nube configurado en config.properties).
--
-- NOTA: si la base de datos ya existe (por ejemplo, defaultdb en
-- Aiven), puedes ejecutar solo el CREATE TABLE y los INSERT
-- dentro de esa base (USE <tu_db>), ya que la app se conecta a
-- la BD indicada en DB_NAME.
--
-- FK: la tabla `participantes` no lleva FK propia; la tabla
-- `inscripciones` (Integrante 3) referenciará a
-- `participantes(id_participante)` cuando se integre su módulo.

CREATE DATABASE IF NOT EXISTS db_eventos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_eventos;

CREATE TABLE IF NOT EXISTS participantes (
    id_participante INT          NOT NULL AUTO_INCREMENT,
    nombres         VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(100) NOT NULL,
    dni             VARCHAR(8)    NOT NULL,
    correo          VARCHAR(120) NOT NULL,
    telefono        VARCHAR(9)    NOT NULL,
    institucion     VARCHAR(150) NOT NULL,
    fecha_registro  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_participante),
    UNIQUE KEY uq_participantes_dni    (dni),
    UNIQUE KEY uq_participantes_correo (correo)
) ENGINE = InnoDB;

-- Datos de prueba
INSERT INTO participantes (nombres, apellidos, dni, correo, telefono, institucion) VALUES
    ('María Fernanda', 'Quispe Mamani',     '78451236', 'maria.quispe@vallegrande.edu.pe',   '987654321', 'Instituto Superior Valle Grande'),
    ('Diego Alonso',   'Melchor Ramírez',   '41258790', 'diego.melchor@vallegrande.edu.pe',  '912345678', 'Instituto Superior Valle Grande'),
    ('Lucía Beatriz',  'Torres Chávez',     '74512098', 'lucia.torres@vallegrande.edu.pe',   '956781234', 'Instituto Superior Valle Grande'),
    ('Jorge Luis',     'Bustos Cárdenas',   '09876543', 'jorge.bustos@unsa.edu.pe',          '901234567', 'Universidad Nacional de San Agustín'),
    ('Ana Cecilia',    'Rivera Delgado',    '45678123', 'ana.rivera@unsa.edu.pe',            '934567890', 'Universidad Nacional de San Agustín'),
    ('Pablo Andrés',   'Huamani Sotelo',    '74125896', 'pablo.huamani@unsa.edu.pe',         '978456123', 'Universidad Nacional de San Agustín'),
    ('Rosa Angélica',  'Mendoza Flores',    '85296374', 'rosa.mendoza@unsa.edu.pe',          '987123456', 'Universidad Nacional de San Agustín'),
    ('Kevin Josué',    'Aguilar Pastor',    '36985214', 'kevin.aguilar@sudamericano.edu.pe', '945612378', 'Instituto Sudamericano'),
    ('Claudia Miluska','Vargas Núñez',      '15935746', 'claudia.vargas@sudamericano.edu.pe','998765412', 'Instituto Sudamericano'),
    ('Fernando José',  'Cáceres Salas',     '65498732', 'fernando.caceres@colegioindependencia.pe', '932165487', 'Colegio Independencia'),
    ('Valeria Soledad','Chávez Peña',       '75315948', 'valeria.chavez@colegioindependencia.pe',  '963214587', 'Colegio Independencia'),
    ('Luis Alberto',   'Ramos Condori',     '48261593', 'luis.ramos@colegioindependencia.pe',      '914785236', 'Colegio Independencia');
