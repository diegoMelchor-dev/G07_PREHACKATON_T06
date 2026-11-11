package vallegrande.edu.pe.cuyconectacrud.dao;

import vallegrande.edu.pe.cuyconectacrud.model.ConexionDB;
import vallegrande.edu.pe.cuyconectacrud.model.EventoOpcion;
import vallegrande.edu.pe.cuyconectacrud.model.Inscripcion;
import vallegrande.edu.pe.cuyconectacrud.model.ParticipanteOpcion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la entidad INSCRIPCIONES (Integrante 3).
 *
 * Todas las consultas SQL del módulo viven aquí; los controladores
 * y las vistas FXML nunca ejecutan SQL.
 */
public class InscripcionDAO {

    /** Consulta base con JOIN a eventos y participantes. */
    private static final String SELECT_BASE =
            "SELECT i.id_inscripcion, i.id_evento, i.id_participante, "
          + "       i.fecha_inscripcion, i.estado, "
          + "       e.nombre AS evento_nombre, e.fecha AS evento_fecha, "
          + "       e.lugar AS evento_lugar, "
          + "       CONCAT(p.nombres, ' ', p.apellidos) AS participante_nombre, "
          + "       p.dni AS participante_dni, "
          + "       p.institucion AS participante_institucion "
          + "FROM inscripciones i "
          + "JOIN eventos e ON e.id_evento = i.id_evento "
          + "JOIN participantes p ON p.id_participante = i.id_participante ";

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    /** Lista todas las inscripciones, las más recientes primero. */
    public List<Inscripcion> listar() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY i.id_inscripcion DESC";

        List<Inscripcion> lista = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /**
     * Búsqueda dinámica: filtra por texto libre y, opcionalmente,
     * por estado y por evento.
     *
     * @param criterio texto libre (id, evento, nombre, DNI o institución);
     *                 null o vacío = sin filtro de texto
     * @param estado   estado exacto; null o vacío = todos
     * @param idEvento evento exacto; null = todos
     */
    public List<Inscripcion> filtrar(String criterio, String estado,
                                     Integer idEvento) throws SQLException {

        StringBuilder sql = new StringBuilder(SELECT_BASE).append(
                "WHERE (CAST(i.id_inscripcion AS CHAR) LIKE ? "
              + "    OR e.nombre LIKE ? OR p.nombres LIKE ? "
              + "    OR p.apellidos LIKE ? OR p.dni LIKE ? "
              + "    OR p.institucion LIKE ?)");

        boolean filtraEstado = estado != null && !estado.isBlank();
        boolean filtraEvento = idEvento != null;

        if (filtraEstado) {
            sql.append(" AND i.estado = ?");
        }
        if (filtraEvento) {
            sql.append(" AND i.id_evento = ?");
        }
        sql.append(" ORDER BY e.fecha DESC, e.nombre, p.apellidos, p.nombres");

        List<Inscripcion> lista = new ArrayList<>();
        String filtro = "%" + (criterio == null ? "" : criterio.trim()) + "%";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            int posicion = 1;
            for (int i = 0; i < 6; i++) {
                ps.setString(posicion++, filtro);
            }
            if (filtraEstado) {
                ps.setString(posicion++, estado.trim());
            }
            if (filtraEvento) {
                ps.setInt(posicion, idEvento);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** Busca una inscripción por su id; null si no existe. */
    public Inscripcion buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE i.id_inscripcion = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Eventos disponibles para el ComboBox, el más próximo primero. */
    public List<EventoOpcion> listarEventos() throws SQLException {
        String sql = "SELECT id_evento, nombre, fecha FROM eventos "
                   + "ORDER BY fecha DESC, nombre";

        List<EventoOpcion> eventos = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date fecha = rs.getDate("fecha");
                eventos.add(new EventoOpcion(
                        rs.getInt("id_evento"),
                        rs.getString("nombre"),
                        fecha == null ? null : fecha.toLocalDate()
                ));
            }
        }
        return eventos;
    }

    /** Participantes disponibles para el ComboBox, por apellidos. */
    public List<ParticipanteOpcion> listarParticipantes()
            throws SQLException {

        String sql = "SELECT id_participante, "
                   + "       CONCAT(nombres, ' ', apellidos) AS nombre_completo, "
                   + "       dni "
                   + "FROM participantes "
                   + "ORDER BY apellidos, nombres";

        List<ParticipanteOpcion> participantes = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                participantes.add(new ParticipanteOpcion(
                        rs.getInt("id_participante"),
                        rs.getString("nombre_completo"),
                        rs.getString("dni")
                ));
            }
        }
        return participantes;
    }

    /**
     * Lista ordenada por evento para el reporte agrupado de
     * JasperReports (cada evento queda en un bloque contiguo).
     */
    public List<Inscripcion> listarParaReporte() throws SQLException {
        String sql = SELECT_BASE
                   + "ORDER BY e.fecha, e.nombre, p.apellidos, p.nombres";

        List<Inscripcion> lista = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ---------------------------------------------------------------
    // Verificaciones previas (existencia y duplicados)
    // ---------------------------------------------------------------

    /** Indica si el evento existe. */
    public boolean existeEvento(int idEvento) throws SQLException {
        return existe("SELECT COUNT(*) FROM eventos WHERE id_evento = ?",
                idEvento);
    }

    /** Indica si el participante existe. */
    public boolean existeParticipante(int idParticipante)
            throws SQLException {

        return existe(
                "SELECT COUNT(*) FROM participantes WHERE id_participante = ?",
                idParticipante);
    }

    /**
     * Indica si ya existe otra inscripción del mismo participante en
     * el mismo evento.
     *
     * @param idExcluir id de la inscripción que se está editando
     *                  (null cuando es un alta), para no chocar
     *                  consigo misma al actualizar.
     */
    public boolean existeInscripcion(int idEvento, int idParticipante,
                                     Integer idExcluir) throws SQLException {

        String sql = "SELECT COUNT(*) FROM inscripciones "
                   + "WHERE id_evento = ? AND id_participante = ?";

        boolean excluye = idExcluir != null;
        if (excluye) {
            sql += " AND id_inscripcion <> ?";
        }

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idEvento);
            ps.setInt(2, idParticipante);
            if (excluye) {
                ps.setInt(3, idExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ---------------------------------------------------------------
    // CREATE / UPDATE / DELETE
    // ---------------------------------------------------------------

    /** Inserta una inscripción y devuelve el id generado. */
    public int insertar(Inscripcion i) throws SQLException {
        String sql = "INSERT INTO inscripciones "
                   + "(id_evento, id_participante, fecha_inscripcion, estado) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            llenar(ps, i);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Actualiza una inscripción por su id.
     *
     * @return filas afectadas (0 = el registro ya no existe).
     */
    public int actualizar(Inscripcion i) throws SQLException {
        String sql = "UPDATE inscripciones "
                   + "SET id_evento = ?, id_participante = ?, "
                   + "    fecha_inscripcion = ?, estado = ? "
                   + "WHERE id_inscripcion = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            llenar(ps, i);
            ps.setInt(5, i.getIdInscripcion());
            return ps.executeUpdate();
        }
    }

    /**
     * Elimina una inscripción por su id.
     *
     * @return true si se eliminó, false si el registro ya no existía.
     */
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM inscripciones WHERE id_inscripcion = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // Privados
    // ---------------------------------------------------------------

    private boolean existe(String sql, int id) throws SQLException {
        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void llenar(PreparedStatement ps, Inscripcion i)
            throws SQLException {

        ps.setInt(1, i.getIdEvento());
        ps.setInt(2, i.getIdParticipante());
        ps.setDate(3, Date.valueOf(i.getFechaInscripcion()));
        ps.setString(4, i.getEstado().trim());
    }

    private Inscripcion mapear(ResultSet rs) throws SQLException {
        return new Inscripcion(
                rs.getInt("id_inscripcion"),
                rs.getInt("id_evento"),
                rs.getInt("id_participante"),
                aLocalDate(rs.getDate("fecha_inscripcion")),
                rs.getString("estado"),
                rs.getString("evento_nombre"),
                aLocalDate(rs.getDate("evento_fecha")),
                rs.getString("evento_lugar"),
                rs.getString("participante_nombre"),
                rs.getString("participante_dni"),
                rs.getString("participante_institucion")
        );
    }

    private LocalDate aLocalDate(Date fecha) {
        return fecha == null ? null : fecha.toLocalDate();
    }
}
