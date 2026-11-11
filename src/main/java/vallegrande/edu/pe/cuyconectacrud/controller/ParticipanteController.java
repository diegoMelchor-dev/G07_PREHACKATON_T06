package vallegrande.edu.pe.cuyconectacrud.controller;

import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import vallegrande.edu.pe.cuyconectacrud.dao.ParticipanteDAO;
import vallegrande.edu.pe.cuyconectacrud.model.Participante;
import vallegrande.edu.pe.cuyconectacrud.report.ReporteParticipantes;
import vallegrande.edu.pe.cuyconectacrud.util.ValidadorParticipante;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Controlador del módulo Gestión de Participantes (Integrante 2).
 *
 * No ejecuta SQL: delega en ParticipanteDAO. Todas las consultas
 * a la base de datos se lanzan en un hilo aparte con Task para no
 * congelar la interfaz.
 */
public class ParticipanteController {

    private static final String TODAS_LAS_INSTITUCIONES = "Todas";
    private static final double RETRASO_BUSQUEDA_MS = 300;

    private final ParticipanteDAO dao = new ParticipanteDAO();
    private final PauseTransition pausaBusqueda =
            new PauseTransition(Duration.millis(RETRASO_BUSQUEDA_MS));

    // ----- Barra superior -----
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbInstitucion;
    @FXML private Button btnRefrescar;
    @FXML private Button btnReporte;
    @FXML private Button btnPdf;

    // ----- Formulario -----
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtDni;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtInstitucion;
    @FXML private Label lblId;
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    // ----- Tabla -----
    @FXML private TableView<Participante> tvParticipantes;
    @FXML private TableColumn<Participante, Integer> colId;
    @FXML private TableColumn<Participante, String> colNombres;
    @FXML private TableColumn<Participante, String> colApellidos;
    @FXML private TableColumn<Participante, String> colDni;
    @FXML private TableColumn<Participante, String> colCorreo;
    @FXML private TableColumn<Participante, String> colTelefono;
    @FXML private TableColumn<Participante, String> colInstitucion;

    // ----- Estado -----
    @FXML private Label lblEstado;

    private Participante seleccionActual;
    private boolean recargandoInstituciones;

    /** Interfaz funcional que sí permite lanzar SQLException, etc. */
    @FunctionalInterface
    private interface Consulta<T> {
        T ejecutar() throws Exception;
    }

    @FXML
    private void initialize() {
        configurarTabla();
        configurarEventos();
        cargarInstituciones();
        cargarTabla();
    }

    // ----------------------------------------------------------------
    // Configuración inicial
    // ----------------------------------------------------------------

    private void configurarTabla() {
        colId.setCellValueFactory(
                new PropertyValueFactory<>("idParticipante"));
        colNombres.setCellValueFactory(
                new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(
                new PropertyValueFactory<>("apellidos"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono"));
        colInstitucion.setCellValueFactory(
                new PropertyValueFactory<>("institucion"));

        tvParticipantes.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tvParticipantes.getSelectionModel()
                .setSelectionMode(SelectionMode.SINGLE);
    }

    private void configurarEventos() {
        // Al seleccionar un registro se carga en el formulario (Update).
        tvParticipantes.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> {
                    seleccionActual = actual;
                    boolean haySeleccion = actual != null;
                    btnActualizar.setDisable(!haySeleccion);
                    btnEliminar.setDisable(!haySeleccion);

                    if (haySeleccion) {
                        cargarEnFormulario(actual);
                    } else {
                        limpiarFormulario();
                    }
                });

        // Búsqueda dinámica: filtra mientras se escribe (Reto #2),
        // con un pequeño retardo para no consultar por cada tecla.
        pausaBusqueda.setOnFinished(evento -> cargarTabla());
        txtBuscar.textProperty().addListener(
                (obs, anterior, nuevo) -> pausaBusqueda.playFromStart());

        // Filtro por institución con ComboBox (Reto #2).
        cmbInstitucion.valueProperty().addListener(
                (obs, anterior, nuevo) -> {
                    if (!recargandoInstituciones) {
                        cargarTabla();
                    }
                });
    }

    // ----------------------------------------------------------------
    // Carga de datos (en hilo aparte)
    // ----------------------------------------------------------------

    private void cargarTabla() {
        String criterio = txtBuscar.getText();
        String valorCombo = cmbInstitucion.getValue();
        String institucion = (valorCombo == null
                || TODAS_LAS_INSTITUCIONES.equals(valorCombo))
                        ? null : valorCombo;

        lblEstado.setText("Consultando participantes...");

        ejecutarEnHilo(
                () -> dao.filtrar(criterio, institucion),
                participantes -> {
                    tvParticipantes.getItems().setAll(participantes);
                    lblEstado.setText(participantes.size()
                            + " participante(s) encontrado(s).");
                },
                "No se pudo cargar la lista de participantes.");
    }

    private void cargarInstituciones() {
        ejecutarEnHilo(
                () -> dao.listarInstituciones(),
                instituciones -> {
                    String seleccionada = cmbInstitucion.getValue();

                    recargandoInstituciones = true;
                    cmbInstitucion.getItems().setAll(
                            TODAS_LAS_INSTITUCIONES);
                    cmbInstitucion.getItems().addAll(instituciones);

                    if (seleccionada != null
                            && cmbInstitucion.getItems()
                                    .contains(seleccionada)) {
                        cmbInstitucion.getSelectionModel().select(
                                seleccionada);
                    } else {
                        cmbInstitucion.getSelectionModel().selectFirst();
                    }
                    recargandoInstituciones = false;
                },
                "No se pudieron cargar las instituciones.");
    }

    // ----------------------------------------------------------------
    // Acciones de la barra superior
    // ----------------------------------------------------------------

    @FXML
    private void onRefrescar(ActionEvent evento) {
        cargarInstituciones();
        cargarTabla();
    }

    @FXML
    private void onReporte(ActionEvent evento) {
        lblEstado.setText("Generando reporte...");

        ejecutarEnHilo(
                () -> {
                    new ReporteParticipantes().mostrarEnVisor();
                    return true;
                },
                ok -> lblEstado.setText(
                        "Reporte generado en el visor de JasperReports."),
                "No se pudo generar el reporte.");
    }

    @FXML
    private void onPdf(ActionEvent evento) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte PDF");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        selector.setInitialFileName("participantes_por_institucion.pdf");

        File destino =
                selector.showSaveDialog(btnPdf.getScene().getWindow());
        if (destino == null) {
            return;
        }

        String ruta = destino.getAbsolutePath();
        lblEstado.setText("Exportando PDF...");

        ejecutarEnHilo(
                () -> {
                    new ReporteParticipantes().exportarPdf(ruta);
                    return ruta;
                },
                r -> {
                    lblEstado.setText("PDF generado.");
                    mostrarInfo("PDF generado",
                            "El reporte se guardó en:\n" + r);
                },
                "No se pudo exportar el reporte a PDF.");
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    @FXML
    private void onNuevo(ActionEvent evento) {
        limpiarFormulario();
    }

    @FXML
    private void onGuardar(ActionEvent evento) {
        Participante nuevo = leerFormulario();
        if (!validarFormulario(nuevo)) {
            return;
        }

        ejecutarEnHilo(
                () -> {
                    if (dao.existeDni(nuevo.getDni(), null)) {
                        throw new IllegalStateException(
                                "Ya existe un participante con el DNI "
                                + nuevo.getDni() + ".");
                    }
                    if (dao.existeCorreo(nuevo.getCorreo(), null)) {
                        throw new IllegalStateException(
                                "Ya existe un participante con el correo "
                                + nuevo.getCorreo() + ".");
                    }
                    return dao.insertar(nuevo);
                },
                id -> {
                    mostrarInfo("Participante registrado",
                            "El participante fue registrado con el ID "
                            + id + ".");
                    limpiarFormulario();
                    cargarInstituciones();
                    cargarTabla();
                },
                "No se pudo registrar el participante.");
    }

    @FXML
    private void onActualizar(ActionEvent evento) {
        if (seleccionActual == null) {
            mostrarInfo("Sin selección",
                    "Selecciona un participante de la tabla para "
                    + "editar sus datos.");
            return;
        }

        Participante p = leerFormulario();
        p.setIdParticipante(seleccionActual.getIdParticipante());
        if (!validarFormulario(p)) {
            return;
        }

        int id = p.getIdParticipante();

        ejecutarEnHilo(
                () -> {
                    // Verificar que el registro exista antes de editar.
                    if (dao.buscarPorId(id) == null) {
                        throw new IllegalStateException(
                                "El registro ya no existe en la base "
                                + "de datos. Refresca la lista.");
                    }
                    if (dao.existeDni(p.getDni(), id)) {
                        throw new IllegalStateException(
                                "Otro participante ya usa el DNI "
                                + p.getDni() + ".");
                    }
                    if (dao.existeCorreo(p.getCorreo(), id)) {
                        throw new IllegalStateException(
                                "Otro participante ya usa el correo "
                                + p.getCorreo() + ".");
                    }
                    int filas = dao.actualizar(p);
                    if (filas == 0) {
                        throw new IllegalStateException(
                                "El registro ya no existe en la base "
                                + "de datos.");
                    }
                    return filas;
                },
                filas -> {
                    mostrarInfo("Participante actualizado",
                            "Los datos del participante fueron "
                            + "actualizados correctamente.");
                    limpiarFormulario();
                    cargarInstituciones();
                    cargarTabla();
                },
                "No se pudo actualizar el participante.");
    }

    @FXML
    private void onEliminar(ActionEvent evento) {
        if (seleccionActual == null) {
            mostrarInfo("Sin selección",
                    "Selecciona un participante de la tabla para "
                    + "eliminarlo.");
            return;
        }

        Participante objetivo = seleccionActual;

        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar participante?");
        confirmacion.setContentText(
                "Se eliminará a " + objetivo.getNombres() + " "
                + objetivo.getApellidos() + " (DNI "
                + objetivo.getDni() + ").\n"
                + "Esta acción no se puede deshacer.");
        confirmacion.getButtonTypes().setAll(
                ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.YES) {
            return;
        }

        int id = objetivo.getIdParticipante();

        ejecutarEnHilo(
                () -> {
                    if (!dao.eliminar(id)) {
                        throw new IllegalStateException(
                                "El registro ya no existe en la base "
                                + "de datos.");
                    }
                    return true;
                },
                ok -> {
                    mostrarInfo("Participante eliminado",
                            "El participante fue eliminado.");
                    limpiarFormulario();
                    cargarInstituciones();
                    cargarTabla();
                },
                "No se pudo eliminar el participante.");
    }

    // ----------------------------------------------------------------
    // Formulario
    // ----------------------------------------------------------------

    private Participante leerFormulario() {
        return new Participante(
                txtNombres.getText(),
                txtApellidos.getText(),
                txtDni.getText(),
                txtCorreo.getText(),
                txtTelefono.getText(),
                txtInstitucion.getText());
    }

    private void cargarEnFormulario(Participante p) {
        txtNombres.setText(p.getNombres());
        txtApellidos.setText(p.getApellidos());
        txtDni.setText(p.getDni());
        txtCorreo.setText(p.getCorreo());
        txtTelefono.setText(p.getTelefono());
        txtInstitucion.setText(p.getInstitucion());
        lblId.setText("Editando registro ID " + p.getIdParticipante());
    }

    private void limpiarFormulario() {
        txtNombres.clear();
        txtApellidos.clear();
        txtDni.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtInstitucion.clear();
        lblId.setText("Nuevo registro");
        seleccionActual = null;
        tvParticipantes.getSelectionModel().clearSelection();
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        txtNombres.requestFocus();
    }

    // ----------------------------------------------------------------
    // Validaciones y mensajes
    // ----------------------------------------------------------------

    private boolean validarFormulario(Participante p) {
        List<String> errores = ValidadorParticipante.validar(p);
        if (errores.isEmpty()) {
            return true;
        }

        Alert aviso = new Alert(AlertType.WARNING);
        aviso.setTitle("Validación de datos");
        aviso.setHeaderText("Corrige los siguientes errores:");
        aviso.setContentText(String.join("\n", errores));
        aviso.showAndWait();
        return false;
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert info = new Alert(AlertType.INFORMATION);
        info.setTitle(titulo);
        info.setHeaderText(null);
        info.setContentText(mensaje);
        info.showAndWait();
    }

    private void mostrarError(String titulo, Throwable e) {
        String detalle;
        if (e == null) {
            detalle = "Error desconocido.";
        } else if (e.getMessage() == null || e.getMessage().isBlank()) {
            detalle = e.toString();
        } else {
            detalle = e.getMessage();
        }

        Alert error = new Alert(AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText(titulo);
        error.setContentText(detalle);
        error.showAndWait();
        lblEstado.setText("Ocurrió un error en la última operación.");
    }

    // ----------------------------------------------------------------
    // Ejecución en hilo aparte
    // ----------------------------------------------------------------

    /**
     * Lanza una consulta a la base de datos en un hilo aparte
     * (Task) y ejecuta la acción de éxito en el hilo de JavaFX,
     * para no congelar la interfaz.
     */
    private <T> void ejecutarEnHilo(Consulta<T> consulta,
                                    Consumer<T> alExito,
                                    String mensajeError) {

        Task<T> tarea = new Task<>() {
            @Override
            protected T call() throws Exception {
                return consulta.ejecutar();
            }
        };

        tarea.setOnSucceeded(evento -> alExito.accept(tarea.getValue()));
        tarea.setOnFailed(evento ->
                mostrarError(mensajeError, tarea.getException()));

        Thread hilo = new Thread(tarea, "consulta-participantes");
        hilo.setDaemon(true);
        hilo.start();
    }
}
