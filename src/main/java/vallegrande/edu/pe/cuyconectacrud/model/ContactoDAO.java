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
}
