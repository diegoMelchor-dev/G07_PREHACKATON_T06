package vallegrande.edu.pe.cuyconectacrud.dao;

import vallegrande.edu.pe.cuyconectacrud.model.ConexionDB;
import vallegrande.edu.pe.cuyconectacrud.model.Participante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la entidad PARTICIPANTES (Integrante 2).
 *
 * Todas las consultas SQL del módulo viven aquí; los controladores
 * y las vistas FXML nunca ejecutan SQL.
 */
public class ParticipanteDAO {

    private static final String COLUMNAS =
            "id_participante, nombres, apellidos, dni, correo, "
          + "telefono, institucion";

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    /** Lista todos los participantes ordenados por apellidos. */
    public List<Participante> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM participantes "
                   + "ORDER BY apellidos, nombres";

        List<Participante> lista = new ArrayList<>();

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
     * Búsqueda dinámica (Reto #2): filtra mientras se escribe y
     * opcionalmente por institución (ComboBox).
     *
     * @param criterio   texto libre (DNI, nombres, apellidos o correo);
     *                   null o vacío = sin filtro de texto
     * @param institucion institución exacta; null o vacía = todas
     */
    public List<Participante> filtrar(String criterio, String institucion)
            throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT " + COLUMNAS + " FROM participantes "
              + "WHERE (CAST(id_participante AS CHAR) LIKE ? "
              + "    OR dni LIKE ? OR nombres LIKE ? OR apellidos LIKE ? "
              + "    OR correo LIKE ? OR institucion LIKE ?)");

        boolean filtraInstitucion =
                institucion != null && !institucion.isBlank();

        if (filtraInstitucion) {
            sql.append(" AND institucion = ?");
        }
        sql.append(" ORDER BY apellidos, nombres");

        List<Participante> lista = new ArrayList<>();
        String filtro = "%" + (criterio == null ? "" : criterio.trim()) + "%";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 1; i <= 6; i++) {
                ps.setString(i, filtro);
            }
            if (filtraInstitucion) {
                ps.setString(7, institucion.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** Busca un participante por su id; null si no existe. */
    public Participante buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM participantes "
                   + "WHERE id_participante = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Instituciones distintas registradas, para el ComboBox de filtro. */
    public List<String> listarInstituciones() throws SQLException {
        String sql = "SELECT DISTINCT institucion FROM participantes "
                   + "ORDER BY institucion";

        List<String> instituciones = new ArrayList<>();

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                instituciones.add(rs.getString("institucion"));
            }
        }
        return instituciones;
    }

    /**
     * Lista ordenada por institución para el reporte agrupado
     * de JasperReports.
     */
    public List<Participante> listarOrdenadoPorInstitucion()
            throws SQLException {

        String sql = "SELECT " + COLUMNAS + " FROM participantes "
                   + "ORDER BY institucion, apellidos, nombres";

        List<Participante> lista = new ArrayList<>();

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
    // Verificación de duplicados
    // ---------------------------------------------------------------

    /**
     * Indica si ya existe otro participante con el mismo DNI.
     *
     * @param idExcluir id del registro que se está editando
     *                  (null cuando es un alta), para no chocar
     *                  consigo mismo al actualizar.
     */
    public boolean existeDni(String dni, Integer idExcluir)
            throws SQLException {

        return existeCampo("dni", dni, idExcluir);
    }

    /** Igual que {@link #existeDni(String, Integer)} pero por correo. */
    public boolean existeCorreo(String correo, Integer idExcluir)
            throws SQLException {

        return existeCampo("correo", correo, idExcluir);
    }

    // ---------------------------------------------------------------
    // CREATE / UPDATE / DELETE
    // ---------------------------------------------------------------

    /** Inserta un participante y devuelve el id generado. */
    public int insertar(Participante p) throws SQLException {
        String sql = "INSERT INTO participantes "
                   + "(nombres, apellidos, dni, correo, telefono, institucion) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            llenar(ps, p);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Actualiza un participante por su id.
     *
     * @return filas afectadas (0 = el registro ya no existe).
     */
    public int actualizar(Participante p) throws SQLException {
        String sql = "UPDATE participantes "
                   + "SET nombres = ?, apellidos = ?, dni = ?, correo = ?, "
                   + "    telefono = ?, institucion = ? "
                   + "WHERE id_participante = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            llenar(ps, p);
            ps.setInt(7, p.getIdParticipante());
            return ps.executeUpdate();
        }
    }

    /**
     * Elimina un participante por su id.
     *
     * @return true si se eliminó, false si el registro ya no existía.
     */
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM participantes WHERE id_participante = ?";

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------
    // Privados
    // ---------------------------------------------------------------

    private boolean existeCampo(String campo, String valor, Integer idExcluir)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM participantes "
                   + "WHERE " + campo + " = ?";

        boolean excluye = idExcluir != null;
        if (excluye) {
            sql += " AND id_participante <> ?";
        }

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, valor.trim());
            if (excluye) {
                ps.setInt(2, idExcluir);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void llenar(PreparedStatement ps, Participante p)
            throws SQLException {

        ps.setString(1, p.getNombres().trim());
        ps.setString(2, p.getApellidos().trim());
        ps.setString(3, p.getDni().trim());
        ps.setString(4, p.getCorreo().trim());
        ps.setString(5, p.getTelefono().trim());
        ps.setString(6, p.getInstitucion().trim());
    }

    private Participante mapear(ResultSet rs) throws SQLException {
        return new Participante(
                rs.getInt("id_participante"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("dni"),
                rs.getString("correo"),
                rs.getString("telefono"),
                rs.getString("institucion")
        );
    }
}
