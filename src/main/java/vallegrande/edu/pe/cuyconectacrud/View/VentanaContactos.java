package vallegrande.edu.pe.cuyconectacrud.View;

import vallegrande.edu.pe.cuyconectacrud.model.ConexionDB;
import vallegrande.edu.pe.cuyconectacrud.model.Contacto;
import vallegrande.edu.pe.cuyconectacrud.model.ContactoDAO;
import vallegrande.edu.pe.cuyconectacrud.model.FiltroFecha;
import vallegrande.edu.pe.cuyconectacrud.util.BusquedaDinamica;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VentanaContactos extends JFrame {

    private final Color verde = new Color(23, 107, 69);
    private final Color fondo = new Color(245, 248, 246);

    private final String[] columnas = {
            "ID", "Nombre", "Teléfono", "Correo",
            "Motivo", "Mensaje", "Fecha"
    };

    private DefaultTableModel modelo;
    private JTable tabla;
    private JTextField txtBuscar;

    private static final String TODOS_MOTIVOS = "Todos los motivos";

    private final ContactoDAO contactoDAO = new ContactoDAO();
    private final JComboBox<String> cmbMotivo =
            new JComboBox<>(new String[] { TODOS_MOTIVOS });
    private final JComboBox<FiltroFecha> cmbFecha =
            new JComboBox<>(FiltroFecha.values());
    private final JLabel lblResultados = new JLabel(" ");
    private boolean actualizandoFiltros = false;

    private JButton btnBuscar;
    private JButton btnActualizar;
    private JButton btnLimpiar;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;

    private SwingWorker<List<Object[]>, Void> cargaActual;

    public VentanaContactos() {
        setTitle("CuyConecta - Contactos");
        setSize(1200, 680);
        setMinimumSize(new Dimension(950, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(fondo);

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(fondo);
        contenido.setBorder(
                BorderFactory.createEmptyBorder(25, 28, 25, 28)
        );

        // Encabezado
        JLabel titulo = new JLabel("Contactos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(verde);

        JLabel subtitulo = new JLabel(
                "Administra los mensajes recibidos desde CuyConecta"
        );
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(95, 110, 100));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(6));
        titulos.add(subtitulo);

        JButton btnInicio = crearBoton("← Volver al inicio");
        btnInicio.addActionListener(e ->
                VentanaInicio.navegarDesdeModulo("Inicio", this)
        );

        JPanel encabezado = new JPanel(new BorderLayout(12, 10));
        encabezado.setOpaque(false);
        encabezado.add(titulos, BorderLayout.CENTER);
        encabezado.add(btnInicio, BorderLayout.EAST);

        // Búsqueda
        txtBuscar = new JTextField(22);
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBuscar.setPreferredSize(new Dimension(230, 38));
        txtBuscar.setToolTipText(
                "Escribe para filtrar: ID, nombre, teléfono, correo, motivo o mensaje"
        );

        btnBuscar = crearBoton("Buscar");
        btnActualizar = crearBoton("Actualizar");
        btnLimpiar = crearBoton("Limpiar filtros");
        btnNuevo = crearBoton("Nuevo contacto");
        btnEditar = crearBoton("Editar");
        btnEliminar = crearBoton("Eliminar");

        cmbMotivo.setPreferredSize(new Dimension(190, 36));
        cmbFecha.setPreferredSize(new Dimension(170, 36));
        lblResultados.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblResultados.setForeground(new Color(95, 110, 100));

        JPanel filaBusqueda = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );
        filaBusqueda.setOpaque(false);
        filaBusqueda.add(new JLabel("Buscar:"));
        filaBusqueda.add(txtBuscar);
        filaBusqueda.add(btnBuscar);
        filaBusqueda.add(btnActualizar);

        JPanel filaFiltros = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );
        filaFiltros.setOpaque(false);
        filaFiltros.add(new JLabel("Filtrar por:"));
        filaFiltros.add(cmbMotivo);
        filaFiltros.add(cmbFecha);
        filaFiltros.add(btnLimpiar);

        JPanel busqueda = new JPanel(new GridLayout(0, 1, 0, 8));
        busqueda.setOpaque(false);
        busqueda.add(filaBusqueda);
        busqueda.add(filaFiltros);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        botones.setOpaque(false);
        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);

        JPanel acciones = new JPanel(new BorderLayout(0, 12));
        acciones.setOpaque(false);
        acciones.add(busqueda, BorderLayout.CENTER);
        acciones.add(botones, BorderLayout.SOUTH);

        JPanel superior = new JPanel(new BorderLayout(0, 18));
        superior.setOpaque(false);
        superior.add(encabezado, BorderLayout.NORTH);
        superior.add(acciones, BorderLayout.CENTER);

        // Tabla
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionBackground(new Color(198, 230, 207));
        tabla.setSelectionForeground(new Color(22, 67, 49));
        tabla.setGridColor(new Color(225, 232, 227));
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        tabla.getTableHeader().setBackground(
                new Color(230, 239, 233)
        );
        tabla.getTableHeader().setForeground(
                new Color(22, 67, 49)
        );
        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(95);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(145);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(
                BorderFactory.createLineBorder(new Color(225, 232, 227))
        );
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panelTabla = new JPanel(new BorderLayout(0, 8));
        panelTabla.setOpaque(false);

        JLabel etiquetaTabla = new JLabel("Contactos registrados");
        etiquetaTabla.setFont(
                new Font("SansSerif", Font.BOLD, 17)
        );

        JPanel tituloTabla = new JPanel(new BorderLayout());
        tituloTabla.setOpaque(false);
        tituloTabla.add(etiquetaTabla, BorderLayout.WEST);
        tituloTabla.add(lblResultados, BorderLayout.EAST);

        panelTabla.add(tituloTabla, BorderLayout.NORTH);
        panelTabla.add(scroll, BorderLayout.CENTER);

        contenido.add(superior, BorderLayout.NORTH);
        contenido.add(panelTabla, BorderLayout.CENTER);

        principal.add(contenido, BorderLayout.CENTER);
        setContentPane(principal);

        // Acciones
        btnBuscar.addActionListener(
                e -> cargarContactos(txtBuscar.getText())
        );

        txtBuscar.addActionListener(
                e -> cargarContactos(txtBuscar.getText())
        );

        btnActualizar.addActionListener(
                e -> cargarContactos(txtBuscar.getText())
        );

        // Filtros avanzados (ComboBox)
        cmbMotivo.addActionListener(e -> {
            if (!actualizandoFiltros) {
                cargarContactos(txtBuscar.getText());
            }
        });
        cmbFecha.addActionListener(e -> {
            if (!actualizandoFiltros) {
                cargarContactos(txtBuscar.getText());
            }
        });
        btnLimpiar.addActionListener(e -> limpiarFiltros());

        // Búsqueda dinámica: filtra mientras se escribe (KeyReleased)
        new BusquedaDinamica(
                txtBuscar, 300, () -> cargarContactos(txtBuscar.getText())
        );

        btnNuevo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarContacto());
        btnEliminar.addActionListener(e -> eliminarContacto());

        cargarMotivos();
        cargarContactos("");
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(verde);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        );
        return boton;
    }

    private void cargarContactos(String busqueda) {
        // Evita que una consulta anterior reemplace resultados más recientes.
        if (cargaActual != null && !cargaActual.isDone()) {
            cargaActual.cancel(true);
        }

        final String texto = busqueda == null ? "" : busqueda.trim();
        final String motivo = motivoSeleccionado();
        final FiltroFecha rangoFecha =
                (FiltroFecha) cmbFecha.getSelectedItem();

        setBotonesCarga(false);

        cargaActual = new SwingWorker<List<Object[]>, Void>() {

            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> filas = new ArrayList<>();

                for (Contacto c : contactoDAO.filtrar(texto, motivo, rangoFecha)) {
                    if (isCancelled()) {
                        break;
                    }

                    filas.add(new Object[] {
                            (long) c.getId(),
                            c.getNombre(),
                            c.getTelefono(),
                            c.getCorreo(),
                            c.getMotivo(),
                            c.getMensaje(),
                            c.getFecha()
                    });
                }

                return filas;
            }

            @Override
            protected void done() {
                // Si esta carga fue cancelada o reemplazada, no actualiza la tabla.
                if (isCancelled() || cargaActual != this) {
                    return;
                }

                try {
                    List<Object[]> contactos = get();
                    modelo.setRowCount(0);

                    for (Object[] contacto : contactos) {
                        modelo.addRow(contacto);
                    }

                    lblResultados.setText(
                            contactos.size()
                                    + (contactos.size() == 1
                                    ? " resultado" : " resultados")
                    );
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    mostrarError(
                            "Se interrumpió la carga de contactos.", ex
                    );
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause() != null
                            ? ex.getCause() : ex;

                    mostrarError(
                            "No se pudieron cargar los contactos.", causa
                    );
                } finally {
                    if (cargaActual == this) {
                        setBotonesCarga(true);
                    }
                }
            }
        };

        cargaActual.execute();
    }

    /** Devuelve el motivo elegido en el ComboBox, o null si es "todos". */
    private String motivoSeleccionado() {
        Object item = cmbMotivo.getSelectedItem();
        if (item == null || TODOS_MOTIVOS.equals(item)) {
            return null;
        }
        return item.toString();
    }

    /** Llena el ComboBox de motivos con los valores reales de la BD. */
    private void cargarMotivos() {
        new SwingWorker<List<String>, Void>() {

            @Override
            protected List<String> doInBackground() throws Exception {
                return contactoDAO.listarMotivos();
            }

            @Override
            protected void done() {
                try {
                    List<String> motivos = get();
                    String anterior = motivoSeleccionado();

                    actualizandoFiltros = true;
                    try {
                        cmbMotivo.removeAllItems();
                        cmbMotivo.addItem(TODOS_MOTIVOS);
                        for (String m : motivos) {
                            cmbMotivo.addItem(m);
                        }

                        boolean existe = anterior != null
                                && motivos.contains(anterior);
                        cmbMotivo.setSelectedItem(
                                existe ? anterior : TODOS_MOTIVOS
                        );
                    } finally {
                        actualizandoFiltros = false;
                    }

                    // El motivo filtrado ya no existe (p. ej. se eliminó).
                    if (anterior != null && !motivos.contains(anterior)) {
                        cargarContactos(txtBuscar.getText());
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    // Si falla, el filtro queda solo con "Todos los motivos".
                }
            }
        }.execute();
    }

    private void limpiarFiltros() {
        actualizandoFiltros = true;
        try {
            txtBuscar.setText("");
            cmbMotivo.setSelectedIndex(0);
            cmbFecha.setSelectedIndex(0);
        } finally {
            actualizandoFiltros = false;
        }
        cargarContactos("");
    }

    private void setBotonesCarga(boolean habilitados) {
        if (btnBuscar != null) {
            btnBuscar.setEnabled(habilitados);
        }
        if (btnActualizar != null) {
            btnActualizar.setEnabled(habilitados);
        }
        if (btnLimpiar != null) {
            btnLimpiar.setEnabled(habilitados);
        }
    }

    private void abrirFormulario(Object[] datos) {
        JTextField nombre = new JTextField();
        JTextField telefono = new JTextField();
        JTextField correo = new JTextField();
        JTextField motivo = new JTextField();
        JTextArea mensaje = new JTextArea(4, 25);
        mensaje.setLineWrap(true);
        mensaje.setWrapStyleWord(true);

        if (datos != null) {
            nombre.setText(texto(datos[1]));
            telefono.setText(texto(datos[2]));
            correo.setText(texto(datos[3]));
            motivo.setText(texto(datos[4]));
            mensaje.setText(texto(datos[5]));
        }

        JPanel formulario = new JPanel(new GridLayout(0, 1, 5, 5));
        formulario.add(new JLabel("Nombre *"));
        formulario.add(nombre);
        formulario.add(new JLabel("Teléfono"));
        formulario.add(telefono);
        formulario.add(new JLabel("Correo electrónico *"));
        formulario.add(correo);
        formulario.add(new JLabel("Motivo *"));
        formulario.add(motivo);
        formulario.add(new JLabel("Mensaje *"));
        formulario.add(new JScrollPane(mensaje));

        int resultado = JOptionPane.showConfirmDialog(
                this,
                formulario,
                datos == null ? "Nuevo contacto" : "Editar contacto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        if (nombre.getText().trim().isEmpty()
                || correo.getText().trim().isEmpty()
                || motivo.getText().trim().isEmpty()
                || mensaje.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Completa nombre, correo, motivo y mensaje.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql = datos == null
                ? "INSERT INTO contactos "
                + "(nombre, telefono, correo, motivo, mensaje) "
                + "VALUES (?, ?, ?, ?, ?)"
                : "UPDATE contactos SET nombre=?, telefono=?, correo=?, "
                + "motivo=?, mensaje=? WHERE id=?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre.getText().trim());
            ps.setString(2, telefono.getText().trim());
            ps.setString(3, correo.getText().trim());
            ps.setString(4, motivo.getText().trim());
            ps.setString(5, mensaje.getText().trim());

            if (datos != null) {
                ps.setLong(6, ((Number) datos[0]).longValue());
            }

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    datos == null
                            ? "Contacto registrado correctamente."
                            : "Contacto actualizado correctamente."
            );

            cargarMotivos();
            cargarContactos(txtBuscar.getText());

        } catch (SQLException ex) {
            mostrarError("No se pudo guardar el contacto.", ex);
        }
    }

    private String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private void editarContacto() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un contacto para editar."
            );
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);
        Object[] datos = new Object[modelo.getColumnCount()];

        for (int i = 0; i < datos.length; i++) {
            datos[i] = modelo.getValueAt(filaModelo, i);
        }

        abrirFormulario(datos);
    }

    private void eliminarContacto() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un contacto para eliminar."
            );
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);
        long id = ((Number) modelo.getValueAt(filaModelo, 0)).longValue();

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar el contacto seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM contactos WHERE id = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this, "Contacto eliminado correctamente."
            );

            cargarMotivos();
            cargarContactos(txtBuscar.getText());

        } catch (SQLException ex) {
            mostrarError("No se pudo eliminar el contacto.", ex);
        }
    }

    private void mostrarError(String mensaje, Throwable ex) {
        JOptionPane.showMessageDialog(
                this,
                mensaje + "\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}