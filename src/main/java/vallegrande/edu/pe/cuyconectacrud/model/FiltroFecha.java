package vallegrande.edu.pe.cuyconectacrud.model;

/**
 * Rangos de fecha disponibles en el ComboBox de filtros avanzados.
 * Cada valor conoce su propia condición SQL, de modo que el DAO
 * no necesita un switch ni recibe texto escrito por el usuario.
 */
public enum FiltroFecha {

    TODAS("Todas las fechas", null),
    HOY("Hoy", "DATE(fecha) = CURDATE()"),
    ULTIMOS_7_DIAS("Últimos 7 días",
            "fecha >= DATE_SUB(NOW(), INTERVAL 7 DAY)"),
    ULTIMOS_30_DIAS("Últimos 30 días",
            "fecha >= DATE_SUB(NOW(), INTERVAL 30 DAY)"),
    ESTE_MES("Este mes",
            "YEAR(fecha) = YEAR(CURDATE()) AND MONTH(fecha) = MONTH(CURDATE())");

    private final String etiqueta;
    private final String condicionSql;

    FiltroFecha(String etiqueta, String condicionSql) {
        this.etiqueta = etiqueta;
        this.condicionSql = condicionSql;
    }

    public boolean tieneCondicion() {
        return condicionSql != null;
    }

    public String getCondicionSql() {
        return condicionSql;
    }

    /** Texto que se muestra en el ComboBox. */
    @Override
    public String toString() {
        return etiqueta;
    }
}
