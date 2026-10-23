
package vallegrande.edu.pe.cuyconectacrud.View;

import vallegrande.edu.pe.cuyconectacrud.model.PedidoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaPedidos extends JFrame {

    private final Color verde = new Color(23, 107, 69);
    private final Color fondo = new Color(245, 248, 246);
    private final Color blanco = Color.WHITE;

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private JTable tablaPedidos;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;

    private JLabel lblTotalPedidos;
    private JLabel lblPendientes;
    private JLabel lblEntregados;

    private JButton btnBuscar;
    private JButton btnNuevo;
    private JButton btnDetalle;

    public VentanaPedidos() {
        setTitle("CuyConecta - Gestión de pedidos");
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(fondo);

        // Encabezado
        JPanel encabezado = new JPanel(new BorderLayout(15, 10));
        encabezado.setBackground(verde);
        encabezado.setBorder(
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
        );

        JPanel encabezadoIzquierdo = new JPanel();
        encabezadoIzquierdo.setOpaque(false);
        encabezadoIzquierdo.setLayout(
                new BoxLayout(encabezadoIzquierdo, BoxLayout.Y_AXIS)
        );

        JButton btnVolver = crearBoton(
                "← Volver al inicio",
                new Color(17, 78, 51)
        );
        btnVolver.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVolver.addActionListener(e ->
                VentanaInicio.navegarDesdeModulo("Inicio", this)
        );

        JLabel titulo = new JLabel("Gestión de pedidos");
        titulo.setForeground(blanco);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel subtitulo = new JLabel(
                "Consulta y administra las compras de CuyConecta"
        );
        subtitulo.setForeground(new Color(225, 242, 232));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        encabezadoIzquierdo.add(btnVolver);
        encabezadoIzquierdo.add(Box.createVerticalStrut(12));
        encabezadoIzquierdo.add(titulo);
        encabezadoIzquierdo.add(Box.createVerticalStrut(6));
        encabezadoIzquierdo.add(subtitulo);

        encabezado.add(encabezadoIzquierdo, BorderLayout.WEST);

        // Contenido principal
        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(fondo);
        contenido.setBorder(
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        );

        // Tarjetas de resumen
        JPanel tarjetas = new JPanel(new GridLayout(1, 3, 15, 0));
        tarjetas.setOpaque(false);

        lblTotalPedidos = new JLabel("0");
        lblPendientes = new JLabel("0");
        lblEntregados = new JLabel("0");

        tarjetas.add(crearTarjeta("Total de pedidos", lblTotalPedidos));
        tarjetas.add(crearTarjeta("Pendientes", lblPendientes));
        tarjetas.add(crearTarjeta("Entregados", lblEntregados));

        // Búsqueda y botones
        JPanel acciones = new JPanel(new BorderLayout(12, 10));
        acciones.setOpaque(false);

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBuscar.setPreferredSize(new Dimension(250, 40));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 225, 218)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtBuscar.setToolTipText(
                "Buscar por número de pedido, cliente o correo"
        );

        btnBuscar = crearBoton("Buscar", verde);
        btnNuevo = crearBoton("+ Nuevo pedido", verde);
        btnDetalle = crearBoton(
                "Ver detalle", new Color(75, 95, 82)
        );

        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        busqueda.add(txtBuscar, BorderLayout.CENTER);
        busqueda.add(btnBuscar, BorderLayout.EAST);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        botones.setOpaque(false);
        botones.add(btnNuevo);
        botones.add(btnDetalle);

        acciones.add(busqueda, BorderLayout.CENTER);
        acciones.add(botones, BorderLayout.SOUTH);

        // Tabla de pedidos
        String[] columnas = {
                "N.º pedido", "Cliente", "Correo",
                "Fecha", "Total (S/)", "Estado"
        };

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaPedidos = new JTable(modelo);
        configurarTabla();

        JScrollPane scroll = new JScrollPane(tablaPedidos);
        scroll.setBorder(BorderFactory.createLineBorder(
                new Color(225, 232, 227)
        ));
        scroll.getViewport().setBackground(blanco);

        JPanel panelTabla = new JPanel(new BorderLayout(0, 8));
        panelTabla.setOpaque(false);

        JLabel etiquetaTabla = new JLabel("Pedidos registrados");
        etiquetaTabla.setFont(
                new Font("SansSerif", Font.BOLD, 17)
        );

        panelTabla.add(etiquetaTabla, BorderLayout.NORTH);
        panelTabla.add(scroll, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 18));
        centro.setOpaque(false);
        centro.add(tarjetas, BorderLayout.NORTH);
        centro.add(acciones, BorderLayout.CENTER);

        contenido.add(centro, BorderLayout.NORTH);
        contenido.add(panelTabla, BorderLayout.CENTER);

        principal.add(encabezado, BorderLayout.NORTH);
        principal.add(contenido, BorderLayout.CENTER);

        setContentPane(principal);

        // Acciones
        btnNuevo.addActionListener(e -> {
            FormularioPedido formulario = new FormularioPedido(this);
            formulario.setLocationRelativeTo(this);
            formulario.setVisible(true);

            if (formulario.isGuardado()) {
                txtBuscar.setText("");
                cargarPedidos();
            }
        });
        btnDetalle.addActionListener(e -> mostrarDetalle());

        // Cargar datos desde MySQL sin bloquear la interfaz.
        cargarPedidos();
    }

    private void configurarTabla() {
        tablaPedidos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaPedidos.setRowHeight(32);
        tablaPedidos.setBackground(Color.WHITE);
        tablaPedidos.setForeground(new Color(35, 58, 45));
        tablaPedidos.setSelectionBackground(new Color(198, 230, 207));
        tablaPedidos.setSelectionForeground(new Color(22, 67, 49));
        tablaPedidos.setGridColor(new Color(225, 232, 227));
        tablaPedidos.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tablaPedidos.setAutoCreateRowSorter(true);
        tablaPedidos.setFillsViewportHeight(true);

        tablaPedidos.getTableHeader().setForeground(
                new Color(22, 67, 49)
        );
        tablaPedidos.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        tablaPedidos.getTableHeader().setBackground(
                new Color(230, 239, 233)
        );
        tablaPedidos.getTableHeader().setReorderingAllowed(false);
    }

    private JPanel crearTarjeta(String titulo, JLabel lblValor) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 232, 227)
                ),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(new Color(95, 110, 100));
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        lblValor.setForeground(verde);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 25));

        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblValor);

        return tarjeta;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);

        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(
                BorderFactory.createEmptyBorder(11, 16, 11, 16)
        );

        return boton;
    }

    // Carga pedidos y actualiza las tarjetas de resumen.
    private void cargarPedidos() {
        lblTotalPedidos.setText("...");
        lblPendientes.setText("...");
        lblEntregados.setText("...");

        btnBuscar.setEnabled(false);
        btnNuevo.setEnabled(false);
        btnDetalle.setEnabled(false);

        new SwingWorker<Object[], Void>() {

            @Override
            protected Object[] doInBackground() throws Exception {
                DefaultTableModel tabla = pedidoDAO.listar();
                int[] resumen = pedidoDAO.obtenerResumen();

                return new Object[]{tabla, resumen};
            }

            @Override
            protected void done() {
                btnBuscar.setEnabled(true);
                btnNuevo.setEnabled(true);
                btnDetalle.setEnabled(true);

                try {
                    Object[] resultado = get();

                    modelo = (DefaultTableModel) resultado[0];
                    int[] resumen = (int[]) resultado[1];

                    tablaPedidos.setModel(modelo);
                    configurarTabla();

                    lblTotalPedidos.setText(
                            String.valueOf(resumen[0])
                    );
                    lblPendientes.setText(
                            String.valueOf(resumen[1])
                    );
                    lblEntregados.setText(
                            String.valueOf(resumen[2])
                    );

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            VentanaPedidos.this,
                            "No se pudieron cargar los pedidos.\n"
                                    + obtenerMensajeError(ex),
                            "Error de base de datos",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    // Busca por número de pedido, cliente o correo.
    private void buscarPedidos() {
        String criterio = txtBuscar.getText().trim();

        if (criterio.isEmpty()) {
            cargarPedidos();
            return;
        }

        btnBuscar.setEnabled(false);

        new SwingWorker<DefaultTableModel, Void>() {

            @Override
            protected DefaultTableModel doInBackground() throws Exception {
                return pedidoDAO.buscar(criterio);
            }

            @Override
            protected void done() {
                btnBuscar.setEnabled(true);

                try {
                    modelo = get();
                    tablaPedidos.setModel(modelo);
                    configurarTabla();

                    if (modelo.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(
                                VentanaPedidos.this,
                                "No se encontraron pedidos.",
                                "Búsqueda",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            VentanaPedidos.this,
                            "No se pudo completar la búsqueda.\n"
                                    + obtenerMensajeError(ex),
                            "Error de base de datos",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    // Muestra los productos del pedido seleccionado.
    private void mostrarDetalle() {
        int filaVista = tablaPedidos.getSelectedRow();

        if (filaVista == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un pedido de la tabla.",
                    "Ver detalle",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int filaModelo = tablaPedidos.convertRowIndexToModel(filaVista);
        String numeroPedido = String.valueOf(
                tablaPedidos.getModel().getValueAt(filaModelo, 0)
        );

        btnDetalle.setEnabled(false);

        new SwingWorker<Object[], Void>() {

            @Override
            protected Object[] doInBackground() throws Exception {
                long pedidoId = pedidoDAO.obtenerIdPorNumero(numeroPedido);

                if (pedidoId == -1) {
                    return null;
                }

                DefaultTableModel modeloDetalle =
                        pedidoDAO.listarProductos(pedidoId);

                return new Object[]{pedidoId, modeloDetalle};
            }

            @Override
            protected void done() {
                btnDetalle.setEnabled(true);

                try {
                    Object[] resultado = get();

                    if (resultado == null) {
                        JOptionPane.showMessageDialog(
                                VentanaPedidos.this,
                                "No se encontró el identificador del pedido.",
                                "Ver detalle",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    DefaultTableModel modeloDetalle =
                            (DefaultTableModel) resultado[1];

                    JTable tablaDetalle = new JTable(modeloDetalle);
                    tablaDetalle.setRowHeight(28);
                    tablaDetalle.setFillsViewportHeight(true);

                    JScrollPane scrollDetalle =
                            new JScrollPane(tablaDetalle);
                    scrollDetalle.setPreferredSize(
                            new Dimension(550, 220)
                    );

                    JOptionPane.showMessageDialog(
                            VentanaPedidos.this,
                            scrollDetalle,
                            "Detalle del pedido " + numeroPedido,
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            VentanaPedidos.this,
                            "No se pudo obtener el detalle del pedido.\n"
                                    + obtenerMensajeError(ex),
                            "Error de base de datos",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
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
}
