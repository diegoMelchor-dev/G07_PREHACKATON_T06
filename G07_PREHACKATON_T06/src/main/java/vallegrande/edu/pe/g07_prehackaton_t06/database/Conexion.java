package vallegrande.edu.pe.g07_prehackaton_t06.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Conexion {
    private static final Properties PROPERTIES = cargarPropiedades();

    private Conexion() {
    }

    private static Properties cargarPropiedades() {
        Properties properties = new Properties();
        try (InputStream input = Conexion.class.getResourceAsStream("/db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError("No se pudo leer db.properties: " + e.getMessage());
        }
        return properties;
    }

    private static String valor(String variable, String clave, String defecto) {
        String entorno = System.getenv(variable);
        if (entorno != null && !entorno.isBlank()) {
            return entorno;
        }
        return PROPERTIES.getProperty(clave, defecto);
    }

    public static Connection conectar() throws SQLException {
        String url = valor("DB_URL", "db.url",
                "jdbc:mysql://127.0.0.1:3307/db_eventos?useSSL=false&serverTimezone=UTC");
        String user = valor("DB_USER", "db.user", "root");
        String password = valor("DB_PASSWORD", "db.password", "");
        return DriverManager.getConnection(url, user, password);
    }
}
