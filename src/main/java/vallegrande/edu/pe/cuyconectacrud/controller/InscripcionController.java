package vallegrande.edu.pe.cuyconectacrud.controller;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;

import vallegrande.edu.pe.cuyconectacrud.dao.InscripcionDAO;
import vallegrande.edu.pe.cuyconectacrud.model.EventoOpcion;
import vallegrande.edu.pe.cuyconectacrud.model.Inscripcion;
import vallegrande.edu.pe.cuyconectacrud.model.ParticipanteOpcion;
import vallegrande.edu.pe.cuyconectacrud.report.ReporteInscripciones;
import vallegrande.edu.pe.cuyconectacrud.util.ValidadorInscripcion;

import java.io.File;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Controlador del módulo Gestión de Inscripciones (Integrante 3).
 *
 * No ejecuta SQL: delega en InscripcionDAO. Todas las consultas a la
 * base de datos se lanzan en un hilo aparte con Task para no congelar
 * la interfaz.
 */
public class InscripcionController {

    private static final double RETRASO_BUSQUEDA_MS = 300;
    private static final String TODOS_ESTADOS = "Todos los estados";
    private static final EventoOpcion TODOS_EVENTOS =
            new EventoOpcion(0, "Todos los eventos", null);
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final InscripcionDAO dao = new InscripcionDAO();
    private final ReporteInscripciones reporte = new ReporteInscripciones();
    private final PauseTransition pausaBusqueda =
            new PauseTransition(Duration.millis(RETRASO_BUSQUEDA_MS));

    // ----- Barra superior -----
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private ComboBox<EventoOpcion> cmbFiltroEvento;
    @FXML private Button btnRefrescar;
    @FXML private Button btnReporte;
    @FXML private Button btnPdf;

    // ----- Formulario -----
    @FXML private Label lblId;
    @FXML private ComboBox<EventoOpcion> cmbEvento;
    @FXML private ComboBox<ParticipanteOpcion> cmbParticipante;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    // ----- Tabla -----
    @FXML private TableView<Inscripcion> tvInscripciones;
    @FXML private TableColumn<Inscripcion, Integer> colId;
    @FXML private TableColumn<Inscripcion, String> colEvento;
    @FXML private TableColumn<Inscripcion, String> colParticipante;
    @FXML private TableColumn<Inscripcion, String> colDni;
    @FXML private TableColumn<Inscripcion, String> colFecha;
    @FXML private TableColumn<Inscripcion, String> colEstado;

    // ----- Barra de estado -----
    @FXML private Label lblEstado;

    private Inscripcion seleccionActual;
    private boolean recargandoCatalogos;

    /** Consulta que devuelve un valor y puede lanzar excepciones. */
    @FunctionalInterface
    private interface Consulta<T> {
        T ejecutar() throws Exception;
    }

    /** Acción sin valor de retorno que puede lanzar excepciones. */
    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws Exception;
    }

    /** Error de regla de negocio: se muestra como advertencia. */
    private static class ReglaNegocioException extends Exception {
        ReglaNegocioException(String mensaje) {
            super(mensaje);
        }
    }

    @FXML
    private void initialize() {
        configurarTabla();
        configurarFormulario();
        configurarEventos();
        cargarCatalogos();
        cargarTabla();
    }

    // ----------------------------------------------------------------
    // Configuración inicial
    // ----------------------------------------------------------------

    private void configurarTabla() {
        colId.setCellValueFactory(
                new PropertyValueFactory<>("idInscripcion"));
        colEvento.setCellValueFactory(
                new PropertyValueFactory<>("eventoNombre"));
        colParticipante.setCellValueFactory(
                new PropertyValueFactory<>("participanteNombre"));
        colDni.setCellValueFactory(
                new PropertyValueFactory<>("participanteDni"));
        colFecha.setCellValueFactory(
                new PropertyValueFactory<>("fechaInscripcionTexto"));
        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado"));

        tvInscripciones.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tvInscripciones.getSelectionModel()
                .setSelectionMode(SelectionMode.SINGLE);
    }

    private void configurarFormulario() {
        cmbEstado.getItems().setAll(Inscripcion.ESTADOS);

        List<String> estadosFiltro = new ArrayList<>();
        estadosFiltro.add(TODOS_ESTADOS);
        estadosFiltro.addAll(Inscripcion.ESTADOS);
        cmbFiltroEstado.getItems().setAll(estadosFiltro);
        cmbFiltroEstado.setValue(TODOS_ESTADOS);

        // DatePicker personalizado: formato dd/MM/yyyy.
        dpFecha.setPromptText("dd/mm/aaaa");
        dpFecha.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha == null ? "" : FORMATO_FECHA.format(fecha);
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(texto.trim(), FORMATO_FECHA);
                } catch (DateTimeParseException e) {
                    return null;
                }
            }
        });

        limpiarCamposFormulario();
    }

    private void configurarEventos() {
        // Al seleccionar un registro se carga en el formulario (Update).
        tvInscripciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> {
                    seleccionActual = actual;
                    boolean haySeleccion = actual != null;
                    btnActualizar.setDisable(!haySeleccion);
                    btnEliminar.setDisable(!haySeleccion);
                    btnGuardar.setDisable(haySeleccion);

                    if (haySeleccion) {
                        cargarEnFormulario(actual);
                    } else {
                        limpiarCamposFormulario();
                    }
                });

        // Búsqueda dinámica: filtra mientras se escribe, con un pequeño
        // retardo para no consultar por cada tecla.
        pausaBusqueda.setOnFinished(evento -> cargarTabla());
        txtBuscar.textProperty().addListener(
                (obs, anterior, nuevo) -> pausaBusqueda.playFromStart());

        // Filtros por estado y por evento (ComboBox).
        cmbFiltroEstado.valueProperty().addListener(
                (obs, anterior, nuevo) -> cargarTabla());
        cmbFiltroEvento.valueProperty().addListener(
                (obs, anterior, nuevo) -> {
                    if (!recargandoCatalogos) {
                        cargarTabla();
                    }
                });
    }

    // ----------------------------------------------------------------
    // Carga de datos (Read)
    // ----------------------------------------------------------------

    /** Carga los eventos y participantes de los ComboBox. */
    private void cargarCatalogos() {
        recargandoCatalogos = true;

        EventoOpcion eventoElegido = cmbEvento.getValue();
        EventoOpcion filtroElegido = cmbFiltroEvento.getValue();
        ParticipanteOpcion participanteElegido = cmbParticipante.getValue();

        ejecutar(dao::listarEventos, eventos -> {
            cmbEvento.getItems().setAll(eventos);
            if (eventoElegido != null && eventos.contains(eventoElegido)) {
                cmbEvento.setValue(eventoElegido);
            }

            List<EventoOpcion> filtro = new ArrayList<>();
            filtro.add(TODOS_EVENTOS);
            filtro.addAll(eventos);
            cmbFiltroEvento.getItems().setAll(filtro);

            // Conserva el filtro de evento si ese evento sigue existiendo.
            boolean conservaFiltro =
                    filtroElegido != null && filtro.contains(filtroElegido);
            cmbFiltroEvento.setValue(
                    conservaFiltro ? filtroElegido : TODOS_EVENTOS);

            recargandoCatalogos = false;

            // Si el evento filtrado desapareció, la tabla ya no coincide.
            if (filtroElegido != null && !conservaFiltro
                    && filtroElegido.id() != 0) {
                cargarTabla();
            }
        }, "No se pudieron cargar los eventos.");

        ejecutar(dao::listarParticipantes, participantes -> {
            cmbParticipante.getItems().setAll(participantes);
            if (participanteElegido != null
                    && participantes.contains(participanteElegido)) {
                cmbParticipante.setValue(participanteElegido);
            }
        }, "No se pudieron cargar los participantes.");
    }

    private void cargarTabla() {
        String criterio = txtBuscar.getText();

        String estadoElegido = cmbFiltroEstado.getValue();
        String estado = (estadoElegido == null
                || TODOS_ESTADOS.equals(estadoElegido))
                ? null : estadoElegido;

        EventoOpcion eventoElegido = cmbFiltroEvento.getValue();
        Integer idEvento = (eventoElegido == null || eventoElegido.id() == 0)
                ? null : eventoElegido.id();

        setEstado("Cargando inscripciones…");

        ejecutar(() -> dao.filtrar(criterio, estado, idEvento), lista -> {
            tvInscripciones.getItems().setAll(lista);
            setEstado(lista.size() + " inscripción(es) mostrada(s)");
        }, "No se pudieron cargar las inscripciones.");
    }

    // ----------------------------------------------------------------
    // Acciones de los botones (Create / Update / Delete)
    // ----------------------------------------------------------------

    @FXML
    private void onNuevo() {
        limpiarFormulario();
        cmbEvento.requestFocus();
    }

    @FXML
    private void onGuardar() {
        Inscripcion nueva = leerFormulario();
        if (nueva == null) {
            return;
        }

        ejecutar(() -> {
            verificarReglas(nueva, null);
            return dao.insertar(nueva);
        }, idNuevo -> {
            mostrarAlerta(AlertType.INFORMATION, "Inscripción registrada",
                    "La inscripción se guardó correctamente (ID " + idNuevo + ").");
            limpiarFormulario();
            cargarTabla();
        }, "No se pudo guardar la inscripción.");
    }

    @FXML
    private void onActualizar() {
        if (seleccionActual == null) {
            mostrarAlerta(AlertType.WARNING, "Sin selección",
                    "Seleccione una inscripción de la tabla para actualizarla.");
            return;
        }

        Inscripcion cambios = leerFormulario();
        if (cambios == null) {
            return;
        }
        int id = seleccionActual.getIdInscripcion();
        cambios.setIdInscripcion(id);

        ejecutar(() -> {
            // Verificación previa: el registro debe seguir existiendo.
            if (dao.buscarPorId(id) == null) {
                throw new ReglaNegocioException(
                        "La inscripción ya no existe. Actualice la lista.");
            }
            verificarReglas(cambios, id);

            if (dao.actualizar(cambios) == 0) {
                throw new ReglaNegocioException(
                        "No se actualizó ningún registro. Actualice la lista.");
            }
            return id;
        }, idActualizado -> {
            mostrarAlerta(AlertType.INFORMATION, "Inscripción actualizada",
                    "Los cambios de la inscripción " + idActualizado
                            + " se guardaron correctamente.");
            limpiarFormulario();
            cargarTabla();
        }, "No se pudo actualizar la inscripción.");
    }

    @FXML
    private void onEliminar() {
        if (seleccionActual == null) {
            mostrarAlerta(AlertType.WARNING, "Sin selección",
                    "Seleccione una inscripción de la tabla para eliminarla.");
            return;
        }

        int id = seleccionActual.getIdInscripcion();
        String detalle = seleccionActual.getParticipanteNombre()
                + " en " + seleccionActual.getEventoNombre();

        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar esta inscripción?");
        confirmacion.setContentText(detalle
                + "\n\nEsta acción no se puede deshacer.");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        ejecutar(() -> dao.eliminar(id), eliminada -> {
            if (eliminada) {
                mostrarAlerta(AlertType.INFORMATION, "Inscripción eliminada",
                        "La inscripción se eliminó correctamente.");
            } else {
                mostrarAlerta(AlertType.WARNING, "Registro inexistente",
                        "La inscripción ya no existía en la base de datos.");
            }
            limpiarFormulario();
            cargarTabla();
        }, "No se pudo eliminar la inscripción.");
    }

    @FXML
    private void onRefrescar() {
        cargarCatalogos();
        cargarTabla();
    }

    // ----------------------------------------------------------------
    // Reportes
    // ----------------------------------------------------------------

    @FXML
    private void onReporte() {
        setEstado("Generando reporte…");
        ejecutarAccion(reporte::mostrarEnVisor,
                () -> setEstado("Reporte abierto en el visor."),
                "No se pudo generar el reporte.");
    }

    @FXML
    private void onPdf() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte de inscritos por evento");
        selector.setInitialFileName("inscritos_por_evento.pdf");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));

        File archivo = selector.showSaveDialog(
                tvInscripciones.getScene().getWindow());
        if (archivo == null) {
            return;
        }

        String ruta = archivo.getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) {
            ruta += ".pdf";
        }

        String rutaFinal = ruta;
        setEstado("Exportando PDF…");
        ejecutarAccion(() -> reporte.exportarPdf(rutaFinal), () -> {
            setEstado("PDF guardado en " + rutaFinal);
            mostrarAlerta(AlertType.INFORMATION, "PDF generado",
                    "El reporte se guardó en:\n" + rutaFinal);
        }, "No se pudo exportar el PDF.");
    }

    // ----------------------------------------------------------------
    // Formulario
    // ----------------------------------------------------------------

    /**
     * Valida el formulario y arma la inscripción; devuelve null (tras
     * mostrar una alerta) si hay errores.
     */
    private Inscripcion leerFormulario() {
        EventoOpcion evento = cmbEvento.getValue();
        ParticipanteOpcion participante = cmbParticipante.getValue();
        LocalDate fecha = dpFecha.getValue();
        String estado = cmbEstado.getValue();

        List<String> errores = ValidadorInscripcion.validar(
                evento, participante, fecha, estado);

        if (!errores.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Datos incompletos o inválidos",
                    "• " + String.join("\n• ", errores));
            return null;
        }

        return new Inscripcion(evento.id(), participante.id(), fecha, estado);
    }

    private void cargarEnFormulario(Inscripcion inscripcion) {
        lblId.setText(String.valueOf(inscripcion.getIdInscripcion()));

        cmbEvento.setValue(cmbEvento.getItems().stream()
                .filter(e -> e.id() == inscripcion.getIdEvento())
                .findFirst().orElse(null));

        cmbParticipante.setValue(cmbParticipante.getItems().stream()
                .filter(p -> p.id() == inscripcion.getIdParticipante())
                .findFirst().orElse(null));

        dpFecha.setValue(inscripcion.getFechaInscripcion());
        cmbEstado.setValue(inscripcion.getEstado());
    }

    /** Deja el formulario en blanco, sin tocar la selección de la tabla. */
    private void limpiarCamposFormulario() {
        lblId.setText("—");
        cmbEvento.setValue(null);
        cmbParticipante.setValue(null);
        dpFecha.setValue(LocalDate.now());
        cmbEstado.setValue(Inscripcion.ESTADO_INSCRITO);
    }

    /** Deja el formulario en blanco y quita la selección de la tabla. */
    private void limpiarFormulario() {
        tvInscripciones.getSelectionModel().clearSelection();
        limpiarCamposFormulario();
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    // ----------------------------------------------------------------
    // Reglas de negocio (se consultan vía DAO)
    // ----------------------------------------------------------------

    /**
     * Verifica que el evento y el participante existan y que no haya
     * otra inscripción igual (mismo evento y mismo participante).
     *
     * @param idExcluir id de la inscripción en edición (null en altas)
     */
    private void verificarReglas(Inscripcion inscripcion, Integer idExcluir)
            throws SQLException, ReglaNegocioException {

        if (!dao.existeEvento(inscripcion.getIdEvento())) {
            throw new ReglaNegocioException(
                    "El evento seleccionado ya no existe. "
                            + "Actualice la lista de eventos.");
        }
        if (!dao.existeParticipante(inscripcion.getIdParticipante())) {
            throw new ReglaNegocioException(
                    "El participante seleccionado ya no existe. "
                            + "Actualice la lista de participantes.");
        }
        if (dao.existeInscripcion(inscripcion.getIdEvento(),
                inscripcion.getIdParticipante(), idExcluir)) {
            throw new ReglaNegocioException(
                    "Este participante ya está inscrito en el "
                            + "evento seleccionado.");
        }
    }

    // ----------------------------------------------------------------
    // Hilos y mensajes
    // ----------------------------------------------------------------

    /** Ejecuta una consulta en segundo plano y entrega el resultado. */
    private <T> void ejecutar(Consulta<T> consulta, Consumer<T> alExito,
                              String mensajeError) {

        Task<T> tarea = new Task<>() {
            @Override
            protected T call() throws Exception {
                return consulta.ejecutar();
            }
        };

        tarea.setOnSucceeded(evento -> alExito.accept(tarea.getValue()));
        tarea.setOnFailed(evento -> {
            recargandoCatalogos = false;
            informarFallo(mensajeError, tarea.getException());
        });

        lanzar(tarea);
    }

    /** Ejecuta una acción sin resultado en segundo plano. */
    private void ejecutarAccion(Accion accion, Runnable alExito,
                                String mensajeError) {

        Task<Void> tarea = new Task<>() {
            @Override
            protected Void call() throws Exception {
                accion.ejecutar();
                return null;
            }
        };

        tarea.setOnSucceeded(evento -> alExito.run());
        tarea.setOnFailed(evento ->
                informarFallo(mensajeError, tarea.getException()));

        lanzar(tarea);
    }

    private void lanzar(Task<?> tarea) {
        Thread hilo = new Thread(tarea, "inscripciones-bd");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void informarFallo(String contexto, Throwable error) {
        if (error instanceof ReglaNegocioException) {
            mostrarAlerta(AlertType.WARNING, "No se puede continuar",
                    error.getMessage());
            setEstado("Operación no realizada.");
            return;
        }

        String detalle;
        if (error instanceof SQLIntegrityConstraintViolationException) {
            detalle = "La base de datos rechazó la operación "
                    + "(registro duplicado o con referencias inválidas).";
        } else if (error != null && error.getMessage() != null) {
            detalle = error.getMessage();
        } else {
            detalle = String.valueOf(error);
        }

        mostrarAlerta(AlertType.ERROR, "Error", contexto + "\n\n" + detalle);
        setEstado("Error: " + contexto);
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void setEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }
}
