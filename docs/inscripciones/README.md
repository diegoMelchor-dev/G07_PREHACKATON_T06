# Módulo Gestión de Inscripciones (Integrante 3)

CRUD de **inscripciones** (relación entre un evento y un participante) y reporte
**Inscritos por Evento**, parte del Sistema de Gestión de Eventos y Conferencias
(Práctica Pre-Hackatón T06 · JavaFX + MySQL + JasperReports).

## Qué hace

| Operación | Descripción |
|-----------|-------------|
| **Create** | Formulario con `ComboBox` de evento y participante, `DatePicker` (dd/MM/yyyy) y estado. |
| **Read** | `TableView` con búsqueda dinámica (mientras se escribe) y filtros por estado y por evento. |
| **Update** | Al seleccionar una fila se carga en el formulario; se verifica que el registro siga existiendo. |
| **Delete** | Eliminación física previa confirmación del usuario. |
| **Reporte** | JasperReports agrupado por evento, con subtotal por evento y total general (visor o PDF). |

### Validaciones

- Campos obligatorios: evento, participante, fecha y estado.
- Estado permitido: `Inscrito`, `Confirmado` o `Cancelado`.
- La fecha de inscripción no puede ser posterior a la fecha del evento.
- Se comprueba que el evento y el participante existan antes de guardar.
- No se permiten duplicados: un participante solo puede inscribirse una vez por evento
  (se valida en el DAO y también con `UNIQUE (id_evento, id_participante)` en MySQL).
- Alertas claras (`Alert`) para advertencias, errores, confirmaciones y éxito.

## Arquitectura (MVC + DAO)

```
src/main/java/vallegrande/edu/pe/cuyconectacrud/
├── model/       Inscripcion, EventoOpcion, ParticipanteOpcion
├── dao/         InscripcionDAO            ← todo el SQL vive aquí
├── controller/  InscripcionController     ← sin SQL; usa Task para no congelar la UI
├── util/        ValidadorInscripcion
├── report/      ReporteInscripciones
├── InscripcionesApp.java / LauncherInscripciones.java
src/main/resources/
├── vallegrande/edu/pe/cuyconectacrud/view/  inscripciones-view.fxml, inscripciones.css
└── reportes/                                inscritos_por_evento.jrxml
sql/
├── script_inscripciones.sql
└── datos_prueba_inscripciones.sql
```

## Base de datos

La tabla `inscripciones` depende de `eventos` y `participantes`. Orden de ejecución:

1. `sql/script_participantes.sql`
2. Tabla `eventos` (módulo de Eventos)
3. `sql/script_inscripciones.sql`
4. *(opcional)* `sql/datos_prueba_inscripciones.sql`

Credenciales: copia `config.properties` a `config.properties` y completa los datos.
Para un MySQL local sin SSL:

```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=db_eventos
DB_USER=root
DB_PASSWORD=tu_password
DB_SSL_MODE=DISABLED
```

## Cómo ejecutar

En IntelliJ IDEA: abrir `LauncherInscripciones` y pulsar el triángulo verde.

## Evidencias (capturas)

Guardar las imágenes en `docs/inscripciones/capturas/` y enlazarlas aquí:

- [ ] Interfaz principal con la tabla de inscripciones
- [ ] Formulario vacío y formulario lleno
- [ ] Alertas: campos vacíos, duplicado, confirmación de eliminación
- [ ] CRUD: registro insertado, editado y eliminado
- [ ] Reporte en el visor de JasperReports y PDF exportado
- [ ] Tabla `inscripciones` con registros en MySQL Workbench
