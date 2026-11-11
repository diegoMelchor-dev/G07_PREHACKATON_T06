package vallegrande.edu.pe.cuyconectacrud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX del módulo Gestión de Participantes
 * (Integrante 2). Se ejecuta sola con:
 *
 * <pre>./mvnw javafx:run</pre>
 *
 * La app Swing principal (Main) no se ve afectada.
 */
public class ParticipantesApp extends Application {

    private static final String RUTA_FXML =
            "/vallegrande/edu/pe/cuyconectacrud/view/participantes-view.fxml";
    private static final String RUTA_CSS =
            "/vallegrande/edu/pe/cuyconectacrud/view/participantes.css";

    @Override
    public void start(Stage escenario) throws Exception {
        FXMLLoader cargador =
                new FXMLLoader(getClass().getResource(RUTA_FXML));
        Parent raiz = cargador.load();

        Scene escena = new Scene(raiz, 1050, 650);
        escena.getStylesheets()
                .add(getClass().getResource(RUTA_CSS).toExternalForm());

        escenario.setTitle(
                "Gestión de Participantes — Eventos y Conferencias");
        escenario.setMinWidth(900);
        escenario.setMinHeight(550);
        escenario.setScene(escena);
        escenario.show();
    }
}
