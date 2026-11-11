package vallegrande.edu.pe.g07_prehackaton_t06.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import vallegrande.edu.pe.g07_prehackaton_t06.dao.EventoDAO;
import vallegrande.edu.pe.g07_prehackaton_t06.model.Evento;
import vallegrande.edu.pe.g07_prehackaton_t06.report.ReporteService;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

public class HelloController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtLugar;
    @FXML private TextArea txtDescripcion;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cbModalidad;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroEstado;
    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, Integer> colId;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, LocalDate> colFecha;
    @FXML private TableColumn<Evento, String> colLugar;
    @FXML private TableColumn<Evento, String> colModalidad;
    @FXML private TableColumn<Evento, String> colEstado;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnReporte;

    private final EventoDAO eventoDAO = new EventoDAO();
    private final ReporteService reporteService = new ReporteService();
    private final ObservableList<Evento> listaEventos = FXCollections.observableArrayList();
    private FilteredList<Evento> eventosFiltrados;
    private Evento eventoSeleccionado;

    @FXML
    public void initialize() {
        cbModalidad.getItems().setAll("Presencial", "Virtual", "Híbrida");
        cbEstado.getItems().setAll("Programado", "En curso", "Finalizado", "Cancelado");
        cbFiltroEstado.getItems().setAll("Todos", "Programado", "En curso", "Finalizado", "Cancelado");
        cbFiltroEstado.setValue("Todos");

        configurarTabla();
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());
        cbFiltroEstado.valueProperty().addListener((obs, oldValue, newValue) -> aplicarFiltros());

        btnGuardar.setOnAction(e -> guardarEvento());
        btnActualizar.setOnAction(e -> actualizarEvento());
        btnEliminar.setOnAction(e -> eliminarEvento());
        btnLimpiar.setOnAction(e -> limpiarFormulario());
        btnReporte.setOnAction(e -> generarReporte());

        tablaEventos.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, seleccionado) -> {
                    if (seleccionado != null) cargarFormulario(seleccionado);
                });
        cargarEventos();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEvento"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colLugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
        colModalidad.setCellValueFactory(new PropertyValueFactory<>("modalidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        eventosFiltrados = new FilteredList<>(listaEventos, evento -> true);
        SortedList<Evento> ordenados = new SortedList<>(eventosFiltrados);
        ordenados.comparatorProperty().bind(tablaEventos.comparatorProperty());
        tablaEventos.setItems(ordenados);
    }

    private void cargarEventos() {
        try {
            listaEventos.setAll(eventoDAO.listar());
            aplicarFiltros();
        } catch (SQLException e) {
            mostrarError("No se pudieron cargar los eventos.", e);
        }
    }

    private void aplicarFiltros() {
        if (eventosFiltrados == null) return;
        String texto = txtBuscar.getText() == null ? "" :
                txtBuscar.getText().trim().toLowerCase(Locale.ROOT);
        String estado = cbFiltroEstado.getValue();

        eventosFiltrados.setPredicate(evento -> {
            String nombre = seguro(evento.getNombre());
            String lugar = seguro(evento.getLugar());
            String estadoEvento = seguro(evento.getEstado());
            boolean coincideTexto = nombre.contains(texto) || lugar.contains(texto);
            boolean coincideEstado = estado == null || estado.equals("Todos")
                    || estadoEvento.equalsIgnoreCase(estado);
            return coincideTexto && coincideEstado;
        });
    }

    private String seguro(String valor) {
        return valor == null ? "" : valor.toLowerCase(Locale.ROOT);
    }

    private void guardarEvento() {
        if (!validarFormulario(null)) return;
        try {
            if (eventoDAO.insertar(obtenerDatosFormulario())) {
                mostrarMensaje(Alert.AlertType.INFORMATION, "Registro exitoso",
                        "El evento se guardó correctamente.");
                cargarEventos();
                limpiarFormulario();
            }
        } catch (SQLException e) {
            mostrarError("No se pudo guardar el evento.", e);
        }
    }

    private void actualizarEvento() {
        if (eventoSeleccionado == null) {
            mostrarAdvertencia("Selecciona un evento de la tabla para actualizarlo.");
            return;
        }
        if (!validarFormulario(eventoSeleccionado.getIdEvento())) return;

        Evento evento = obtenerDatosFormulario();
        evento.setIdEvento(eventoSeleccionado.getIdEvento());
        try {
            if (eventoDAO.actualizar(evento)) {
                mostrarMensaje(Alert.AlertType.INFORMATION, "Actualización exitosa",
                        "El evento se actualizó correctamente.");
                cargarEventos();
                limpiarFormulario();
            } else {
                mostrarAdvertencia("El evento ya no existe o fue eliminado.");
                cargarEventos();
            }
        } catch (SQLException e) {
            mostrarError("No se pudo actualizar el evento.", e);
        }
    }

    private void eliminarEvento() {
        if (eventoSeleccionado == null) {
            mostrarAdvertencia("Selecciona un evento de la tabla para eliminarlo.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar este evento?");
        confirmacion.setContentText(eventoSeleccionado.getNombre());
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (eventoDAO.eliminar(eventoSeleccionado.getIdEvento())) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Eliminación exitosa",
                            "El evento fue eliminado.");
                } else {
                    mostrarAdvertencia("El evento ya no existe.");
                }
                cargarEventos();
                limpiarFormulario();
            } catch (SQLException e) {
                mostrarError("No se pudo eliminar el evento.", e);
            }
        }
    }

    private boolean validarFormulario(Integer idExcluir) {
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        String lugar = txtLugar.getText() == null ? "" : txtLugar.getText().trim();

        if (nombre.isEmpty() || lugar.isEmpty() || dpFecha.getValue() == null
                || cbModalidad.getValue() == null || cbEstado.getValue() == null) {
            mostrarAdvertencia("Completa el nombre, fecha, lugar, modalidad y estado.");
            return false;
        }
        if (nombre.length() > 150 || lugar.length() > 150) {
            mostrarAdvertencia("El nombre y el lugar no pueden superar 150 caracteres.");
            return false;
        }
        if (txtDescripcion.getText() != null && txtDescripcion.getText().length() > 60000) {
            mostrarAdvertencia("La descripción es demasiado extensa.");
            return false;
        }

        try {
            boolean duplicado = eventoDAO.existeDuplicado(
                    nombre, Date.valueOf(dpFecha.getValue()), lugar, idExcluir);
            if (duplicado) {
                mostrarAdvertencia("Ya existe un evento con el mismo nombre, fecha y lugar.");
                return false;
            }
        } catch (SQLException e) {
            mostrarError("No se pudo validar si el evento está duplicado.", e);
            return false;
        }
        return true;
    }

    private Evento obtenerDatosFormulario() {
        return new Evento(0, txtNombre.getText().trim(),
                txtDescripcion.getText() == null ? "" : txtDescripcion.getText().trim(),
                dpFecha.getValue(), txtLugar.getText().trim(),
                cbModalidad.getValue(), cbEstado.getValue());
    }

    private void cargarFormulario(Evento evento) {
        eventoSeleccionado = evento;
        txtNombre.setText(evento.getNombre());
        txtDescripcion.setText(evento.getDescripcion());
        dpFecha.setValue(evento.getFecha());
        txtLugar.setText(evento.getLugar());
        cbModalidad.setValue(evento.getModalidad());
        cbEstado.setValue(evento.getEstado());
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDescripcion.clear();
        txtLugar.clear();
        dpFecha.setValue(null);
        cbModalidad.setValue(null);
        cbEstado.setValue(null);
        eventoSeleccionado = null;
        tablaEventos.getSelectionModel().clearSelection();
    }

    private void generarReporte() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte de eventos");
        selector.setInitialFileName("reporte_eventos.pdf");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Documento PDF", "*.pdf"));
        File archivo = selector.showSaveDialog(tablaEventos.getScene().getWindow());
        if (archivo == null) return;

        String ruta = archivo.getAbsolutePath().toLowerCase(Locale.ROOT).endsWith(".pdf")
                ? archivo.getAbsolutePath() : archivo.getAbsolutePath() + ".pdf";
        try {
            reporteService.generarPDF(java.util.List.copyOf(eventosFiltrados), ruta);
            mostrarMensaje(Alert.AlertType.INFORMATION, "Reporte generado",
                    "El PDF se guardó correctamente en:\n" + ruta);
        } catch (Exception e) {
            mostrarError("No se pudo generar el reporte PDF.", e);
        }
    }

    private void mostrarAdvertencia(String mensaje) {
        mostrarMensaje(Alert.AlertType.WARNING, "Atención", mensaje);
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(e.getMessage() == null ? e.toString() : e.getMessage());
        alerta.showAndWait();
        e.printStackTrace();
    }
}
