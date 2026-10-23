package vallegrande.edu.pe.cuyconectacrud.View;

import vallegrande.edu.pe.cuyconectacrud.model.ConexionDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VentanaReportes extends JFrame {

    private final Color verde = new Color(23, 107, 69);
    private final Color fondo = new Color(245, 248, 246);
    private final Color blanco = Color.WHITE;

    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<String> cmbEstado;

    private JLabel lblTotal;
    private JLabel lblPendientes;
    private JLabel lblEntregados;

    private JButton btnFiltrar;
    private JButton btnActualizar;
    private JButton btnExportar;

    private SwingWorker<ResultadoReporte, Void> cargaActual;

    private static class ResultadoReporte {
        final List<Object[]> pedidos;
        final int total;
        final int pendientes;
        final int entregados;

        ResultadoReporte(
                List<Object[]> pedidos,
                int total,
                int pendientes,
                int entregados
        ) {
            this.pedidos = pedidos;
            this.total = total;
            this.pendientes = pendientes;
            this.entregados = entregados;
        }
    }

    public VentanaReportes() {
        setTitle("CuyConecta - Reportes");
        setSize(1100, 650);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(fondo);

        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setBackground(fondo);
        contenido.setBorder(
                BorderFactory.createEmptyBorder(25, 28, 25, 28)
        );

        // Encabezado y navegación
        JLabel titulo = new JLabel("Reportes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(verde);

        JLabel subtitulo = new JLabel(
                "Consulta y exporta el resumen de compras y pedidos"
        );

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

        JPanel encabezado = new JPanel(new BorderLayout(12, 0));
        encabezado.setOpaque(false);
        encabezado.add(titulos, BorderLayout.CENTER);
        encabezado.add(btnInicio, BorderLayout.EAST);

        // Tarjetas de resumen
        JPanel tarjetas = new JPanel(new GridLayout(1, 3, 15, 0));
        tarjetas.setOpaque(false);

        lblTotal = new JLabel("0");
        lblPendientes = new JLabel("0");
        lblEntregados = new JLabel("0");

        tarjetas.add(crearTarjeta("Total de pedidos", lblTotal));
        tarjetas.add(crearTarjeta("Pendientes", lblPendientes));
        tarjetas.add(crearTarjeta("Entregados", lblEntregados));

        // Filtros y acciones
        cmbEstado = new JComboBox<>(
                new String[]{"Todos", "Pendiente", "Entregado", "Cancelado"}
        );

        btnFiltrar = crearBoton("Filtrar");
        btnActualizar = crearBoton("Actualizar");
        btnExportar = crearBoton("Exportar CSV");

        JPanel filtro = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );
        filtro.setOpaque(false);
        filtro.add(new JLabel("Estado:"));
        filtro.add(cmbEstado);
        filtro.add(btnFiltrar);
        filtro.add(btnActualizar);

        JPanel acciones = new JPanel(new BorderLayout());
        acciones.setOpaque(false);
        acciones.add(filtro, BorderLayout.WEST);
        acciones.add(btnExportar, BorderLayout.EAST);

        String[] columnas = {
                "N.º pedido", "Cliente", "Correo", "Fecha",
                "Total (S/)", "Estado"
        };

        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setRowHeight(32);
        tabla.setBackground(blanco);
        tabla.setForeground(new Color(35, 58, 45));
        tabla.setSelectionBackground(new Color(198, 230, 207));
        tabla.setSelectionForeground(new Color(22, 67, 49));
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        tabla.getTableHeader().setForeground(new Color(22, 67, 49));
        tabla.getTableHeader().setBackground(new Color(230, 239, 233));
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabla);

        JPanel panelTabla = new JPanel(new BorderLayout(0, 8));
        panelTabla.setOpaque(false);

        JLabel tituloTabla = new JLabel("Detalle del reporte");
        tituloTabla.setFont(new Font("SansSerif", Font.BOLD, 17));

        panelTabla.add(tituloTabla, BorderLayout.NORTH);
        panelTabla.add(scroll, BorderLayout.CENTER);

        JPanel resumenYFiltro = new JPanel(new BorderLayout(0, 16));
        resumenYFiltro.setOpaque(false);
        resumenYFiltro.add(tarjetas, BorderLayout.NORTH);
        resumenYFiltro.add(acciones, BorderLayout.SOUTH);

        JPanel superior = new JPanel(new BorderLayout(0, 18));
        superior.setOpaque(false);
        superior.add(encabezado, BorderLayout.NORTH);
        superior.add(resumenYFiltro, BorderLayout.CENTER);

        contenido.add(superior, BorderLayout.NORTH);
        contenido.add(panelTabla, BorderLayout.CENTER);

        principal.add(contenido, BorderLayout.CENTER);
        setContentPane(principal);

        btnFiltrar.addActionListener(e -> cargarReporte());

        btnActualizar.addActionListener(e -> {
            cmbEstado.setSelectedIndex(0);
            cargarReporte();
        });

        btnExportar.addActionListener(e -> exportarCSV());

        cargarReporte();
    }

    private JPanel crearTarjeta(String titulo, JLabel valor) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(blanco);
        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 232, 227)
                        ),
                        BorderFactory.createEmptyBorder(15, 18, 15, 18)
                )
        );

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("SansSerif", Font.PLAIN, 13));
        etiqueta.setForeground(new Color(95, 110, 100));

        valor.setFont(new Font("SansSerif", Font.BOLD, 25));
        valor.setForeground(verde);

        tarjeta.add(etiqueta);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(valor);

        return tarjeta;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(verde);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(
                BorderFactory.createEmptyBorder(10, 13, 10, 13)
        );
        return boton;
    }

    private void cargarReporte() {
        if (cargaActual != null && !cargaActual.isDone()) {
            cargaActual.cancel(true);
        }

        final String estado = String.valueOf(cmbEstado.getSelectedItem());

        btnFiltrar.setEnabled(false);
        btnActualizar.setEnabled(false);
        btnExportar.setEnabled(false);

        cargaActual = new SwingWorker<ResultadoReporte, Void>() {

            @Override
            protected ResultadoReporte doInBackground() throws Exception {
                List<Object[]> pedidos = new ArrayList<>();

                StringBuilder sql = new StringBuilder(
                        "SELECT order_number, customer_name, customer_email, "
                                + "created_at, total_amount, order_status "
                                + "FROM purchase_orders "
                );

                if ("Pendiente".equals(estado)) {
                    sql.append(
                            "WHERE UPPER(order_status) "
                                    + "IN ('PENDING', 'RECEIVED') "
                    );
                } else if ("Entregado".equals(estado)) {
                    sql.append(
                            "WHERE UPPER(order_status) = 'DELIVERED' "
                    );
                } else if ("Cancelado".equals(estado)) {
                    sql.append(
                            "WHERE UPPER(order_status) = 'CANCELLED' "
                    );
                }

                sql.append("ORDER BY created_at DESC");

                int total;
                int pendientes;
                int entregados;

                try (Connection con = ConexionDB.conectar()) {

                    try (PreparedStatement ps =
                                 con.prepareStatement(sql.toString());
                         ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {
                            if (isCancelled()) {
                                return null;
                            }

                            pedidos.add(new Object[]{
                                    rs.getString("order_number"),
                                    rs.getString("customer_name"),
                                    rs.getString("customer_email"),
                                    rs.getTimestamp("created_at"),
                                    rs.getBigDecimal("total_amount"),
                                    rs.getString("order_status")
                            });
                        }
                    }

                    if (isCancelled()) {
                        return null;
                    }

                    String sqlResumen =
                            "SELECT COUNT(*) AS total, "
                                    + "COALESCE(SUM(CASE WHEN UPPER(order_status) "
                                    + "IN ('PENDING', 'RECEIVED') "
                                    + "THEN 1 ELSE 0 END), 0) AS pendientes, "
                                    + "COALESCE(SUM(CASE WHEN UPPER(order_status) "
                                    + "= 'DELIVERED' THEN 1 ELSE 0 END), 0) "
                                    + "AS entregados "
                                    + "FROM purchase_orders";

                    try (PreparedStatement ps =
                                 con.prepareStatement(sqlResumen);
                         ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            total = rs.getInt("total");
                            pendientes = rs.getInt("pendientes");
                            entregados = rs.getInt("entregados");
                        } else {
                            total = 0;
                            pendientes = 0;
                            entregados = 0;
                        }
                    }
                }

                return new ResultadoReporte(
                        pedidos, total, pendientes, entregados
                );
            }

            @Override
            protected void done() {
                if (isCancelled() || cargaActual != this) {
                    return;
                }

                try {
                    ResultadoReporte resultado = get();

                    if (resultado == null) {
                        return;
                    }

                    modelo.setRowCount(0);

                    for (Object[] pedido : resultado.pedidos) {
                        modelo.addRow(pedido);
                    }

                    lblTotal.setText(String.valueOf(resultado.total));
                    lblPendientes.setText(
                            String.valueOf(resultado.pendientes)
                    );
                    lblEntregados.setText(
                            String.valueOf(resultado.entregados)
                    );

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    mostrarError(
                            "Se interrumpió la carga del reporte.", ex
                    );
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause() != null
                            ? ex.getCause() : ex;

                    lblTotal.setText("-");
                    lblPendientes.setText("-");
                    lblEntregados.setText("-");

                    mostrarError("No se pudo cargar el reporte.", causa);
                } finally {
                    if (cargaActual == this) {
                        btnFiltrar.setEnabled(true);
                        btnActualizar.setEnabled(true);
                        btnExportar.setEnabled(true);
                    }
                }
            }
        };

        cargaActual.execute();
    }

    private void exportarCSV() {
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay datos para exportar.",
                    "Reporte",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte CSV");
        selector.setSelectedFile(new File("reporte_pedidos.csv"));

        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();

        try (PrintWriter writer = new PrintWriter(archivo, "UTF-8")) {
            writer.println("sep=;");

            for (int columna = 0;
                 columna < modelo.getColumnCount(); columna++) {
                if (columna > 0) {
                    writer.print(";");
                }

                writer.print(
                        escaparCSV(modelo.getColumnName(columna))
                );
            }
            writer.println();

            for (int fila = 0; fila < modelo.getRowCount(); fila++) {
                for (int columna = 0;
                     columna < modelo.getColumnCount(); columna++) {
                    if (columna > 0) {
                        writer.print(";");
                    }

                    Object valor = modelo.getValueAt(fila, columna);
                    writer.print(
                            escaparCSV(valor == null ? "" : valor.toString())
                    );
                }

                writer.println();
            }

            if (writer.checkError()) {
                throw new IOException(
                        "Ocurrió un error al escribir el archivo."
                );
            }

            JOptionPane.showMessageDialog(
                    this, "Reporte exportado correctamente."
            );

        } catch (Exception ex) {
            mostrarError("No se pudo exportar el reporte.", ex);
        }
    }

    private String escaparCSV(String valor) {
        return "\"" + valor.replace("\"", "\"\"") + "\"";
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
