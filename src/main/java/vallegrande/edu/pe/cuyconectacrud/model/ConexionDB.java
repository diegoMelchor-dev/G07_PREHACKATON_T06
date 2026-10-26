package vallegrande.edu.pe.cuyconectacrud.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {

    private static final Properties CONFIG = cargarConfig();

    private static Properties cargarConfig() {
        Properties p = new Properties();
        Path ruta = Path.of("config.properties");
        if (Files.isRegularFile(ruta)) {
            try (InputStream in = Files.newInputStream(ruta)) {
                p.load(in);
            } catch (IOException e) {
                System.err.println("No se pudo leer config.properties: " + e.getMessage());
            }
        }
        return p;
    }

    private static String valor(String clave) {
        String v = System.getenv(clave);
        return (v != null && !v.isBlank()) ? v : CONFIG.getProperty(clave);
    }

    public static Connection conectar() throws SQLException {
        String host = valor("DB_HOST");
        String puerto = valor("DB_PORT");
        String baseDatos = valor("DB_NAME");
        String usuario = valor("DB_USER");
        String password = valor("DB_PASSWORD");
        String sslMode = valor("DB_SSL_MODE");

        if (host == null || puerto == null || baseDatos == null
                || usuario == null || password == null) {
            throw new SQLException(
                    "Faltan datos de conexión. Copia config.properties.example a "
                            + "config.properties y completa tus credenciales.");
        }

        if (sslMode == null || sslMode.isBlank()) {
            sslMode = "REQUIRED";
        }

        String url = "jdbc:mysql://" + host + ":" + puerto + "/"
                + baseDatos + "?sslMode=" + sslMode;

        if (sslMode.equalsIgnoreCase("DISABLED")) {
            url += "&allowPublicKeyRetrieval=true";
        }

        return DriverManager.getConnection(url, usuario, password);
    }
}