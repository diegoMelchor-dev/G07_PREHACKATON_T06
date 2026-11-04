package vallegrande.edu.pe.cuyconectacrud.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactoDAO {

    public List<Contacto> listar() throws SQLException {
        String sql = "SELECT id, nombre, correo, telefono,\n" +
                     "       motivo, mensaje, fecha\n" +
                     "FROM contactos\n" +
                     "ORDER BY id DESC\n";

        List<Contacto> contactos = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                contactos.add(new Contacto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("telefono"),
                        rs.getString("motivo"),
                        rs.getString("mensaje"),
                        rs.getTimestamp("fecha")
                ));
            }
        }

        return contactos;
    }

    public List<Contacto> buscar(String criterio)
            throws SQLException {

        String sql = "SELECT id, nombre, correo, telefono,\n" +
                     "       motivo, mensaje, fecha\n" +
                     "FROM contactos\n" +
                     "WHERE nombre LIKE ?\n" +
                     "   OR correo LIKE ?\n" +
                     "   OR motivo LIKE ?\n" +
                     "ORDER BY id DESC\n";

        List<Contacto> contactos = new ArrayList<>();
        String filtro = "%" + criterio.trim() + "%";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contactos.add(new Contacto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getString("telefono"),
                            rs.getString("motivo"),
                            rs.getString("mensaje"),
                            rs.getTimestamp("fecha")
                    ));
                }
            }
        }

        return contactos;
    }

    /**
     * Búsqueda combinada para la ventana de contactos:
     * texto libre + motivo (categoría) + rango de fecha.
     *
     * @param criterio texto a buscar en id, nombre, teléfono, correo,
     *                 motivo o mensaje (vacío = sin filtro de texto)
     * @param motivo   motivo exacto, o null/vacío para todos
     * @param fecha    rango de fecha, o null/TODAS para todas
     */
    public List<Contacto> filtrar(String criterio, String motivo,
                                  FiltroFecha fecha) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT id, nombre, correo, telefono, motivo, mensaje, fecha "
              + "FROM contactos "
              + "WHERE (CAST(id AS CHAR) LIKE ? OR nombre LIKE ? "
              + "OR telefono LIKE ? OR correo LIKE ? "
              + "OR motivo LIKE ? OR mensaje LIKE ?)");

        boolean filtraMotivo = motivo != null && !motivo.isBlank();
        if (filtraMotivo) {
            sql.append(" AND motivo = ?");
        }
        if (fecha != null && fecha.tieneCondicion()) {
            sql.append(" AND ").append(fecha.getCondicionSql());
        }
        sql.append(" ORDER BY fecha DESC, id DESC");

        String filtro = "%" + (criterio == null ? "" : criterio.trim()) + "%";
        List<Contacto> contactos = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 1; i <= 6; i++) {
                ps.setString(i, filtro);
            }
            if (filtraMotivo) {
                ps.setString(7, motivo);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contactos.add(new Contacto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getString("telefono"),
                            rs.getString("motivo"),
                            rs.getString("mensaje"),
                            rs.getTimestamp("fecha")
                    ));
                }
            }
        }

        return contactos;
    }

    /** Motivos distintos registrados, para llenar el ComboBox de filtro. */
    public List<String> listarMotivos() throws SQLException {
        String sql = "SELECT DISTINCT motivo FROM contactos "
                   + "WHERE motivo IS NOT NULL AND motivo <> '' "
                   + "ORDER BY motivo";

        List<String> motivos = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                motivos.add(rs.getString("motivo"));
            }
        }

        return motivos;
    }
}
