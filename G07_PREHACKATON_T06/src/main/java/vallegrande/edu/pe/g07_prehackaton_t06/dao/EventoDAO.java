package vallegrande.edu.pe.g07_prehackaton_t06.dao;

import vallegrande.edu.pe.g07_prehackaton_t06.database.Conexion;
import vallegrande.edu.pe.g07_prehackaton_t06.model.Evento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public List<Evento> listar() throws SQLException {
        String sql = """
                SELECT id_evento, nombre, descripcion, fecha, lugar, modalidad, estado
                FROM eventos
                ORDER BY fecha DESC, id_evento DESC
                """;
        List<Evento> eventos = new ArrayList<>();
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                eventos.add(mapear(rs));
            }
        }
        return eventos;
    }

    public boolean insertar(Evento evento) throws SQLException {
        String sql = """
                INSERT INTO eventos (nombre, descripcion, fecha, lugar, modalidad, estado)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            asignarDatos(ps, evento);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean actualizar(Evento evento) throws SQLException {
        String sql = """
                UPDATE eventos
                SET nombre = ?, descripcion = ?, fecha = ?, lugar = ?, modalidad = ?, estado = ?
                WHERE id_evento = ?
                """;
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            asignarDatos(ps, evento);
            ps.setInt(7, evento.getIdEvento());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean eliminar(int idEvento) throws SQLException {
        String sql = "DELETE FROM eventos WHERE id_evento = ?";
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idEvento);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean existeDuplicado(String nombre, Date fecha, String lugar,
                                    Integer idExcluir) throws SQLException {
        String sql = """
                SELECT COUNT(*) FROM eventos
                WHERE LOWER(nombre) = LOWER(?) AND fecha = ? AND LOWER(lugar) = LOWER(?)
                AND (? IS NULL OR id_evento <> ?)
                """;
        try (Connection conexion = Conexion.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDate(2, fecha);
            ps.setString(3, lugar);
            if (idExcluir == null) {
                ps.setNull(4, Types.INTEGER);
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(4, idExcluir);
                ps.setInt(5, idExcluir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void asignarDatos(PreparedStatement ps, Evento evento) throws SQLException {
        ps.setString(1, evento.getNombre());
        ps.setString(2, evento.getDescripcion());
        ps.setDate(3, Date.valueOf(evento.getFecha()));
        ps.setString(4, evento.getLugar());
        ps.setString(5, evento.getModalidad());
        ps.setString(6, evento.getEstado());
    }

    private Evento mapear(ResultSet rs) throws SQLException {
        return new Evento(
                rs.getInt("id_evento"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDate("fecha").toLocalDate(),
                rs.getString("lugar"),
                rs.getString("modalidad"),
                rs.getString("estado")
        );
    }
}
