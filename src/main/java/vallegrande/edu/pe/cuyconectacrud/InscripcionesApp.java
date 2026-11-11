package vallegrande.edu.pe.cuyconectacrud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX del módulo Gestión de Inscripciones
 * (Integrante 3). Se ejecuta con LauncherInscripciones.
 */
public class InscripcionesApp extends Application {

    private static final String RUTA_FXML =
            "/vallegrande/edu/pe/cuyconectacrud/view/inscripciones-view.fxml";
    private static final String RUTA_CSS =
            "/vallegrande/edu/pe/cuyconectacrud/view/inscripciones.css";

    @Override
    public void start(Stage escenario) throws Exception {
        FXMLLoader cargador =
                new FXMLLoader(getClass().getResource(RUTA_FXML));
        Parent raiz = cargador.load();

        Scene escena = new Scene(raiz, 1100, 660);
        escena.getStylesheets()
                .add(getClass().getResource(RUTA_CSS).toExternalForm());

        escenario.setTitle(
                "Gestión de Inscripciones — Eventos y Conferencias");
        escenario.setMinWidth(950);
        escenario.setMinHeight(560);
        escenario.setScene(escena);
        escenario.show();
    }
}
