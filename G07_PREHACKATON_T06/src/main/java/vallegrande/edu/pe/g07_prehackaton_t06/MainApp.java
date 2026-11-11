package vallegrande.edu.pe.g07_prehackaton_t06;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        URL fxml = MainApp.class.getResource(
                "/vallegrande/edu/pe/g01_prehackaton_t06/hello-view.fxml");
        if (fxml == null) {
            throw new IllegalStateException("No se encontró hello-view.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxml);
        Scene scene = new Scene(loader.load(), 1100, 720);
        stage.setTitle("Sistema de Gestión de Eventos - T06");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
