
package vallegrande.edu.pe.cuyconectacrud.View;

import vallegrande.edu.pe.cuyconectacrud.model.PedidoDAO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class FormularioPedido extends JDialog {

    private final Color verde = new Color(23, 107, 69);
    private final Color verdeOscuro = new Color(17, 76, 49);
    private final Color verdeClaro = new Color(235, 244, 238);
    private final Color verdeBorde = new Color(205, 224, 211);
    private final Color verdeSeleccion = new Color(198, 226, 208);
    private final Color textoOscuro = new Color(43, 61, 49);

    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtCorreo = new JTextField(22);
    private final JTextField txtTelefono = new JTextField(18);
    private final JTextField txtDireccion = new JTextField(25);
    private final JTextField txtDistrito = new JTextField(18);
    private final JTextField txtCiudad = new JTextField(18);
    private final JTextField txtDepartamento = new JTextField(18);
    private final JTextField txtEnvio = new JTextField("0.00", 10);
    private final JTextField txtDescuento = new JTextField("0.00", 10);

    private final JTextArea txtComentario = new JTextArea(3, 25);

    private final JComboBox<String> cmbEntrega =
            new JComboBox<>(new String[]{"DELIVERY", "PICKUP"});

    private final JComboBox<String> cmbPago =
            new JComboBox<>(new String[]{
                    "TRANSFERENCIA", "YAPE", "CONTRA-ENTREGA"
            });

    private final DefaultTableModel modeloProductos =
            new DefaultTableModel(
                    new Object[]{"Producto", "Cantidad", "Precio unitario"}, 0
            ) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return true;
                }
            };

    private final JTable tablaProductos = new JTable(modeloProductos);

    private final JLabel lblSubtotal = new JLabel("S/ 0.00");
    private final JLabel lblTotal = new JLabel("S/ 0.00");

    private boolean guardado = false;

    public FormularioPedido(JFrame propietario) {
        super(propietario, "Registrar nuevo pedido", true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 760);
        setMinimumSize(new Dimension(720, 650));
        setLocationRelativeTo(propietario);

        construirInterfaz();
    }

    public boolean isGuardado() {
        return guardado;
    }

    private void construirInterfaz() {
        JPanel principal = new JPanel(new BorderLayout(0, 0));
        principal.setBackground(verdeClaro);

        // Encabezado verde
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(verde);
        encabezado.setBorder(
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        );

        JLabel titulo = new JLabel("Registrar pedido");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(
                "Completa los datos del cliente y agrega los productos"
        );
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(225, 242, 230));

        JPanel textosEncabezado = new JPanel();
        textosEncabezado.setOpaque(false);
        textosEncabezado.setLayout(
                new BoxLayout(textosEncabezado, BoxLayout.Y_AXIS)
        );
        textosEncabezado.add(titulo);
        textosEncabezado.add(Box.createVerticalStrut(5));
        textosEncabezado.add(subtitulo);

        encabezado.add(textosEncabezado, BorderLayout.CENTER);
        principal.add(encabezado, BorderLayout.NORTH);

        // Contenido desplazable
        JPanel formulario = new JPanel();
        formulario.setBackground(verdeClaro);
        formulario.setBorder(
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        );
        formulario.setLayout(
                new BoxLayout(formulario, BoxLayout.Y_AXIS)
        );

        // Datos del cliente
        JPanel panelDatos = crearPanelSeccion("Datos del cliente");

        JPanel datos = new JPanel(new GridLayout(0, 2, 12, 10));
        datos.setBackground(Color.WHITE);

        agregarCampo(datos, "Nombre del cliente *", txtNombre);
        agregarCampo(datos, "Correo electrónico *", txtCorreo);
        agregarCampo(datos, "Teléfono *", txtTelefono);
        agregarCampo(datos, "Dirección de entrega *", txtDireccion);
        agregarCampo(datos, "Distrito *", txtDistrito);
        agregarCampo(datos, "Ciudad *", txtCiudad);
        agregarCampo(datos, "Departamento *", txtDepartamento);
        agregarCampo(datos, "Método de entrega", cmbEntrega);
        agregarCampo(datos, "Método de pago", cmbPago);
        agregarCampo(datos, "Costo de envío (S/)", txtEnvio);
        agregarCampo(datos, "Descuento (S/)", txtDescuento);

        panelDatos.add(datos, BorderLayout.CENTER);
        formulario.add(panelDatos);
        formulario.add(Box.createVerticalStrut(14));

        // Productos
        JPanel panelProductos = crearPanelSeccion("Productos del pedido");

        configurarTabla();

        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        scrollProductos.setPreferredSize(new Dimension(650, 155));
        scrollProductos.setBorder(
                BorderFactory.createLineBorder(verdeBorde)
        );

        panelProductos.add(scrollProductos, BorderLayout.CENTER);

        JPanel botonesProductos = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 10)
        );
        botonesProductos.setBackground(Color.WHITE);

        JButton btnAgregar = crearBoton("+ Agregar producto", verde);
        JButton btnQuitar = crearBoton("Quitar seleccionado", new Color(174, 75, 60));

        btnAgregar.addActionListener(e -> {
            modeloProductos.addRow(new Object[]{"", "1", "0.00"});
            int ultimaFila = modeloProductos.getRowCount() - 1;
            tablaProductos.setRowSelectionInterval(ultimaFila, ultimaFila);
            tablaProductos.editCellAt(ultimaFila, 0);
            tablaProductos.requestFocusInWindow();
        });

        btnQuitar.addActionListener(e -> quitarProducto());

        botonesProductos.add(btnAgregar);
        botonesProductos.add(btnQuitar);
        panelProductos.add(botonesProductos, BorderLayout.SOUTH);

        formulario.add(panelProductos);
        formulario.add(Box.createVerticalStrut(14));

        // Comentario
        JPanel panelComentario = crearPanelSeccion("Comentario del cliente");

        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        txtComentario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtComentario.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
        txtComentario.setBackground(new Color(252, 254, 252));

        JScrollPane scrollComentario = new JScrollPane(txtComentario);
        scrollComentario.setPreferredSize(new Dimension(650, 80));
        scrollComentario.setBorder(
                BorderFactory.createLineBorder(verdeBorde)
        );

        panelComentario.add(scrollComentario, BorderLayout.CENTER);
        formulario.add(panelComentario);
        formulario.add(Box.createVerticalStrut(14));

        // Resumen de importes
        JPanel panelTotales = crearPanelSeccion("Resumen del pedido");
        JPanel totales = new JPanel(new GridLayout(2, 2, 12, 10));
        totales.setBackground(Color.WHITE);
        totales.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );

        JLabel etiquetaSubtotal = new JLabel("Subtotal:");
        etiquetaSubtotal.setFont(new Font("SansSerif", Font.PLAIN, 14));
        etiquetaSubtotal.setForeground(textoOscuro);

        lblSubtotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblSubtotal.setForeground(verde);

        JLabel etiquetaTotal = new JLabel("TOTAL DEL PEDIDO:");
        etiquetaTotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        etiquetaTotal.setForeground(verdeOscuro);

        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotal.setForeground(verde);

        totales.add(etiquetaSubtotal);
        totales.add(lblSubtotal);
        totales.add(etiquetaTotal);
        totales.add(lblTotal);

        panelTotales.add(totales, BorderLayout.CENTER);
        formulario.add(panelTotales);

        JScrollPane scrollFormulario = new JScrollPane(formulario);
        scrollFormulario.setBorder(null);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);
        principal.add(scrollFormulario, BorderLayout.CENTER);

        // Botones inferiores
        JPanel pie = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 12)
        );
        pie.setBackground(verdeClaro);
        pie.setBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, verdeBorde)
        );

        JButton btnCalcular = crearBoton("Calcular total", new Color(103, 135, 111));
        JButton btnCancelar = crearBoton("Cancelar", new Color(105, 113, 107));
        JButton btnGuardar = crearBoton("Guardar pedido", verde);

        btnCalcular.addActionListener(e -> calcularTotales());
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarPedido());

        pie.add(btnCalcular);
        pie.add(btnCancelar);
        pie.add(btnGuardar);

        principal.add(pie, BorderLayout.SOUTH);
        setContentPane(principal);

        // Fila inicial
        modeloProductos.addRow(new Object[]{"", "1", "0.00"});

        // Recalcular al editar productos, envío o descuento.
        modeloProductos.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE
                    || e.getType() == javax.swing.event.TableModelEvent.INSERT
                    || e.getType() == javax.swing.event.TableModelEvent.DELETE) {
                actualizarTotalesSilenciosamente();
            }
        });

        DocumentListener listenerImportes = crearListenerImportes();

        txtEnvio.getDocument().addDocumentListener(listenerImportes);
        txtDescuento.getDocument().addDocumentListener(listenerImportes);

        // Guardar al presionar Enter desde un campo de texto,
        // salvo en el área de comentario.
        txtNombre.addActionListener(e -> txtCorreo.requestFocusInWindow());
        txtCorreo.addActionListener(e -> txtTelefono.requestFocusInWindow());
        txtTelefono.addActionListener(e -> txtDireccion.requestFocusInWindow());

        actualizarTotalesSilenciosamente();
    }

    private JPanel crearPanelSeccion(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(verdeBorde),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 16));
        etiqueta.setForeground(verde);
        etiqueta.setBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, verdeBorde)
        );

        panel.add(etiqueta, BorderLayout.NORTH);
        return panel;
    }

    private void configurarTabla() {
        tablaProductos.setRowHeight(30);
        tablaProductos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaProductos.setForeground(textoOscuro);
        tablaProductos.setBackground(Color.WHITE);
        tablaProductos.setSelectionBackground(verdeSeleccion);
        tablaProductos.setSelectionForeground(verdeOscuro);
        tablaProductos.setGridColor(verdeBorde);
        tablaProductos.setShowVerticalLines(false);
        tablaProductos.setFillsViewportHeight(true);
        tablaProductos.setAutoCreateRowSorter(false);

        tablaProductos.getTableHeader().setBackground(verde);
        tablaProductos.getTableHeader().setForeground(Color.WHITE);
        tablaProductos.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        tablaProductos.getTableHeader().setReorderingAllowed(false);
    }

    private JButton crearBoton(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(fondo.darker()),
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)
                )
        );
        return boton;
    }

    private void agregarCampo(
            JPanel panel, String etiqueta, JComponent campo) {

        JPanel grupo = new JPanel(new BorderLayout(4, 5));
        grupo.setBackground(Color.WHITE);

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(textoOscuro);

        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        if (campo instanceof JTextField) {
            JTextField campoTexto = (JTextField) campo;
            campoTexto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(verdeBorde),
                    BorderFactory.createEmptyBorder(6, 7, 6, 7)
            ));
            campoTexto.setBackground(new Color(252, 254, 252));
        } else if (campo instanceof JComboBox) {
            campo.setBackground(Color.WHITE);
        }

        grupo.add(label, BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);
        panel.add(grupo);
    }

    private void quitarProducto() {
        int filaVista = tablaProductos.getSelectedRow();

        if (filaVista < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona el producto que deseas quitar.",
                    "Producto no seleccionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (tablaProductos.isEditing()
                && !tablaProductos.getCellEditor().stopCellEditing()) {
            return;
        }

        int filaModelo = tablaProductos.convertRowIndexToModel(filaVista);
        modeloProductos.removeRow(filaModelo);

        actualizarTotalesSilenciosamente();
    }

    private DocumentListener crearListenerImportes() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarTotalesSilenciosamente();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarTotalesSilenciosamente();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarTotalesSilenciosamente();
            }
        };
    }

    private BigDecimal dinero(String texto, String campo) {
        try {
            BigDecimal valor = new BigDecimal(
                    texto.trim().replace(",", ".")
            );

            if (valor.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        campo + " no puede ser negativo."
                );
            }

            return valor.setScale(2, RoundingMode.HALF_UP);

        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "Ingresa un valor numérico válido en " + campo + "."
            );
        }
    }

    private List<Object[]> leerProductos() {
        List<Object[]> productos = new ArrayList<>();

        if (tablaProductos.isEditing()
                && !tablaProductos.getCellEditor().stopCellEditing()) {
            throw new IllegalArgumentException(
                    "Termina de editar la celda del producto."
            );
        }

        for (int i = 0; i < modeloProductos.getRowCount(); i++) {
            String nombre = String.valueOf(
                    modeloProductos.getValueAt(i, 0)
            ).trim();

            if (nombre.isEmpty()) {
                throw new IllegalArgumentException(
                        "Completa el nombre del producto en la fila "
                                + (i + 1) + "."
                );
            }

            BigDecimal cantidad;
            BigDecimal precio;

            try {
                cantidad = new BigDecimal(
                        String.valueOf(modeloProductos.getValueAt(i, 1))
                                .trim().replace(",", ".")
                );

                precio = dinero(
                        String.valueOf(modeloProductos.getValueAt(i, 2)),
                        "precio unitario"
                );

            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "Revisa la cantidad del producto en la fila "
                                + (i + 1) + "."
                );
            }

            if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser mayor que cero "
                                + "(fila " + (i + 1) + ")."
                );
            }

            if (cantidad.stripTrailingZeros().scale() > 0) {
                throw new IllegalArgumentException(
                        "La cantidad debe ser un número entero "
                                + "(fila " + (i + 1) + ")."
                );
            }

            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "El precio no puede ser negativo."
                );
            }

            productos.add(new Object[]{nombre, cantidad, precio});
        }

        if (productos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Agrega al menos un producto."
            );
        }

        return productos;
    }

    private BigDecimal calcularSubtotal(List<Object[]> productos) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Object[] producto : productos) {
            BigDecimal cantidad = (BigDecimal) producto[1];
            BigDecimal precio = (BigDecimal) producto[2];

            subtotal = subtotal.add(cantidad.multiply(precio));
        }

        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal[] obtenerTotales(List<Object[]> productos) {
        BigDecimal subtotal = calcularSubtotal(productos);
        BigDecimal envio = dinero(
                txtEnvio.getText(), "costo de envío"
        );
        BigDecimal descuento = dinero(
                txtDescuento.getText(), "descuento"
        );

        if (descuento.compareTo(subtotal.add(envio)) > 0) {
            throw new IllegalArgumentException(
                    "El descuento no puede superar el subtotal más el envío."
            );
        }

        BigDecimal total = subtotal.add(envio)
                .subtract(descuento)
                .setScale(2, RoundingMode.HALF_UP);

        return new BigDecimal[]{subtotal, envio, descuento, total};
    }

    private void calcularTotales() {
        try {
            List<Object[]> productos = leerProductos();
            BigDecimal[] totales = obtenerTotales(productos);

            mostrarTotales(totales);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Revisa los datos",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void actualizarTotalesSilenciosamente() {
        try {
            // No interrumpir al usuario mientras está editando una celda.
            if (tablaProductos.isEditing()) {
                return;
            }

            List<Object[]> productos = leerProductos();
            BigDecimal[] totales = obtenerTotales(productos);

            mostrarTotales(totales);

        } catch (IllegalArgumentException ex) {
            lblSubtotal.setText("S/ —");
            lblTotal.setText("S/ —");
        }
    }

    private void mostrarTotales(BigDecimal[] totales) {
        lblSubtotal.setText("S/ " + totales[0].toPlainString());
        lblTotal.setText("S/ " + totales[3].toPlainString());
    }

    private void guardarPedido() {
        try {
            List<Object[]> productos = leerProductos();

            String nombre = txtNombre.getText().trim();
            String correo = txtCorreo.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String distrito = txtDistrito.getText().trim();
            String ciudad = txtCiudad.getText().trim();
            String departamento = txtDepartamento.getText().trim();

            if (nombre.isEmpty() || correo.isEmpty()
                    || telefono.isEmpty() || direccion.isEmpty()
                    || distrito.isEmpty() || ciudad.isEmpty()
                    || departamento.isEmpty()) {
                throw new IllegalArgumentException(
                        "Completa todos los campos obligatorios marcados con *."
                );
            }

            if (!correo.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new IllegalArgumentException(
                        "Ingresa un correo electrónico válido."
                );
            }

            if (telefono.length() < 6) {
                throw new IllegalArgumentException(
                        "Ingresa un número de teléfono válido."
                );
            }

            BigDecimal[] totales = obtenerTotales(productos);
            mostrarTotales(totales);

            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas guardar el pedido por S/ "
                            + totales[3].toPlainString() + "?",
                    "Confirmar pedido",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }

            PedidoDAO pedidoDAO = new PedidoDAO();

            pedidoDAO.crearPedido(
                    nombre,
                    correo,
                    telefono,
                    direccion,
                    distrito,
                    ciudad,
                    departamento,
                    (String) cmbEntrega.getSelectedItem(),
                    txtComentario.getText().trim(),
                    (String) cmbPago.getSelectedItem(),
                    totales[0],
                    totales[1],
                    totales[2],
                    totales[3],
                    productos
            );

            guardado = true;

            JOptionPane.showMessageDialog(
                    this,
                    "El pedido se guardó correctamente.",
                    "Pedido registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Revisa los datos",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar el pedido.\n"
                            + (ex.getMessage() == null
                            ? "Comprueba la conexión con la base de datos."
                            : ex.getMessage()),
                    "Error al guardar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
