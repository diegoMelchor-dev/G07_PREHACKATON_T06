
package vallegrande.edu.pe.cuyconectacrud.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    public static Connection conectar() throws SQLException {
        String host = System.getenv("DB_HOST");
        String puerto = System.getenv("DB_PORT");
        String baseDatos = System.getenv("DB_NAME");
        String usuario = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (host == null || puerto == null || baseDatos == null
                || usuario == null || password == null) {
            throw new SQLException(
                    "Faltan variables de entorno de la base de datos."
            );
        }

        String url = "jdbc:mysql://" + host + ":" + puerto + "/"
                + baseDatos + "?sslMode=REQUIRED";

        return DriverManager.getConnection(url, usuario, password);
    }
}
