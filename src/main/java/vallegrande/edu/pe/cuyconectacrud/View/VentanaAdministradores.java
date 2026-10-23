
package vallegrande.edu.pe.cuyconectacrud.View;

import vallegrande.edu.pe.cuyconectacrud.model.Admin;
import vallegrande.edu.pe.cuyconectacrud.model.AdminDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VentanaAdministradores extends JFrame {

    private final AdminDAO dao = new AdminDAO();
    private List<Admin> administradoresCache = new ArrayList<>();

    private final Color verdeOscuro = new Color(22, 67, 49);
    private final Color verdePrincipal = new Color(38, 128, 82);
    private final Color verdeClaro = new Color(226, 242, 231);
    private final Color fondo = new Color(242, 247, 243);
    private final Color texto = new Color(35, 58, 45);
    private final Color textoSecundario = new Color(91, 113, 97);
    private final Color borde = new Color(222, 233, 224);
    private final Color rojo = new Color(180, 55, 60);

    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JTextField buscar = new JTextField(20);
    private final JLabel etiquetaConteo = new JLabel("Administradores: 0");

    private JButton botonActualizar;
    private JButton botonNuevo;
    private JButton botonBuscar;
    private JButton botonVer;
    private JButton botonEditar;
    private JButton botonEliminar;

    private SwingWorker<List<Admin>, Void> trabajadorCarga;

    public VentanaAdministradores() {
        setTitle("CuyConecta 3.0 | Administradores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(fondo);

        modelo = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Correo electrónico",
                        "Estado",
                        "Fecha de creación"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);

        construirContenido();
        cargarAdministradores();
    }

    private void construirContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 22));
        contenido.setBackground(fondo);
        contenido.setBorder(new EmptyBorder(28, 30, 26, 30));

        // Encabezado
        JPanel encabezado = new JPanel(new BorderLayout(15, 15));
        encabezado.setOpaque(false);

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JButton volver = crearBoton("←  Volver al inicio", verdeOscuro);
        volver.setAlignmentX(Component.LEFT_ALIGNMENT);
        volver.addActionListener(e ->
                VentanaInicio.navegarDesdeModulo("Inicio", this)
        );

        JLabel titulo = new JLabel("Administradores");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(verdeOscuro);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel(
                "Gestiona las cuentas que tienen acceso al sistema."
        );
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descripcion.setForeground(textoSecundario);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        titulos.add(volver);
        titulos.add(Box.createVerticalStrut(14));
        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(7));
        titulos.add(descripcion);

        botonNuevo = crearBoton("+ Nuevo administrador", verdePrincipal);
        botonNuevo.addActionListener(e -> abrirFormulario(null));

        JPanel zonaNuevo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        zonaNuevo.setOpaque(false);
        zonaNuevo.add(botonNuevo);

        encabezado.add(titulos, BorderLayout.CENTER);
        encabezado.add(zonaNuevo, BorderLayout.EAST);

        // Panel principal de tabla
        JPanel zonaTabla = new JPanel(new BorderLayout(0, 16));
        zonaTabla.setBackground(Color.WHITE);
        zonaTabla.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde),
                new EmptyBorder(18, 18, 16, 18)
        ));

        // Barra de búsqueda
        JPanel herramientas = new JPanel(new BorderLayout(12, 8));
        herramientas.setOpaque(false);

        JPanel busqueda = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );
        busqueda.setOpaque(false);

        JLabel etiquetaBuscar = new JLabel("Buscar:");
        etiquetaBuscar.setForeground(texto);
        etiquetaBuscar.setFont(new Font("SansSerif", Font.BOLD, 13));

        buscar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        buscar.setForeground(texto);
        buscar.setBackground(new Color(250, 252, 250));
        buscar.setCaretColor(verdePrincipal);
        buscar.setPreferredSize(new Dimension(245, 39));
        buscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde),
                new EmptyBorder(5, 10, 5, 10)
        ));
        buscar.setToolTipText("Buscar por ID o correo electrónico");
        buscar.addActionListener(e -> filtrar());

        botonBuscar = crearBoton("Buscar", verdePrincipal);
        botonBuscar.addActionListener(e -> filtrar());

        botonActualizar = crearBoton("↻ Actualizar", verdeOscuro);
        botonActualizar.addActionListener(e -> cargarAdministradores());

        busqueda.add(etiquetaBuscar);
        busqueda.add(buscar);
        busqueda.add(botonBuscar);
        busqueda.add(botonActualizar);

        etiquetaConteo.setFont(new Font("SansSerif", Font.BOLD, 12));
        etiquetaConteo.setForeground(textoSecundario);
        etiquetaConteo.setHorizontalAlignment(SwingConstants.RIGHT);

        herramientas.add(busqueda, BorderLayout.CENTER);
        herramientas.add(etiquetaConteo, BorderLayout.EAST);

        configurarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // Acciones
        JPanel acciones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 6)
        );
        acciones.setBackground(Color.WHITE);

        botonVer = crearBoton("Ver detalle", verdeOscuro);
        botonEditar = crearBoton("Editar", verdePrincipal);
        botonEliminar = crearBoton("Eliminar", rojo);

        botonVer.addActionListener(e -> verSeleccionado());
        botonEditar.addActionListener(e -> editarSeleccionado());
        botonEliminar.addActionListener(e -> eliminarSeleccionado());

        acciones.add(botonVer);
        acciones.add(botonEditar);
        acciones.add(botonEliminar);

        zonaTabla.add(herramientas, BorderLayout.NORTH);
        zonaTabla.add(scroll, BorderLayout.CENTER);
        zonaTabla.add(acciones, BorderLayout.SOUTH);

        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(zonaTabla, BorderLayout.CENTER);

        add(contenido, BorderLayout.CENTER);

        actualizarEstadoAcciones();
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEstadoAcciones();
            }
        });
    }

    private void configurarTabla() {
        tabla.setRowHeight(43);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setForeground(texto);
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionForeground(verdeOscuro);
        tabla.setSelectionBackground(verdeClaro);
        tabla.setGridColor(borde);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setAutoCreateRowSorter(true);

        tabla.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        tabla.getTableHeader().setBackground(verdeClaro);
        tabla.getTableHeader().setForeground(verdeOscuro);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 43));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setOpaque(true);
        tabla.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, borde)
        );

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(55);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(330);
        tabla.getColumnModel().getColumn(2).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean seleccionado,
                            boolean enfocado, int fila, int columna) {

                        Component c = super.getTableCellRendererComponent(
                                table, value, seleccionado, enfocado, fila, columna
                        );

                        setHorizontalAlignment(SwingConstants.CENTER);
                        setFont(new Font("SansSerif", Font.BOLD, 12));

                        if (!seleccionado) {
                            if ("Activo".equals(value)) {
                                c.setForeground(verdePrincipal);
                            } else {
                                c.setForeground(rojo);
                            }
                        } else {
                            c.setForeground(verdeOscuro);
                        }

                        return c;
                    }
                }
        );
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(190);
    }

    private JButton crearBoton(String nombre, Color color) {
        JButton boton = new JButton(nombre);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setBorder(new EmptyBorder(11, 16, 11, 16));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void cargarAdministradores() {
        if (trabajadorCarga != null && !trabajadorCarga.isDone()) {
            return;
        }

        botonActualizar.setEnabled(false);
        botonBuscar.setEnabled(false);
        botonNuevo.setEnabled(false);

        trabajadorCarga = new SwingWorker<List<Admin>, Void>() {
            @Override
            protected List<Admin> doInBackground() throws Exception {
                return dao.listar();
            }

            @Override
            protected void done() {
                botonActualizar.setEnabled(true);
                botonBuscar.setEnabled(true);
                botonNuevo.setEnabled(true);

                try {
                    administradoresCache = get();
                    filtrar();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            VentanaAdministradores.this,
                            "No se pudieron cargar los administradores.\n"
                                    + obtenerMensajeError(ex),
                            "Error de conexión",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        trabajadorCarga.execute();
    }

    private void filtrar() {
        String criterio = buscar.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        modelo.setRowCount(0);

        for (Admin admin : administradoresCache) {
            String correo = admin.getCorreo() == null
                    ? ""
                    : admin.getCorreo().toLowerCase(Locale.ROOT);

            String id = String.valueOf(admin.getId());

            if (id.contains(criterio) || correo.contains(criterio)) {
                modelo.addRow(new Object[]{
                        admin.getId(),
                        admin.getCorreo(),
                        admin.isActivo() ? "Activo" : "Inactivo",
                        admin.getFechaCreacion()
                });
            }
        }

        etiquetaConteo.setText(
                "Mostrando " + modelo.getRowCount()
                        + " de " + administradoresCache.size()
        );

        actualizarEstadoAcciones();
    }

    private Admin seleccionado() {
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un administrador de la tabla.",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        int filaModelo = tabla.convertRowIndexToModel(fila);
        int id = ((Number) modelo.getValueAt(filaModelo, 0)).intValue();

        try {
            Admin admin = dao.buscarPorId(id);

            if (admin == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "El administrador ya no existe. Actualiza la lista.",
                        "Registro no encontrado",
                        JOptionPane.WARNING_MESSAGE
                );
                cargarAdministradores();
            }

            return admin;

        } catch (SQLException ex) {
            mostrarError(ex);
            return null;
        }
    }

    private void abrirFormulario(Admin admin) {
        FormularioAdmin formulario =
                new FormularioAdmin(this, dao, admin);

        formulario.setVisible(true);

        if (formulario.isGuardado()) {
            cargarAdministradores();
        }
    }

    private void editarSeleccionado() {
        Admin admin = seleccionado();

        if (admin != null) {
            abrirFormulario(admin);
        }
    }

    private void verSeleccionado() {
        Admin admin = seleccionado();

        if (admin == null) {
            return;
        }

        JPanel detalle = new JPanel(new GridLayout(0, 1, 8, 8));
        detalle.setBorder(new EmptyBorder(10, 10, 10, 10));
        detalle.setBackground(Color.WHITE);

        JLabel id = new JLabel("ID: " + admin.getId());
        JLabel correo = new JLabel("Correo: " + admin.getCorreo());
        JLabel estado = new JLabel(
                "Estado: " + (admin.isActivo() ? "Activo" : "Inactivo")
        );
        JLabel fecha = new JLabel(
                "Fecha de creación: " + admin.getFechaCreacion()
        );

        for (JLabel etiqueta : new JLabel[]{id, correo, estado, fecha}) {
            etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 14));
            etiqueta.setForeground(texto);
            detalle.add(etiqueta);
        }

        JOptionPane.showMessageDialog(
                this,
                detalle,
                "Detalle del administrador",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void eliminarSeleccionado() {
        Admin admin = seleccionado();

        if (admin == null) {
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar al administrador?\n\n"
                        + admin.getCorreo()
                        + "\n\nEsta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        botonEliminar.setEnabled(false);

        try {
            if (dao.eliminar(admin.getId())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Administrador eliminado correctamente.",
                        "CuyConecta 3.0",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar el registro. "
                                + "Puede que sea el último administrador activo "
                                + "o que el registro ya no exista.",
                        "Operación no realizada",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            cargarAdministradores();

        } catch (SQLException ex) {
            mostrarError(ex);
        } finally {
            actualizarEstadoAcciones();
        }
    }

    private void actualizarEstadoAcciones() {
        boolean haySeleccion = tabla != null
                && tabla.getSelectedRow() >= 0;

        if (botonVer != null) {
            botonVer.setEnabled(haySeleccion);
            botonEditar.setEnabled(haySeleccion);
            botonEliminar.setEnabled(haySeleccion);
        }
    }

    private String obtenerMensajeError(Exception ex) {
        Throwable causa = ex;

        while (causa.getCause() != null) {
            causa = causa.getCause();
        }

        return causa.getMessage() == null
                ? ex.toString()
                : causa.getMessage();
    }

    private void mostrarError(Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                "No se pudo completar la operación.\n"
                        + obtenerMensajeError(ex),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
