
package vallegrande.edu.pe.cuyconectacrud.model;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public List<Admin> listar() throws SQLException {
        List<Admin> lista = new ArrayList<>();

        String sql = "SELECT id, correo, activo, fecha_creacion "
                + "FROM admin_users ORDER BY id";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Admin(
                        rs.getInt("id"),
                        rs.getString("correo"),
                        rs.getBoolean("activo"),
                        rs.getString("fecha_creacion")
                ));
            }
        }
        return lista;
    }

    public Admin buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, correo, activo, fecha_creacion "
                + "FROM admin_users WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("id"),
                            rs.getString("correo"),
                            rs.getBoolean("activo"),
                            rs.getString("fecha_creacion")
                    );
                }
            }
        }
        return null;
    }

    public boolean crear(String correo, String password)
            throws SQLException, IOException, InterruptedException {

        String hash = generarHashWerkzeug(password);

        String sql = "INSERT INTO admin_users "
                + "(correo, password_hash, activo) "
                + "VALUES (?, ?, TRUE)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, hash);
            return ps.executeUpdate() == 1;
        }
    }

    private String generarHashWerkzeug(String password)
            throws IOException, InterruptedException {

        String script =
                "import sys; "
                        + "from werkzeug.security import generate_password_hash; "
                        + "print(generate_password_hash(sys.stdin.readline().rstrip('\\n')))";

        Process proceso = new ProcessBuilder(
                "py", "-c", script
        ).redirectErrorStream(true).start();

        try (OutputStream entrada = proceso.getOutputStream()) {
            entrada.write((password + "\n").getBytes(StandardCharsets.UTF_8));
        }

        java.io.InputStream salida = proceso.getInputStream();
        java.io.ByteArrayOutputStream buffer =
                new java.io.ByteArrayOutputStream();

        byte[] datos = new byte[1024];
        int cantidad;

        while ((cantidad = salida.read(datos)) != -1) {
            buffer.write(datos, 0, cantidad);
        }

        String resultado = new String(
                buffer.toByteArray(),
                StandardCharsets.UTF_8
        ).trim();

        int codigo = proceso.waitFor();

        if (codigo != 0
                || !(resultado.startsWith("pbkdf2:")
                || resultado.startsWith("scrypt:"))) {
            throw new IOException(
                    "No se pudo generar el hash. Verifica Python y Werkzeug."
            );
        }

        return resultado;
    }

    public boolean actualizar(int id, String nuevoCorreo, boolean activo)
            throws SQLException {

        String sql = "UPDATE admin_users SET correo = ?, activo = ? "
                + "WHERE id = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoCorreo);
            ps.setBoolean(2, activo);
            ps.setInt(3, id);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM admin_users WHERE id = ? "
                + "AND NOT (activo = TRUE AND "
                + "(SELECT cantidad FROM "
                + "(SELECT COUNT(*) AS cantidad FROM admin_users "
                + "WHERE activo = TRUE) AS conteo) <= 1)";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }
}
