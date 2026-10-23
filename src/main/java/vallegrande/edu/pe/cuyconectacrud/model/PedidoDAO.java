
package vallegrande.edu.pe.cuyconectacrud.model;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PedidoDAO {

    public DefaultTableModel crearModeloPedidos() {
        return new DefaultTableModel(
                new Object[]{
                        "N.º pedido", "Cliente", "Correo",
                        "Fecha", "Total (S/)", "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    public DefaultTableModel crearModeloProductos() {
        return new DefaultTableModel(
                new Object[]{
                        "Producto", "Cantidad",
                        "Precio unitario", "Subtotal"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    public DefaultTableModel listar() throws SQLException {
        String sql =
                "SELECT order_number, customer_name, customer_email, "
                        + "created_at, total_amount, order_status "
                        + "FROM purchase_orders ORDER BY created_at DESC";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return llenarModeloPedidos(rs);
        }
    }

    public DefaultTableModel buscar(String criterio) throws SQLException {
        String sql =
                "SELECT order_number, customer_name, customer_email, "
                        + "created_at, total_amount, order_status "
                        + "FROM purchase_orders "
                        + "WHERE order_number LIKE ? "
                        + "OR customer_name LIKE ? "
                        + "OR customer_email LIKE ? "
                        + "ORDER BY created_at DESC";

        String busqueda = "%" + (criterio == null ? "" : criterio.trim()) + "%";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);

            try (ResultSet rs = ps.executeQuery()) {
                return llenarModeloPedidos(rs);
            }
        }
    }

    private DefaultTableModel llenarModeloPedidos(ResultSet rs)
            throws SQLException {

        DefaultTableModel modelo = crearModeloPedidos();

        while (rs.next()) {
            Object fecha = rs.getObject("created_at");
            BigDecimal total = rs.getBigDecimal("total_amount");

            modelo.addRow(new Object[]{
                    rs.getString("order_number"),
                    rs.getString("customer_name"),
                    rs.getString("customer_email"),
                    fecha == null ? "" : fecha.toString(),
                    total == null ? BigDecimal.ZERO : total,
                    rs.getString("order_status")
            });
        }

        return modelo;
    }

    public int[] obtenerResumen() throws SQLException {
        String sql =
                "SELECT COUNT(*) AS total, "
                        + "COALESCE(SUM(CASE WHEN order_status = 'RECEIVED' "
                        + "THEN 1 ELSE 0 END), 0) AS pendientes, "
                        + "COALESCE(SUM(CASE WHEN order_status = 'DELIVERED' "
                        + "THEN 1 ELSE 0 END), 0) AS entregados "
                        + "FROM purchase_orders";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new int[]{
                        rs.getInt("total"),
                        rs.getInt("pendientes"),
                        rs.getInt("entregados")
                };
            }
        }

        return new int[]{0, 0, 0};
    }

    public int contarPedidos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM purchase_orders";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int contarPorEstado(String estado) throws SQLException {
        String sql =
                "SELECT COUNT(*) FROM purchase_orders WHERE order_status = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estado);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public DefaultTableModel listarProductos(long orderId)
            throws SQLException {

        String sql =
                "SELECT product_name, quantity, unit_price, line_total "
                        + "FROM purchase_order_items WHERE order_id = ?";

        DefaultTableModel modelo = crearModeloProductos();

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setLong(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelo.addRow(new Object[]{
                            rs.getString("product_name"),
                            rs.getBigDecimal("quantity"),
                            rs.getBigDecimal("unit_price"),
                            rs.getBigDecimal("line_total")
                    });
                }
            }
        }

        return modelo;
    }

    public Long obtenerIdPorNumero(String numeroPedido)
            throws SQLException {

        String sql =
                "SELECT order_id FROM purchase_orders WHERE order_number = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, numeroPedido);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("order_id");
                }
            }
        }

        return null;
    }

    public void crearPedido(
            String nombre,
            String correo,
            String telefono,
            String direccion,
            String distrito,
            String ciudad,
            String departamento,
            String metodoEntrega,
            String comentario,
            String metodoPago,
            BigDecimal subtotal,
            BigDecimal envio,
            BigDecimal descuento,
            BigDecimal total,
            List<Object[]> productos
    ) throws SQLException {

        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException(
                    "El pedido debe contener al menos un producto."
            );
        }

        String entrega = normalizar(metodoEntrega);
        String pago = normalizar(metodoPago);

        if (!entrega.equals("delivery") && !entrega.equals("pickup")) {
            throw new IllegalArgumentException(
                    "El método de entrega debe ser DELIVERY o PICKUP."
            );
        }

        if (!pago.equals("transferencia")
                && !pago.equals("yape")
                && !pago.equals("contra-entrega")) {
            throw new IllegalArgumentException(
                    "El método de pago debe ser TRANSFERENCIA, YAPE "
                            + "o CONTRA-ENTREGA."
            );
        }

        validarImporte(subtotal, "subtotal");
        validarImporte(envio, "costo de envío");
        validarImporte(descuento, "descuento");
        validarImporte(total, "total");

        BigDecimal totalEsperado = subtotal.add(envio).subtract(descuento)
                .setScale(2, RoundingMode.HALF_UP);

        if (totalEsperado.compareTo(total.setScale(2, RoundingMode.HALF_UP)) != 0) {
            throw new IllegalArgumentException(
                    "El total no coincide con subtotal + envío - descuento."
            );
        }

        String numeroPedido = "PED-" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        );

        String sqlPedido =
                "INSERT INTO purchase_orders ("
                        + "order_number, customer_name, customer_email, "
                        + "customer_phone, delivery_address, delivery_district, "
                        + "delivery_city, delivery_department, delivery_method, "
                        + "customer_comment, payment_method, payment_status, "
                        + "order_status, subtotal, shipping_cost, discount_amount, "
                        + "total_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // line_total es GENERATED ALWAYS, por eso no se incluye.
        String sqlProducto =
                "INSERT INTO purchase_order_items "
                        + "(order_id, product_name, quantity, unit_price) "
                        + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.conectar()) {
            boolean autocommitOriginal = conexion.getAutoCommit();

            try {
                conexion.setAutoCommit(false);

                long orderId;

                try (PreparedStatement ps = conexion.prepareStatement(
                        sqlPedido, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, numeroPedido);
                    ps.setString(2, nombre.trim());
                    ps.setString(3, correo.trim());
                    ps.setString(4, telefono.trim());
                    ps.setString(5, direccion.trim());
                    ps.setString(6, distrito.trim());
                    ps.setString(7, ciudad.trim());
                    ps.setString(8, departamento.trim());
                    ps.setString(9, entrega);
                    ps.setString(10, comentario);
                    ps.setString(11, pago);
                    ps.setString(12, "PENDING");
                    ps.setString(13, "RECEIVED");
                    ps.setBigDecimal(14, subtotal.setScale(2, RoundingMode.HALF_UP));
                    ps.setBigDecimal(15, envio.setScale(2, RoundingMode.HALF_UP));
                    ps.setBigDecimal(16, descuento.setScale(2, RoundingMode.HALF_UP));
                    ps.setBigDecimal(17, total.setScale(2, RoundingMode.HALF_UP));

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("No se pudo insertar el pedido.");
                    }

                    try (ResultSet claves = ps.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se obtuvo el ID del pedido registrado."
                            );
                        }
                        orderId = claves.getLong(1);
                    }
                }

                try (PreparedStatement ps = conexion.prepareStatement(sqlProducto)) {
                    for (Object[] producto : productos) {
                        if (producto == null || producto.length < 3) {
                            throw new IllegalArgumentException(
                                    "Los datos de un producto son inválidos."
                            );
                        }

                        String nombreProducto =
                                String.valueOf(producto[0]).trim();
                        BigDecimal cantidad =
                                convertirDecimal(producto[1], "cantidad");
                        BigDecimal precio =
                                convertirDecimal(producto[2], "precio");

                        if (nombreProducto.isEmpty()) {
                            throw new IllegalArgumentException(
                                    "El nombre del producto está vacío."
                            );
                        }
                        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new IllegalArgumentException(
                                    "La cantidad debe ser mayor que cero."
                            );
                        }
                        if (cantidad.stripTrailingZeros().scale() > 0) {
                            throw new IllegalArgumentException(
                                    "La cantidad debe ser un número entero."
                            );
                        }
                        if (precio.compareTo(BigDecimal.ZERO) < 0) {
                            throw new IllegalArgumentException(
                                    "El precio no puede ser negativo."
                            );
                        }

                        ps.setLong(1, orderId);
                        ps.setString(2, nombreProducto);
                        ps.setBigDecimal(3, cantidad);
                        ps.setBigDecimal(4, precio.setScale(2, RoundingMode.HALF_UP));
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                conexion.commit();

            } catch (SQLException | RuntimeException ex) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    ex.addSuppressed(errorRollback);
                }
                throw ex;

            } finally {
                try {
                    conexion.setAutoCommit(autocommitOriginal);
                } catch (SQLException ex) {
                    // Se conserva cualquier error original del guardado.
                }
            }
        }
    }

    private String normalizar(String valor) {
        return valor == null
                ? ""
                : valor.trim().toLowerCase(Locale.ROOT);
    }

    private void validarImporte(BigDecimal valor, String campo) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El " + campo + " no puede ser nulo ni negativo."
            );
        }
    }

    private BigDecimal convertirDecimal(Object valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    "El valor de " + campo + " está vacío."
            );
        }

        try {
            return new BigDecimal(
                    String.valueOf(valor).trim().replace(",", ".")
            );
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "El valor de " + campo + " no es válido."
            );
        }
    }
}
